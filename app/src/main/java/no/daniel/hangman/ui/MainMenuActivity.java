package no.daniel.hangman.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import no.daniel.hangman.R;

/**
 * The main menu activity of the game, this is where you can start a game session,
 * go to settings, learn how to play or exit the game.
 */
public class MainMenuActivity extends FullscreenActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        showRunnable2 = () -> { };

        visible = false;
        contentView = findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(view -> toggle());

        hide();
    }

    /**
     * Called when interacting with any main menu button.
     * @param button the button that was pressed.
     */
    public void onButtonTouch(View button) {
        switch (button.getId()) {
            case R.id.play_button:
                startGame();
                break;
            case R.id.tutorial_button:
                // TODO: Implement popup with a description of how to play.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.alert_title_tutorial)
                        .setMessage(R.string.alert_text_tutorial)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton(R.string.alert_positive_tutorial, (dialog, which) -> { })
                        .create().show();
                break;
            case R.id.settings_button:
                startSettings();
                break;
            case R.id.exit_button:
                exit();
                break;
            default:
                break;
        }
    }

    private void startGame() {
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        startActivity(intent);
    }

    private void startSettings() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }

    private void exit() {
        finishAndRemoveTask();
    }

    @Override
    @SuppressLint("InlinedApi")
    protected void show() {
        // Show the system bar
        contentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        visible = true;

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hideRunnable2);
    }
}
