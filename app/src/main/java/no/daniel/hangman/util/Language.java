package no.daniel.hangman.util;

public enum Language {
    English(""), Norsk("æøå");

    public final String specChars;

    Language(String specialCharacters) {
        this.specChars = specialCharacters;
    }
}
