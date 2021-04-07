package nl.hu.cisq1.lingo.trainer.application;

import nl.hu.cisq1.lingo.CiTestConfiguration;
import nl.hu.cisq1.lingo.trainer.application.exceptions.IdNotFoundException;
import nl.hu.cisq1.lingo.trainer.data.GameRepository;
import nl.hu.cisq1.lingo.trainer.domain.Feedback;
import nl.hu.cisq1.lingo.trainer.domain.Game;
import nl.hu.cisq1.lingo.trainer.domain.GameStatus;
import nl.hu.cisq1.lingo.trainer.domain.Mark;
import nl.hu.cisq1.lingo.trainer.domain.exceptions.InvalidStateException;
import nl.hu.cisq1.lingo.trainer.presentation.dto.OutputGameTurnDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(CiTestConfiguration.class)
class GameServiceIntegrationTest {

    @Autowired
    private GameService gameService;

    @MockBean
    private GameRepository gameRepository;

    @ParameterizedTest
    @MethodSource("provideGuessExamples")
    @DisplayName("guessing word")
    void guessWord(String wordToGuess, String attempt, GameStatus expectedGameStatus, Feedback expectedFeedback, int tryTimes) {
        Game game = new Game();
        game.startNewTurn(wordToGuess);

        when(gameRepository.findById(0L))
                .thenReturn(Optional.of(game));

        OutputGameTurnDTO gameOutput = null;
        for (int i = 0; i < tryTimes; i++) {
            gameOutput = gameService.guess(attempt, 0L);
        }

        assertEquals(expectedGameStatus, gameOutput.gameStatus);
        assertEquals(expectedFeedback, gameOutput.feedbackList.get(0));
    }

    private static Stream<Arguments> provideGuessExamples() {
        //Goede woord is KAASJE
        Feedback f1 = new Feedback("baard", List.of(Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT));
        f1.giveHint(null, "baard");
        Feedback f2 = new Feedback("kaart", List.of(Mark.ABSENT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.ABSENT));
        f2.giveHint(null, "baard");
        Feedback f3 = new Feedback("fout", List.of(Mark.INVALID, Mark.INVALID, Mark.INVALID, Mark.INVALID));
        f3.giveHint(null, "baard");
        return Stream.of(
                Arguments.of("baard", "baard", GameStatus.WON, f1, 1),
                Arguments.of("baard", "kaart", GameStatus.PLAYING, f2, 1),
                Arguments.of("baard", "fout", GameStatus.LOST, f3, 5)
                );
    }

    @Test
    @DisplayName("starting a game")
    void startGame() {
        OutputGameTurnDTO gameOutput = gameService.startNewGame();

        assertEquals(GameStatus.PLAYING, gameOutput.gameStatus);
        assertEquals(5, gameOutput.attemptLeft);
        assertEquals(0, gameOutput.score);
        assertEquals(5, gameOutput.hint.length());
        assertEquals(new ArrayList<>(), gameOutput.feedbackList);
    }

    @Test
    @DisplayName("can't start new round when still playing")
    void stillPlaying() {
        Game game = new Game();
        game.startNewTurn("BAARD");

        when(gameRepository.findById(0L))
                .thenReturn(Optional.of(game));

        assertThrows(InvalidStateException.class, () -> gameService.startNewTurn(0L));
    }

    @Test
    @DisplayName("can't start new round when game lost")
    void roundLost() {
        Game game = new Game();
        game.startNewTurn("BAARD");
        while (game.getCurrentTurn().getAttemptCount() > 5){
            game.makeGuess("FOUT");
        }
        when(gameRepository.findById(0L))
                .thenReturn(Optional.of(game));

        assertThrows(InvalidStateException.class, () -> gameService.startNewTurn(0L));
    }

    @Test
    @DisplayName("throw error with invalid ID request")
    void invalidID() {
        assertThrows(IdNotFoundException.class, () -> gameService.guess("testid", (long) -1));
        assertThrows(IdNotFoundException.class, () -> gameService.startNewTurn((long) -1));
        assertThrows(IdNotFoundException.class, () -> gameService.getGame((long) -1));
    }

    @Test
    @DisplayName("doing an attempt adds feedback")
    void doGuess() {
        Game game = new Game();
        game.startNewTurn("BAARD");

        when(gameRepository.findById(0L))
                .thenReturn(Optional.of(game));

        OutputGameTurnDTO gameOutput = gameService.guess("BAARD", 0L);

        assertEquals(4, gameOutput.attemptLeft);
        assertEquals(1, gameOutput.feedbackList.size());
    }

    @ParameterizedTest
    @MethodSource("provideWordExamples")
    @DisplayName("new turn has other wordLength")
    void newTurn(String word, int previousLength, int exceptedLength){
        Game game = new Game();
        game.startNewTurn(word);
        when(gameRepository.findById(0L))
                .thenReturn(Optional.of(game));

        OutputGameTurnDTO gameOutput = gameService.guess(word, 0L);
        assertEquals(previousLength, gameOutput.hint.length());

        gameOutput = gameService.startNewTurn(0L);
        assertEquals(exceptedLength, gameOutput.hint.length());
    }

    private static Stream<Arguments> provideWordExamples() {
        return Stream.of(
                Arguments.of("BAARD", 5, 6),
                Arguments.of("KAASJE", 6, 7),
                Arguments.of("KOEKJES", 7, 5)
        );
    }
}