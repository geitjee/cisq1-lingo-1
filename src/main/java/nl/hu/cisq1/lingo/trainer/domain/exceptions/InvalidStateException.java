package nl.hu.cisq1.lingo.trainer.domain.exceptions;

public class InvalidStateException extends RuntimeException{
    public InvalidStateException(String message) {
        super(message);
    }
}
