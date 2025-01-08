package ua.edu.chmnu.network.java;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class SingleThreadDownloader {
    public static void downloadFile(String fileURL, String saveDir) {
        try {

            URL url = new URL(fileURL);
            URLConnection connection = url.openConnection();

            InputStream inputStream = connection.getInputStream();

            String fileName = new File(url.getPath()).getName();
            String saveFilePath = saveDir + File.separator + fileName;

            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

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

        new File(saveDir).mkdirs();

        downloadFile(fileURL, saveDir);
    }
}