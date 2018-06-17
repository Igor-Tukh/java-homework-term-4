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
        private ArrayList<Double> averegeRequestTimeOnClient;
        private ArrayList<Integer> handlingRequestTimeOnServer;
        private ArrayList<Integer> handlingClientTimeOnServer;

        public void setInitialTestingConfiguration(TestingConfiguration testingConfiguration) {
            initialTestingConfiguration = new TestingConfiguration(testingConfiguration.metrics,
                    testingConfiguration.elements_number, testingConfiguration.clients_number,
                    testingConfiguration.time_delta, testingConfiguration.requests_number,
                    testingConfiguration.metrics_upper_bound, testingConfiguration.metrics_step);
        }

        public void addAveregeRequestTimeOnClient(double time) {
            averegeRequestTimeOnClient.add(time);
        }

        public void addHandlingRequestTimeOnServer(int time) {
            handlingRequestTimeOnServer.add(time);
        }

        public void addHandlingClientTimeOnServer(int time) {
            handlingClientTimeOnServer.add(time);
        }

        public ArrayList<Double> getAveregeRequestTimeOnClient() {
            return averegeRequestTimeOnClient;
        }

        public ArrayList<Integer> getHandlingClientTimeOnServer() {
            return handlingClientTimeOnServer;
        }

        public ArrayList<Integer> getHandlingRequestTimeOnServer() {
            return handlingRequestTimeOnServer;
        }
    }

    protected abstract void startTestingIteration() throws IOException;

    public void startTesting() throws IOException {
        testingResults.setInitialTestingConfiguration(testingConfiguration);
        for (; !testingConfiguration.testingIsOver(); testingConfiguration.update()) {
            startTestingIteration();
        }
    }

    public TestingResults getTestingResults() {
        return testingResults;
    }
}
