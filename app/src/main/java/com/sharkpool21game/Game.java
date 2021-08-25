package com.sharkpool21game;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sharkpool21game.utils.Constants;
import com.sharkpool21game.utils.SPManager;
import com.sharkpool21game.utils.SharedValues;

import java.util.Random;

public class Game extends AppCompatActivity {

    private final int leftAndRightSideWidth = 44;
    private final int middleWidth = 30;
    private final int separatorWidth = 3;
    private final int defaultScreenWidth = 360;
    private int pathWidth;

    private String lan;
    private SPManager spManager;

    private View leftSide, middle, rightSide, path1, path2, path3, path4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        init();
    }

    private void init(){
        lan = SharedValues.getString(getApplicationContext(), Constants.KEY_LANGUAGE, Constants.LAN_ENG);

        leftSide = findViewById(R.id.leftSide);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) leftSide.getLayoutParams();
        marginLayoutParams.width = getMappedSize(leftAndRightSideWidth-2);
        leftSide.setLayoutParams(marginLayoutParams);

        rightSide = findViewById(R.id.rightSide);
        marginLayoutParams = (ViewGroup.MarginLayoutParams) rightSide.getLayoutParams();
        marginLayoutParams.width = getMappedSize(leftAndRightSideWidth);
        marginLayoutParams.leftMargin = getMappedSize(separatorWidth+2);
        rightSide.setLayoutParams(marginLayoutParams);

        middle = findViewById(R.id.middle);
        marginLayoutParams = (ViewGroup.MarginLayoutParams) middle.getLayoutParams();
        marginLayoutParams.width = getMappedSize(middleWidth);
        marginLayoutParams.leftMargin = getMappedSize(separatorWidth);
        middle.setLayoutParams(marginLayoutParams);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        pathWidth = (width - (2*getMappedSize(leftAndRightSideWidth) + 6*getMappedSize(separatorWidth) + getMappedSize(30)))/4;

        path1 = findViewById(R.id.path_1);
        marginLayoutParams = (ViewGroup.MarginLayoutParams) path1.getLayoutParams();
        marginLayoutParams.width = pathWidth;
        path1.setLayoutParams(marginLayoutParams);

        path2 = findViewById(R.id.path_2);
        path2.setLayoutParams(marginLayoutParams);

        path3 = findViewById(R.id.path_3);
        path3.setLayoutParams(marginLayoutParams);

        path4 = findViewById(R.id.path_4);
        path4.setLayoutParams(marginLayoutParams);
    }

    private void onStartGame(){

    }

    private void createShark(){
        int rand_int1 = new Random().nextInt(5) + 1;

        //Shark shark = new Shark(this);
    }

    private int getMappedSize(int defSize){
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
}