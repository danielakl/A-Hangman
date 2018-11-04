package no.daniel.hangman.data.dao;

import java.util.ArrayList;
import java.util.List;

import no.daniel.hangman.data.entity.Word;

public class WordDAO implements DAO<Word> {
    @Override
    public Word get(Object identifier) {
        return null;
    }

    @Override
    public List<Word> get(String filter) {
        return new ArrayList<>();
    }

    @Override
    public Word create(Word entity) {
        return null;
    }

    @Override
    public Word update(Word updated) {
        return null;
    }

    @Override
    public long delete() {
        return 0;
    }

    @Override
    public boolean delete(Word entity) {
        return false;
    }
}
