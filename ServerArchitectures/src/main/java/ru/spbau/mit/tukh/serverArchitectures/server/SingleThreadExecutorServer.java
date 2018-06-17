package ru.spbau.mit.tukh.serverArchitectures.server;

import ru.spbau.mit.tukh.serverArchitectures.Serialize;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadExecutorServer extends Server {
    private long handlingRequestTimeOnServer;
    private long handlingClientTimeOnServer;
    private long averageRequestTimeOnClient;

    synchronized public void addHandlingRequestTimeOnServer(long value) {
        handlingRequestTimeOnServer += value;
    }

    synchronized public void addHandlingClientTimeOnServer(long value) {
        handlingClientTimeOnServer += value;
    }

    synchronized public void addAveregeRequestTimeOnClient(long value) {
        averageRequestTimeOnClient += value;
    }

    public SingleThreadExecutorServer(TestingConfiguration testingConfiguration, int port) {
        this.testingConfiguration = testingConfiguration;
        this.testingResults = new TestingResults();
        this.port = port;
    }

    public void startTestingIteration() throws IOException, InterruptedException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Thread[] clientThreads = new Thread[testingConfiguration.clientsNumber];

            handlingRequestTimeOnServer = 0;
            handlingClientTimeOnServer = 0;
            averageRequestTimeOnClient = 0;

            ExecutorService threadPool = Executors.newFixedThreadPool(4);

            for (int i = 0; i < testingConfiguration.clientsNumber; i++) {
                ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
                Socket socket = serverSocket.accept();
                clientThreads[i] = new Thread(() -> {
                    try {
                        try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
                            for (int j = 0; j < testingConfiguration.requestsNumber; j++) {
                                long startTime = System.currentTimeMillis();
                                int[] array = new int[0];
                                try {
                                    array = Serialize.deserializeArrayFromDataInputStream(dataInputStream);
                                } catch (IOException e) {
                                    e.printStackTrace(); // Nothing to do here;
                                }
                                int[] finalArray = array;
                                threadPool.submit(() -> {
                                    addHandlingRequestTimeOnServer(getTimeDuringSort(finalArray));
                                    singleThreadExecutor.submit(() -> {
                                        try {
                                            Serialize.writeArrayToDataOutputStream(dataOutputStream, finalArray);
                                            dataOutputStream.flush();
                                        } catch (IOException e) {
                                            e.printStackTrace(); // Nothing to do here
                                        }
                                        addHandlingClientTimeOnServer(System.currentTimeMillis() - startTime);
                                    });
                                });
                            }
                            addAveregeRequestTimeOnClient(dataInputStream.readLong());
                        }
                    } catch (IOException e) {
                        e.printStackTrace(); // Nothing to do here
                    }
                });
                clientThreads[i].start();
            }

            for (Thread clientThread : clientThreads) {
                clientThread.join();
            }

            testingResults.addHandlingClientTimeOnServer(handlingClientTimeOnServer
                    / testingConfiguration.requestsNumber
                    / testingConfiguration.clientsNumber);
            testingResults.addHandlingRequestTimeOnServer(handlingRequestTimeOnServer
                    / testingConfiguration.requestsNumber
                    / testingConfiguration.clientsNumber);
            testingResults.addAverageRequestTimeOnClient(averageRequestTimeOnClient
                    / testingConfiguration.requestsNumber
                    / testingConfiguration.clientsNumber);
        }
    }
}
