package com.backend.thesis.service;

import NeuralNetwork.ActivationFunctions.*;
import NeuralNetwork.BuildingBlocks.Batch;
import NeuralNetwork.BuildingBlocks.DataList;
import NeuralNetwork.BuildingBlocks.RegularizerStruct;
import NeuralNetwork.Layers.Common.ActivationLayer;
import NeuralNetwork.Layers.Common.DropoutLayer;
import NeuralNetwork.Layers.Common.HiddenLayer;
import NeuralNetwork.Layers.Common.LossLayer;
import NeuralNetwork.Layers.LayerBase;
import NeuralNetwork.LossFunctions.ILossFunction;
import NeuralNetwork.LossFunctions.MeanAbsoluteError;
import NeuralNetwork.LossFunctions.MeanSquaredError;
import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.Optimizers.*;
import com.backend.thesis.domain.dto.DatasetForEditingDto;
import com.backend.thesis.domain.dto.Frequency;
import com.backend.thesis.utility.Constants;
import com.backend.thesis.utility.Helper;
import com.backend.thesis.utility.Type;
import com.backend.thesis.utility.other.RequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class NeuralNetworkService {
    private static class Batches {
        private Frequency frequency;

        private LocalDateTime trainTargetStartDate;

        private final Batch fullInput;
        private final Batch fullTarget;

        private final Batch trainInput;
        private final Batch trainTarget;
        private final Batch testInput;
        private final Batch testTarget;

        public Batches() {
            this.fullInput = new Batch();
            this.fullTarget = new Batch();

            this.trainInput = new Batch();
            this.trainTarget = new Batch();
            this.testInput = new Batch();
            this.testTarget = new Batch();
        }
    }

    private final ObjectMapper objectMapper;

    public NeuralNetworkService(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Type.ActionResult<JSONObject> neuralNetwork(
            final SimpMessagingTemplate simpMessagingTemplate,
            final Map<String, String> activeWebsockets,
            final String cookie,
            final DatasetForEditingDto datasetForEditingDto,
            final Long trainPercent,
            final Long forecastCount,
            final int inputWindowSize,
            final int batchSize,
            final int epochCount,
            final String optimizerName,
            final Double startingLearningRate,
            final Double learningRateDecay,
            final Double epsilon,
            final Double beta1,
            final Double beta2,
            final Double rho,
            final Double momentum,
            final String lossFunction,
            final Double maxPercentageDifference,
            final String rawLayers
    ) {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final PrintStream outputStream = new PrintStream(byteArrayOutputStream);


            final NeuralNetwork neuralNetwork = NeuralNetworkService.buildNeuralNetwork(
                    this.objectMapper, inputWindowSize, optimizerName, startingLearningRate, learningRateDecay, epsilon, beta1, beta2, rho, momentum, lossFunction, maxPercentageDifference, rawLayers, outputStream
            );
            final Batches batches = NeuralNetworkService.buildBatches(
                    datasetForEditingDto, trainPercent, inputWindowSize
            );


            final Thread outputMonitorThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        if (!activeWebsockets.containsKey(cookie)) {
                            neuralNetwork.stopTraining();
                            break;
                        }

                        final String capturedOutput = byteArrayOutputStream.toString();
                        if (!capturedOutput.isEmpty()) {
                            simpMessagingTemplate.convertAndSendToUser(cookie, "/queue/notification/loss", capturedOutput);
                            byteArrayOutputStream.reset();
                        }

                        Thread.sleep(500);
                    } catch (final Exception ignore) {
                        System.out.println(ignore);
                        break;
                    }
                }
            });


            // Training
            final int epochPrintEvery = (int) Math.ceil(epochCount / 100.0);

            outputMonitorThread.start();
            neuralNetwork.train(
                    batches.trainInput, batches.trainTarget, epochCount, batchSize, epochPrintEvery, Integer.MAX_VALUE, Constants.NEURAL_NETWORK_TIMEOUT_MS
            );

            outputMonitorThread.interrupt();
            outputMonitorThread.join();

            final String remainingOutput = byteArrayOutputStream.toString();
            if (!remainingOutput.isEmpty()) {
                simpMessagingTemplate.convertAndSendToUser(cookie, "/queue/notification/loss", remainingOutput);
                byteArrayOutputStream.reset();
            }
            // Training end


            final JSONObject jsonResult = NeuralNetworkService.getResult(neuralNetwork, trainPercent, forecastCount, batches, byteArrayOutputStream);
            return new Type.ActionResult<>(Type.ActionResultType.SUCCESS, "Akcia bola úspešne dokončená", jsonResult);
        } catch (final RequestException requestException) {
            return new Type.ActionResult<>(Type.ActionResultType.FAILURE, requestException.getMessage(), null);
        } catch (final Exception exception) {
            return new Type.ActionResult<>(Type.ActionResultType.FAILURE, "Pri vykonávaní akcie nastala chyba", null);
        }
    }

    private static JSONObject getResult(
            final NeuralNetwork neuralNetwork,
            final Long trainPercent,
            final Long forecastCount,
            final Batches batches,
            final ByteArrayOutputStream byteArrayOutputStream
    ) throws RequestException {
        try {
            final JSONObject result = new JSONObject();

            // Accuracy
            result.put("train_accuracy", NeuralNetworkService.getAccuracyMetrics(neuralNetwork, batches.trainInput, batches.trainTarget, byteArrayOutputStream));
            if (trainPercent < 100) {
                result.put("test_accuracy", NeuralNetworkService.getAccuracyMetrics(neuralNetwork, batches.testInput, batches.testTarget, byteArrayOutputStream));
            }
            // Accuracy end

            // Predictions
            final double[] fittedValues = neuralNetwork.predict(batches.fullInput).getColumn(0).getDataListRawValues();


            final JSONArray trainDateArray = new JSONArray();
            final JSONArray trainRealArray = new JSONArray();
            final JSONArray trainFittedArray = new JSONArray();
            final JSONArray trainResidualsArray = new JSONArray();

            LocalDateTime activeDate = batches.trainTargetStartDate;
            for (int i = 0; i < batches.trainTarget.getRowsSize(); ++i) {
                final double realValue = batches.fullTarget.getRow(i).getValue(0);
                final double fittedValue = fittedValues[i];

                trainDateArray.put(Helper.localDateTimeToString(activeDate));
                trainRealArray.put(realValue);
                trainFittedArray.put(fittedValue);
                trainResidualsArray.put(realValue - fittedValue);

                activeDate = Helper.getNextDate(activeDate, batches.frequency);
            }

            final JSONObject trainResult = new JSONObject();
            trainResult.put("date", trainDateArray);
            trainResult.put("real", trainRealArray);
            trainResult.put("fitted", trainFittedArray);
            trainResult.put("residuals", trainResidualsArray);
            result.put("train", trainResult);


            if (trainPercent < 100) {
                final JSONArray testDateArray = new JSONArray();
                final JSONArray testRealArray = new JSONArray();
                final JSONArray testFittedArray = new JSONArray();
                final JSONArray testResidualsArray = new JSONArray();

                for (int i = batches.trainTarget.getRowsSize(); i < fittedValues.length; ++i) {
                    final double realValue = batches.fullTarget.getRow(i).getValue(0);
                    final double fittedValue = fittedValues[i];

                    testDateArray.put(Helper.localDateTimeToString(activeDate));
                    testRealArray.put(realValue);
                    testFittedArray.put(fittedValue);
                    testResidualsArray.put(realValue - fittedValue);

                    activeDate = Helper.getNextDate(activeDate, batches.frequency);
                }

                final JSONObject testResult = new JSONObject();
                testResult.put("date", testDateArray);
                testResult.put("real", testRealArray);
                testResult.put("fitted", testFittedArray);
                testResult.put("residuals", testResidualsArray);
                result.put("test", testResult);
            }
            // Predictions end


            if (forecastCount > 0) {
                ;
            }

            return result;
        } catch (final Exception exception) {
            throw new RequestException("Pri spracovaní výsledkov neurónovej siete nastala chyba");
        }
    }

    private static JSONObject getAccuracyMetrics(final NeuralNetwork neuralNetwork, final Batch input, final Batch target, final ByteArrayOutputStream byteArrayOutputStream) throws Exception {
        final JSONObject result = new JSONObject();

        neuralNetwork.test(input, target);
        final JSONObject capturedTestOutput = new JSONObject(byteArrayOutputStream.toString());
        result.put("Presnosť", capturedTestOutput.getDouble("accuracy"));

        final Batch predicted = neuralNetwork.predict(input);
        result.put("mse", NeuralNetworkService.calculateMSE(input.getColumn(0), predicted.getColumn(0)));
        result.put("rmse", NeuralNetworkService.calculateRMSE(input.getColumn(0), predicted.getColumn(0)));
        result.put("mae", NeuralNetworkService.calculateMAE(input.getColumn(0), predicted.getColumn(0)));

        return result;
    }

    private static double calculateMSE(final DataList predicted, final DataList target) {
        double sum = 0.0;
        for (int i = 0; i < predicted.getDataListSize(); ++i) {
            sum += Math.pow(target.getValue(i) - predicted.getValue(i), 2.0);
        }

        return sum / predicted.getDataListSize();
    }

    private static double calculateRMSE(final DataList predicted, final DataList target) {
        double sum = 0.0;
        for (int i = 0; i < predicted.getDataListSize(); ++i) {
            sum += Math.pow(target.getValue(i) - predicted.getValue(i), 2.0);
        }

        return Math.sqrt(sum / predicted.getDataListSize());
    }

    private static double calculateMAE(final DataList predicted, final DataList target) {
        double sum = 0.0;
        for (int i = 0; i < predicted.getDataListSize(); ++i) {
            sum += Math.abs(target.getValue(i) - predicted.getValue(i));
        }

        return sum / predicted.getDataListSize();
    }


    private static Batches buildBatches(final DatasetForEditingDto datasetForEditingDto, final Long trainPercent, final int inputWindowSize) throws RequestException {
        final List<Type.DatasetRow> rows = datasetForEditingDto.getRows();
        final double[] rawValues = datasetForEditingDto.getRawValues();

        final int datasetSize = rawValues.length;
        final int trainSize = (int) (datasetSize / 100.0 * trainPercent);
        final int testSize = datasetSize - trainSize;

        final double[] rawTrainValues = Arrays.copyOfRange(rawValues, 0, trainSize);
        final double[] rawTestValues = Arrays.copyOfRange(rawValues, trainSize, datasetSize);

        if (inputWindowSize >= trainSize || (testSize > 0 && inputWindowSize >= testSize)) {
            throw new RequestException("Veľkosť vstupného okna je príliš veľká");
        }


        final Batches batches = new Batches();
        batches.frequency = Helper.stringToFrequency(datasetForEditingDto.getDatasetInfoDto().getFrequencyType());
        batches.trainTargetStartDate = rows.get(inputWindowSize).dateTime();


        NeuralNetworkService.processRawValues(rawValues, inputWindowSize, batches.fullInput, batches.fullTarget);
        NeuralNetworkService.processRawValues(rawTrainValues, inputWindowSize, batches.trainInput, batches.trainTarget);
        NeuralNetworkService.processRawValues(rawTestValues, inputWindowSize, batches.testInput, batches.testTarget);

        return batches;
    }

    private static void processRawValues(final double[] rawValues, final int inputWindowSize, final Batch input, final Batch target) {
        for (int batchIndex = 0; batchIndex < rawValues.length - inputWindowSize; ++batchIndex) {
            final int endIndex = batchIndex + inputWindowSize;

            final DataList inputList = new DataList(Arrays.copyOfRange(rawValues, batchIndex, endIndex));
            input.addRow(inputList);

            final DataList targetList = new DataList(Arrays.copyOfRange(rawValues, endIndex, endIndex + 1));
            target.addRow(targetList);
        }
    }

    private static NeuralNetwork buildNeuralNetwork(
            final ObjectMapper objectMapper,
            final int inputWindowSize,
            final String optimizerName,
            final Double startingLearningRate,
            final Double learningRateDecay,
            final Double epsilon,
            final Double beta1,
            final Double beta2,
            final Double rho,
            final Double momentum,
            final String lossFunction,
            final Double maxPercentageDifference,
            final String rawLayers,
            final PrintStream outputStream
    ) throws RequestException {
        final List<Map<String, Object>> layers = NeuralNetworkService.processRawLayers(objectMapper, rawLayers);
        assert (layers.getFirst().get("type").equals("hidden"));

        final NeuralNetwork neuralNetwork = new NeuralNetwork(inputWindowSize, outputStream);
        int weightsSize = inputWindowSize;

        for (final Map<String, Object> layer : layers) {
            if (layer.get("type").equals("hidden")) {
                final int neuronsSize = (int) layer.get("neurons_count");
                final HiddenLayer hiddenLayer = new HiddenLayer(weightsSize, neuronsSize);

                final double biasesRegularizerL1 = NeuralNetworkService.parseNumericValue(layer.get("biases_regularizer_l1"));
                final double biasesRegularizerL2 = NeuralNetworkService.parseNumericValue(layer.get("biases_regularizer_l2"));
                final double weightsRegularizerL1 = NeuralNetworkService.parseNumericValue(layer.get("biases_regularizer_l1"));
                final double weightsRegularizerL2 = NeuralNetworkService.parseNumericValue(layer.get("biases_regularizer_l2"));

                if (biasesRegularizerL1 != 0.0 || biasesRegularizerL2 != 0.0 || weightsRegularizerL1 != 0.0 || weightsRegularizerL2 != 0.0) {
                    hiddenLayer.initializeRegularizer(
                            new RegularizerStruct(biasesRegularizerL1, biasesRegularizerL2, weightsRegularizerL1, weightsRegularizerL2)
                    );
                }

                final ActivationLayer activationLayer = NeuralNetworkService.getActivationLayer(layer);


                neuralNetwork.addHiddenLayer(hiddenLayer);
                neuralNetwork.addActivationLayer(activationLayer);

                weightsSize = neuronsSize;
            } else {
                final double keepRate = NeuralNetworkService.parseNumericValue(layer.get("keep_rate")) / 100.0;
                neuralNetwork.addDropoutLayer(new DropoutLayer(keepRate));
            }
        }

        final List<LayerBase> neuralNetworkLayers = neuralNetwork.getLayers();
        final HiddenLayer lastHiddenLayer = (HiddenLayer) neuralNetworkLayers.get(neuralNetworkLayers.size() - 2);

        if (lastHiddenLayer.getNeuronsSize() != 1) {
            // Last layer must output a single value, if it is not the case, add one more layer
            neuralNetwork.addHiddenLayer(new HiddenLayer(lastHiddenLayer.getNeuronsSize(), 1));
            neuralNetwork.addActivationLayer(new ActivationLayer(new Linear()));
        }


        final LossLayer lossLayer = NeuralNetworkService.getLossLayer(lossFunction, maxPercentageDifference);
        neuralNetwork.addLossLayer(lossLayer);

        final OptimizerBase optimizer = NeuralNetworkService.getOptimizer(
                neuralNetwork, optimizerName, startingLearningRate, learningRateDecay, epsilon, beta1, beta2, rho, momentum
        );
        neuralNetwork.setOptimizer(optimizer);

        return neuralNetwork;
    }

    private static List<Map<String, Object>> processRawLayers(final ObjectMapper objectMapper, final String rawLayers) throws RequestException {
        final List<Map<String, Object>> layers = new ArrayList<>();

        try {
            final JSONArray jsonLayers = new JSONArray(rawLayers);

            for (int i = 0; i < jsonLayers.length(); ++i) {
                final String stringLayer = jsonLayers.getString(i);
                final Map<String, Object> layer = objectMapper.readValue(stringLayer, Map.class);

                layers.add(layer);
            }
        } catch (final Exception exception) {
            throw new RequestException("Chyba pri spracovaní vrstiev");
        }

        return layers;
    }

    public static ActivationLayer getActivationLayer(final Map<String, Object> layer) throws RequestException {
        final String activationFunctionName = String.valueOf(layer.get("activation_function"));
        IActivationFunction activationFunction;

        switch (activationFunctionName) {
            case "linear" -> activationFunction = new Linear();
            case "relu" -> activationFunction = new RectifiedLinearUnit();
            case "leaky_relu" -> {
                final double slope = NeuralNetworkService.parseNumericValue(layer.get("slope"));
                activationFunction = new LeakyRectifiedLinearUnit(slope);
            }
            case "sigmoid" -> activationFunction = new Sigmoid();
            case "tan_h" -> activationFunction = new Tanh();
            default -> throw new RequestException("Neznáma aktivačná funkcia");
        }

        return new ActivationLayer(activationFunction);
    }

    public static LossLayer getLossLayer(final String lossFunctionName, final Double maxPercentageDifference) throws RequestException {
        ILossFunction lossFunction;

        switch (lossFunctionName) {
            case "mean_squared_error" -> lossFunction = new MeanSquaredError(maxPercentageDifference / 100.0);
            case "mean_absolute_error" -> lossFunction = new MeanAbsoluteError(maxPercentageDifference / 100.0);
            default -> throw new RequestException("Neznáma stratová funkcia");
        }

        return new LossLayer(lossFunction);
    }

    public static OptimizerBase getOptimizer(
            final NeuralNetwork neuralNetwork,
            final String optimizerName,
            final Double startingLearningRate,
            final Double learningRateDecay,
            final Double epsilon,
            final Double beta1,
            final Double beta2,
            final Double rho,
            final Double momentum
    ) throws RequestException {
        return switch (optimizerName) {
            case "adaptive_gradient" ->
                    new AdaptiveGradient(neuralNetwork, startingLearningRate, learningRateDecay, epsilon);
            case "adaptive_momentum" ->
                    new AdaptiveMomentum(neuralNetwork, startingLearningRate, learningRateDecay, epsilon, beta1, beta2);
            case "root_mean_square_propagation" ->
                    new RootMeanSquarePropagation(neuralNetwork, startingLearningRate, learningRateDecay, epsilon, rho);
            case "stochastic_gradient_descent" ->
                    new StochasticGradientDescent(neuralNetwork, startingLearningRate, learningRateDecay);
            case "stochastic_gradient_descent_with_momentum" ->
                    new StochasticGradientDescentWithMomentum(neuralNetwork, startingLearningRate, learningRateDecay, momentum);
            default -> throw new RequestException("Neznámy optimalizátor");
        };
    }

    public static double parseNumericValue(final Object object) {
        if (object instanceof Double) {
            return (double) object;
        } else {
            return (int) object;
        }
    }
}
