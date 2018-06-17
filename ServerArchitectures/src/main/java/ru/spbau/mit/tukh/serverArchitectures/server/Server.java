package ru.spbau.mit.tukh.serverArchitectures.server;

import java.io.IOException;
import java.util.ArrayList;

public abstract class Server {
    protected TestingConfiguration testingConfiguration;
    protected TestingResults testingResults;
    protected int port;

    public enum Metrics {
        ELEMENTS_NUMBER, CLIENTS_NUMBER, TIME_DELTA;
    }

    public class TestingConfiguration {
        Metrics metrics;
        int elements_number;
        int clients_number;
        int time_delta;
        int requests_number;
        int metrics_upper_bound;
        int metrics_step;

        TestingConfiguration(Metrics metrics, int elements_number, int clients_number, int time_delta,
                             int request_number, int metrics_upper_bound, int metrics_step) {
            this.metrics = metrics;
            this.elements_number = elements_number;
            this.clients_number = clients_number;
            this.time_delta = time_delta;
            this.requests_number = request_number;
            this.metrics_upper_bound = metrics_upper_bound;
            this.metrics_step = metrics_step;
        }

        public boolean testingIsOver() {
            int current_value;

            switch (metrics) {
                case CLIENTS_NUMBER:
                    current_value = clients_number;
                    break;
                case ELEMENTS_NUMBER:
                    current_value = elements_number;
                    break;
                default:
                    current_value = time_delta;
                    break;
            }

            return current_value > metrics_upper_bound;

        }

        public void update() {
            switch (metrics) {
                case CLIENTS_NUMBER:
                    clients_number += metrics_step;
                    break;
                case ELEMENTS_NUMBER:
                    elements_number += metrics_step;
                    break;
                default:
                    time_delta += metrics_step;
                    break;
            }
        }
    }

    public class TestingResults {
        private TestingConfiguration initialTestingConfiguration;
        private ArrayList<Long> averegeRequestTimeOnClient;
        private ArrayList<Long> handlingRequestTimeOnServer;
        private ArrayList<Long> handlingClientTimeOnServer;

        public void setInitialTestingConfiguration(TestingConfiguration testingConfiguration) {
            initialTestingConfiguration = new TestingConfiguration(testingConfiguration.metrics,
                    testingConfiguration.elements_number, testingConfiguration.clients_number,
                    testingConfiguration.time_delta, testingConfiguration.requests_number,
                    testingConfiguration.metrics_upper_bound, testingConfiguration.metrics_step);
        }

        public void addAveregeRequestTimeOnClient(long time) {
            averegeRequestTimeOnClient.add(time);
        }

        public void addHandlingRequestTimeOnServer(long time) {
            handlingRequestTimeOnServer.add(time);
        }

        public void addHandlingClientTimeOnServer(long time) {
            handlingClientTimeOnServer.add(time);
        }

        public ArrayList<Long> getAveregeRequestTimeOnClient() {
            return averegeRequestTimeOnClient;
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
            startTestingIteration();
        }
    }

    public TestingResults getTestingResults() {
        return testingResults;
    }

    protected long getTimeDuringSort(int[] array) {
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
}
