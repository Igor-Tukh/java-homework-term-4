package ru.spbau.mit.tukh.cw01;

import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Solver, which recursive walks and evaluates MD5.
 */
public class RecursiveSolver {
    /**
     * Computes MD5 of a given path (directory/file).
     *
     * @param path is path to compute MD5.
     * @return MD5 of the argument.
     * @throws IOException if there are some problems with reading file.
     */
    public static byte[] getMD5(@NotNull Path path) throws IOException {
        if (Files.isDirectory(path)) {
            return getMD5OfDirectory(path);
        }
        return Utils.getMD5ofFile(path);
    }

    private static byte[] getMD5OfDirectory(@NotNull Path path) throws IOException {
        MessageDigest messageDigest = Utils.getNewMD5MessageDigest();
        List<Path> content = Files.list(path).sorted().collect(Collectors.toList());

        messageDigest.update(path.getFileName().toString().getBytes());
        for (Path subpath : content) {
            if (!Files.isDirectory(subpath)) {
                messageDigest.update(Utils.getMD5ofFile(subpath));
            } else {
                messageDigest.update(getMD5OfDirectory(subpath));
            }
        }

        return messageDigest.digest();
    }
}
