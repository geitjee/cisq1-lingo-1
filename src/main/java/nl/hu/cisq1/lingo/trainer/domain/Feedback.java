package nl.hu.cisq1.lingo.trainer.domain;

import nl.hu.cisq1.lingo.trainer.domain.exceptions.InvalidFeedbackException;

import java.util.List;
import java.util.Objects;

public class Feedback {
    private String attempt;
    private List<Mark> marks;

    public Feedback(String attempt, List<Mark> marks) {
        if (attempt.length() != marks.size()){
            throw new InvalidFeedbackException("attempt and feedback don't have the same lenght!");
        }
        this.attempt = attempt;
        this.marks = marks;
    }

    public boolean isWordGuessed(){
        return marks.stream().allMatch(m -> m.equals(Mark.CORRECT));
    }

    public boolean isGuessInvalid(){//if guess is not the same lenght or non existent
        return marks.stream().allMatch(m -> m.equals(Mark.INVALID));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feedback feedback = (Feedback) o;
        return Objects.equals(attempt, feedback.attempt) && Objects.equals(marks, feedback.marks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attempt, marks);
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "attempt='" + attempt + '\'' +
                ", marks=" + marks +
                '}';
    }
}
