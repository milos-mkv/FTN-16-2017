package managers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class Console {

    @AllArgsConstructor
    @NoArgsConstructor
    public static class Log {
        public String time;
        public String level;
        public String message;
    }

    public enum Level { INFO, ERROR, WARNING }

    @Getter
    private static final List<Log> logs = new ArrayList<>();
//    @Getter
//    private static final List<Log> logss = new ArrayList<>();

    private Console() { }

    public static void log(Level level, String message) {
        Log log = new Log();
        log.time = getTime() + "[";
        switch (level) {
            case ERROR:     log.level = "ERROR  "; break;
            case INFO:      log.level = "INFO   "; break;
            case WARNING:   log.level = "WARNING"; break;
            default:        log.level = "DEBUG  "; break;
        }
        log.message = "] " + message;
        logs.add(log);
    }

    public static String getTime() {
        return "[" + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] ";
    }

}
