package com.sbt.multithreading;

import java.io.*;
import java.util.ArrayList;

public class CounterThreadReader implements Runnable {
    String folder;
    String inFile;
    ArrayList<String> fileStr;
    FixedThreadPool fixedThreadPool;
    ResultObject rez;
    private int quantityString;

    CounterThreadReader(String folder, String inFile, /*ListString outFile,*/ FixedThreadPool fixedThreadPool, ResultObject rez, int quantityString/*, ResultObject rez, String file*/) {
        this.folder = folder;
        this.inFile = inFile;
        //this.outFile = outFile;
        this.fixedThreadPool = fixedThreadPool;
        this.rez = rez;
        this.quantityString = quantityString;

    }

    @Override
    public void run() {
        fileStr = new ArrayList();
        try {
            File file_ = new File(folder + "\\" + inFile);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file_), "windows-1251"));
            int cnt;
            cnt = 0;
            String line = reader.readLine();
            //fileStr.add(file);
            int j = 0;
            while (line != null) {
                fileStr.add(line);
                cnt++;
                line = reader.readLine();
                j++;
                if (j >= quantityString) {
                    fixedThreadPool.execute(new CounterThreadWorker(rez, fileStr));
                    fileStr = new ArrayList();
                    j = 0;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fixedThreadPool.execute(new CounterThreadWorker(rez, fileStr));
    }

}


