package no.daniel.hangman.data.dao;

import java.util.ArrayList;
import java.util.List;

import no.daniel.hangman.data.entity.Word;
import no.daniel.hangman.util.Language;

public class WordDAO {
    public List<Word> get(Language language, int limit) {
        List<Word> engWords = new ArrayList<>();
        List<Word> norWords = new ArrayList<>();

        engWords.add(new Word("inspissate", Language.English));
        engWords.add(new Word("underperformances", Language.English));
        engWords.add(new Word("tiniest", Language.English));
        engWords.add(new Word("stirrup cup", Language.English));
        engWords.add(new Word("ignorant", Language.English));
        engWords.add(new Word("milewide", Language.English));
        engWords.add(new Word("atchievement", Language.English));
        engWords.add(new Word("ambulates", Language.English));
        engWords.add(new Word("appropriate", Language.English));
        engWords.add(new Word("caltha", Language.English));

        norWords.add(new Word("bjørn", Language.Norsk));
        norWords.add(new Word("luft", Language.Norsk));
        norWords.add(new Word("havn", Language.Norsk));
        norWords.add(new Word("farvel", Language.Norsk));
        norWords.add(new Word("lærer", Language.Norsk));
        norWords.add(new Word("fjord", Language.Norsk));
        norWords.add(new Word("fisk", Language.Norsk));
        norWords.add(new Word("abonnent", Language.Norsk));
        norWords.add(new Word("fengsel", Language.Norsk));
        norWords.add(new Word("hund", Language.Norsk));

        switch (language) {
            default:
            case English:
                return engWords;
            case Norsk:
                return norWords;
        }
    }

    public Word create(Word... entity) {
        return null;
    }

    public long delete() {
        return 0;
    }

    public boolean delete(Word entity) {
        return false;
    }
}
