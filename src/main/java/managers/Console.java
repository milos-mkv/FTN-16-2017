package managers;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class Console {

    public enum Level { INFO, ERROR, WARNING }

    @Getter
    private static final List<String> logs = new ArrayList<>();

    private Console() { }

    public static void log(Level level, String message) {
        var stringBuilder = new StringBuilder().append(getTime());
        switch (level) {
            case ERROR:   stringBuilder.append("[ERROR  ] "); break;
            case INFO:    stringBuilder.append("[INFO   ] "); break;
            case WARNING: stringBuilder.append("[WARNING] "); break;
            default:      stringBuilder.append("[DEBUG  ] "); break;
        }
        stringBuilder.append(message);
        logs.add(stringBuilder.toString());
    }

    public static String getTime() {
        return "[" + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] ";
    }

}
