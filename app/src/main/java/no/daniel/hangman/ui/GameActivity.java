package no.daniel.hangman.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
    private static final int CHANCES = 10;
    private static final int[] hangmanImages = setupImages();

    private Game game;
    private String langIndex;

    private TextView roundsView;
    private TextView roundsLostView;
    private TextView roundsWonView;
    private ImageView hangmanView;
    private TextView wordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        visible = false;
        contentView = findViewById(R.id.fullscreen_content);
        roundsView = findViewById(R.id.rounds_view);
        roundsLostView = findViewById(R.id.rounds_lost_view);
        roundsWonView = findViewById(R.id.rounds_won_view);
        hangmanView = findViewById(R.id.hangman_view);
        wordView = findViewById(R.id.word_view);

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
            if (Game.INSTANCE != null) {
                game.close();
            }

            // Setup keyboard.
            setupKeyboard();

            // Initialize game logic.
            GameManager gameManager = GameManager.initialize(preferences);
            game = gameManager.createGame(CHANCES);
            roundsView.setText(getResources().getString(R.string.rounds_progress, game.getRoundsPlayed(), game.getRounds()));
            roundsLostView.setText(getResources().getString(R.string.rounds_lost, game.getRoundsLost()));
            roundsWonView.setText(getResources().getString(R.string.rounds_won, game.getRoundsWon()));
            hangmanView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.hangman_0));
            updateWord();
        }
    }

    /**
     * Called when back button is pressed.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exit();
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
                exit();
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
            } else {
                hangmanView.setImageBitmap(BitmapFactory.decodeResource(getResources(),
                        hangmanImages[CHANCES - game.getChancesLeft()]));
            }
            checkGameState();
        }
    }

    private void checkGameState() {
        if (game.hasWon() || game.getChancesLeft() <= 0) {
            if (game.hasWon()) {
                // TODO: Play win sound
                roundsWonView.setText(getResources().getString(R.string.rounds_won, game.getRoundsWon()));
            } else {
                // TODO: Play loss sound
                roundsLostView.setText(getResources().getString(R.string.rounds_lost, game.getRoundsLost()));
            }
            roundsView.setText(getResources().getString(R.string.rounds_progress, game.getRoundsPlayed(), game.getRounds()));

            // TODO: Wait a short while
            game = game.nextGame();
            if (Game.INSTANCE == null) {
                // TODO: Play victory sound
                // TODO: Display victory dialog -> send to main menu
                exit();
            } else {
                reset();
            }
        }
    }

    private void setupKeyboard() {
        int index = (langIndex == null) ? 0 : Integer.parseInt(langIndex);
        int[] keyboards = {R.layout.keyboard_english, R.layout.keyboard_norwegian};
        View keyboard = findViewById(R.id.keyboard);
        if (keyboard != null) {
            ((ViewGroup) keyboard.getParent()).removeView(keyboard);
        }
        LayoutInflater.from(this).inflate(keyboards[index % keyboards.length], (ViewGroup) contentView);
    }

    private static int[] setupImages() {
        int[] images = new int[CHANCES + 1];
        images[0] = R.drawable.hangman_0;
        images[1] = R.drawable.hangman_1;
        images[2] = R.drawable.hangman_2;
        images[3] = R.drawable.hangman_3;
        images[4] = R.drawable.hangman_4;
        images[5] = R.drawable.hangman_5;
        images[6] = R.drawable.hangman_6;
        images[7] = R.drawable.hangman_7;
        images[8] = R.drawable.hangman_8;
        images[9] = R.drawable.hangman_9;
        images[10] = R.drawable.hangman_10;
        return images;
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
        langIndex = null;
        game.close();
        finishAndRemoveTask();
    }

    private void reset() {
        updateWord();
        setupKeyboard();
        hangmanView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.hangman_0));
    }
}
