package lime1st.limeApp.common.exception;

public class DataSaveException extends RuntimeException {

    public DataSaveException() {
        super("Data");
    }

    public DataSaveException(String message) {
        super("Data " + message);
    }
}
