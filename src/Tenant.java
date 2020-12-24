import java.util.*;

public class Tenant {
    String name;
    List<Store> stores;
    private Map<ExceptionType, List<Exception>> exceptions;

    public Tenant(String name) {
        this.name = name;
        stores = new ArrayList<>();

        Map<ExceptionType, List<Exception>> exceptions = new HashMap<>();
        exceptions.put(ExceptionType.OPEN, new LinkedList<>());
        exceptions.put(ExceptionType.CLOSE, new LinkedList<>());

        this.exceptions = exceptions;
    }

    public void addStore(Store store) {
        this.stores.add(store);
    }

    public Map<ExceptionType, List<Exception>> getExceptions() {
        return exceptions;
    }

    public void addException(Exception exception) {
        Utilities.addExceptions(exception, this.exceptions);
    }
}
