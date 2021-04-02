package nl.hu.cisq1.lingo.trainer.domain;


import nl.hu.cisq1.lingo.trainer.domain.exceptions.InvalidTurnException;
import nl.hu.cisq1.lingo.words.domain.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Turn {
    private Word wordToGuess;
    private Integer attemptCount;

    private List<Feedback> feedbackList = new ArrayList<Feedback>();

    public Turn(Word wordToGuess) {
        this.wordToGuess = wordToGuess;
        this.attemptCount = 0;
    }

    public Feedback guessAttempt(String attempt) {
        attempt = attempt.toUpperCase();
        if (attemptCount >= 5) {//Error gooien ofzo
            throw new InvalidTurnException("You already tried 5 times and lost the game, please start a new game.");
        }
        Feedback feedback = new Feedback(attempt, getMarks(attempt));
        feedback.giveHint(getHint(), wordToGuess.getValue());
        feedbackList.add(feedback);
        attemptCount++;
        return feedback;
    }

    public Integer getAttemptCount() {
        return attemptCount;
    }

    public List<Feedback> getFeedbackList() {
        return feedbackList;
    }

    public Word getWordToGuess() {
        return wordToGuess;
    }

    public String getHint(){
        if (feedbackList.isEmpty()){
            return wordToGuess.getValue().charAt(0) + ".".repeat(wordToGuess.getLength()-1);
        }
        return feedbackList.get(feedbackList.size()-1).getHint();
    }

    private List<Mark> getMarks(String attempt){
        List<Mark> marks = new ArrayList<>();
        for (int i = 0; i < attempt.length(); i++) {
            if (attempt.length() != wordToGuess.getLength()){
                marks.add(Mark.INVALID);
            } else if (attempt.charAt(i) == wordToGuess.getValue().charAt(i)){
                marks.add(Mark.CORRECT);
            } else if (wordToGuess.getValue().contains(String.valueOf(attempt.charAt(i)))){
                marks.add(Mark.PRESENT);
            } else{
                marks.add(Mark.ABSENT);
            }
        }

        for (int i = 0; i < marks.size(); i++) { //gehele loop functie om te controleren of de PRESENT correct is
            if (marks.get(i) == Mark.PRESENT){
                char presentChar = attempt.charAt(i);
                int presentInAttempt = 0;
                int presentInWord = 0;
                for (int j = 0; j < attempt.length(); j++) {
                    if (attempt.charAt(j) == presentChar){
                        if (marks.get(j) != Mark.CORRECT){
                            presentInAttempt++;
                        }
                    }
                }
                for (int j = 0; j < wordToGuess.getLength(); j++) {
                    if (wordToGuess.getValue().charAt(j) == presentChar){
                        if (marks.get(j) != Mark.CORRECT){
                            presentInWord++;
                        }
                    }
                }
                if (presentInWord >= 1){
                    if (presentInAttempt == presentInWord){
                        continue;
                    }else if (presentInAttempt > presentInWord){
                        int count = 0;
                        for (int j = 0; j < attempt.length(); j++) {
                            if (attempt.charAt(j) == presentChar){
                                count++;
                                if (count > presentInWord){
                                    marks.set(j, Mark.ABSENT);
                                }
                            }
                        }
                    }
                }else{
                    marks.set(i, Mark.ABSENT);
                }
            }
        }

        return marks;
    }


}
