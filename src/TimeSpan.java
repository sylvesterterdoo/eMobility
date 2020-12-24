import java.time.LocalDate;
import java.time.LocalTime;

public class TimeSpan {
    private LocalTime from;
    private LocalTime to;

    public TimeSpan(LocalTime from, LocalTime to) {
        this.from = from;
        this.to = to;
    }


    public LocalTime getFrom() {
        return from;
    }

    public void setFrom(LocalTime from) {
        this.from = from;
    }

    public LocalTime getTo() {
        return to;
    }

    public void setTo(LocalTime to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "TimeSpan{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}
