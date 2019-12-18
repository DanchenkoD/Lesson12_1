package com.sbt.multithreading;

import java.util.ArrayList;
import java.util.HashMap;

public class CounterThreadWorker implements Runnable {
    ResultObject rez;
    ArrayList<String> fileStr;

    CounterThreadWorker(ResultObject res, ArrayList<String> fileStr) {
        this.rez = res;
        this.fileStr = fileStr;
    }

    @Override
    public void run() {
        HashMap<String, Integer> words;
        String[] splitted;
        int cnt;
        cnt = 0;
        words = new HashMap<>();
        for (int j = 0; j < fileStr.size(); j++) {
            splitted = fileStr.get(j).replace(",", "").replace(".", "").replace("  ", " ").split(" ");
            for (int i = 0; i < splitted.length; i++) {
                if (words.containsKey(splitted[i].toLowerCase())) {
                    int value = words.get(splitted[i].toLowerCase());
                    words.put(splitted[i].toLowerCase(), value + 1);
                } else {
                    words.put(splitted[i].toLowerCase(), 1);

                }
            }
            cnt++;
        }
        System.out.println("Количество строк: " + cnt + "; различных слов: " + words.size() + "; поток: " + Thread.currentThread().getName() /*+ "; Имя файла: " + fileStr.get(0)*/);

        synchronized (rez) {
            rez.counter += words.size();
        }
    }

}
