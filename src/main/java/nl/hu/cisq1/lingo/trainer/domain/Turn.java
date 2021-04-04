package nl.hu.cisq1.lingo.trainer.domain;


import nl.hu.cisq1.lingo.trainer.domain.exceptions.InvalidTurnException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Turn {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String wordToGuess;
    @Column
    private Integer attemptCount;
    @JoinColumn
    @OneToMany(cascade = CascadeType.ALL)
    private List<Feedback> feedbackList = new ArrayList<>();


    public Turn(String wordToGuess) {
        this.wordToGuess = wordToGuess.toLowerCase();
        this.attemptCount = 0;
    }

    public Turn() {

    }

    public Feedback guessAttempt(String attempt) {
        attempt = attempt.toLowerCase();
        if (attemptCount >= 5) {
            throw new InvalidTurnException("You already tried 5 times and lost the game, please start a new game.");
        }
        Feedback feedback = new Feedback(attempt, getMarks(attempt));
        feedback.giveHint(getHint(), wordToGuess);
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

    public String getWordToGuess() {
        return wordToGuess;
    }

    public String getHint(){
        if (feedbackList.isEmpty()){
            return wordToGuess.charAt(0) + ".".repeat(wordToGuess.length()-1);
        }
        return feedbackList.get(feedbackList.size()-1).getHint();
    }

    private List<Mark> getMarks(String attempt){
        System.out.println(wordToGuess + ", " + attempt);
        List<Mark> marks = new ArrayList<>();
        for (int i = 0; i < attempt.length(); i++) {
            if (attempt.length() != wordToGuess.length()){
                marks.add(Mark.INVALID);
            } else if (attempt.charAt(i) == wordToGuess.charAt(i)){
                marks.add(Mark.CORRECT);
            } else if (wordToGuess.contains(String.valueOf(attempt.charAt(i)))){
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
                    if (attempt.charAt(j) == presentChar && marks.get(j) != Mark.CORRECT) {
                        presentInAttempt++;
                    }
                }
                for (int j = 0; j < wordToGuess.length(); j++) {
                    if (wordToGuess.charAt(j) == presentChar && marks.get(j) != Mark.CORRECT) {
                        presentInWord++;
                    }
                }
                if (presentInWord >= 1){
                    if (presentInAttempt > presentInWord){
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
