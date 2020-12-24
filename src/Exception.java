import java.time.LocalDate;
import java.time.LocalTime;

enum ExceptionType {
    OPEN,
    CLOSE
}

public class Exception {
    LocalDate fromDate;
    LocalDate toDate;
    TimeSpan timeSpan;
    ExceptionType exceptionType;

    public Exception(LocalDate fromDate, LocalDate toDate, TimeSpan timeSpan, ExceptionType exceptionType) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.timeSpan = timeSpan;
        this.exceptionType = exceptionType;
    }

    public boolean isDateBetween(LocalDate localDate) {
        if ((localDate.compareTo(fromDate)) >= 0 &&
                (localDate.compareTo(toDate) <= 0)) {
            return true;
        }
        return false;
    }

    public boolean isTimeBetween(LocalTime localTime) {
        if ((localTime.compareTo(timeSpan.getFrom())) >= 0 &&
                (localTime.compareTo(timeSpan.getTo()) < 0)) {
            return true;
        }
        return false;
    }

}
