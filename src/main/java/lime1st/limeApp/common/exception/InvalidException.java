package lime1st.limeApp.common.exception;

public class InvalidException extends RuntimeException {
    public InvalidException() {
        super("Invalid");
    }
    public InvalidException(String message) {
        super(message);
    }
}
