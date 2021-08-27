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
    private int numberOfSuccess;

    private LinearLayout leftSide, middle, rightSide, path1, path2, path3, path4;
    private RelativeLayout rootView;
    private Ball ball1, ball2, ball3, ball4, ball5;
    private ViewGroup.LayoutParams[] ballsParams = new ViewGroup.LayoutParams[5];
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
        isEating = false;
        numberOfSuccess = 0;

        rootView = findViewById(R.id.rootView);
        if (controller.equals("1")) {
            rootView.setOnClickListener(this);
        } else {
            rootView.setOnTouchListener(new ControllerTouchListener(this, this));
        }

        sharkId = 3000;

        ball1 = findViewById(R.id.ball1);
        ball1.setListener(this);
        ballsParams[0] = ball1.getLayoutParams();

        ball2 = findViewById(R.id.ball2);
        ball2.setListener(this);
        ballsParams[1] = ball2.getLayoutParams();

        ball3 = findViewById(R.id.ball3);
        ball3.setListener(this);
        ballsParams[2] = ball3.getLayoutParams();

        ball4 = findViewById(R.id.ball4);
        ball4.setListener(this);
        ballsParams[3] = ball4.getLayoutParams();

        ball5 = findViewById(R.id.ball5);
        ball5.setListener(this);
        ballsParams[4] = ball5.getLayoutParams();

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
        /*final Handler interval = new Handler();
        interval.postDelayed(new Runnable() {
            @Override
            public void run() {
                createShark();
                interval.postDelayed(this, new Random().nextInt(waveDelay) + wavePlusDelay);
            }
        }, new Random().nextInt(waveDelay) + wavePlusDelay);*/

        createShark();
    }

    private void createShark() {
        /*int numberOfSpawn = new Random().nextInt(5) + 1;

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
        }*/

        Shark shark = new Shark(this, screenHeight, sharkId, this, 1);
        path1.addView(shark);

        shark.startAnim();
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

        if ((sharkX - 30 <= ballX && sharkX + 30 >= ballX) &&
                (ballY - 36 <= sharkY && ballY + 36 >= sharkY)) {
            Log.d("onSharkMovement: ", "Catch----------------");

            isEating = true;
            shark.eatBall(sharkX, sharkY);

            if (currentBallIndex == 1) {
                ball1.stop();
                rootView.removeView(ball1);
                currentBall = ball2;
                ++currentBallIndex;
            } else if (currentBallIndex == 2) {
                ball2.stop();
                rootView.removeView(ball2);
                currentBall = ball3;
                ++currentBallIndex;
            } else if (currentBallIndex == 3) {
                ball3.stop();
                rootView.removeView(ball3);
                currentBall = ball4;
                ++currentBallIndex;
            } else if (currentBallIndex == 4) {
                ball4.stop();
                rootView.removeView(ball4);
                currentBall = ball5;
                ++currentBallIndex;
            } else if (currentBallIndex == 5) {
                ball5.stop();
                rootView.removeView(ball5);
                currentBall = null;
                if (numberOfSuccess >= 3) {
                    reCreateBalls();
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
        }
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

    @Override
    public void onMove(Float pos) {
        if (settingNewBall || currentBall == null || isEating)
            return;

        if (pos >= (screenWidth - getMappedSize(leftAndRightSideWidth + 10))) {
            settingNewBall = true;
            Log.d("DONE-", "DONE: " + pos);
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
                    reCreateBalls();
                }
            }

            CharSequence score = topScore.getText();
            topScore.setText(String.valueOf(Integer.parseInt(String.valueOf(score)) + 1));

            settingNewBall = false;
            isMoving = false;
        }
    }
}