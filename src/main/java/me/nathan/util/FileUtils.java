package me.nathan.util;

import java.io.*;

public class FileUtils {

    private FileUtils() {

    }

    public static String loadAsString(String file) {

        String result = "";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String buffer = "";
            while ((buffer = reader.readLine()) != null) {
                result += buffer + "\n";
            }

            reader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}
