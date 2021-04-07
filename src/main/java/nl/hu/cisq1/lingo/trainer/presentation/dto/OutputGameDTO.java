package nl.hu.cisq1.lingo.trainer.presentation.dto;

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
    public List<List<Feedback>> history= new ArrayList<>();

    public OutputGameDTO(Game game) {
        this.id = game.getId();
        this.score = game.getScore();
        this.gameStatus = game.getStatus();
        for (Turn t: game.getTurnList()) {
            List<Feedback> feedbackDTOS = new ArrayList<>();
            for (Feedback f: t.getFeedbackList()) {
                feedbackDTOS.add(f);
            }
            history.add(feedbackDTOS);
        }
    }
}
