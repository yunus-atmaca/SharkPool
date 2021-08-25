package com.sharkpool21game.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

public class SPManager {
    private static final String TAG = "SPManager";
    private static final int NUMBER_OF_SOUND = 3;

    private static SPManager ins = null;

    private final Context context;
    private boolean soundOn;

    private SoundPool sp;
    private int  gameOver, sharkAteTheBall, toTheShore;
    private int  gameOverId, sharkAteTheBallId, toTheShoreId;

    private SPManager(Context context) {
        soundOn = SharedValues.getBoolean(context, Constants.KEY_SOUND, true);;
        this.context = context;

        initializeSP();
    }

    public static SPManager instance(Context context) {
        if (ins == null)
            ins = new SPManager(context);

        return ins;
    }


    private void initializeSP() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        sp = new SoundPool.Builder()
                .setMaxStreams(NUMBER_OF_SOUND)
                .setAudioAttributes(attributes)
                .build();

        sharkAteTheBall = sp.load(context, Constants.SHARK_ATE_THE_BALL, 1);
        gameOver = sp.load(context, Constants.GAME_OVER, 1);
        toTheShore = sp.load(context, Constants.TO_THE_SHORE, 1);
    }

    public void setSoundOn(boolean sound) {
        soundOn = sound;
    }

    public void play(int sound) {
        if (!soundOn)
            return;

        if (sp == null) {
            initializeSP();
        }

        switch (sound) {
            case Constants.GAME_OVER:
                gameOverId = sp.play(gameOver, 1, 1, 0, 0, 1);
                break;
            case Constants.SHARK_ATE_THE_BALL:
                sharkAteTheBallId = sp.play(sharkAteTheBall, 1, 1, 0, 0, 1);
                break;
            case Constants.TO_THE_SHORE:
                toTheShoreId = sp.play(toTheShore, 1, 1, 0, 0, 1);
                break;

            default:
                Log.d(TAG, "Undefined sound");
                break;
        }
    }

    public void stop(int sound) {
        if (sp == null)
            return;

        switch (sound) {
            case Constants.GAME_OVER:
                sp.stop(gameOverId);
                break;
            case Constants.SHARK_ATE_THE_BALL:
                sp.stop(sharkAteTheBallId);
                break;
            case Constants.TO_THE_SHORE:
                sp.stop(toTheShoreId);
                break;
            default:
                Log.d(TAG, "Undefined sound");
                break;
        }

    }

    public void releaseSP() {
        if (sp != null) {
            sp.release();
            sp = null;
        }
    }
}
