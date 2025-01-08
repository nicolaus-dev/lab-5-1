package ua.edu.chmnu.network.java;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadDownloader {
    public static void downloadFile(String fileURL, String saveDir, int numThreads) {
        try {

            URL url = new URL(fileURL);
            URLConnection connection = url.openConnection();

            int fileSize = connection.getContentLength();
            if (fileSize <= 0) {
                System.err.println("Invalid file size or inaccessible URL.");
                return;
            }

            String fileName = new File(url.getPath()).getName();
            String saveFilePath = saveDir + File.separator + fileName;

            RandomAccessFile outputFile = new RandomAccessFile(saveFilePath, "rw");
            outputFile.setLength(fileSize);
            outputFile.close();

            int chunkSize = fileSize / numThreads;
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);

            for (int i = 0; i < numThreads; i++) {
                int startByte = i * chunkSize;
                int endByte = (i == numThreads - 1) ? fileSize - 1 : (startByte + chunkSize - 1);

                executor.execute(new DownloadTask(fileURL, saveFilePath, startByte, endByte, i + 1));
            }

            executor.shutdown();
            while (!executor.isTerminated()) {

            }

            System.out.println("File downloaded successfully: " + saveFilePath);
        } catch (Exception e) {
            System.err.println("Error while downloading file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the URL to download:");
        String fileURL = scanner.nextLine();

        System.out.println("Enter the directory to save the file:");
        String saveDir = scanner.nextLine();

        System.out.println("Enter the number of threads:");
        int numThreads = scanner.nextInt();

        new File(saveDir).mkdirs();

        downloadFile(fileURL, saveDir, numThreads);
    }
}

class DownloadTask implements Runnable {
    private final String fileURL;
    private final String saveFilePath;
    private final int startByte;
    private final int endByte;
    private final int threadId;

    public DownloadTask(String fileURL, String saveFilePath, int startByte, int endByte, int threadId) {
        this.fileURL = fileURL;
        this.saveFilePath = saveFilePath;
        this.startByte = startByte;
        this.endByte = endByte;
        this.threadId = threadId;
    }

    @Override
    public void run() {
        try {

            URL url = new URL(fileURL);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);

            InputStream inputStream = connection.getInputStream();

            RandomAccessFile outputFile = new RandomAccessFile(saveFilePath, "rw");
            outputFile.seek(startByte);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputFile.write(buffer, 0, bytesRead);
            }

            outputFile.close();
            inputStream.close();

            System.out.println("Thread " + threadId + " completed downloading its chunk.");
        } catch (Exception e) {
            System.err.println("Thread " + threadId + " failed: " + e.getMessage());
        }
    }
}