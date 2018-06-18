package ru.spbau.mit.tukh.serverArchitectures.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NonBlockingServer extends Server {
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

    AtomicBoolean testingIsOver = new AtomicBoolean(false);
    AtomicInteger clientsFinished = new AtomicInteger(0);

    private ServerSocketChannel serverSocketChannel;
    private Selector readSelector;
    private Selector writeSelector;
    ExecutorService threadPool;
    private Thread readThread;
    private Thread writeThread;

    private ConcurrentLinkedQueue<NonBlockingClient> readRegisterQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<NonBlockingClient> writeRegisterQueue = new ConcurrentLinkedQueue<>();

    public NonBlockingServer(TestingConfiguration testingConfiguration, int port) {
        this.testingConfiguration = testingConfiguration;
        this.testingResults = new TestingResults();
        serverSocketChannel = null;

        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            readSelector = Selector.open();
            writeSelector = Selector.open();
            threadPool = Executors.newFixedThreadPool(4);

            readThread = new Thread(() -> {
                while (!testingIsOver.get()) {
                    while (readRegisterQueue.size() > 0) {
                        NonBlockingClient client = readRegisterQueue.poll();
                        client.registerInReadSelector(readSelector);
                    }
                }

                try {
                    if (readSelector.select() > 0) {
                        Set<SelectionKey> selectedKeys = readSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                        for (; keyIterator.hasNext(); keyIterator.remove()) {
                            SelectionKey selectionKey = keyIterator.next();
                            NonBlockingClient attachedClient = (NonBlockingClient) selectionKey.attachment();

                            if (!attachedClient.over()) {
                                attachedClient.read();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            readThread.start();

            writeThread = new Thread(() -> {
                while (!testingIsOver.get()) {
                    while (writeRegisterQueue.size() > 0) {
                        NonBlockingClient client = writeRegisterQueue.poll();
                        client.registerInWriteSelector(writeSelector);
                    }
                }

                try {
                    if (readSelector.select() > 0) {
                        Set<SelectionKey> selectedKeys = writeSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                        for (; keyIterator.hasNext(); keyIterator.remove()) {
                            SelectionKey selectionKey = keyIterator.next();
                            NonBlockingClient attachedClient = (NonBlockingClient) selectionKey.attachment();

                            if (!attachedClient.over()) {
                                attachedClient.write();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writeThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startTestingIteration() throws IOException, InterruptedException {
        for (int i = 0; i < testingConfiguration.clientsNumber; i++) {
            SocketChannel channel = serverSocketChannel.accept();
            channel.configureBlocking(false);
            NonBlockingClient client = new NonBlockingClient(channel, this);
            readRegisterQueue.add(client);
            writeRegisterQueue.add(client);
            readSelector.wakeup();
            writeSelector.wakeup();
        }

        readThread.join();
        writeThread.join();

        writeSelector.close();
        readSelector.close();
        serverSocketChannel.close();

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
