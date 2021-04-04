package nl.hu.cisq1.lingo.trainer.presentation.DTO;

import nl.hu.cisq1.lingo.trainer.domain.Mark;

import java.util.List;

public class OutputFeedbackDTO {
    public String guess;
    public List<Mark> marks;
    public String hint;

    public OutputFeedbackDTO(String guess, List<Mark> marks, String hint) {
        this.guess = guess;
        this.marks = marks;
        this.hint = hint;
    }
}
