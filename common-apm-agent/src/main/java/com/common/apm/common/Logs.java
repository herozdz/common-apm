package com.common.apm.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tommy on 2018/3/12.
 */
public class Logs {
    FileWriter fileWriter = null;
    private static Logs log = new Logs();
    private File file;
    private long fileMaxSize = 20 * 1024;

    public static void info(String info,String logPath) {
        log.write(info,logPath);
    }

    public void write(String log,String logPath) {
        try {
            FileWriter write = getFileWriter(logPath);
            write.write( log +"\r\n");
            write.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileWriter getFileWriter(String logPath) {
        try {
            if (fileWriter == null) {
                synchronized (this) {
                    if (fileWriter == null) {
                        this.file = openFile(logPath);
                        fileWriter = new FileWriter(file, true);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (file.length() > fileMaxSize) {
            for (File file1 : file.getParentFile().listFiles()) {
                //agent agent-1 agent-2 agent-3 agent-4 agent-5
            }
        }
        return fileWriter;
    }

    private File openFile(String logPath) {
        try {
            String rootDir = logPath;
            File root = new File(rootDir);
            if (!root.exists() || !root.isDirectory()) {
                root.mkdirs();
            }
            File file = new File(root, "apm-agent.log");
            if (file.exists()) {
                file.createNewFile();
            }
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        List<Thread> list = new ArrayList<Thread>();
        for (int i = 0; i < 1000; i++) {

            list.add(new Thread() {
                @Override
                public void run() {
                    Logs.info("1",System.getProperty("user.dir") + "/logs/");
                }
            });
        }
        for (Thread thread : list) {
            thread.start();
        }
        for (Thread thread : list) {
            thread.join();
        }
    }
}
