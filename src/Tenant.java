import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Tenant {
    String name;
    List<Store> stores;
    private Map<ExceptionType, List<Exception>> exceptions;

    public Tenant(String name) {
        this.name = name;
        stores = new ArrayList<>();
    }

    public void addStore(Store store) {
        this.stores.add(store);
    }

    public void addException(Exception exception) {
        Utilities.addExceptions(exception, this.exceptions);
    }
}
