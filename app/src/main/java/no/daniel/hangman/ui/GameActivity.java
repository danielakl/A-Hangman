package no.daniel.hangman.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
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
public class GameActivity extends AppCompatActivity {
    private static GameManager gameManager;
    private Game game;
    private String langIndex;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private View contentView;
    private TextView wordView;

    private final Handler hideHandler = new Handler();
    private final Runnable hideRunnable2 = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            contentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private boolean visible;
    private final Runnable hideRunnable = this::hide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        visible = true;
        contentView = findViewById(R.id.fullscreen_content);
        wordView = findViewById(R.id.word_view);

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(view -> toggle());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
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
            LayoutInflater.from(this).inflate(keyboards[index % keyboards.length], (ViewGroup) contentView);

            // Initialize game logic.
            gameManager = GameManager.initialize(preferences);
            game = gameManager.createGame();
            updateWord();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        langIndex = null;
        game.close();
        finish();
    }

    // TODO: This might not be called when going back and changing the settings or game.close() is broken.
    @Override
    protected void onStop() {
        super.onStop();
        langIndex = null;
        game.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void toggle() {
        if (visible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        visible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.postDelayed(hideRunnable2, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        contentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        visible = true;

        // Remove hide runnable.
        hideHandler.removeCallbacks(hideRunnable2);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        hideHandler.removeCallbacks(hideRunnable);
        hideHandler.postDelayed(hideRunnable, delayMillis);
    }
}
