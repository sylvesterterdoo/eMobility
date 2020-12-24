import java.time.LocalDate;

enum ExceptionType {
    OPEN,
    CLOSE
}

public class Exceptions {
    LocalDate fromDate;
    LocalDate toDate;
    TimeSpan timeSpan;
    ExceptionType exceptionType;
}
