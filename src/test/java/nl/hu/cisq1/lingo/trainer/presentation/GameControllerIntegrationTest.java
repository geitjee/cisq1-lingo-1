package nl.hu.cisq1.lingo.trainer.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import nl.hu.cisq1.lingo.CiTestConfiguration;
import nl.hu.cisq1.lingo.trainer.application.exceptions.IdNotFoundException;
import nl.hu.cisq1.lingo.trainer.data.GameRepository;
import nl.hu.cisq1.lingo.trainer.domain.Game;
import nl.hu.cisq1.lingo.trainer.domain.exceptions.InvalidStateException;
import nl.hu.cisq1.lingo.trainer.presentation.dto.InputGuessDTO;
import nl.hu.cisq1.lingo.words.data.SpringWordRepository;
import nl.hu.cisq1.lingo.words.domain.Word;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(CiTestConfiguration.class)
@AutoConfigureMockMvc
class GameControllerIntegrationTest {

    @MockBean
    private GameRepository gameRepository;

    @MockBean
    private SpringWordRepository wordRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("starting a game")
    void startGame() throws Exception {
        when(wordRepository.findRandomWordByLength(5))
                .thenReturn(Optional.of(new Word("BAARD")));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/lingo/start");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameStatus", is("PLAYING")))
                .andExpect(jsonPath("$.attemptLeft", is(5)))
                .andExpect(jsonPath("$.hint", is("b....")))
                .andExpect(jsonPath("$.feedbackList", is(new ArrayList())));
    }

    @Test
    @DisplayName("make guess")
    void makeGuess() throws Exception {
        Game game = new Game();
        game.startNewTurn("baard");

        when(gameRepository.findById(0L))
                .thenReturn(Optional.of(game));
        when(wordRepository.findRandomWordByLength(5))
                .thenReturn(Optional.of(new Word("baard")));


        //de gevonden manier om een requestbody mee te sturen :/
        InputGuessDTO inputGuessDTO = new InputGuessDTO();
        inputGuessDTO.attempt = "kaart";
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(inputGuessDTO);


        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/lingo/guess/0").contentType(MediaType.APPLICATION_JSON)
                .content(requestJson);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameStatus", is("PLAYING")))
                .andExpect(jsonPath("$.attemptLeft", is(4)))
                .andExpect(jsonPath("$.hint", is("baar.")))
                .andExpect(jsonPath("$.feedbackList", hasSize(1)));
    }

    @Test
    @DisplayName("make correct guess")
    void makeCorrectGuess() throws Exception {
        Game game = new Game();
        game.startNewTurn("baard");

        when(gameRepository.findById(0L))
                .thenReturn(Optional.of(game));
        when(wordRepository.findRandomWordByLength(5))
                .thenReturn(Optional.of(new Word("baard")));


        //de gevonden manier om een requestbody mee te sturen :/
        InputGuessDTO inputGuessDTO = new InputGuessDTO();
        inputGuessDTO.attempt = "baard";
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(inputGuessDTO);


        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/lingo/guess/0").contentType(MediaType.APPLICATION_JSON)
                .content(requestJson);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameStatus", is("WON")))
                .andExpect(jsonPath("$.attemptLeft", is(4)))
                .andExpect(jsonPath("$.hint", is("baard")))
                .andExpect(jsonPath("$.feedbackList", hasSize(1)));
    }

    @Test
    @DisplayName("can't guess if game is lost")
    void makeGuessLost() throws Exception {
        Game game = new Game();
        game.startNewTurn("baard");
        while (game.getCurrentTurn().getAttemptCount() <= 4) {
            game.makeGuess("fout");
        }

        when(gameRepository.findById(0L))
                .thenReturn(Optional.of(game));
        when(wordRepository.findRandomWordByLength(5))
                .thenReturn(Optional.of(new Word("baard")));


        //de gevonden manier om een requestbody mee te sturen :/
        InputGuessDTO inputGuessDTO = new InputGuessDTO();
        inputGuessDTO.attempt = "fout";
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(inputGuessDTO);


        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/lingo/guess/0").contentType(MediaType.APPLICATION_JSON)
                .content(requestJson);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> assertTrue(mvcResult.getResolvedException() instanceof InvalidStateException))
                .andExpect(mvcResult -> assertEquals("Can't make a guess the game has been lost create a new game", mvcResult.getResolvedException().getMessage()));
    }

    @Test
    @DisplayName("start new turn")
    void startTurn() throws Exception {
        Game game = new Game();
        game.startNewTurn("baard");
        game.makeGuess("baard");
        when(gameRepository.findById(0L))
                .thenReturn(Optional.of(game));
        when(wordRepository.findRandomWordByLength(5))
                .thenReturn(Optional.of(new Word("baard")));
        when(wordRepository.findRandomWordByLength(6))
                .thenReturn(Optional.of(new Word("koekje")));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/lingo/turn/0");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameStatus", is("PLAYING")))
                .andExpect(jsonPath("$.attemptLeft", is(5)))
                .andExpect(jsonPath("$.hint", is("k.....")))
                .andExpect(jsonPath("$.feedbackList", is(new ArrayList())));
    }

    @Test
    @DisplayName("cant start turn when playing")
    void startPlayingTurn() throws Exception {
        Game game = new Game();
        game.startNewTurn("woord");

        when(gameRepository.findById(0L))
                .thenReturn(Optional.of(game));
        when(wordRepository.findRandomWordByLength(5))
                .thenReturn(Optional.of(new Word("BAARD")));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/lingo/turn/0");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> assertTrue(mvcResult.getResolvedException() instanceof InvalidStateException))
                .andExpect(mvcResult -> assertEquals("A turn is already started, finish the turn before starting a new one", mvcResult.getResolvedException().getMessage()));
    }

    @Test
    @DisplayName("cant start turn if game doesn't exist")
    void startNonExistGame() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/lingo/turn/0");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertTrue(mvcResult.getResolvedException() instanceof IdNotFoundException))
                .andExpect(mvcResult -> assertEquals("No game found with id: 0", mvcResult.getResolvedException().getMessage()));
    }

    @Test
    @DisplayName("cant start turn if game is lost")
    void startLostGame() throws Exception {
        Game game = new Game();
        game.startNewTurn("woord");
        while (game.getCurrentTurn().getAttemptCount() <= 4) {
            game.makeGuess("fout");
        }

        when(gameRepository.findById(0L))
                .thenReturn(Optional.of(game));
        when(wordRepository.findRandomWordByLength(5))
                .thenReturn(Optional.of(new Word("BAARD")));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/lingo/turn/0");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> assertTrue(mvcResult.getResolvedException() instanceof InvalidStateException))
                .andExpect(mvcResult -> assertEquals("The game is over because you lost, start a new game", mvcResult.getResolvedException().getMessage()));
    }

    @Test
    @DisplayName("get game info with id")
    void getGame() throws Exception {
        Game game = new Game();
        game.startNewTurn("woord");
        game.makeGuess("fout");
        when(gameRepository.findById(0L))
                .thenReturn(Optional.of(game));
        when(wordRepository.findRandomWordByLength(5))
                .thenReturn(Optional.of(new Word("BAARD")));


        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/lingo/0");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameStatus", is("PLAYING")))
                .andExpect(jsonPath("$.score", is(0)))
                .andExpect(jsonPath("$.history", hasSize(1)));
    }

    @Test
    @DisplayName("get game info with invalid id")
    void getNonExistingGame() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/lingo/0");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertTrue(mvcResult.getResolvedException() instanceof IdNotFoundException))
                .andExpect(mvcResult -> assertEquals("No game found with id: 0", mvcResult.getResolvedException().getMessage()));
    }
}