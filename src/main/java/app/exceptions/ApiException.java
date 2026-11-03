package app.exceptions;

import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiException extends RuntimeException {
    private final HttpStatus status;
    public static final Logger logger = LoggerFactory.getLogger(ApiException.class);

    public ApiException(HttpStatus status, String msg){
        super(msg);
        this.status = status;
        logger.error("ApiException (status={}): {}", status, msg);
    }
    public int getCode(){
        return status.getCode();
    }
    public HttpStatus getStatus() {
        return status;
    }
}
