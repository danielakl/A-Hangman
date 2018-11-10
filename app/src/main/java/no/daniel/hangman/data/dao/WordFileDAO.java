package no.daniel.hangman.data.dao;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import no.daniel.hangman.App;
import no.daniel.hangman.R;
import no.daniel.hangman.data.entity.Word;
import no.daniel.hangman.util.Language;

public class WordFileDAO {
    private static final String TAG = "MyApp - WordFileDAO";

    public List<Word> get(Language language, int limit) {
        try (InputStream in = App.getContext().getResources().openRawResource(R.raw.wordlist);
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            List<Word> words = new ArrayList<>();

            // Read file
            String line = br.readLine(); // Skip CSV header line.
            line = br.readLine();
            while (line != null) {
                String[] values = line.split(",");
                // Populate words list.
                switch (values[0]) {
                    case "english":
                        if (language == Language.English) {
                            words.add(new Word(values[1], Language.English));
                        }
                        break;
                    case "norsk":
                        if (language == Language.Norsk) {
                            words.add(new Word(values[1], Language.Norsk));
                        }
                        break;
                }
                if (limit != 0 && words.size() >= limit) break;
                line = br.readLine();
            }
            return words;
        } catch (IOException | NullPointerException e) {
            Log.e(TAG, e.toString());
        }
        return new ArrayList<>();
    }
}
