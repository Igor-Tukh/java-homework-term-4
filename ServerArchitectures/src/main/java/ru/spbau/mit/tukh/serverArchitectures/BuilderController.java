package ru.spbau.mit.tukh.serverArchitectures;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;
import ru.spbau.mit.tukh.serverArchitectures.server.Server;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class BuilderController {
    private static Scene scene;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    static void setScene(Scene scene) {
        BuilderController.scene = scene;
    }


    public void onBuildPressed(ActionEvent actionEvent) {
        String[] values = {"HandlingRequestTimeOnServer", "HandlingClientTimeOnServer", "AverageRequestTimeOnClient"};
        String[] metr = {"clients", "elements", "delta"};
        String[] servers = {"threadForEach", "singleThreadExecutor", "nonBlocking"};

        String path = "results/";

        LineChart<Integer, Integer> lineChart = (LineChart<Integer, Integer>) scene.lookup("#chart");
        lineChart.setAnimated(false);
        lineChart.setTitle("Metrics dependency");
        XYChart.Series series[] = new XYChart.Series[3];

        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                yAxis.setLabel("time, ms");
                xAxis.setLabel(metr[j]);

                series[0] = new XYChart.Series<Integer, Integer>();
                series[0].setName("Thread for each client server");
                series[1] = new XYChart.Series<Integer, Integer>();
                series[1].setName("Single thread executor server");
                series[2] = new XYChart.Series<Integer, Integer>();
                series[2].setName("Non blocking server");

                for (int k = 0; k < 3; k++) {
                    try (Scanner scanner = new Scanner(new File(path + metr[j] + "/" + servers[k] + "/" + "output" + i))) {
                        int elementsCnt = scanner.nextInt();
                        int clientsNumber = scanner.nextInt();
                        int timeDelta = scanner.nextInt();
                        int requestsNumber = scanner.nextInt();
                        int metricsUpperBound = scanner.nextInt();
                        int metricsStep = scanner.nextInt();
                        Server.Metrics metrics = Server.Metrics.getValueByString(scanner.next());
                        int n = scanner.nextInt();
                        int value;
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

                        for(int j1 = 0; j1 < n; j1++, value += metricsStep) {
                            series[k].getData().add(new XYChart.Data<>(value, scanner.nextInt()));
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                lineChart.getData().addAll(series[0], series[1], series[2]);

                WritableImage image = lineChart.snapshot(new SnapshotParameters(), null);
                File file = new File(path + "charts/" + values[i] + "(" + metr[j] + ").png");

                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                } catch (IOException e) {
                    // Nothing to do here
                }


                lineChart.getData().clear();
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Creating charts is finished");
        alert.setContentText("Check at results/chart");
        alert.show();
    }
}
