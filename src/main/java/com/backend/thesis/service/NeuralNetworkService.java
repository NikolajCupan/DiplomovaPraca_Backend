package com.backend.thesis.service;

import com.backend.thesis.domain.entity.DatasetEntity;
import com.backend.thesis.utility.Type;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NeuralNetworkService {
    public Type.ActionResult<JSONObject> neuralNetwork(
            final DatasetEntity datasetEntity,
            final Long trainPercent,
            final Long forecastCount,
            final Long inputWindowSize,
            final Optional<Integer> batchSize,
            final Long epochCount,
            final String optimizer,
            final Double startingLearningRate,
            final Double learningRateDecay,
            final Double epsilon,
            final Double beta1,
            final Double beta2,
            final Double rho,
            final Double momentum,
            final String lossFunction,
            final Double maxPercentageDifference,
            final String layers
    ) {
        JSONArray jsonLayers;
        try {
            jsonLayers = new JSONArray(layers);
        } catch (final Exception exception) {
            return new Type.ActionResult<>(Type.ActionResultType.FAILURE, "Chyba pri spracovaní vrstiev", null);
        }

        return null;
    }
}
