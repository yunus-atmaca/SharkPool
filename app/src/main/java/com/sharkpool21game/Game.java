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

import com.sharkpool21game.utils.Constants;
import com.sharkpool21game.utils.ControllerTouchListener;
import com.sharkpool21game.utils.SPManager;
import com.sharkpool21game.utils.SharedValues;

import java.util.Random;

public class Game extends AppCompatActivity implements Shark.SharkListener, View.OnClickListener, ControllerTouchListener.OnTouchListener {

    private final int leftAndRightSideWidth = 44;
    private final int middleWidth = 30;
    private final int separatorWidth = 3;
    private final int defaultScreenWidth = 360;
    private int pathWidth;

    private String lan;
    private String controller;
    private SPManager spManager;
    private int translationY;
    private int sharkId;

    private LinearLayout leftSide, middle, rightSide, path1, path2, path3, path4;
    private RelativeLayout rootView;
    private Ball ball1, ball2, ball3, ball4, ball5;


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
        int width = displayMetrics.widthPixels;
        translationY = displayMetrics.heightPixels;
        pathWidth = (width - (2 * getMappedSize(leftAndRightSideWidth) + 6 * getMappedSize(separatorWidth) + getMappedSize(middleWidth))) / 4;

        rootView = findViewById(R.id.rootView);
        if(controller.equals("1")){
            rootView.setOnClickListener(this);
        }else{
            rootView.setOnTouchListener(new ControllerTouchListener(this, this));
        }

        sharkId = 3000;

        ball1 = findViewById(R.id.ball1);
        ball2 = findViewById(R.id.ball2);
        ball3 = findViewById(R.id.ball3);
        ball4 = findViewById(R.id.ball4);
        ball5 = findViewById(R.id.ball5);

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


        /*leftSide = findViewById(R.id.leftSide);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) leftSide.getLayoutParams();
        marginLayoutParams.width = getMappedSize(leftAndRightSideWidth - 2);
        leftSide.setLayoutParams(marginLayoutParams);

        rightSide = findViewById(R.id.rightSide);
        marginLayoutParams = (ViewGroup.MarginLayoutParams) rightSide.getLayoutParams();
        marginLayoutParams.width = getMappedSize(leftAndRightSideWidth);
        marginLayoutParams.leftMargin = getMappedSize(separatorWidth + 2);
        rightSide.setLayoutParams(marginLayoutParams);

        middle = findViewById(R.id.middle);
        marginLayoutParams = (ViewGroup.MarginLayoutParams) middle.getLayoutParams();
        marginLayoutParams.width = getMappedSize(middleWidth);
        marginLayoutParams.leftMargin = getMappedSize(separatorWidth);
        middle.setLayoutParams(marginLayoutParams);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        translationY = displayMetrics.heightPixels;

        pathWidth = (width - (2 * getMappedSize(leftAndRightSideWidth) + 6 * getMappedSize(separatorWidth) + getMappedSize(30))) / 4;

        path1 = findViewById(R.id.path_1);
        marginLayoutParams = (ViewGroup.MarginLayoutParams) path1.getLayoutParams();
        marginLayoutParams.width = pathWidth;
        path1.setLayoutParams(marginLayoutParams);

        path2 = findViewById(R.id.path_2);
        path2.setLayoutParams(marginLayoutParams);

        path3 = findViewById(R.id.path_3);
        path3.setLayoutParams(marginLayoutParams);

        path4 = findViewById(R.id.path_4);
        path4.setLayoutParams(marginLayoutParams);*/

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
        }, new Random().nextInt(waveDelay) + wavePlusDelay );
    }

    private void createShark() {
        int numberOfSpawn = new Random().nextInt(5) + 1;

        for (int i = 0; i < numberOfSpawn; ++i) {
            int pathIndex = new Random().nextInt(5) + 1;
            Shark shark = new Shark(this, translationY, sharkId, this, pathIndex);

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
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        return width * defSize / defaultScreenWidth;
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
        try{
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
        }catch (Exception e){
            //Ignore
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.rootView){
            Log.d("Game-Test", "Click-RootView");
        }
    }

    @Override
    public void onUp() {
        //Log.d("Game-Test", "Click-onUp");
        ball1.stop();
    }

    @Override
    public void onLongPress() {
        //Log.d("Game-Test", "Click-onLongPress");
        ball1.start();
    }
}