import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

public class Store {
    private static int instanceCounter = 0;

    private int storeNumber;
    private List<ChargingStation> chargingStations;
    private Map<ExceptionType, List<Exception>> exceptions;
    private Map<DayOfWeek, List<TimeSpan>> openingTimeSchedule;

    public Store() {
        this.storeNumber = instanceCounter;
        instanceCounter += 1;
    }

    public int getStoreNumber() {
        return storeNumber;
    }

    public void addException(Exception exception) {
        Utilities.addExceptions(exception, this.exceptions);
    }
}
