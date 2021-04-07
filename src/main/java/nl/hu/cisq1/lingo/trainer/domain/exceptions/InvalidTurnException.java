package nl.hu.cisq1.lingo.trainer.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTurnException extends RuntimeException{
    public InvalidTurnException(String message) {
        super(message);
    }
}
