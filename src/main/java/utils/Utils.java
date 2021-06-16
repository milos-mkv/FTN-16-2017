package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public abstract class Utils {

    public static void Assert(boolean expression, final String message) {
        if (!expression) {
            throw new RuntimeException(message);
        }
    }

    public static String ReadFromFile(final String file) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append(System.getProperty("line.separator"));
            }
            bufferedReader.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
