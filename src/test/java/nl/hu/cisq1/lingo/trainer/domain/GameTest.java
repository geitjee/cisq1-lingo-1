package nl.hu.cisq1.lingo.trainer.domain;

import nl.hu.cisq1.lingo.trainer.domain.exceptions.InvalidStateException;
import nl.hu.cisq1.lingo.trainer.domain.exceptions.InvalidTurnException;
import nl.hu.cisq1.lingo.words.domain.Word;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    @DisplayName("before starting a turn, turn is null, turnHistory is empty, game state is STARTING and throws error when making a guess")
    void TurnBeforeGameStart() {
        Game game = new Game();
        assertNull(game.getCurrentTurn());
        assertEquals(new ArrayList<Turn>(), game.getTurnList());
        assertEquals(GameStatus.STARTING, game.getStatus());
        assertThrows(InvalidStateException.class, () -> game.makeGuess("BAARD"));
    }

    @Test
    @DisplayName("can't start a new turn twice in a row")
    void StartGameTurnTwice() {
        Game game = new Game();
        game.startNewTurn(new Word("BAARD"));
        assertThrows(InvalidStateException.class, () -> game.startNewTurn(new Word("BAARS")));
    }

    @Test
    @DisplayName("After 5 tries the gameState is lost, you can't start a new turn and can't make another guess")
    void LoseGameTest() {
        Game game = new Game();
        game.startNewTurn(new Word("BAARD"));
        game.makeGuess("SNOEP");
        game.makeGuess("SNOEP");
        game.makeGuess("SNOEP");
        game.makeGuess("SNOEP");
        game.makeGuess("SNOEP");
        assertEquals(GameStatus.LOST, game.getStatus());
        assertThrows(InvalidStateException.class, () -> game.startNewTurn(new Word("BAARS")));
        assertThrows(InvalidStateException.class, () -> game.makeGuess("SNOEP"));
    }

    @ParameterizedTest
    @MethodSource("provideWordExamples")
    @DisplayName("After winning the wordlenght increases 1 and if the lenght was 7 it goes to 5")
    void WordLenghtIncreases(String word, int currentLenght, int expectedLenght) {
        Game game = new Game();
        game.startNewTurn(new Word(word));
        assertEquals(currentLenght, game.getWordLength());
        game.makeGuess(word);
        assertEquals(expectedLenght, game.getWordLength());
    }

    private static Stream<Arguments> provideWordExamples() {
        return Stream.of(
                Arguments.of("BAARD", 5, 6),
                Arguments.of("KAASJE", 6, 7),
                Arguments.of("KOEKJES", 7, 5)
        );
    }

    @ParameterizedTest
    @MethodSource("provideScoreExamples")
    @DisplayName("After winning the wordlenght increases 1 and if the lenght was 7 it goes to 5")
    void ScoreIncreasesTest(String word, int attempts, int expectedScore) {
        Game game = new Game();
        game.startNewTurn(new Word(word));
        for (int i = 0; i < attempts-1; i++) {
            game.makeGuess("FOUT");
        }
        game.makeGuess(word);
        assertEquals(expectedScore, game.getScore());
    }

    private static Stream<Arguments> provideScoreExamples() {
        return Stream.of(
                Arguments.of("BAARD", 1, 25),
                Arguments.of("BAARD", 2, 20),
                Arguments.of("BAARD", 3, 15),
                Arguments.of("BAARD", 4, 10),
                Arguments.of("BAARD", 5, 5)
                );
    }
}