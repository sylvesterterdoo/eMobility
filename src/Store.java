import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Store {
    private static int instanceCounter = 0;

    private int storeNumber;
    private Tenant tenant;
    private List<ChargingStation> chargingStations;
    private Map<ExceptionType, List<Exception>> exceptions;
    private Map<DayOfWeek, List<TimeSpan>> openingTimeSchedule;

    public Store(Tenant tenant) {
        this.tenant = tenant;
        this.storeNumber = instanceCounter;

        Map<ExceptionType, List<Exception>> exceptions = new HashMap<>();
        exceptions.put(ExceptionType.OPEN, new LinkedList<>());
        exceptions.put(ExceptionType.CLOSE, new LinkedList<>());

        this.exceptions = exceptions;

        instanceCounter += 1;
    }

    public int getStoreNumber() {
        return storeNumber;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public Map<ExceptionType, List<Exception>> getExceptions() {
        return exceptions;
    }

    public void addException(Exception exception) {
        Utilities.addExceptions(exception, this.exceptions);
    }
}
