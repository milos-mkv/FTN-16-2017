package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface Utils {

    static String readFromFile(final String file) {
        StringBuilder stringBuilder = null;
        try (var bufferedReader = new BufferedReader(new FileReader(file))) {
            stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            Logger.getGlobal().log(Level.WARNING, e.getMessage());
        }
        return stringBuilder != null ? stringBuilder.toString() : null;
    }

}
