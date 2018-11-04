package no.daniel.hangman.game;

import android.content.SharedPreferences;

import java.util.Collections;
import java.util.List;

import no.daniel.hangman.data.dao.WordDAO;
import no.daniel.hangman.data.entity.Word;
import no.daniel.hangman.util.Language;

public final class GameManager {
    private static GameManager INSTANCE;
    private List<Word> words;
    private Language language;

    /**
     * Creates an instance of GameManager or returns an existing instance.
     * @param preferences SharedPreferences used to get current language.
     * @return an instance of GameManager.
     */
    public static GameManager initialize(SharedPreferences preferences) {
        if (INSTANCE == null) {
            int index = preferences.getInt("list_language", 0);
            INSTANCE = new GameManager(Language.values()[index]);
        }
        return INSTANCE;
    }

    private GameManager(Language language) {
        this.words = getWords(language);
        this.language = language;
    }

    /**
     * Change the language a game uses, any ongoing games must be closed and
     * recreated to take effect. Use the game's close function, then create a
     * new one with {@link #createGame()}.
     * @param language the language a game will use.
     */
    public void setLanguage(Language language) {
        if (this.language != language) {
            this.language = language;
            words = getWords(language);
        }
    }

    /**
     * Create a game session.
     * @return a Game session.
     */
    public Game createGame() {
        Collections.shuffle(words);
        return Game.session(words, 11);
    }

    private static List<Word> getWords(Language language) {
        return new WordDAO().get(language.name().toLowerCase());
    }
}
