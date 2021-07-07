package ru.spbau.mit.tukh.hw03;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Simple FTP client, which two kinds of requests to server:
 * '1 path', which returns directory by path listing, if it is directory;
 * '2 path', which returns file by path, if it is file.
 * Connects by given IP (as unary program argument) or to localhost otherwise.
 */
public class MyClient {
    private static final int BUF_SIZE = 1024;
    private static final String HELP_STRING = "Usage:\nlist <path>\nget <path>\nexit";
    private static final String ARG_HELP_STRING = "Usage:\nip as arg to connect or nothing as localhost.";
    private static byte[] buf = new byte[BUF_SIZE];
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    MyClient(String ip) throws IOException {
        Socket socket = new Socket(InetAddress.getByName(ip), Utils.CONNECTIONS_PORT);
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    public static void main(String[] args) throws IOException {
        String parsedIP = null;
        if (args.length > 1) {
            System.out.println(ARG_HELP_STRING);
            System.exit(0);
        } else if (args.length > 0) {
            parsedIP = args[0];
        } else {
            parsedIP = "localhost";
        }

        MyClient myClient = new MyClient(parsedIP);

        try (Scanner in = new Scanner(System.in)) {
            label:
            while (true) {
                System.out.print(">>> ");
                String command = in.next();

                switch (command) {
                    case "exit":
                        break label;
                    case "list":
                        myClient.requestList(in.next());
                        break;
                    case "get":
                        myClient.requestGet(in.next());
                        break;
                    default:
                        System.out.println("Unknown command\n");
                        System.out.println(HELP_STRING);
                        break;
                }
            }
        }
    }

    /**
     * Implementation of requests methods.
     */

    void requestGet(String path) throws IOException {
        dataOutputStream.writeInt(2);
        dataOutputStream.writeUTF(path);
        dataOutputStream.flush();

        long size = dataInputStream.readLong();

        if (size == 0) {
            System.out.println(0);
        }

        int red;
        while (size > 0) {
            red = dataInputStream.read(buf, 0, (int) Math.min(size, BUF_SIZE));
            System.out.print(new String(buf, 0, red));
            size -= red;
        }
    }

    void requestList(String path) throws IOException {
        dataOutputStream.writeInt(1);
        dataOutputStream.writeUTF(path);

        int size = dataInputStream.readInt();
        System.out.println(size);
        for (int i = 0; i < size; i++) {
            System.out.println(dataInputStream.readUTF() + " " + dataInputStream.readBoolean());
        }
    }
}
