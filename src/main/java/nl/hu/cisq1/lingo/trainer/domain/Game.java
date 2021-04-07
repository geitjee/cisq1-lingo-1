package nl.hu.cisq1.lingo.trainer.domain;

import nl.hu.cisq1.lingo.trainer.domain.exceptions.InvalidStateException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private int score = 0;

    @Column
    private int wordLength;

    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.STARTING;

    @OneToOne(cascade = CascadeType.ALL)
    private Turn currentTurn;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Turn> turnList = new ArrayList<>();

    public void startNewTurn(String word){
        if (status == GameStatus.STARTING || status == GameStatus.WON){
            this.wordLength = word.length();
            currentTurn = new Turn(word);
            turnList.add(currentTurn);
            status = GameStatus.PLAYING;
        }else if (status == GameStatus.PLAYING){
            throw new InvalidStateException("A turn is already started, finish the turn before starting a new one");
        }else{
            throw new InvalidStateException("The game is over because you lost, start a new game");
        }
    }

    public GameStatus makeGuess(String attempt){
        if (status == GameStatus.PLAYING){
            Feedback feedback = currentTurn.guessAttempt(attempt);
            if (feedback.isWordGuessed()){
                addScore(currentTurn.getAttemptCount());
                status = GameStatus.WON;
                increaseWordLength();
            }else if (!feedback.isWordGuessed() && currentTurn.getAttemptCount() >= 5){
                status = GameStatus.LOST;
            } else{
                status = GameStatus.PLAYING;
            }
            return status;
        }else{
            if (status == GameStatus.LOST){
                throw new InvalidStateException("Can't make a guess the game has been lost create a new game");
            }else{
                throw new InvalidStateException("Start a turn before doing a guess");
            }
        }
    }

    private void increaseWordLength(){
        if (wordLength >= 7){
            wordLength = 5;
        }else {
            wordLength++;
        }
    }

    private void addScore(int attemptNumber){
        score += 5 * (5 - attemptNumber) + 5;
    }

    public int getScore() {
        return score;
    }

    public Turn getCurrentTurn() {
        return currentTurn;
    }

    public List<Turn> getTurnList() {
        return turnList;
    }

    public int getWordLength() {
        return wordLength;
    }

    public GameStatus getStatus() {
        return status;
    }

    public Long getId() {
        return id;
    }
}
