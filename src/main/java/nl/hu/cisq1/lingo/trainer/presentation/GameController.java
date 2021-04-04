package nl.hu.cisq1.lingo.trainer.presentation;

import nl.hu.cisq1.lingo.trainer.application.GameService;
import nl.hu.cisq1.lingo.trainer.presentation.DTO.InputGuessDTO;
import nl.hu.cisq1.lingo.trainer.presentation.DTO.OutputGameDTO;
import nl.hu.cisq1.lingo.trainer.presentation.DTO.OutputGameTurnDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lingo")
public class GameController {
    private final GameService service;

    public GameController(GameService s){
        this.service = s;
    }

    @GetMapping("/start")
    public OutputGameTurnDTO startGame(){
        return service.startNewGame();
    }

    @PostMapping("/guess/{id}")
    public OutputGameTurnDTO guess(@RequestBody InputGuessDTO attempt, @PathVariable Long id){
        System.out.println(attempt.attempt);
        return service.guess(attempt.attempt, id);
    }

    @PostMapping("/turn/{id}")
    public OutputGameTurnDTO startNewTurn(@PathVariable Long id){
        return service.startNewTurn(id);
    }

    @GetMapping("/{id}")
    public OutputGameDTO getGame(@PathVariable Long id){
        return service.getGame(id);
    }
}
