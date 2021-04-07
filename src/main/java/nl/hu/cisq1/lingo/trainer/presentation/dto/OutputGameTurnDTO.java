package nl.hu.cisq1.lingo.trainer.presentation.dto;

import nl.hu.cisq1.lingo.trainer.domain.Feedback;
import nl.hu.cisq1.lingo.trainer.domain.Game;
import nl.hu.cisq1.lingo.trainer.domain.GameStatus;

import java.util.ArrayList;
import java.util.List;

public class OutputGameTurnDTO {
    public Long id;
    public int score;
    public List<Feedback> feedbackList = new ArrayList<>();
    public String hint;
    public GameStatus gameStatus;
    public int attemptLeft;

    public OutputGameTurnDTO(Game game) {
        this.id = game.getId();
        this.score = game.getScore();
        game.getCurrentTurn().getFeedbackList().forEach((feedback) -> feedbackList.add(feedback));
        this.hint = game.getCurrentTurn().getHint();
        this.gameStatus = game.getStatus();
        this.attemptLeft = 5 - game.getCurrentTurn().getAttemptCount();
    }
}
