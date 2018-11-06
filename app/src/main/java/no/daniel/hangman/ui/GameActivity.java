package no.daniel.hangman.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.NavUtils;

import no.daniel.hangman.R;
import no.daniel.hangman.game.Game;
import no.daniel.hangman.game.GameManager;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends FullscreenActivity {
    private static GameManager gameManager;
    private Game game;
    private String langIndex;

    private TextView wordView;
    private TextView roundsView;
    private TextView roundsWonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        visible = false;
        contentView = findViewById(R.id.fullscreen_content);
        wordView = findViewById(R.id.word_view);
        roundsView = findViewById(R.id.rounds_view);
        roundsWonView = findViewById(R.id.rounds_won_view);

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(view -> toggle());

        hide();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Setup language.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String language = preferences.getString("list_language", "0");
        if (langIndex == null || !langIndex.equals(language)) {
            langIndex = language;

            // Setup keyboard.
            int index = (langIndex == null) ? 0 : Integer.parseInt(langIndex);
            int[] keyboards = {R.layout.keyboard_english, R.layout.keyboard_norwegian};

            // Reset keyboard
            View keyboard = findViewById(R.id.keyboard);
            if (keyboard != null) {
                ((ViewGroup) keyboard.getParent()).removeView(keyboard);
            }
            LayoutInflater.from(this).inflate(keyboards[index % keyboards.length], (ViewGroup) contentView);

            // Initialize game logic.
            gameManager = GameManager.initialize(preferences);
            game = gameManager.createGame();
            roundsView.setText(getResources().getString(R.string.rounds_progress, game.getRoundsPlayed(), game.getRounds()));
            roundsWonView.setText(getResources().getString(R.string.rounds_won, game.getRoundsWon()));
            updateWord();
        }
    }

    /**
     * Called when back button is pressed.
     */
    @Override
    public void onBackPressed() {
        langIndex = null;
        game.close();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        langIndex = null;
        game.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button.
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_settings:
                startSettings();
                return true;
            case R.id.menu_exit:
                exit();
            default:
                return false;
        }
    }

    /**
     * Called by buttons in the custom keyboard.
     * @param view the pressed key.
     */
    public void onKeyPress(View view) {
        if (game.getChancesLeft() > 0 && !game.hasWon()) {
            Button button = (Button) view;
            button.animate().alpha(0.0f).setDuration(200).setStartDelay(100);
            button.animate().scaleX(0.15f).scaleY(0.15f).setDuration(400).setStartDelay(100).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    button.setVisibility(View.GONE);
                }
            }).start();
            if (game.guess(button.getText().charAt(0))) {
                updateWord();
            }
            roundsView.setText(getResources().getString(R.string.rounds_progress, game.getRoundsPlayed(), game.getRounds()));
            roundsWonView.setText(getResources().getString(R.string.rounds_won, game.getRoundsWon()));
            // TODO: checkWin method -> Check game.hasWon(), if true play sound byte, maybe animation, then start next round.
        }
    }

    private void updateWord() {
        StringBuilder sb = new StringBuilder();
        String word = game.getDisplayWord();
        for (char letter : word.toCharArray()) {
            if (letter != '?') {
                sb.append(letter);
            } else {
                sb.append('_');
            }
        }
        wordView.setText(sb.toString());
    }

    private void startSettings() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }

    private void exit() {
        finishAndRemoveTask();
    }
}
