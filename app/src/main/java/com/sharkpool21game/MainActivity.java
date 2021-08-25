package com.sharkpool21game;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.sharkpool21game.utils.Constants;
import com.sharkpool21game.utils.I18n;
import com.sharkpool21game.utils.SharedValues;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Main-Activity";

    private ImageView play, settings, review;
    private boolean onStartGame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {

        String lan = SharedValues.getString(getApplicationContext(), Constants.KEY_LANGUAGE, Constants.LAN_ENG);
        I18n.loadLanguage(this, lan);

        play = findViewById(R.id.play);
        play.setOnClickListener(this);

        settings = findViewById(R.id.settings);
        settings.setOnClickListener(this);

        review = findViewById(R.id.review);
        review.setOnClickListener(this);

        onStartGame = false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.play) {
            onPlay();
        } else if (view.getId() == R.id.settings) {
            onSettings();
        } else if (view.getId() == R.id.review) {
            onReview();
        } else {
            Log.d(TAG, "Unimplemented call");
        }
    }

    private void onPlay() {

    }

    private void onSettings() {
        Settings settingsFrag = new Settings();
        settingsFrag.show(getSupportFragmentManager(), "Setting-Dialog");
    }

    private void onReview() {
        Review review = new Review();
        review.show(getSupportFragmentManager(), "Review-Dialog");
    }

    private void setButtonsClickable(boolean clickable) {
        play.setClickable(clickable);
        settings.setClickable(clickable);
        review.setClickable(clickable);
    }
}