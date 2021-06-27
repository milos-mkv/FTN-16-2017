package managers;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class ErrorManager {

    @Getter
    private static List<String> errors;

    public static void initialize() {
        errors = new ArrayList<>();
    }

    public static String getLatestError() {
        return errors.size() > 0 ? errors.get(errors.size() - 1) : "";
    }

}
