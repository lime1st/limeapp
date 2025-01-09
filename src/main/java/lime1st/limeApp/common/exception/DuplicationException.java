package lime1st.limeApp.common.exception;

public class DuplicationException extends RuntimeException{

    public DuplicationException() {
        super("Duplicate");
    }

    public DuplicationException(String message) {
        super(message);
    }
}
