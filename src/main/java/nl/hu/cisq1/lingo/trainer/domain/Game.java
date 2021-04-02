package nl.hu.cisq1.lingo.trainer.domain;

import nl.hu.cisq1.lingo.trainer.domain.exceptions.InvalidStateException;
import nl.hu.cisq1.lingo.words.domain.Word;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private int score;
    private int wordLength;
    private GameStatus status;

    private Turn currentTurn;
    private List<Turn> turnList;

    public Game() {
        this.score = 0;
        this.status = GameStatus.STARTING;
        this.currentTurn = null;
        this.turnList = new ArrayList<Turn>();
    }
    public void startNewTurn(Word word){
        if (status == GameStatus.STARTING || status == GameStatus.WON){
            this.wordLength = word.getLength();
            currentTurn = new Turn(word);
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
                turnList.add(currentTurn);
                status = GameStatus.WON;
                increaseWordLength();
            }else if (!feedback.isWordGuessed() && currentTurn.getAttemptCount() >= 5){
                turnList.add(currentTurn);
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
}
