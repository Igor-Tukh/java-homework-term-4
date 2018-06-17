package ru.spbau.mit.tukh.serverArchitectures.server;

import ru.spbau.mit.tukh.serverArchitectures.Serialize;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadForEachServer extends Server {
    public ThreadForEachServer(TestingConfiguration testingConfiguration, int port) {
        this.testingConfiguration = testingConfiguration;
        this.testingResults = new TestingResults();
        this.port = port;
    }

    public void startTestingIteration() throws IOException, InterruptedException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Thread[] clientThreads = new Thread[testingConfiguration.clientsNumber];

            AtomicLong handlingRequestTimeOnServer = new AtomicLong(0);
            AtomicLong handlingClientTimeOnServer = new AtomicLong(0);
            AtomicLong averegeRequestTimeOnClient = new AtomicLong(0);

            for (int i = 0; i < testingConfiguration.clientsNumber; i++) {
                try (Socket socket = serverSocket.accept()) {
                    clientThreads[i] = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                                 DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
                                // Third metrics calculates on client, so we will receive its value after all requests
                                for (int j = 0; j < testingConfiguration.requestsNumber; j++) {
                                    long startTime = System.currentTimeMillis();
                                    int[] array = Serialize.deserializeArrayFromDataInputStream(dataInputStream);
                                    handlingRequestTimeOnServer.addAndGet(getTimeDuringSort(array));
                                    Serialize.writeArrayToDataOutputStream(dataOutputStream, array);
                                    dataOutputStream.flush();
                                    handlingClientTimeOnServer.addAndGet(System.currentTimeMillis() - startTime);
                                }
                                averegeRequestTimeOnClient.addAndGet(dataInputStream.readLong());
                            } catch (IOException e) {
                                // Nothing to do here
                            }
                        }
                    });
                    clientThreads[i].start();
                }
            }

            for (Thread clientThread : clientThreads) {
                clientThread.join();
            }

            testingResults.addHandlingClientTimeOnServer(handlingClientTimeOnServer.get()
                    / testingConfiguration.requestsNumber
                    / testingConfiguration.clientsNumber);
            testingResults.addHandlingRequestTimeOnServer(handlingRequestTimeOnServer.get()
                    / testingConfiguration.requestsNumber
                    / testingConfiguration.clientsNumber);
            testingResults.addAveregeRequestTimeOnClient(averegeRequestTimeOnClient.get()
                    / testingConfiguration.requestsNumber
                    / testingConfiguration.clientsNumber);
        }
    }
}
