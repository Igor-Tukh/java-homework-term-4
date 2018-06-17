package ru.spbau.mit.tukh.serverArchitectures.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadForEachServer extends Server{
    public ThreadForEachServer(TestingConfiguration testingConfiguration, int port) {
        this.testingConfiguration = testingConfiguration;
        this.testingResults = new TestingResults();
        this.port = port;
    }

    public void startTestingIteration() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        for (int i = 0; i < testingConfiguration.clients_number; i++) {
            Socket socket = serverSocket.accept();
        }
    }
}
