package ru.spbau.mit.tukh.hw03;

import java.io.DataInputStream;
import java.io.IOException;

public class Utils {
    static final int CONNECTIONS_PORT = 23913;

    static parseResult parseServerRequestType(DataInputStream dataInputStream) {
        try {
            int requestType = dataInputStream.readInt();
            switch (requestType) {
                case 1:
                    return parseResult.REQUEST_LIST;
                case 2:
                    return parseResult.REQUEST_GET;
                default:
                    return parseResult.FAILED;
            }
        } catch (IOException e) {
            return parseResult.FAILED;
        }
    }

    enum parseResult {
        REQUEST_LIST, REQUEST_GET, FAILED
    }
}
