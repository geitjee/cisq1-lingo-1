package nl.hu.cisq1.lingo.trainer.domain;

import nl.hu.cisq1.lingo.trainer.domain.exceptions.InvalidFeedbackException;
import nl.hu.cisq1.lingo.trainer.domain.exceptions.InvalidTurnException;
import nl.hu.cisq1.lingo.words.domain.Word;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TurnTest {

    @Test
    @DisplayName("On create attempt count = 0")
    void OnCreateAttemptCountCheck(){
        Turn turn = new Turn("TEST");
        assertEquals(0, turn.getAttemptCount());
    }

    @Test
    @DisplayName("Turn feedback is correct")
    void TurnFeedbackIsCorrect(){
        Turn turn = new Turn("KAASJE");//woord om te raden is baard
        Feedback expectedFeedback = new Feedback("poesen", List.of(Mark.ABSENT, Mark.ABSENT, Mark.PRESENT, Mark.CORRECT, Mark.ABSENT, Mark.ABSENT));
        expectedFeedback.giveHint(null, turn.getWordToGuess());
        assertTrue(expectedFeedback.equals(turn.guessAttempt("poesen")));
        assertEquals(expectedFeedback.hashCode(), turn.guessAttempt("poesen").hashCode());
    }

    @Test
    @DisplayName("guess made as 6th try throws an InvalidTurnException")
    void TrySixTries() {
        Turn turn = new Turn("TEST");
        for (int i = 0; i < 5; i++) {
            turn.guessAttempt("NICE");
        }
        assertThrows(InvalidTurnException.class,
                () -> turn.guessAttempt("FAIL"));
    }

    @Test
    @DisplayName("gets correct hint.")
    void GetHint(){
        Turn turn = new Turn("kaasje");
        assertEquals("k.....", turn.getHint());
        turn.guessAttempt("koekje");
        assertEquals("k...je", turn.getHint());
        turn.guessAttempt("fout");
        assertEquals("k...je", turn.getHint());
        turn.guessAttempt("poesen");
        assertEquals("k..sje", turn.getHint());
        turn.guessAttempt("kaasje");
        assertEquals("kaasje", turn.getHint());
    }

    @Test
    @DisplayName("gets correct feedback list")
    void GetTurnFeedback() {
        Turn turn = new Turn("KAASJE");
        turn.guessAttempt("KOEKJE");
        turn.guessAttempt("FOUT");
        turn.guessAttempt("POESEN");
        turn.guessAttempt("KAASJE");

        Feedback f1 = new Feedback("koekje", List.of(Mark.CORRECT, Mark.ABSENT, Mark.ABSENT, Mark.ABSENT, Mark.CORRECT, Mark.CORRECT));
        f1.giveHint(null, turn.getWordToGuess());
        Feedback f2 = new Feedback("fout", List.of(Mark.INVALID, Mark.INVALID, Mark.INVALID, Mark.INVALID));
        f2.giveHint(f1.getHint(), turn.getWordToGuess());
        Feedback f3 = new Feedback("poesen", List.of(Mark.ABSENT, Mark.ABSENT, Mark.PRESENT, Mark.CORRECT, Mark.ABSENT, Mark.ABSENT));
        f3.giveHint(f2.getHint(), turn.getWordToGuess());
        Feedback f4 = new Feedback("kaasje", List.of(Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT));
        f4.giveHint(f3.getHint(), turn.getWordToGuess());
        List<Feedback> expectedFeedback = List.of(f1, f2, f3, f4);

        assertEquals(expectedFeedback, turn.getFeedbackList());
    }


}