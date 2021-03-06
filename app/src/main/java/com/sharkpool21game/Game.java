package com.sharkpool21game;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Rect;
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

import java.util.ArrayList;
import java.util.Random;

public class Game extends AppCompatActivity implements
        Shark.SharkListener,
        View.OnClickListener,
        ControllerTouchListener.OnTouchListener,
        Ball.onBallListener {

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
    private boolean isEating;
    private boolean isGameOver;
    private int numberOfSuccess;
    private boolean isInitial;

    private Handler interval;
    private Runnable runnable;

    private LinearLayout leftSide, middle, rightSide, path1, path2, path3, path4;
    private RelativeLayout rootView;
    private RelativeLayout rootGameOverView;
    private Ball ball1, ball2, ball3, ball4, ball5;
    private Ball currentBall;
    private TextView topScore, userScore, bestScore;

    private int maxDelayForSpawn = 1000;
    private int maxPlusDelayForSpawn = 300;

    private int waveDelay = 2000;
    private int wavePlusDelay = 600;

    private int sharkSpeed = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        init();
    }

    private void init() {
        spManager = SPManager.instance(getApplicationContext());
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
        isEating = false;
        isGameOver = false;
        isInitial = true;
        numberOfSuccess = 0;

        rootView = findViewById(R.id.rootView);
        if (controller.equals("1")) {
            rootView.setOnClickListener(this);
        } else {
            rootView.setOnTouchListener(new ControllerTouchListener(this, this));
        }

        rootGameOverView = findViewById(R.id.rootGameOver);
        userScore = findViewById(R.id.userScore);
        bestScore = findViewById(R.id.bestScore);
        findViewById(R.id.home).setOnClickListener(this);
        findViewById(R.id.restart).setOnClickListener(this);

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

        onStartGame();
    }

    private void onStartGame() {
        interval = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                isInitial = false;
                if (isGameOver)
                    return;
                createShark();
                interval.postDelayed(this, new Random().nextInt(waveDelay) + wavePlusDelay);
            }
        };

        interval.postDelayed(runnable, isInitial ? 0 : new Random().nextInt(waveDelay) + wavePlusDelay);
    }

    private void createShark() {
        if (isGameOver)
            return;

        int numberOfSpawn = new Random().nextInt(5) + 1;

        for (int i = 0; i < numberOfSpawn; ++i) {
            int pathIndex = new Random().nextInt(5) + 1;
            Shark shark = new Shark(this, screenHeight, sharkId, this, pathIndex, sharkSpeed);

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

        /*Shark shark = new Shark(this, screenHeight, sharkId, this, 1);
        path1.addView(shark);

        shark.startAnim();*/
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
    public void onSharkMovement(Shark shark) {
        if (isEating || currentBall == null) {
            return;
        }

        Rect rectBall = new Rect();
        currentBall.getGlobalVisibleRect(rectBall);
        float ballX = rectBall.exactCenterX();
        float ballY = rectBall.exactCenterY();

        Rect rectShark = new Rect();
        shark.getGlobalVisibleRect(rectShark);
        float sharkX = rectShark.exactCenterX();
        float sharkY = rectShark.exactCenterY();

        if (ballX == 0 && sharkX == 0)
            return;

        if ((sharkX - 44 <= ballX && sharkX + 44 >= ballX) &&
                (ballY - 64 <= sharkY && ballY + 64 >= sharkY)) {
            Log.d("onSharkMovement: ", "Catch----------------");

            isEating = true;
            spManager.play(Constants.SHARK_ATE_THE_BALL);
            shark.eatBall(sharkX, sharkY);

            if (currentBallIndex == 1) {
                ball1.stop();
                ball1.setVisibility(View.INVISIBLE);
                currentBall = ball2;
                ++currentBallIndex;
            } else if (currentBallIndex == 2) {
                ball2.stop();
                ball2.setVisibility(View.INVISIBLE);
                currentBall = ball3;
                ++currentBallIndex;
            } else if (currentBallIndex == 3) {
                ball3.stop();
                ball3.setVisibility(View.INVISIBLE);
                currentBall = ball4;
                ++currentBallIndex;
            } else if (currentBallIndex == 4) {
                ball4.stop();
                ball4.setVisibility(View.INVISIBLE);
                currentBall = ball5;
                ++currentBallIndex;
            } else if (currentBallIndex == 5) {
                ball5.stop();
                ball5.setVisibility(View.INVISIBLE);
                currentBall = null;
                if (numberOfSuccess >= 3) {
                    changeSpeed();
                    reCreateBalls();
                } else {
                    //Game over
                    spManager.play(Constants.GAME_OVER);
                    gameOver();
                }
            }
        }

        //173.5   |   219.0
        //223.5   |   219.0
        //271.5   |   219.0
        //Log.d("onSharkMovement0: ", "Catch: " + rectBall.exactCenterX() + "   |   " + rectShark.exactCenterX());

        //822.0   |   649.0
        //822.0   |   819.0
        //822.0   |   999.0
        //Log.d("onSharkMovement0: ", "Catch: " + rectBall.exactCenterY() + "   |   " + rectShark.exactCenterY());
    }

    @Override
    public void onEndEating() {
        isMoving = false;
        isEating = false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.rootView) {
            //Log.d("Game-Test", "Click-RootView");
            if (settingNewBall || currentBall == null || isEating)
                return;

            if (isMoving) {
                isMoving = false;
                currentBall.stop();
            } else {
                isMoving = true;
                currentBall.start();
            }
        } else if (view.getId() == R.id.home) {
            onBackPressed();
        } else if (view.getId() == R.id.restart) {
            restart();
        }
    }

    private String getScoreText() {
        if (lan.equals(Constants.LAN_ENG)) {
            return "Score: ";
        } else if (lan.equals(Constants.LAN_RU)) {
            return "????????: ";
        }
        return "?????????";
    }

    private String getBestScoreText() {
        if (lan.equals(Constants.LAN_ENG)) {
            return "Best score: ";
        } else if (lan.equals(Constants.LAN_RU)) {
            return "???????????? ????????: ";
        }
        return "???????????????";
    }

    @Override
    public void onUp() {
        //Log.d("Game-Test", "Click-onUp");
        if (settingNewBall || currentBall == null || isEating)
            return;

        currentBall.stop();
    }

    @Override
    public void onLongPress() {
        //Log.d("Game-Test", "Click-onLongPress");
        if (settingNewBall || currentBall == null || isEating)
            return;

        currentBall.start();
    }

    private void reCreateBalls() {
        try {
            ball1.stop();
            rootView.removeView(ball1);
            ball2.stop();
            rootView.removeView(ball2);
            ball3.stop();
            rootView.removeView(ball3);
            ball4.stop();
            rootView.removeView(ball4);
            ball5.stop();
            rootView.removeView(ball5);
        } catch (Exception ignored) {
            //Ignore
        }

        int mapped16 = getMappedSize(16);
        int mapped12 = getMappedSize(12);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        params.topMargin = mapped16;
        params.bottomMargin = mapped16;
        params.leftMargin = mapped12;

        ball3 = new Ball(this);
        ball3.setLayoutParams(params);
        ball3.setListener(this);
        ball3.setImageResource(R.drawable.ic_ball);
        ball3.setId(R.id.ball3);
        rootView.addView(ball3);
        /////////////////////////////////////////////////////
        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.addRule(RelativeLayout.ABOVE, R.id.ball3);
        params.topMargin = mapped16;
        params.bottomMargin = mapped16;
        params.leftMargin = mapped12;

        ball2 = new Ball(this);
        ball2.setLayoutParams(params);
        ball2.setListener(this);
        ball2.setImageResource(R.drawable.ic_ball);
        ball2.setId(R.id.ball2);
        rootView.addView(ball2);
        ///////////////////////////////////////////////////
        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.addRule(RelativeLayout.ABOVE, R.id.ball2);
        params.topMargin = mapped16;
        params.bottomMargin = mapped16;
        params.leftMargin = mapped12;

        ball1 = new Ball(this);
        ball1.setLayoutParams(params);
        ball1.setListener(this);
        ball1.setImageResource(R.drawable.ic_ball);
        ball1.setId(R.id.ball1);
        rootView.addView(ball1);
        ///////////////////////////////////////////////////
        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.addRule(RelativeLayout.BELOW, R.id.ball3);
        params.topMargin = mapped16;
        params.bottomMargin = mapped16;
        params.leftMargin = mapped12;

        ball4 = new Ball(this);
        ball4.setLayoutParams(params);
        ball4.setListener(this);
        ball4.setImageResource(R.drawable.ic_ball);
        ball4.setId(R.id.ball4);
        rootView.addView(ball4);
        ///////////////////////////////////////////////////
        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.addRule(RelativeLayout.BELOW, R.id.ball4);
        params.topMargin = mapped16;
        params.bottomMargin = mapped16;
        params.leftMargin = mapped12;

        ball5 = new Ball(this);
        ball5.setLayoutParams(params);
        ball5.setListener(this);
        ball5.setImageResource(R.drawable.ic_ball);
        ball5.setId(R.id.ball5);
        rootView.addView(ball5);

        currentBallIndex = 1;
        isMoving = false;
        isEating = false;
        numberOfSuccess = 0;

        currentBall = ball1;
    }

    private void gameOver() {
        isGameOver = true;

        interval.removeCallbacksAndMessages(null);
        interval.removeCallbacks(runnable);

        removeAllSharks(path1);
        removeAllSharks(path2);
        removeAllSharks(path3);
        removeAllSharks(path4);

        int savedScore = SharedValues.getInt(getApplicationContext(), Constants.KEY_BEST_SCORE, 0);
        int topUserScore = Integer.parseInt(topScore.getText().toString());

        if (savedScore < topUserScore) {
            SharedValues.setInt(getApplicationContext(), Constants.KEY_BEST_SCORE, topUserScore);
            bestScore.setText(getBestScoreText() + ("" + topUserScore));
        } else {
            bestScore.setText(getBestScoreText() + ("" + savedScore));
        }

        userScore.setText(getScoreText() + ("" + topUserScore));
        rootGameOverView.setVisibility(View.VISIBLE);
    }

    private void restart() {
        settingNewBall = false;
        currentBallIndex = 1;
        isMoving = false;
        isEating = false;
        isGameOver = false;
        isInitial = true;
        numberOfSuccess = 0;
        topScore.setText("0");

        maxDelayForSpawn = 1000;
        maxPlusDelayForSpawn = 300;
        waveDelay = 2000;
        wavePlusDelay = 600;
        sharkSpeed = 5000;

        reCreateBalls();
        rootGameOverView.setVisibility(View.GONE);

        onStartGame();
    }

    private void removeAllSharks(LinearLayout path) {
        int numberOfChild = path.getChildCount();
        for (int i = 0; i < numberOfChild; ++i) {
            try {
                ((Shark) path.getChildAt(i)).stop();
            } catch (Exception ignored) {
                //Ignore
            }
        }
        path.removeAllViews();
    }

    private void changeSpeed() {
        if (maxDelayForSpawn > 500) {
            maxDelayForSpawn = maxDelayForSpawn - 50;
        }

        if (maxPlusDelayForSpawn > 150) {
            maxPlusDelayForSpawn = maxPlusDelayForSpawn - 25;
        }

        if (waveDelay > 1000) {
            waveDelay = waveDelay - 100;
        }

        if (wavePlusDelay > 300) {
            wavePlusDelay = wavePlusDelay - 50;
        }

        if (wavePlusDelay > 2500) {
            sharkSpeed = sharkSpeed - 250;
        }
    }

    @Override
    public void onMove(Float pos) {
        if (settingNewBall || currentBall == null || isEating)
            return;

        if (pos >= (screenWidth - getMappedSize(leftAndRightSideWidth + 10))) {
            settingNewBall = true;
            Log.d("DONE-", "DONE: " + pos);
            spManager.play(Constants.TO_THE_SHORE);
            currentBall.stop();

            if (currentBallIndex == 1) {
                currentBall = ball2;
                ++currentBallIndex;
                ++numberOfSuccess;
            } else if (currentBallIndex == 2) {
                currentBall = ball3;
                ++currentBallIndex;
                ++numberOfSuccess;
            } else if (currentBallIndex == 3) {
                currentBall = ball4;
                ++currentBallIndex;
                ++numberOfSuccess;
            } else if (currentBallIndex == 4) {
                currentBall = ball5;
                ++currentBallIndex;
                ++numberOfSuccess;
            } else if (currentBallIndex == 5) {
                currentBall = null;
                ++numberOfSuccess;

                if (numberOfSuccess >= 3) {
                    changeSpeed();
                    reCreateBalls();
                } else {
                    //Game over
                    spManager.play(Constants.GAME_OVER);
                    gameOver();
                }
            }

            CharSequence score = topScore.getText();
            topScore.setText(String.valueOf(Integer.parseInt(String.valueOf(score)) + 1));

            settingNewBall = false;
            isMoving = false;
        }
    }
}