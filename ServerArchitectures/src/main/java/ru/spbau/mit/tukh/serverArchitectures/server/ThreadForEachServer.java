package ru.spbau.mit.tukh.serverArchitectures.server;

import ru.spbau.mit.tukh.serverArchitectures.Serialize;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadForEachServer extends Server {
    private long handlingRequestTimeOnServer;
    private long handlingClientTimeOnServer;
    private long averageRequestTimeOnClient;

    synchronized public void addHandlingRequestTimeOnServer(long value) {
        handlingRequestTimeOnServer += value;
    }

    synchronized public void addHandlingClientTimeOnServer(long value) {
        handlingClientTimeOnServer += value;
    }

    synchronized public void addAverageRequestTimeOnClient(long value) {
        averageRequestTimeOnClient += value;
    }

    public ThreadForEachServer(TestingConfiguration testingConfiguration, int port) {
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

            for (int i = 0; i < testingConfiguration.clientsNumber; i++) {
                Socket socket = serverSocket.accept();
                System.out.println("One more client added");
                clientThreads[i] = new Thread(() -> {
                    try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                         DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
                        // Third metrics calculates on client, so we will receive its value after all requests
                        for (int j = 0; j < testingConfiguration.requestsNumber; j++) {
                            long startTime = System.currentTimeMillis();
                            int[] array = Serialize.deserializeArrayFromDataInputStream(dataInputStream);

                            addHandlingRequestTimeOnServer(getTimeDuringSort(array));
                            Serialize.writeArrayToDataOutputStream(dataOutputStream, array);
                            dataOutputStream.flush();
                            addHandlingClientTimeOnServer(System.currentTimeMillis() - startTime);
                        }
                        addAverageRequestTimeOnClient(dataInputStream.readLong());
                    } catch (IOException e) {
                        e.printStackTrace(); // Nothing to do here
                    }
                    try {
                        socket.close();
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
