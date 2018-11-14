package ru.spbau.mit.tukh.cw01;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Different utils.
 */
public class Utils {
    private static final int BUFFER_SIZE = 1024;

    public static byte[] getMD5ofFile(Path path) throws IOException {
        InputStream inputStream = Files.newInputStream(path);
        MessageDigest messageDigest = getNewMD5MessageDigest();
        byte[] buf = new byte[BUFFER_SIZE];
        int numberOfBytes;
        while ((numberOfBytes = inputStream.read(buf)) > 0) {
            messageDigest.update(buf, 0, numberOfBytes);
        }
        return messageDigest.digest();
    }

    public static MessageDigest getNewMD5MessageDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static byte[] getTestEmptyDirectoryMD5() {
        MessageDigest messageDigest = getNewMD5MessageDigest();
        messageDigest.update("Empty".getBytes());
        return messageDigest.digest();
    }

    public static byte[] getTestNotEmptyDirectoryMD5() {
        MessageDigest messageDigest = getNewMD5MessageDigest();
        messageDigest.update("NotEmpty".getBytes());
        try {
            messageDigest.update(getMD5ofFile(Paths.get("src/test/resources/NotEmpty/1")));
            messageDigest.update(getMD5ofFile(Paths.get("src/test/resources/NotEmpty/2")));
            messageDigest.update("Inner".getBytes());
            messageDigest.update(getMD5ofFile(Paths.get("src/test/resources/NotEmpty/Inner/2")));
        } catch (IOException e) {
            // - . -
        }
        return messageDigest.digest();
    }
}
