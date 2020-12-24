import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

enum StationType {
    CUSTOMER,
    EMPLOYEE,
    MANAGER
}
public class ChargingStation {
    private static int instanceCounter = 0;

    private int chargingStationNumber;
    private Store store;
    private StationType stationType;
    private Map<ExceptionType, List<Exception>> exceptions;
    private Map<DayOfWeek, List<TimeSpan>> openingTimeSchedule;

    public ChargingStation(Store store, StationType stationType) {
        this.store = store;
        this.stationType = stationType;

        Map<ExceptionType, List<Exception>> exceptions = new HashMap<>();
        exceptions.put(ExceptionType.OPEN, new LinkedList<>());
        exceptions.put(ExceptionType.CLOSE, new LinkedList<>());

        this.exceptions = exceptions;

        this.openingTimeSchedule = getSchedule(stationType);
        this.chargingStationNumber = instanceCounter;
        instanceCounter += 1;

    }


    private Map<DayOfWeek, List<TimeSpan>> getSchedule(StationType stationType) {
        Schedule schedule = new Schedule(stationType);
        return schedule.getSchedule();
    }

    public Map<ExceptionType, List<Exception>> getExceptions() {
        return exceptions;
    }

    public void addException(Exception exception) {
        Utilities.addExceptions(exception, this.exceptions);
    }

    public boolean isChargingStationOpenDuring(Timestamp timestamp) {
        LocalDateTime dateTime = timestamp.toLocalDateTime();

        // check the exceptions for the store and the tenant before checking the schedule.
        if (isExpectionsBetween(exceptions.get(ExceptionType.CLOSE), dateTime)) {
            return false;
        } else if (isExpectionsBetween(exceptions.get(ExceptionType.OPEN), dateTime)) {
            return true;
        } else if (isExpectionsBetween(store.getExceptions().get(ExceptionType.CLOSE), dateTime)){
            return false;
        } else if (isExpectionsBetween(store.getExceptions().get(ExceptionType.OPEN), dateTime)) {
            return true;
        } else if (isExpectionsBetween(store.getTenant().getExceptions().get(ExceptionType.CLOSE), dateTime)){
            return false;
        } else if (isExpectionsBetween(store.getTenant().getExceptions().get(ExceptionType.OPEN), dateTime)) {
            return true;
        } else {
            return isOpeningHoursInSchedule(dateTime.toLocalTime(), dateTime.getDayOfWeek());
        }
    }

    /** Helper method that checks the opening times from a station schedule
     * given a LocalTime and DayOfWeek */
    private boolean isOpeningHoursInSchedule(LocalTime checkTime, DayOfWeek weekDay) {
        return checkTime.query(temporal -> {
            for (TimeSpan timeSpan : openingTimeSchedule.get(weekDay)) {
                LocalTime from = timeSpan.getFrom();
                LocalTime to = timeSpan.getTo();
                if ((checkTime.compareTo(from) >= 0) && (checkTime.compareTo(to) < 0)) {
                    return true;
                }
            }
            return false;
        });
    }


    private boolean isExpectionsBetween(List<Exception> exceptions, LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        for(Exception exception: exceptions) {
            if (exception.isDateBetween(date)) {
                if (exception.isTimeBetween(time)) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
         this.exceptions.computeIfAbsent(exception.exceptionType, k -> {
            List<Exception> newExceptions = new ArrayList<>();
            newExceptions.add(exception);
            return newExceptions;
        });

        this.exceptions.computeIfPresent(exception.exceptionType, (k, v) -> {
            List<Exception> savedExceptions = exceptions.get(k);
            savedExceptions.add(exception);
            return savedExceptions;
        });
     */
}
