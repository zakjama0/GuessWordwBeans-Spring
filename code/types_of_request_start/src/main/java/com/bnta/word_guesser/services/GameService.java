package com.bnta.word_guesser.services;
import com.bnta.word_guesser.models.Game;
import com.bnta.word_guesser.models.Guess;
import com.bnta.word_guesser.models.Reply;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
public class GameService {
    private Game game;
    private String currentWord;
    private ArrayList<String> guessedLetters;

    public GameService(){
    }

    public Reply startNewGame(){
        this.game = new Game("hello");
        this.currentWord = "*****";
        this.guessedLetters = new ArrayList<>();
        return new Reply(currentWord, "New game started", false);
    }

    public Reply processGuess(Guess guess){

        // create new Reply object
        Reply reply;

        // Check if game has started
        if (this.game == null) {
            reply = new Reply(
                    this.currentWord,
                    String.format("Game has not been started"),
                    false);
            return reply;
        }

        // check if letter has already been guessed
        if (this.guessedLetters.contains(guess.getLetter())) {
            reply = new Reply(
                    this.currentWord,
                    String.format("Already guessed %s", guess.getLetter()
                    ), false);
            return reply;
        }

        // add letter to previous guesses
        this.guessedLetters.add(guess.getLetter());

        // check for incorrect guess
        if (!game.getWord().contains(guess.getLetter())) {
            reply = new Reply(
                    this.currentWord,
                    String.format("%s is not in the word", guess.getLetter()),
                    false
            );
            return reply;
        }

        // process correct guess
        String runningResult = game.getWord();

        for (Character letter : game.getWord().toCharArray()) {
            if (!this.guessedLetters.contains(letter.toString())) {
                runningResult = runningResult.replace(letter, '*');
            }
        }

        this.currentWord = runningResult;

        reply = new Reply(
                this.currentWord,
                String.format("%s is in the word", guess.getLetter()),
                true)
        ;
        return reply;
    }





    // getters and setters
    public String getCurrentWord() {
        return currentWord;
    }

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public ArrayList<String> getGuessedLetters() {
        return guessedLetters;
    }

    public void setGuessedLetters(ArrayList<String> guessedLetters) {
        this.guessedLetters = guessedLetters;
    }
}
