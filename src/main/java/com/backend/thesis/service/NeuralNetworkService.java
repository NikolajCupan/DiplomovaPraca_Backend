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
import com.backend.thesis.utility.Type;
import com.backend.thesis.utility.other.RequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NeuralNetworkService {
    private static class Batches {
        private final Batch trainInput;
        private final Batch trainTarget;
        private final Batch testInput;
        private final Batch testTarget;

        public Batches() {
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
            final NeuralNetwork neuralNetwork = NeuralNetworkService.buildNeuralNetwork(
                    this.objectMapper, inputWindowSize, optimizerName, startingLearningRate, learningRateDecay, epsilon, beta1, beta2, rho, momentum, lossFunction, maxPercentageDifference, rawLayers
            );
            final Batches batches = NeuralNetworkService.buildBatches(
                    datasetForEditingDto, trainPercent, inputWindowSize
            );

            final int printEveryEpoch = (int) Math.ceil(epochCount / 100.0);
            neuralNetwork.train(
                    batches.trainInput, batches.trainTarget, epochCount, batchSize, printEveryEpoch, Integer.MAX_VALUE
            );

            return null;
        } catch (final RequestException requestException) {
            return new Type.ActionResult<>(Type.ActionResultType.FAILURE, requestException.getMessage(), null);
        }
    }

    private static Batches buildBatches(final DatasetForEditingDto datasetForEditingDto, final Long trainPercent, final int inputWindowSize) throws RequestException {
        final double[] rawValues = datasetForEditingDto.getRawValues();

        final int datasetSize = rawValues.length;
        final int trainSize = (int) (datasetSize / 100.0 * trainPercent);
        final int testSize = datasetSize - trainSize;

        final double[] rawTrainValues = Arrays.copyOfRange(rawValues, 0, trainSize);
        final double[] rawTestValues = Arrays.copyOfRange(rawValues, trainSize, datasetSize);

        if (inputWindowSize >= trainSize || inputWindowSize >= testSize) {
            throw new RequestException("Veľkosť vstupného okna je príliš veľká");
        }

        final Batches batches = new Batches();
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
            final String rawLayers
    ) throws RequestException {
        final List<Map<String, Object>> layers = NeuralNetworkService.processRawLayers(objectMapper, rawLayers);
        assert (layers.getFirst().get("type").equals("hidden"));

        final NeuralNetwork neuralNetwork = new NeuralNetwork(inputWindowSize);
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
