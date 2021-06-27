package managers;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class ErrorManager {

    @Getter
    private static final List<String> errors = new ArrayList<>();

    private ErrorManager() { /* Empty */ }

    public static String getLatestError() {
        return !errors.isEmpty() ? errors.get(errors.size() - 1) : "";
    }

}
