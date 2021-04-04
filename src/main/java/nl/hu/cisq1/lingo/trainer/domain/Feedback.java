package nl.hu.cisq1.lingo.trainer.domain;

import nl.hu.cisq1.lingo.trainer.domain.exceptions.InvalidFeedbackException;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String attempt;

    @ElementCollection
    private List<Mark> marks;

    @Column
    private String hint;

    public Feedback(String attempt, List<Mark> marks) {
        if (attempt.length() != marks.size()){
            throw new InvalidFeedbackException("attempt and feedback don't have the same lenght!");
        }
        this.attempt = attempt;
        this.marks = marks;
    }

    public Feedback() {

    }

    public String getAttempt() {
        return attempt;
    }

    public List<Mark> getMarks() {
        return marks;
    }

    public boolean isWordGuessed(){
        return marks.stream().allMatch(m -> m.equals(Mark.CORRECT));
    }

    public boolean isGuessValid(){//if guess is not the same lenght or non existent
        return marks.stream().noneMatch (m -> m.equals(Mark.INVALID));
    }

    public String giveHint(String previousHint, String wordToGuess){
        if (previousHint == null || previousHint.isEmpty() || previousHint.length() != wordToGuess.length()){
            previousHint = wordToGuess.charAt(0) + ".".repeat(wordToGuess.length()-1);
        }
        if(!isGuessValid()){
            hint = previousHint;
            return previousHint;
        }
        StringBuilder newHint = new StringBuilder();
        for (int i = 0; i < wordToGuess.length(); i++) {
            if (marks.get(i) == Mark.CORRECT) {
                newHint.append(attempt.charAt(i));
            }else if (previousHint.charAt(i) != '.') {
                newHint.append(previousHint.charAt(i));
            } else {
                newHint.append('.');
            }
        }
        hint = newHint.toString();
        return hint;
    }

    public String getHint() {
        return hint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feedback feedback = (Feedback) o;
        return Objects.equals(attempt, feedback.attempt) && Objects.equals(marks, feedback.marks) && Objects.equals(hint, feedback.hint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attempt, marks, hint);
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "attempt='" + attempt + '\'' +
                ", marks=" + marks +
                ", hint='" + hint + '\'' +
                '}';
    }
}
