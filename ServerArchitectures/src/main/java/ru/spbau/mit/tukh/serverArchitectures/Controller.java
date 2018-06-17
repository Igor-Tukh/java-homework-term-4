package ru.spbau.mit.tukh.serverArchitectures;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import ru.spbau.mit.tukh.serverArchitectures.server.Server;
import ru.spbau.mit.tukh.serverArchitectures.server.SingleThreadExecutorServer;
import ru.spbau.mit.tukh.serverArchitectures.server.ThreadForEachServer;

import java.io.IOException;

public class Controller {
    private Server.Metrics metrics = Server.Metrics.TIME_DELTA;
    private Server.ServerType serverType = Server.ServerType.THREAD_FOR_EACH;
    private int clientsNumber;
    private int requestsNumber;
    private int elementsNumber;
    private int timeDelta;
    private int metricsUpperBound;
    private int metricsStep;

    private static Scene scene;

    private static final int PORT = 23930;

    public static void setScene(Scene scene) {
        Controller.scene = scene;
    }

    public void onArchitectureChose(ActionEvent actionEvent) {
        switch (((MenuItem) actionEvent.getTarget()).getId()) {
            case "threadForEach":
                serverType = Server.ServerType.THREAD_FOR_EACH;
                break;
            case "singleThreadExecutor":
                serverType = Server.ServerType.SINGLE_THREAD_EXECUTOR;
                break;
            default:
                serverType = Server.ServerType.NON_BLOCKING;
                break;
        }
    }

    public void onMetricsChose(ActionEvent actionEvent) {
        switch (((MenuItem) actionEvent.getTarget()).getId()) {
            case "timeDelta":
                metrics = Server.Metrics.TIME_DELTA;
                break;
            case "elementsNumber":
                metrics = Server.Metrics.ELEMENTS_NUMBER;
                break;
            default:
                metrics = Server.Metrics.CLIENTS_NUMBER;
                break;
        }
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.show();
    }

    public void onStartPressed() {
        try {
            clientsNumber = Integer.parseInt(((TextField) scene.lookup("#tclientsNumber")).getText());
            requestsNumber = Integer.parseInt(((TextField) scene.lookup("#trequestsNumber")).getText());
            elementsNumber = Integer.parseInt(((TextField) scene.lookup("#telementsNumber")).getText());
            timeDelta = Integer.parseInt(((TextField) scene.lookup("#ttimeDelta")).getText());
            metricsUpperBound = Integer.parseInt(((TextField) scene.lookup("#tmetricsUpperBound")).getText());
            metricsStep = Integer.parseInt(((TextField) scene.lookup("#tmetricsStep")).getText());
        } catch (NumberFormatException e) {
            showErrorMessage("Incorrect input (can't parse integer)");
            return;
        }

        boolean incorrectMetricBounds = false;
        switch (metrics) {
            case TIME_DELTA:
                incorrectMetricBounds = timeDelta > metricsUpperBound;
                break;
            case CLIENTS_NUMBER:
                incorrectMetricBounds = clientsNumber > metricsUpperBound;
                break;
            default:
                incorrectMetricBounds = elementsNumber > metricsUpperBound;
                break;
        }

        if (incorrectMetricBounds) {
            showErrorMessage("Metrics upper bound is lower then its start value");
            return;
        }

        if (metricsStep < 0) {
            showErrorMessage("Metrics step should be positive");
            return;
        }

        Server.TestingConfiguration testingConfiguration = new Server.TestingConfiguration(metrics, elementsNumber,
                clientsNumber, timeDelta, requestsNumber, metricsUpperBound, metricsStep);

        Server server;
        switch (serverType) {
            case THREAD_FOR_EACH:
                server = new ThreadForEachServer(testingConfiguration, PORT);
                break;
            case SINGLE_THREAD_EXECUTOR:
                server = new SingleThreadExecutorServer(testingConfiguration, PORT);
                break;
            default:
                return;
        }
        try {
            server.startTesting();
            server.saveResultsToFile("output");
        } catch (IOException | InterruptedException e) {
            showErrorMessage("Error during testing");
            return;
        }
    }
}
