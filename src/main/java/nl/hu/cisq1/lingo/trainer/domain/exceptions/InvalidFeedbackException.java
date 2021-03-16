package nl.hu.cisq1.lingo.trainer.domain.exceptions;

public class InvalidFeedbackException extends RuntimeException{
    public InvalidFeedbackException(String message) {
        super(message);
    }
}
