package cz.cvut.fit.smejkdo1.bak.acpf.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Loads or saves file with its full path in fileName.
 */
public class FetchFile {
    public static List<String> lines(String fileName){
        File file = new File(fileName);
        return lines(file);
    }

    public static List<String> lines(File file){
        List<String> result = new ArrayList<>();

        Scanner sc;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.err.println("File " + file.getPath() + " not found.");
            e.printStackTrace();
            return result;
        }
        while(sc.hasNext())
            result.add(sc.nextLine());
        return result;
    }


    public static void save(List<String> lines, String fileName){
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(fileName))) {
            for (String s : lines) {
                pw.println(s);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void save(String data, String filePath, String fileName) {
        makeDirectory(filePath);
        try (FileOutputStream fos = new FileOutputStream(filePath + fileName)) {
            fos.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void makeDirectory(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    public static File fetchDirectory(String filePath) {
        File directory = new File(filePath);
        if (!directory.isDirectory())
            return null;
        return directory;
    }
}
