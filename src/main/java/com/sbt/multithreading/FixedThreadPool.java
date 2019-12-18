package com.sbt.multithreading;

import java.util.LinkedList;
import java.util.Queue;

public class FixedThreadPool {
    private int threadCount;
    private final PoolWorker[] threads;
    private final Queue<Runnable> queue;
    private volatile boolean isRunning = false;

    public FixedThreadPool(int threadCount) {
        this.threadCount = threadCount;
        queue = new LinkedList<>();
        threads = new PoolWorker[threadCount];
    }

    public void start() {
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new PoolWorker();
            threads[i].start();
            isRunning = true;
            System.out.println(threads[i].getName() + " started..");
        }
    }

    public void execute(Runnable runnable) {
        synchronized (queue) {
            queue.add(runnable);
            queue.notify();
        }
    }

    public int getQueueCount() {
        int cnt;
        synchronized (queue) {
            cnt = queue.size();
        }
        if (threadCount >= 0) {
            try {
                this.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return cnt;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void shutdown() {
        isRunning = false;
    }

    private class PoolWorker extends Thread {

        public void run() {
            Runnable r;

            while (isRunning | !queue.isEmpty()) {
                synchronized (queue) {
                    while (isRunning & queue.isEmpty()) {
                        try {
                            System.out.println(this.getName() + " is waiting..." + " isRunning " + isRunning + " queue.isEmpty() " + queue.isEmpty());
                            queue.wait(500);
                        } catch (InterruptedException e) {
                        }
                    }
                    if (!queue.isEmpty()) {
                        r = queue.remove();
                    } else {
                        r = null;
                        if (!isRunning) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
                if (r != null) {
                    r.run();
                }
            }
            if (!isRunning & queue.isEmpty()) {
                this.interrupt();
                System.err.println("[Current threads count fixedThreadPool: " + threadCount + "] " + this.getName());
                threadCount--;
            }
        }
    }

}
