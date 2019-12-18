package com.sbt.multithreading;


import java.io.File;
import java.io.FilenameFilter;

public class Main {
    public static void main(String[] args) {
        int quantityString = 1000;
        long start = System.currentTimeMillis();
        ResultObject rez = new ResultObject();
        //ListString listString = new ListString();
        rez.counter = 0;
        String folderName = "src\\main\\resources";
        File folder = new File(folderName);
        FixedThreadPool fixedThreadPool = new FixedThreadPool(8);
        LimitedThreadPool limitedThreadPool = new LimitedThreadPool(6, 8);
        limitedThreadPool.start();

        String[] files = folder.list(new FilenameFilter() {

            @Override
            public boolean accept(File folder, String name) {
                return name.endsWith(".txt");
            }

        });
        for (String fileName : files) {
            limitedThreadPool.execute(new CounterThreadReader(folderName, fileName, /*listString,*/ fixedThreadPool, rez, quantityString));
        }


        /*for (int i = 0; i < 8; i++ )
            fixedThreadPool.execute(new CounterThreadWorker(rez,listString));*/

        fixedThreadPool.start();

        try {
            System.out.println(" Main is waiting... limitedThreadPool.getQueueCount():" + limitedThreadPool.getQueueCount() + "; fixedThreadPool.getQueueCount(): " + fixedThreadPool.getQueueCount());
            Thread.sleep(50);
        } catch (InterruptedException e) {
        }
        limitedThreadPool.shutdown();
        fixedThreadPool.shutdown();

        while (!(limitedThreadPool.getThreadsSize() == 0 & fixedThreadPool.getThreadCount() == 0)) {
            try {
                System.out.println(" Main is waiting... limitedThreadPool.getThreadsSize():" + limitedThreadPool.getThreadsSize() + "; fixedThreadPool.getThreadCount(): " + fixedThreadPool.getThreadCount());
                Thread.sleep(10);
                //System.out.println("listString.inFile.size() "+listString.inFile.size());
            } catch (InterruptedException e) {
            }
        }

        System.out.println("Всего слов многопоточно :" + rez.counter);
        long finish = System.currentTimeMillis();
        long timeConsumedMillis = finish - start;
        System.out.println("Затрачено времени многопоточно : " + timeConsumedMillis);


    }
}
