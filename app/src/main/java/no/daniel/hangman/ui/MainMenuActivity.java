package no.daniel.hangman.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Process;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import no.daniel.hangman.R;

/**
 * The main menu activity of the game, this is where you can start a game session,
 * go to settings, learn how to play or exit the game.
 */
public class MainMenuActivity extends AppCompatActivity {
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private View contentView;

    private boolean visible;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

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
        Intent intent = new Intent("no.daniel.hangman.StartGame");
        startActivity(intent);
    }

    private void startSettings() {
        Intent intent = new Intent("no.daniel.hangman.Settings");
        startActivity(intent);
    }

    private void exit() {
        // Ref: https://stackoverflow.com/questions/17719634/how-to-exit-an-android-app-using-code
        // Ref: https://stackoverflow.com/questions/7075349/android-clear-activity-stack
        // Stop async code.
        hideHandler.removeCallbacks(hideRunnable);
        hideHandler.removeCallbacks(hideRunnable2);
        // Clear activity stack.
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        // Kill process and JVM.
        Process.killProcess(Process.myPid());
        System.exit(1);
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

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hideRunnable2);
    }
}
