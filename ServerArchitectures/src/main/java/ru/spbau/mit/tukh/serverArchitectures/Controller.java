package ru.spbau.mit.tukh.serverArchitectures;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import ru.spbau.mit.tukh.serverArchitectures.server.NonBlockingServer;
import ru.spbau.mit.tukh.serverArchitectures.server.Server;
import ru.spbau.mit.tukh.serverArchitectures.server.SingleThreadExecutorServer;
import ru.spbau.mit.tukh.serverArchitectures.server.ThreadForEachServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

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

    private static final String path = "src/main/resources/";

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

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
                server = new NonBlockingServer(testingConfiguration, PORT);
                break;
        }
        try {
            server.startTesting();
            server.saveResultsToFile(path + "output0", path + "output1", path + "output2");
            drawResults();
        } catch (IOException | InterruptedException e) {
            showErrorMessage("Error during testing");
        }
    }

    private void drawResults() {
        LineChart<Integer, Integer> lineChart = (LineChart<Integer, Integer>) scene.lookup("#chart");
        lineChart.setTitle("Metrics dependency");
        XYChart.Series series[] = new XYChart.Series[3];
        series[0] = new XYChart.Series<Integer, Integer>();
        series[0].setName("Handling request time on server");
        series[1] = new XYChart.Series<Integer, Integer>();
        series[1].setName("Handling client time on server");
        series[2] = new XYChart.Series<Integer, Integer>();
        series[2].setName("Average request time on client");

        yAxis.setLabel("time, ms");
        for(int i = 0; i < 3; i++) {
            try (Scanner scanner = new Scanner(new File(path + "output" + i))) {
                int elementsCnt = scanner.nextInt();
                int clientsNumber = scanner.nextInt();
                int timeDelta = scanner.nextInt();
                int requestsNumber = scanner.nextInt();
                int metricsUpperBound = scanner.nextInt();
                int metricsStep = scanner.nextInt();
                Server.Metrics metrics = Server.Metrics.getValueByString(scanner.next());
                int n = scanner.nextInt();
                int value;
                xAxis.setLabel(metrics.getStringValue());
                switch (metrics) {
                    case TIME_DELTA:
                        value = timeDelta;
                        break;
                    case CLIENTS_NUMBER:
                        value = clientsNumber;
                        break;
                    default:
                        value = elementsCnt;
                        break;
                }

                for(int j = 0; j < n; j++, value += metricsStep) {
                    series[i].getData().add(new XYChart.Data<>(value, scanner.nextInt()));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        lineChart.getData().addAll(series[0], series[1], series[2]);
    }
}
