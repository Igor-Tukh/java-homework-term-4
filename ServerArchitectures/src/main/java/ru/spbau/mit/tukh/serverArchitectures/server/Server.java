package ru.spbau.mit.tukh.serverArchitectures.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public abstract class Server {
    TestingConfiguration testingConfiguration;
    TestingResults testingResults;
    int port;

    public enum Metrics {
        ELEMENTS_NUMBER, CLIENTS_NUMBER, TIME_DELTA;

        public String getStringValue() {
            switch (this) {
                case CLIENTS_NUMBER:
                    return "clients";
                case TIME_DELTA:
                    return "delta";
                default:
                    return "elements";
            }
        }
    }

    public enum ServerType {
        THREAD_FOR_EACH, SINGLE_THREAD_EXECUTOR, NON_BLOCKING;
    }

    public static class TestingConfiguration {
        Metrics metrics;
        int elementsNumber;
        int clientsNumber;
        int timeDelta;
        int requestsNumber;
        int metricsUpperBound;
        int metricsStep;

        public TestingConfiguration(Metrics metrics, int elementsNumber, int clientsNumber, int timeDelta,
                             int requestNumber, int metricsUpperBound, int metricsStep) {
            this.metrics = metrics;
            this.elementsNumber = elementsNumber;
            this.clientsNumber = clientsNumber;
            this.timeDelta = timeDelta;
            this.requestsNumber = requestNumber;
            this.metricsUpperBound = metricsUpperBound;
            this.metricsStep = metricsStep;
        }

        boolean testingIsOver() {
            int current_value;

            switch (metrics) {
                case CLIENTS_NUMBER:
                    current_value = clientsNumber;
                    break;
                case ELEMENTS_NUMBER:
                    current_value = elementsNumber;
                    break;
                default:
                    current_value = timeDelta;
                    break;
            }

            return current_value > metricsUpperBound;

        }

        void update() {
            switch (metrics) {
                case CLIENTS_NUMBER:
                    clientsNumber += metricsStep;
                    break;
                case ELEMENTS_NUMBER:
                    elementsNumber += metricsStep;
                    break;
                default:
                    timeDelta += metricsStep;
                    break;
            }
        }

        void saveState(PrintWriter printWriter) {
            printWriter.println(metrics.getStringValue());
            printWriter.println(elementsNumber);
            printWriter.println(clientsNumber);
            printWriter.println(timeDelta);
            printWriter.println(requestsNumber);
            printWriter.println(metricsUpperBound);
            printWriter.println(metricsStep);
        }
    }

    public class TestingResults {
        private TestingConfiguration initialTestingConfiguration;
        private ArrayList<Long> averageRequestTimeOnClient = new ArrayList<>();
        private ArrayList<Long> handlingRequestTimeOnServer = new ArrayList<>();
        private ArrayList<Long> handlingClientTimeOnServer = new ArrayList<>();

        public void setInitialTestingConfiguration(TestingConfiguration testingConfiguration) {
            initialTestingConfiguration = new TestingConfiguration(testingConfiguration.metrics,
                    testingConfiguration.elementsNumber, testingConfiguration.clientsNumber,
                    testingConfiguration.timeDelta, testingConfiguration.requestsNumber,
                    testingConfiguration.metricsUpperBound, testingConfiguration.metricsStep);
        }

        public void addAveregeRequestTimeOnClient(long time) {
            averageRequestTimeOnClient.add(time);
        }

        public void addHandlingRequestTimeOnServer(long time) {
            handlingRequestTimeOnServer.add(time);
        }

        public void addHandlingClientTimeOnServer(long time) {
            handlingClientTimeOnServer.add(time);
        }

        public ArrayList<Long> getAverageRequestTimeOnClient() {
            return averageRequestTimeOnClient;
        }

        public ArrayList<Long> getHandlingClientTimeOnServer() {
            return handlingClientTimeOnServer;
        }

        public ArrayList<Long> getHandlingRequestTimeOnServer() {
            return handlingRequestTimeOnServer;
        }
    }

    protected abstract void startTestingIteration() throws IOException, InterruptedException;

    public void startTesting() throws IOException, InterruptedException {
        testingResults.setInitialTestingConfiguration(testingConfiguration);
        for (; !testingConfiguration.testingIsOver(); testingConfiguration.update()) {
            System.out.println("Started new testing iteration");
            startTestingIteration();
        }
    }

    public TestingResults getTestingResults() {
        return testingResults;
    }

    long getTimeDuringSort(int[] array) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < array.length; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if (array[i] > array[j]) {
                    int k = array[j];
                    array[j] = array[i];
                    array[i] = k;
                }
            }
        }
        return System.currentTimeMillis() - startTime;
    }

    public void saveResultsToFile(String filename) {
        if (!testingConfiguration.testingIsOver()) {
            return;
        }

        try (PrintWriter printWriter = new PrintWriter(filename)) {
            testingResults.initialTestingConfiguration.saveState(printWriter);
            printWriter.println(testingResults.getAverageRequestTimeOnClient().size());
            for (long value: testingResults.getHandlingRequestTimeOnServer()) {
                printWriter.print(value + " ");
            }
            printWriter.println();
            for (long value: testingResults.getHandlingClientTimeOnServer()) {
                printWriter.print(value + " ");
            }
            printWriter.println();
            for (long value: testingResults.getAverageRequestTimeOnClient()) {
                printWriter.print(value + " ");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error opening file");
        }
    }
}
