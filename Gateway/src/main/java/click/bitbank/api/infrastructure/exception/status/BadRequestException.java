package click.bitbank.api.infrastructure.exception.status;

import org.springframework.http.HttpStatus;
import click.bitbank.api.infrastructure.exception.GlobalException;

public class BadRequestException extends GlobalException {

    public BadRequestException() {
        super(HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }

    public BadRequestException(String reason, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, reason, cause);
    }
}
