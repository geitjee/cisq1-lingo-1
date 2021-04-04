package nl.hu.cisq1.lingo.trainer.presentation.DTO;

import nl.hu.cisq1.lingo.trainer.domain.Feedback;
import nl.hu.cisq1.lingo.trainer.domain.Game;
import nl.hu.cisq1.lingo.trainer.domain.GameStatus;
import nl.hu.cisq1.lingo.trainer.domain.Turn;

import java.util.ArrayList;
import java.util.List;

public class OutputGameDTO {
    public Long id;
    public int score;
    public GameStatus gameStatus;
    public List<List<OutputFeedbackDTO>> history= new ArrayList<>();

    public OutputGameDTO(Game game) {
        this.id = game.getId();
        this.score = game.getScore();
        this.gameStatus = game.getStatus();
        for (Turn t: game.getTurnList()) {
            List<OutputFeedbackDTO> feedbackDTOS = new ArrayList<>();
            for (Feedback f: t.getFeedbackList()) {
                feedbackDTOS.add(new OutputFeedbackDTO(f.getAttempt(), f.getMarks(), f.getHint()));
            }
            history.add(feedbackDTOS);
        }
    }
}
