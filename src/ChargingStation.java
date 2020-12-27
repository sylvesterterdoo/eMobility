
/**
 * File: ChargingStation.java
 * This class models the ChargingStation entity.
 */

import java.sql.Timestamp;
import java.time.*;
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


    /**
     * @return the ChargingStation exceptions
     */
    public Map<ExceptionType, List<Exception>> getExceptions() {
        return exceptions;
    }

    /**
     * Adds a new exception to the ChargingStation
     * @param exception
     */
    public void addException(Exception exception) {
        Utilities.addExceptions(exception, this.exceptions);
    }

    /**
     * Checks whether a Charging station is open
     * @param timestamp
     * @return a true if Charging station is open
     */
    public boolean isChargingStationOpenDuring(Timestamp timestamp) {
        LocalDateTime dateTime = timestamp.toLocalDateTime();

        if (Exception.isExceptionBetween(exceptions.get(ExceptionType.CLOSE), dateTime)) {
            return false;
        } else if (Exception.isExceptionBetween(exceptions.get(ExceptionType.OPEN), dateTime)) {
            return true;
        } else if (Exception.isExceptionBetween(store.getExceptions().get(ExceptionType.CLOSE), dateTime)){
            return false;
        } else if (Exception.isExceptionBetween(store.getExceptions().get(ExceptionType.OPEN), dateTime)) {
            return true;
        } else if (Exception.isExceptionBetween(store.getTenant().getExceptions().get(ExceptionType.CLOSE), dateTime)){
            return false;
        } else if (Exception.isExceptionBetween(store.getTenant().getExceptions().get(ExceptionType.OPEN), dateTime)) {
            return true;
        } else {
            return isOpeningHoursInSchedule(dateTime.toLocalTime(), dateTime.getDayOfWeek());
        }
    }

    /**
     * Check when the next open/closed status will change given a timestamp
     * @param timestamp
     * @return a timestamp indicating this time.
     */
    public Timestamp nextOpenClosedStatusChange(Timestamp timestamp) {
        LocalDateTime dateTime = timestamp.toLocalDateTime();

        Timestamp result = dateStatusChange(exceptions.get(ExceptionType.CLOSE), dateTime);
        if (result == null) {
            result = dateStatusChange(exceptions.get(ExceptionType.OPEN), dateTime);
        }

        if (result == null) {
            result = dateStatusChange(store.getExceptions().get(ExceptionType.CLOSE), dateTime);
        }
        if (result == null) {
            result = dateStatusChange(store.getExceptions().get(ExceptionType.OPEN), dateTime);
        }

        if (result == null) {
            result = dateStatusChange(store.getTenant().getExceptions().get(ExceptionType.CLOSE), dateTime);
        }
        if (result == null) {
            result = dateStatusChange(store.getTenant().getExceptions().get(ExceptionType.OPEN), dateTime);
        }
        if (result == null) {
            result = sheduleStatusChange(dateTime);
        }

        return result;

    }


    /** Convert a given LocalTime and Localdate to Timestamp */
    private Timestamp convertLocalTimeToTimestamp(LocalTime time, LocalDateTime dateTime) {
        Instant instant = time.atDate(dateTime.toLocalDate())
                .atZone(ZoneId.systemDefault()).toInstant();
        return new Timestamp(Date.from(instant).getTime());

    }


    /** Checks the opening times from a station schedule
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

    /** Checks when the next open/closed status will change given a list of exceptions */
    private Timestamp dateStatusChange(List<Exception> exceptions, LocalDateTime checkDateTime) {

        LocalDateTime nextDateTime = checkDateTime.plusDays(1);

        LocalDate checkDate = checkDateTime.toLocalDate();
        LocalTime checkTime = checkDateTime.toLocalTime();
        for(Exception exception: exceptions) {  // checkDate.equals(nextDateTime.toLocalDate())
            if (exception.isDateBetween(checkDate) ) {
                return getNextTimeFrom(exception, checkDateTime);
            } else if (checkDate.equals(nextDateTime.toLocalDate())) {
                // if it's the to return the nextday (check the time is not after the normal)
                return getNextTimeFrom(exception, checkDateTime);
            }
        }
        return null;
    }

    /** Get the appropriate next time from an exception give a specific LocalDatetime */
    private Timestamp getNextTimeFrom(Exception exception, LocalDateTime dateTime) {
        TimeSpan currentTimeSpan = exception.timeSpan;
        LocalTime checkTime = dateTime.toLocalTime();

        Timestamp timestamp = null;
        if (currentTimeSpan.getFrom().compareTo(checkTime) > 0 && !isOpeningHours(currentTimeSpan, checkTime)) {
            // before opening time
            timestamp = convertLocalTimeToTimestamp(currentTimeSpan.getFrom(), dateTime);

        } else if (isOpeningHours(currentTimeSpan, checkTime)) {
            // during opening time
            timestamp =  convertLocalTimeToTimestamp(currentTimeSpan.getTo(), exception.toDate.atTime(checkTime));

        }
        return timestamp;
    }

    /** Checks when the next open/closed status will change given a schedule */
    public Timestamp sheduleStatusChange(LocalDateTime dateTime) {
        DayOfWeek checkDay = dateTime.getDayOfWeek();
        LocalTime checkTime= dateTime.toLocalTime();

        Timestamp timestamp = null;
        int i = 0;
        List<TimeSpan> timeSpans = openingTimeSchedule.get(checkDay);

        while (i < timeSpans.size()) {
            TimeSpan currentTimeSpan = timeSpans.get(i);


            if (currentTimeSpan.getFrom().compareTo(checkTime) > 0 && !isOpeningHours(currentTimeSpan, checkTime)) {
                // before opening time
                timestamp = convertLocalTimeToTimestamp(currentTimeSpan.getFrom(), dateTime);
                break;

            } else if (isOpeningHours(currentTimeSpan, checkTime)) {
                // during opening time
                timestamp =  convertLocalTimeToTimestamp(currentTimeSpan.getTo(), dateTime);
                break;

            } else if (currentTimeSpan.getTo().compareTo(checkTime) < 0 ) {
                // after opening time
                if ((i+1) < timeSpans.size()) {
                    // get next timeSpan from same day
                    timestamp = convertLocalTimeToTimestamp(timeSpans.get(i+1).getFrom(), dateTime);
                    break;
                } else {
                    // get nextDay timeSpan or next monday if it is saturday
                    int daysToAdd = checkDay.equals(DayOfWeek.SATURDAY) ? 2 : 1;
                    checkDay = checkDay.plus(daysToAdd);
                    timeSpans = openingTimeSchedule.get(checkDay);
                    timestamp = convertLocalTimeToTimestamp(timeSpans.get(0).getFrom(), dateTime.plusDays(daysToAdd));
                    break;
                }
            }
            // next timeSpan
            i += 1;
        }

        return timestamp;
    }


    /** Checks the openingHours of a station given a TimeSpan and LocalTime */
    private boolean isOpeningHours(TimeSpan timeSpan, LocalTime checkTime) {
        return checkTime.query(temporal -> {
            LocalTime from = timeSpan.getFrom();
            LocalTime to = timeSpan.getTo();
            if ((checkTime.compareTo(from) >= 0) && (checkTime.compareTo(to) < 0)) {
                return true;
            }
            return false;
        });
    }


}
