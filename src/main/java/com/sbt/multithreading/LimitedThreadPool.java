package com.sbt.multithreading;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class LimitedThreadPool {
    private final int minThreadCount;
    private final int maxThreadCount;
    private final ArrayList<PoolWorker> threads;
    private final Queue<Runnable> queue;
    private volatile boolean isRunning = false;


    public LimitedThreadPool(int min, int max) {
        this.minThreadCount = min;
        this.maxThreadCount = max;

        threads = new ArrayList<>(max);
        queue = new LinkedList<>();

        for (int i = 0; i < minThreadCount; i++) {
            threads.add(new PoolWorker());
            System.err.println("add new thread " + threads.get(threads.size() - 1).getName());
        }
    }

    public void start() {
        for (PoolWorker thread : threads)
            thread.start();
        isRunning = true;
    }

    public int getQueueCount() {
        checkThreadCount();
        synchronized (queue) {
            return queue.size();
        }
    }

    public int getThreadsSize() {
        checkThreadCount();
        return threads.size();
    }

    public void shutdown() {
        isRunning = false;
        checkThreadCount();
    }

    public void execute(Runnable runnable) {
        synchronized (queue) {
            queue.add(runnable);
            queue.notify();
            checkThreadCount();
        }
    }

    private void checkThreadCount() {
        if (queue.size() > threads.size() && threads.size() < maxThreadCount) {
            threads.add(new PoolWorker());
            System.err.println("add extend thread " + threads.get(threads.size() - 1).getName());

            if (isRunning)
                threads.get(threads.size() - 1).start();
        }
        if (queue.size() < threads.size() && threads.size() > minThreadCount) {
            System.err.println("remove extend thread " + threads.get(threads.size() - 1).getName());
            threads.get(threads.size() - 1).interrupt();
            threads.remove(threads.size() - 1);
        }
        //System.err.println("[Current threads count: " + threads.size() + "]");
        if (!isRunning & queue.size() == 0) {
            for (int i = 0; i < threads.size(); i++) {
                if (threads.size() > 0) {
                    System.out.println("remove extend thread threads.size() " + threads.size());
                    System.out.println("remove extend thread " + threads.get(threads.size() - 1).getName());
                    threads.get(threads.size() - 1).interrupt();
                    threads.remove(threads.size() - 1);
                }
            }
            System.err.println("[Current threads count limitedThreadPool: " + threads.size() + "]");//+ threads.get(threads.size() - 1).getName());
            if (threads.size() == 0) {
                try {
                    System.err.println("finalize limitedThreadPool : " + threads.size() + "]");
                    this.finalize();
                } catch (Throwable throwable) {
                    System.err.println("finalize : " + threads.size() + "]" + "throwable " + throwable.getLocalizedMessage());
                    throwable.printStackTrace();
                }
            }
        }
    }

    private class PoolWorker extends Thread {
        @Override
        public void run() {
            Runnable r;

            while (!Thread.interrupted()) {
                System.err.println("Thread.interrupted() limitedThreadPool " + Thread.interrupted() + "  queue.isEmpty() " + queue.isEmpty());
                synchronized (queue) {
                    while (queue.isEmpty() & isRunning) {
                        try {
                            checkThreadCount();
                            System.err.println("Thread.currentThread().getName() limitedThreadPool wait(10) : " + Thread.currentThread().getName() + "]");
                            queue.wait(10);
                        } catch (InterruptedException e) {
                            return;
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
        }
    }

}
