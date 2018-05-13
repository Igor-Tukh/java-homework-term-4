package ru.spbau.mit.tukh.hw03;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Simple FTP server.
 * Supports requests of two kinds:
 * '1 path', which returns directory by path listing, if it is directory;
 * '2 path', which returns file by path, if it is file.
 */
public class MyServer {
    private static final int BUF_SIZE = 1024;
    private static byte[] buf = new byte[BUF_SIZE];

    public static void main(String[] args) throws IOException {
        ExecutorService requestsPool = Executors.newFixedThreadPool(4);
        ServerSocket serverSocket = new ServerSocket(Utils.CONNECTIONS_PORT);

        while (true) {
            Socket currentSocket = serverSocket.accept();
            requestsPool.execute(() -> {
                        try (DataInputStream dataInputStream = new DataInputStream(currentSocket.getInputStream());
                             DataOutputStream dataOutputStream = new DataOutputStream(currentSocket.getOutputStream())) {
                            while (!Thread.interrupted()) {
                                Utils.parseResult typeParseResult = Utils.parseServerRequestType(dataInputStream);

                                boolean needToStop = false;
                                switch (typeParseResult) {
                                    case FAILED:
                                        System.err.println("Request type parse failed: " + currentSocket.getInetAddress());
                                        currentSocket.close();
                                        needToStop = true;
                                        break;
                                    case REQUEST_LIST:
                                        answerList(dataOutputStream, dataInputStream.readUTF());
                                        break;
                                    case REQUEST_GET:
                                        answerGet(dataOutputStream, dataInputStream.readUTF());
                                        break;
                                }

                                if (needToStop) {
                                    break;
                                }
                            }
                        } catch (IOException e) {
                            // Nothing to do
                            e.printStackTrace();
                        }
                    }

            );
        }
    }

    private static void answerList(DataOutputStream dataOutputStream, String path) throws IOException {
        File file = new File(path);
        File[] files = file.listFiles();

        if (files != null) {
            dataOutputStream.writeInt(files.length);
            for (File currentFile : files) {
                dataOutputStream.writeUTF(currentFile.getName());
                dataOutputStream.writeBoolean(currentFile.isDirectory());
            }
        } else {
            dataOutputStream.writeInt(0);
        }
    }

    private static void answerGet(DataOutputStream dataOutputStream, String path) throws IOException {
        File file = new File(path);
        if (file.isDirectory() || !file.exists()) {
            dataOutputStream.writeLong(0);
            return;
        }

        dataOutputStream.writeLong(file.length());
        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));

        int red;
        while ((red = dataInputStream.read(buf)) > 0) {
            dataOutputStream.write(buf, 0, red);
        }

        dataInputStream.close();
    }
}
