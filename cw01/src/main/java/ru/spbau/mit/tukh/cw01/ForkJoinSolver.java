package ru.spbau.mit.tukh.cw01;

import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

/**
 * ForkJoinSolver computes MD5 of a given file/directory using ForkJoinPool.
 */
public class ForkJoinSolver {
    public static byte[] getMD5(@NotNull Path path) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Future<byte[]> MD5 = forkJoinPool.submit(new MD5Task(path));
        try {
            return MD5.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(); // Here should be some exception to throw, but haven't time to write it.
        }

        return null;
    }

    private static class MD5Task extends RecursiveTask<byte[]> {
        private Path path;

        MD5Task(@NotNull Path path) {
            this.path = path;
        }

        @Override
        protected byte[] compute() {
            if (!Files.isDirectory(path)) {
                try {
                    return Utils.getMD5ofFile(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                MessageDigest messageDigest = Utils.getNewMD5MessageDigest();
                messageDigest.update(path.getFileName().toString().getBytes());

                List<Path> content = Files.list(path).sorted().collect(Collectors.toList());
                ArrayList<MD5Task> tasks = new ArrayList<>();
                for (Path subpath : content) {
                    MD5Task md5Task = new MD5Task(subpath);
                    md5Task.fork();
                    tasks.add(md5Task);
                }

                for (MD5Task task : tasks) {
                    messageDigest.update(task.join());
                }

                return messageDigest.digest();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
