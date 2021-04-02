package nl.hu.cisq1.lingo.trainer.domain;

import nl.hu.cisq1.lingo.trainer.domain.exceptions.InvalidFeedbackException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FeedbackTest {

    @Test
    @DisplayName("word is guessed if all letters are correct")
    void wordIsGuessed() {
        Feedback feedback = new Feedback("woord", List.of(Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT));
        assertTrue(feedback.isWordGuessed());
    }

    @Test
    @DisplayName("word is not guessed if a letter isn't correct")
    void wordIsNotGuessed() {
        Feedback feedback = new Feedback("woord", List.of(Mark.ABSENT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT));
        assertFalse(feedback.isWordGuessed());
    }

    @Test
    @DisplayName("guess is not valid")
    void guessIsInvalid() {
        Feedback feedback = new Feedback("woord", List.of(Mark.INVALID, Mark.INVALID, Mark.INVALID, Mark.INVALID, Mark.INVALID));
        assertFalse(feedback.isGuessValid());
    }

    @Test
    @DisplayName("guess is valid")
    void guessIsNotInvalid() {
        Feedback feedback = new Feedback("woord", List.of(Mark.ABSENT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT));
        assertTrue(feedback.isGuessValid());
    }

    @Test
    @DisplayName("guess length doesn't match feedback length")
    void invalidFeedback() {
        assertThrows(InvalidFeedbackException.class,
                () -> new Feedback("woord", List.of(Mark.CORRECT)));
    }

    @ParameterizedTest
    @MethodSource("provideHintExamples")
    @DisplayName("hint feedback is correct")
    void hintIsCorrect(String previousHint, Feedback feedback, String expectedHint) {
        assertEquals(expectedHint, feedback.giveHint(previousHint, "KAASJE"));
    }

    //Hint source
    private static Stream<Arguments> provideHintExamples() {
        //Goede woord is KAASJE
        Feedback f1 = new Feedback("KOEKJE", List.of(Mark.CORRECT, Mark.ABSENT, Mark.ABSENT, Mark.ABSENT, Mark.CORRECT, Mark.CORRECT));
        Feedback f2 = new Feedback("FOUT", List.of(Mark.INVALID, Mark.INVALID, Mark.INVALID, Mark.INVALID));
        Feedback f3 = new Feedback("POESEN", List.of(Mark.ABSENT, Mark.ABSENT, Mark.PRESENT, Mark.CORRECT, Mark.ABSENT, Mark.ABSENT));
        Feedback f4 = new Feedback("KAASJE", List.of(Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT));
        return Stream.of(
                Arguments.of("K.....", f1, "K...JE"),
                Arguments.of("K...JE", f2, "K...JE"),
                Arguments.of("K...JE", f3, "K..SJE"),
                Arguments.of("K..SJE", f4, "KAASJE")
        );
    }

    @Test
    @DisplayName("get hint without providing previous hint")
    void hintWithoutPrevious() {
        Feedback feedback = new Feedback("KAATS", List.of(Mark.ABSENT, Mark.CORRECT, Mark.CORRECT, Mark.ABSENT, Mark.ABSENT));
        String expectedHint = "BAA..";
        assertEquals(expectedHint, feedback.giveHint("", "BAARD"));
        assertEquals(expectedHint, feedback.giveHint(null, "BAARD"));
    }

    @Test
    @DisplayName("get hint with invalid guess")
    void hintInvalidGuess() {
        Feedback feedback = new Feedback("FOUT", List.of(Mark.INVALID, Mark.INVALID, Mark.INVALID, Mark.INVALID));
        String expectedHint = "B....";
        assertEquals(expectedHint, feedback.giveHint(null, "BAARD"));
    }

    @Test
    @DisplayName("get hint with previous hint not having the same lenght as word to guess")
    void getHintInvalidPrevious() {
        Feedback feedback = new Feedback("KAATS", List.of(Mark.ABSENT, Mark.CORRECT, Mark.CORRECT, Mark.ABSENT, Mark.ABSENT));
        String expectedHint = "BAA..";
        assertEquals(expectedHint, feedback.giveHint("B..", "BAARD"));
    }

    @Test
    @DisplayName("test to cover feedback equals() function")
    void coverEquals(){
        Feedback feedback = new Feedback("KAATS", List.of(Mark.ABSENT, Mark.CORRECT, Mark.CORRECT, Mark.ABSENT, Mark.ABSENT));
        assertTrue(feedback.equals(feedback));
        assertFalse(feedback.equals(null));
        assertFalse(feedback.equals(Mark.CORRECT));
    }
}