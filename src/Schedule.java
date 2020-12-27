/**
 *  File: Schedule.java
 *  This class models a Charging station Schedule
 */
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

public class Schedule {
    private Map<DayOfWeek, List<TimeSpan>> schedule;

    private List<TimeSpan> timeSpans;
    private List<DayOfWeek> daysOfWeek;

    public Schedule(StationType stationType) {
        schedule = new LinkedHashMap<>();
        timeSpans = new ArrayList<>();
        daysOfWeek = new ArrayList<>();

        for(DayOfWeek day : DayOfWeek.values()){
            schedule.put(day, new LinkedList<>());
        }
        setUp(stationType);
    }


    private void setUp(StationType type) {
        LocalTime from, to;

        switch (type) {
            case CUSTOMER:   // configure charging stations basic opening hours

                // First half of the day monday, tuesday, thursday
                from = LocalTime.of(8, 00);
                to   = LocalTime.of(12, 00);
                timeSpans.add(new TimeSpan(from, to));

                // Second half of the day  monday, tuesday, thursday
                from = LocalTime.of(13, 00);
                to   = LocalTime.of(17, 30);
                timeSpans.add(new TimeSpan(from, to));

                // Set the times for monday, tuesday and thursday
                daysOfWeek.addAll(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.THURSDAY));
                setTimeSpanOfWeekDays(timeSpans, daysOfWeek);

                // Set the time for wednesday
                from = LocalTime.of(8, 00);
                to   = LocalTime.of(13, 00);
                schedule.get(DayOfWeek.WEDNESDAY).add(new TimeSpan(from, to));

                // Set the time for the first half of the day friday.
                from = LocalTime.of(8, 00);
                to   = LocalTime.of(12, 00);
                timeSpans.add(new TimeSpan(from, to));

                // Set the time for the second half of the day friday.
                from = LocalTime.of(13, 00);
                to   = LocalTime.of(20, 00);
                timeSpans.add(new TimeSpan(from, to));
                daysOfWeek.add(DayOfWeek.FRIDAY);

                // Set the time for friday
                setTimeSpanOfWeekDays(timeSpans, daysOfWeek);

                // Set the time for saturday.
                from = LocalTime.of(10, 00);
                to   = LocalTime.of(13, 00);
                schedule.get(DayOfWeek.SATURDAY).add(new TimeSpan(from, to));

                break;

            case EMPLOYEE:      // configure charging stations only accessible to store employees.

                // First half of the day
                from = LocalTime.of(6, 30);
                to   = LocalTime.of(19, 00);
                timeSpans.add(new TimeSpan(from, to));

                // set the times for monday, tuesday and thursday
                daysOfWeek.addAll(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.THURSDAY));
                setTimeSpanOfWeekDays(timeSpans, daysOfWeek);

                // set the time for wednesday
                from = LocalTime.of(6, 30);
                to   = LocalTime.of(14, 30);
                schedule.get(DayOfWeek.WEDNESDAY).add(new TimeSpan(from, to));

                // set the time for friday
                from = LocalTime.of(6, 30);
                to   = LocalTime.of(21, 00);
                schedule.get(DayOfWeek.FRIDAY).add(new TimeSpan(from, to));

                // set the time for saturday
                from = LocalTime.of(9, 00);
                to   = LocalTime.of(14, 30);
                schedule.get(DayOfWeek.SATURDAY).add(new TimeSpan(from, to));

                break;
            case MANAGER:   // configure charging stations only accessible to store Manager.
                // set the time for saturday
                from = LocalTime.MIDNIGHT;
                to   = LocalTime.MAX;
                timeSpans.add(new TimeSpan(from, to));

                daysOfWeek.addAll(Arrays.asList(DayOfWeek.values()));
                setTimeSpanOfWeekDays(timeSpans, daysOfWeek);

                break;
            default:
                break;
        }

    }


    /** Set the TimeSpan of multiple week days */
    private void setTimeSpanOfWeekDays(List<TimeSpan> times, List<DayOfWeek> weekDays) {
        for (DayOfWeek day : weekDays)
        {
            for(TimeSpan time : times) {
                schedule.get(day).add(time);
            }
        }
        // clear collection.
        times.clear();
        weekDays.clear();
    }

    public Map<DayOfWeek, List<TimeSpan>> getSchedule() {
        return schedule;
    }
}
