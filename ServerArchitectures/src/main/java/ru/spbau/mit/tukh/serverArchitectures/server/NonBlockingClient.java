package ru.spbau.mit.tukh.serverArchitectures.server;

import ru.spbau.mit.tukh.serverArchitectures.Serialize;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NonBlockingClient {
    private final static int BUFFER_SIZE = 131072; // it is enough for testing

    private ConcurrentLinkedQueue<ResponseInfo> responsesInfo = new ConcurrentLinkedQueue<>();

    private SocketChannel socketChannel;
    private ByteBuffer readingBuffer;
    private ByteBuffer writingBuffer;
    private NonBlockingServer server;

    private int responses;
    private boolean marked;
    private int requests;
    private boolean waitingForArray;
    private byte[] message;
    private int pos;
    private boolean haveGotFinalTime;

    NonBlockingClient(SocketChannel chanel, NonBlockingServer server) {
        this.socketChannel = chanel;
        this.server = server;
        readingBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        writingBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        writingBuffer.flip();
        waitingForArray = true;
    }

    public void registerInReadSelector(Selector selector) {
        try {
            socketChannel.register(selector, SelectionKey.OP_READ, this);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }

    public void registerInWriteSelector(Selector selector) {
        try {
            socketChannel.register(selector, SelectionKey.OP_WRITE, this);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }

    public void read() throws IOException {
        int red = 0;
        while ((red = socketChannel.read(readingBuffer)) > 0) {
            red = socketChannel.read(readingBuffer);
        }

        readingBuffer.flip();

        if (requests == server.testingConfiguration.requestsNumber) {
            if (readingBuffer.remaining() >= 8) {
                long time = readingBuffer.getLong();
                server.addAverageRequestTimeOnClient(time);
                haveGotFinalTime = true;
                markIfNeed();
            } else {
                return;
            }
        }

        while (readingBuffer.hasRemaining()) {
            if (waitingForArray) {
                message[pos++] = readingBuffer.get();
            } else {
                if (readingBuffer.remaining() < 4) {
                    readingBuffer.compact();
                    break;
                } else {
                    int size = readingBuffer.getInt();
                    message = new byte[size];
                    waitingForArray = true;
                    pos = 0;
                }
            }

            if (pos == message.length) {
                long startTime = System.currentTimeMillis();
                int[] array = new int[0];
                try {
                    array = Serialize.deserializeArrayFromByteArray(message);
                } catch (IOException e) {
                    e.printStackTrace(); // Nothing to do here;
                }
                int[] finalArray = array;
                server.threadPool.submit(() -> {
                    server.addHandlingRequestTimeOnServer(server.getTimeDuringSort(finalArray));
                    putToBuffer(startTime, finalArray);
                });
                requests++;
                markIfNeed();
            }
        }
    }

    public void write() throws IOException {
        if (responses == server.testingConfiguration.requestsNumber) {
            return;
        }

        if (responsesInfo.size() == 0) {
            return;
        }

        synchronized (writingBuffer) {
            if (responsesInfo.peek().getSize() == 0) {
                responsesInfo.poll();
                if (responsesInfo.size() == 0) {
                    return;
                }
            }

            int current = responsesInfo.peek().getSize();
            int wrote = socketChannel.write(writingBuffer);

            while (wrote > 0) {
                if (current > wrote) {
                    current -= wrote;
                    wrote = 0;
                } else {
                    current = 0;
                    wrote -= current;
                }
                responsesInfo.peek().setSize(current);

                if (responsesInfo.peek().getSize() == 0) {
                    responses++;
                    markIfNeed();

                    server.addHandlingClientTimeOnServer(System.currentTimeMillis() - responsesInfo.peek().getStartTime());
                    responsesInfo.poll();
                }
            }
        }
    }

    public boolean over() {
        return responses == server.testingConfiguration.requestsNumber && requests == server.testingConfiguration.requestsNumber;
    }

    private void putToBuffer(long startTime, int[] array) {
        byte[] data = Serialize.serializeArray(array);
        responsesInfo.add(new ResponseInfo(startTime, data.length + 4));
        synchronized (writingBuffer) {
            writingBuffer.compact();
            writingBuffer.putInt(data.length);
            writingBuffer.put(data);
            writingBuffer.flip();
        }
    }

    private void markIfNeed() {
        if (responses != requests) {
            return;
        }
        if (responses != server.testingConfiguration.requestsNumber) {
            return;
        }
        if (!haveGotFinalTime) {
            return;
        }

        if (marked) {
            return;
        }

        marked = true;
        server.clientsFinished.addAndGet(1);
        if (server.clientsFinished.get() == server.testingConfiguration.clientsNumber) {
            server.testingIsOver.set(true);
        }
    }

    private static class ResponseInfo {
        private long startTime;
        private int size;

        ResponseInfo(long startTime, int size) {
            this.startTime = startTime;
            this.size = size;
        }

        long getStartTime() {
            return startTime;
        }

        int getSize() {
            return size;
        }

        void setSize(int size) {
            this.size = size;
        }
    }
}