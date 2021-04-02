package nl.hu.cisq1.lingo.trainer.domain.exceptions;

public class InvalidTurnException extends RuntimeException{
    public InvalidTurnException(String message) {
        super(message);
    }
}
