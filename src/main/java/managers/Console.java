package managers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class Console {

    @NoArgsConstructor
    public static class Log {
        public String time;
        public Level level;
        public String message;
    }

    public enum Level { INFO, ERROR, WARNING }

    @Getter
    private static final List<Log> logs = new ArrayList<>();

    private Console() { }

    public static void log(Level level, String message) {
        Log log = new Log();
        log.time = getTime() + "[";
        log.level = level;
        log.message = message;
        logs.add(log);
    }

    public static String getTime() {
        return "[" + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] ";
    }

}
