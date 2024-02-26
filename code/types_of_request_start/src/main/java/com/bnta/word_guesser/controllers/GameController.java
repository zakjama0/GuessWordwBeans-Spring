package com.bnta.word_guesser.controllers;

import com.bnta.word_guesser.models.Game;
import com.bnta.word_guesser.models.Guess;
import com.bnta.word_guesser.models.Reply;
import com.bnta.word_guesser.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping(value = "/games")
public class GameController {

    @Autowired
    GameService gameService;
    // need a dependency here

    @PostMapping
    public ResponseEntity<Reply> newGame(){
    Reply reply = gameService.startNewGame();
    return new ResponseEntity<>(reply, HttpStatus.CREATED);
    }
    // Make sure you post then get
    @GetMapping
    public ResponseEntity<Reply> getGameStatus(){
        Reply reply;
        // check if game has started
        if(gameService.getGame() == null){
            reply = new Reply(gameService.getCurrentWord(), "Game has not started", false);
        }else {
            reply = new Reply(gameService.getCurrentWord(), "Game in progress", false);
        }
        return new ResponseEntity<>(reply, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<Reply> handleGuess (@RequestBody Guess guess) {
        // every request is either header or body
        // return result
        Reply reply = gameService.processGuess(guess);
        return new ResponseEntity<>(reply, HttpStatus.OK);

    }

}
