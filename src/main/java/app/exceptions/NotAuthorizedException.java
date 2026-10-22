package app.exceptions;

public class NotAuthorizedException extends RuntimeException {

    private final int serverResponseCode;

    public NotAuthorizedException(int serverResponseCode, String message) {
        super(message);
        this.serverResponseCode = serverResponseCode;
    }

    public int getServerResponseCode() {
        return serverResponseCode;
    }
}