/**
 * File: Tenant.java
 * This class models the Tenant entity
 */

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

    /**
     * Adds a store to a the list of stores belonging to a tenant.
     * @param store
     */
    public void addStore(Store store) {
        this.stores.add(store);
    }

    /**
     * @return the tenant exceptions
     */
    public Map<ExceptionType, List<Exception>> getExceptions() {
        return exceptions;
    }

    /**
     * Adds a new exception to the tenant
     * @param exception
     */
    public void addException(Exception exception) {
        Utilities.addExceptions(exception, this.exceptions);
    }
}
