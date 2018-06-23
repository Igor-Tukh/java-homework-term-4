package ru.spbau.mit.tukh.serverArchitectures.client;

import ru.spbau.mit.tukh.serverArchitectures.Serialize;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

public class Client {
    private String ip;
    private int requests_number;
    private int elements_number;
    private int time_delta;
    private int port;
    private int[] array;
    private Random random = new Random();

    public Client(String ip, int requests_number, int elements_number, int time_delta, int port) {
        this.ip = ip;
        this.requests_number = requests_number;
        this.elements_number = elements_number;
        this.time_delta = time_delta;
        this.port = port;
        array = new int[elements_number];
    }

    public void execute() {
        System.out.println("One more client started");

        long averageTime = 0;

        try {
            Socket socket = new Socket(InetAddress.getByName(ip), port);
            try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                 DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
                for (int i = 0; i < requests_number; i++) {
                    long startTime = System.currentTimeMillis();
                    fillRandomValues();
                    Serialize.writeArrayToDataOutputStream(dataOutputStream, array);
                    dataOutputStream.flush();
                    System.out.println("One more data package was send");
                    int[] array1 = Serialize.deserializeArrayFromDataInputStream(dataInputStream);
                    if (!compare(array, array1)) {
                        System.err.println("Incorrect response");
                    } else {
                        System.out.println("Correct response");
                    }

                    averageTime += System.currentTimeMillis() - startTime;
                    Thread.sleep(time_delta);
                }
                dataOutputStream.writeLong(averageTime);
            } catch (InterruptedException e) {
                e.printStackTrace(); // Nothing to do here
            }
        } catch (IOException e) {
            e.printStackTrace(); // Nothing to do here
        }
    }

    private void fillRandomValues() {
        for (int i = 0; i < elements_number; i++) {
            array[i] = random.nextInt();
        }
    }

    // Method only for checking if response is correct
    private boolean compare(int[] first, int[] second) {
        if (first.length != second.length) {
            return false;
        }

        Arrays.sort(first);

        for (int i = 0; i < first.length; i++) {
            if (first[i] != second[i]) {
                return false;
            }
        }

        return true;
    }
}
