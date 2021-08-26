package com.sharkpool21game;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sharkpool21game.utils.Constants;
import com.sharkpool21game.utils.ControllerTouchListener;
import com.sharkpool21game.utils.SPManager;
import com.sharkpool21game.utils.SharedValues;

import java.util.Random;

public class Game extends AppCompatActivity implements Shark.SharkListener, View.OnClickListener, ControllerTouchListener.OnTouchListener, Ball.onBallListener {

    private final int leftAndRightSideWidth = 44;
    private final int middleWidth = 30;
    private final int separatorWidth = 3;
    private final int defaultScreenWidth = 360;
    private int pathWidth;

    private String lan;
    private String controller;
    private SPManager spManager;
    private int sharkId;

    private int screenWidth;
    private int screenHeight;

    private boolean settingNewBall;
    private int currentBallIndex;
    private boolean isMoving;

    private LinearLayout leftSide, middle, rightSide, path1, path2, path3, path4;
    private RelativeLayout rootView;
    private Ball ball1, ball2, ball3, ball4, ball5;
    private Ball currentBall;
    private TextView topScore;

    private int maxDelayForSpawn = 1000;
    private int maxPlusDelayForSpawn = 300;

    private int waveDelay = 2000;
    private int wavePlusDelay = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        init();
    }

    private void init() {
        lan = SharedValues.getString(getApplicationContext(), Constants.KEY_LANGUAGE, Constants.LAN_ENG);
        controller = SharedValues.getString(getApplicationContext(), Constants.KEY_CONTROL, "1");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        pathWidth = (screenWidth - (2 * getMappedSize(leftAndRightSideWidth) + 6 * getMappedSize(separatorWidth) + getMappedSize(middleWidth))) / 4;

        settingNewBall = false;
        currentBallIndex = 1;
        isMoving = false;

        rootView = findViewById(R.id.rootView);
        if (controller.equals("1")) {
            rootView.setOnClickListener(this);
        } else {
            rootView.setOnTouchListener(new ControllerTouchListener(this, this));
        }

        sharkId = 3000;

        ball1 = findViewById(R.id.ball1);
        ball1.setListener(this);
        ball2 = findViewById(R.id.ball2);
        ball2.setListener(this);
        ball3 = findViewById(R.id.ball3);
        ball3.setListener(this);
        ball4 = findViewById(R.id.ball4);
        ball4.setListener(this);
        ball5 = findViewById(R.id.ball5);
        ball5.setListener(this);

        currentBall = ball1;

        topScore = findViewById(R.id.topScore);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                getMappedSize(leftAndRightSideWidth - 2),
                RelativeLayout.LayoutParams.MATCH_PARENT
        );

        leftSide = findViewById(R.id.leftSide);
        leftSide.setLayoutParams(params);

        path1 = findViewById(R.id.path_1);
        params = new RelativeLayout.LayoutParams(
                pathWidth,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        params.leftMargin = getMappedSize(separatorWidth);
        params.addRule(RelativeLayout.END_OF, R.id.leftSide);
        path1.setLayoutParams(params);

        path2 = findViewById(R.id.path_2);
        params = new RelativeLayout.LayoutParams(
                pathWidth,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        params.leftMargin = getMappedSize(separatorWidth);
        params.addRule(RelativeLayout.END_OF, R.id.path_1);
        path2.setLayoutParams(params);

        middle = findViewById(R.id.middle);
        params = new RelativeLayout.LayoutParams(
                getMappedSize(middleWidth),
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        params.leftMargin = getMappedSize(separatorWidth);
        params.addRule(RelativeLayout.END_OF, R.id.path_2);
        middle.setLayoutParams(params);

        path3 = findViewById(R.id.path_3);
        params = new RelativeLayout.LayoutParams(
                pathWidth,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        params.leftMargin = getMappedSize(separatorWidth);
        params.addRule(RelativeLayout.END_OF, R.id.middle);
        path3.setLayoutParams(params);

        path4 = findViewById(R.id.path_4);
        params = new RelativeLayout.LayoutParams(
                pathWidth,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        params.leftMargin = getMappedSize(separatorWidth);
        params.addRule(RelativeLayout.END_OF, R.id.path_3);
        path4.setLayoutParams(params);

        rightSide = findViewById(R.id.rightSide);
        params = new RelativeLayout.LayoutParams(
                getMappedSize(leftAndRightSideWidth),
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        params.leftMargin = getMappedSize(separatorWidth + 2);
        params.addRule(RelativeLayout.END_OF, R.id.path_4);
        rightSide.setLayoutParams(params);

        //onStartGame();
    }

    private void onStartGame() {
        final Handler interval = new Handler();
        interval.postDelayed(new Runnable() {
            @Override
            public void run() {
                createShark();
                interval.postDelayed(this, new Random().nextInt(waveDelay) + wavePlusDelay);
            }
        }, new Random().nextInt(waveDelay) + wavePlusDelay);
    }

    private void createShark() {
        int numberOfSpawn = new Random().nextInt(5) + 1;

        for (int i = 0; i < numberOfSpawn; ++i) {
            int pathIndex = new Random().nextInt(5) + 1;
            Shark shark = new Shark(this, screenHeight, sharkId, this, pathIndex);

            new Handler().postDelayed(() -> {
                if (pathIndex == 1) {
                    path1.addView(shark);
                } else if (pathIndex == 2) {
                    path2.addView(shark);
                } else if (pathIndex == 3) {
                    path3.addView(shark);
                } else if (pathIndex == 4) {
                    path4.addView(shark);
                }
                shark.startAnim();

            }, new Random().nextInt(maxDelayForSpawn) + maxPlusDelayForSpawn);
        }
    }

    private int getMappedSize(int defSize) {
        return screenWidth * defSize / defaultScreenWidth;
    }

    @Override
    protected void onResume() {
        spManager = SPManager.instance(getApplicationContext());
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        spManager.releaseSP();
        super.onDestroy();
    }

    @Override
    public void onAnimEnd(int id, Shark shark, int parent) {
        try {
            if (parent == 1) {
                path1.removeView(shark);
            } else if (parent == 2) {
                path2.removeView(shark);
            } else if (parent == 3) {
                path3.removeView(shark);
            } else if (parent == 4) {
                path4.removeView(shark);
            }
            //Log.d("Game-123", "onAnimEnd-Removed");
        } catch (Exception e) {
            //Ignore
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.rootView) {
            //Log.d("Game-Test", "Click-RootView");
            if(settingNewBall || currentBall == null)
                return;

            if(isMoving){
                isMoving = false;
                currentBall.stop();
            }else{
                isMoving = true;
                currentBall.start();
            }
        }
    }

    @Override
    public void onUp() {
        //Log.d("Game-Test", "Click-onUp");
        if(settingNewBall || currentBall == null)
            return;

        currentBall.stop();
    }

    @Override
    public void onLongPress() {
        //Log.d("Game-Test", "Click-onLongPress");
        if(settingNewBall || currentBall == null)
            return;

        currentBall.start();
    }

    @Override
    public void onMove(Float pos) {
        if(settingNewBall || currentBall == null)
            return;

        if (pos >= (screenWidth - getMappedSize(leftAndRightSideWidth + 10))) {
            settingNewBall = true;
            Log.d("DONE-", "DONE: " + pos);
            currentBall.stop();

            if(currentBallIndex == 1){
                currentBall = ball2;
                ++currentBallIndex;
            }else if (currentBallIndex == 2){
                currentBall = ball3;
                ++currentBallIndex;
            }else if (currentBallIndex == 3){
                currentBall = ball4;
                ++currentBallIndex;
            }else if (currentBallIndex == 4){
                currentBall = ball5;
                ++currentBallIndex;
            }else if (currentBallIndex == 5){
                currentBall = null;
            }

            CharSequence score = topScore.getText();
            topScore.setText(String.valueOf(Integer.parseInt(String.valueOf(score)) + 1));

            settingNewBall = false;
        }
    }
}