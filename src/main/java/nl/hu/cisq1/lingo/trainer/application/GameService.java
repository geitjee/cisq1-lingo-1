package nl.hu.cisq1.lingo.trainer.application;

import nl.hu.cisq1.lingo.trainer.application.exceptions.InvalidIdException;
import nl.hu.cisq1.lingo.trainer.data.GameRepository;
import nl.hu.cisq1.lingo.trainer.domain.Game;
import nl.hu.cisq1.lingo.trainer.presentation.DTO.OutputGameDTO;
import nl.hu.cisq1.lingo.trainer.presentation.DTO.OutputGameTurnDTO;
import nl.hu.cisq1.lingo.words.application.WordService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class GameService {
    private GameRepository repository;
    private Game game;
    private WordService wordService;

    public GameService(GameRepository repository, WordService wordService) {
        this.repository = repository;
        this.wordService = wordService;
    }

    public OutputGameTurnDTO startNewGame(){
        game = new Game();
        game.startNewTurn(wordService.provideRandomWord(5));
        this.repository.save(game);
        return gameToOutputDTO();
    }

    public OutputGameTurnDTO startNewTurn(Long id){
        game = repository.findById(id).orElseThrow(() -> new InvalidIdException("No game found with id: " + id));
        game.startNewTurn(wordService.provideRandomWord(game.getWordLength()));
        this.repository.save(game);
        return gameToOutputDTO();
    }

    public OutputGameTurnDTO guess(String attempt, Long id){
        System.out.println(attempt + "2");
        game = repository.findById(id).orElseThrow(() -> new InvalidIdException("No game found with id: " + id));
        game.makeGuess(attempt);
        this.repository.save(game);
        return gameToOutputDTO();
    }

    private OutputGameTurnDTO gameToOutputDTO(){
        return new OutputGameTurnDTO(game);
    }

    public OutputGameDTO getGame(Long id){
        return new OutputGameDTO(repository.findById(id).orElseThrow(() -> new InvalidIdException("No game found with id: " + id)));
    }
}
