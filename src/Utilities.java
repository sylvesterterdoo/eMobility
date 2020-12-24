import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utilities {

    public static void addExceptions(Exception exception, Map<ExceptionType, List<Exception>> exceptions) {
        exceptions.computeIfAbsent(exception.exceptionType, k -> {
            List<Exception> newExceptions = new ArrayList<>();
            newExceptions.add(exception);
            return newExceptions;
        });

        exceptions.computeIfPresent(exception.exceptionType, (k, v) -> {
            List<Exception> savedExceptions = exceptions.get(k);
            savedExceptions.add(exception);
            return savedExceptions;
        });
    }
}
