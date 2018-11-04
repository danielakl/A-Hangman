package no.daniel.hangman.data.entity;

import androidx.annotation.NonNull;

import no.daniel.hangman.util.Language;

public class Word implements Entity {
    public final String content;
    public final Language language;

    public Word(@NonNull String word, Language language) {
        content = cleanWord(word, language.specChars);
        this.language = language;
    }

    private static String cleanWord(@NonNull String word, String specialCharacters) {
        return word.trim().toLowerCase().replaceAll("[-_]", " ").replaceAll("[^a-z " + specialCharacters + "]", "");
    }
}
