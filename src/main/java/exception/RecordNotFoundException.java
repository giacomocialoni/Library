package exception;

@SuppressWarnings("serial")
public class RecordNotFoundException extends DAOException {
    public RecordNotFoundException(String message) {
        super(message);
    }
}