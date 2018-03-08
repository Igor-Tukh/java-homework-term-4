package ru.spbau.mit.tukh.hw01;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Implementation of threading pool class. After creating ThreadPoolObject, starts several threads and execute all given
 * task. Each task will start being in process in the first moment it can start.
 */
public class ThreadPoolImpl implements ThreadPool {
    private Thread[] threads;
    private ThreadPoolTaskQueue threadPoolTaskQueue = new ThreadPoolTaskQueue();

    /**
     * Creates several threads initially.
     * Then process some tasks (LightFuture interface).
     *
     * @param numberOfThreads number of initially starting threads.
     */
    ThreadPoolImpl(int numberOfThreads) {
        threads = new Thread[numberOfThreads];

        Runnable threadsTask = () -> {
            while (true) {
                LightFutureImpl lightFuture;
                if ((lightFuture = threadPoolTaskQueue.poll()) != null) {
                    if (lightFuture.supplier instanceof StopSupplier) {
                        lightFuture.supplier.get();
                        break;
                    }
                    lightFuture.startExecution();
                }
            }
        };

        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(threadsTask);
            threads[i].start();
        }
    }

    private void joinAll() {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public void shutdown() {
        for (Thread ignored : threads) {
            addTask(new StopSupplier());
        }
        joinAll();
    }

    @Override
    public synchronized <R> void addTask(Supplier<R> supplier) {
        LightFutureImpl<R> lightFuture = new LightFutureImpl<>(supplier);
        threadPoolTaskQueue.add(lightFuture);
    }

    @Override
    public <R> void addLightFutureImpl(LightFutureImpl<R> lightFuture) {
        threadPoolTaskQueue.add(lightFuture);
    }

    /**
     * Class for synchronized work with queue, which stores LightFuture objects.
     */
    private static class ThreadPoolTaskQueue {
        private final Queue<LightFutureImpl> queue = new ArrayDeque<>();

        /**
         * Removes first element in queue, if it exists and null otherwise.
         * Note: in our way of using, queue elements are always not null.
         *
         * @return first element in the queue.
         */
        LightFutureImpl poll() {
            synchronized (queue) {
                while (queue.isEmpty()) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                    }
                }
                return queue.poll();
            }
        }

        /**
         * Adds element to queue (to the end).
         *
         * @param lightFuture LightFuture object to add.
         */
        void add(LightFutureImpl lightFuture) {
            synchronized (queue) {
                queue.add(lightFuture);
                queue.notify();
            }
        }
    }

    /**
     * Special class for stopping.
     */
    static class StopSupplier implements Supplier<Integer> {
        @Override
        public Integer get() {
            return 0;
        }
    }

    /**
     * Implementation of the LightFuture interface.
     *
     * @param <R> generic argument of storing supplier return type.
     */
    public class LightFutureImpl<R> implements LightFuture<R> {
        private boolean isReady;
        private R result;
        private Supplier<R> supplier;
        private Throwable exceptionDuringGet;

        LightFutureImpl(Supplier<R> supplier) {
            this.supplier = supplier;
        }

        @Override
        public boolean isReady() {
            synchronized (this) {
                return isReady;
            }
        }

        @Override
        public R get() throws LightExecutionException {
            synchronized (this) {
                while (!isReady) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }

                if (exceptionDuringGet != null) {
                    throw new LightExecutionException(exceptionDuringGet.getMessage());
                }

                return result;
            }
        }

        @Override
        public <N> LightFuture<N> thenApply(Function<R, N> function) {
            synchronized (this) {
                // To throw exception from father-LightFuture object we should run it.
                R result = null;
                Supplier<N> newSupplier = null;
                LightExecutionException executionException = null;
                try {
                    result = LightFutureImpl.this.get();
                } catch (LightExecutionException e) {
                    executionException = e;
                }

                if (result != null) {
                    R finalResult = result;
                    newSupplier = () -> function.apply(finalResult);
                }

                LightFutureImpl<N> lightFuture = new LightFutureImpl<>(newSupplier);
                if (executionException != null) {
                    lightFuture.addException(executionException);
                }
                addLightFutureImpl(lightFuture);
                return lightFuture;
            }
        }

        /**
         * Starts execution of current LightFuture task.
         */
        private void startExecution() {
            synchronized (this) {
                R result = null;

                try {
                    result = supplier.get();
                } catch (Throwable throwable) {
                    exceptionDuringGet = throwable;
                }

                this.result = result;
                isReady = true;
                notifyAll();
            }
        }

        private void addException(LightExecutionException exceptionDuringGet) {
            synchronized (this) {
                isReady = true;
                this.exceptionDuringGet = exceptionDuringGet;
                notifyAll();
            }
        }
    }
}
