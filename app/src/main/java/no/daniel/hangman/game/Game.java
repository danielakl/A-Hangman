package no.daniel.hangman.game;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import no.daniel.hangman.data.entity.Word;

public final class Game {
    private static Game INSTANCE;

    /**
     * These immutable properties are the same for each round played.
     */
    private final Queue<Word> queue;
    private final int chances;
    private final int rounds;

    /**
     * These mutable properties are changed for each round played.
     */
    private Word currentWord;
    private String displayWord;
    private int chancesLeft;
    private boolean hasWon = false;
    private int roundsPlayed = 0;
    private int roundsWon = 0;

    /**
     * Create a new game session if a session is not already started.
     * Call {@link #close()} close to stop a session in progress.
     * @param words the words to guess.
     * @param chances the number of times the player gets to guess the wrong letter.
     * @return a game session.
     */
    public static Game session(List<Word> words, int chances) {
        if (INSTANCE == null) {
            INSTANCE = new Game(words, chances);
        }
        return INSTANCE;
    }

    private Game(List<Word> words, int chances) {
        this.queue =  new ArrayDeque<>(words);
        this.chances = chances;
        this.rounds = words.size();
        if (!reset()) {
            throw new IllegalArgumentException("The given list of words cannot be empty.");
        }
    }

    /**
     * Used to close the current session.
     */
    public void close() {
        INSTANCE = null;
    }

    /**
     * Make a guess at the word.
     * @param guess the character to guess.
     * @return true if the guessed character is in the word,
     * false if not or out of chances or already won.
     */
    public boolean guess(char guess) {
        boolean correctGuess = false;
        if (chancesLeft > 0 && !hasWon) {
            StringBuilder sb = new StringBuilder(displayWord);
            char[] word = currentWord.content.toCharArray();
            for (int i = 0; i < word.length; i++) {
                if (guess == word[i]) {
                    sb.setCharAt(i, guess);
                    correctGuess = true;
                }
            }
            displayWord = sb.toString();
            if (!displayWord.contains("?")) {
                roundsPlayed++;
                roundsWon++;
                hasWon = true;
            }
            if (!correctGuess) {
                chancesLeft--;
            }
        }
        return correctGuess;
    }

    /**
     * Start the next game if you've run out of guesses or guessed the word.
     * @return this game if you have chances left and haven't guessed the word.
     * Returns a reset version of this game if you ran out of chances or guessed the word.
     * Returns null if there are no more words left in the session to guess at.
     */
    public Game nextGame() {
        if (chancesLeft <= 0 || hasWon) {
            if (!reset()) {
                return null;
            }
        }
        return this;
    }

    /**
     * Get a displayable word, this is the word the player tries to guess.
     * Only guessed letters are unveiled, unknown letters is shown as question marks.
     * @return the display word.
     */
    public String getDisplayWord() {
        return displayWord;
    }

    /**
     * The total number of rounds you can play.
     * @return total number of rounds.
     */
    public int getRounds() {
        return rounds;
    }

    /**
     * The number of rounds played.
     * @return rounds played.
     */
    public int getRoundsPlayed() {
        return roundsPlayed;
    }

    /**
     * The number of rounds won.
     * @return rounds won.
     */
    public int getRoundsWon() {
        return roundsWon;
    }

    /**
     * The number of chances left.
     * @return chances left.
     */
    public int getChancesLeft() {
        return chancesLeft;
    }

    /**
     * True if the user have won the round, false if not.
     * @return true or false.
     */
    public boolean hasWon() {
        return hasWon;
    }

    private boolean reset() {
        this.currentWord = queue.poll();
        if (currentWord == null) {
            return false;
        }
        this.displayWord = initDisplayWord(currentWord.content);
        this.chancesLeft = chances;
        this.hasWon = false;
        return true;
    }

    private static String initDisplayWord(String word) {
        StringBuilder sb = new StringBuilder();
        for (char letter : word.toCharArray()) {
            if (letter == ' ' || letter == '-') {
                sb.append(letter);
            } else {
                sb.append('?');
            }
        }
        return sb.toString();
    }
}
