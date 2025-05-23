package com.backend.thesis.controller;

import com.backend.thesis.domain.dto.DatasetForEditingDto;
import com.backend.thesis.domain.entity.DatasetEntity;
import com.backend.thesis.service.DatasetService;
import com.backend.thesis.service.NeuralNetworkService;
import com.backend.thesis.utility.Constants;
import com.backend.thesis.utility.Helper;
import com.backend.thesis.utility.Type;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class NeuralNetworkController {
    private final Map<String, String> activeWebsockets;
    private final SimpMessagingTemplate simpMessagingTemplate;

    private final DatasetService datasetService;
    private final NeuralNetworkService neuralNetworkService;

    public NeuralNetworkController(
            final SimpMessagingTemplate simpMessagingTemplate,
            final DatasetService datasetService,
            final NeuralNetworkService neuralNetworkService
    ) {
        this.activeWebsockets = new ConcurrentHashMap<>();
        this.simpMessagingTemplate = simpMessagingTemplate;

        this.datasetService = datasetService;
        this.neuralNetworkService = neuralNetworkService;
    }

    @EventListener
    public void onSocketConnected(final SessionConnectedEvent event) {
        final StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        final var header = ((GenericMessage<?>) sha.getHeader("simpConnectMessage")).getHeaders().get("nativeHeaders");

        final Map<String, List<String>> headerMap = (Map<String, List<String>>) header;
        final String cookie = headerMap.get("login").get(0);
        final String sessionId = sha.getSessionId();

        this.activeWebsockets.put(cookie, sessionId);
    }

    @EventListener
    public void onSocketDisconnected(SessionDisconnectEvent event) {
        final StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        final String sessionId = sha.getSessionId();

        this.activeWebsockets.values().remove(sessionId);
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/model/neural-network")
    public ResponseEntity<Type.RequestResult<String>> handleNeuralNetworkModel(
            final HttpServletRequest request,
            @RequestParam(name = "idDataset") final String idDataset,
            @RequestParam(name = "train_percent") final String trainPercent,
            @RequestParam(name = "forecast_count") final String forecastCount,
            @RequestParam(name = "input_window_size") final String inputWindowSize,
            @RequestParam(name = "batch_size", required = false) final Optional<String> batchSize,
            @RequestParam(name = "epoch_count") final String epochCount,
            @RequestParam(name = "optimizer") final String optimizer,
            @RequestParam(name = "starting_learning_rate") final String startingLearningRate,
            @RequestParam(name = "learning_rate_decay") final String learningRateDecay,
            @RequestParam(name = "epsilon") final String epsilon,
            @RequestParam(name = "beta1") final String beta1,
            @RequestParam(name = "beta2") final String beta2,
            @RequestParam(name = "rho") final String rho,
            @RequestParam(name = "momentum") final String momentum,
            @RequestParam(name = "loss_function") final String lossFunction,
            @RequestParam(name = "max_percentage_difference") final String maxPercentageDifference,
            @RequestParam(name = "layers") final String layers
    ) {
        final String cookie = request.getAttribute(Constants.SESSION_COOKIE_NAME).toString();
        final Type.ActionResult<DatasetEntity> datasetResult = this.datasetService.getDatasetOfUser(
                cookie,
                Helper.stringToLong(idDataset)
        );

        if (!datasetResult.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(datasetResult.message(), null), HttpStatus.BAD_REQUEST);
        }
        if (!this.activeWebsockets.containsKey(cookie)) {
            return new ResponseEntity<>(new Type.RequestResult<>("Websocket spojenie nebolo nadviazané", null), HttpStatus.BAD_REQUEST);
        }


        final DatasetForEditingDto datasetForEditingDto = this.datasetService.getDatasetOfUserForEditing(
                cookie, datasetResult.data().getIdDataset(), true
        ).data();

        final Type.ActionResult<JSONObject> result = this.neuralNetworkService.neuralNetwork(
                this.simpMessagingTemplate,
                this.activeWebsockets,
                cookie,
                datasetForEditingDto,
                Helper.stringToLong(trainPercent),
                Helper.stringToLong(forecastCount),
                Helper.stringToLong(inputWindowSize).intValue(),
                Helper.tryStringToInt(batchSize).orElse(Integer.MAX_VALUE),
                Helper.stringToLong(epochCount).intValue(),
                optimizer,
                Helper.stringToDouble(startingLearningRate),
                Helper.stringToDouble(learningRateDecay),
                Helper.stringToDouble(epsilon),
                Helper.stringToDouble(beta1),
                Helper.stringToDouble(beta2),
                Helper.stringToDouble(rho),
                Helper.stringToDouble(momentum),
                lossFunction,
                Helper.stringToDouble(maxPercentageDifference),
                layers
        );

        if (result.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data().toString()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), result.getHttpStatus());
        }
    }
}
