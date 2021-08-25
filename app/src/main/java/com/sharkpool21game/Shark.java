package com.sharkpool21game;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


import androidx.appcompat.widget.AppCompatImageView;

public class Shark extends AppCompatImageView implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {

    private boolean isLeft;

    public Shark(Context context, int marginLeft){
        super(context);

        //setLayoutParams(new ViewGroup.LayoutParams());
    }

    public Shark(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        isLeft = true;

        new CountDownTimer(16000, 500) {
            public void onTick(long millisUntilFinished) {
                setImageResource(isLeft ? R.drawable.ic_shark_right : R.drawable.ic_shark_left);
                isLeft = !isLeft;
            }

            public void onFinish() {

            }
        }.start();

        createViewsAndAnimations();
    }

    private void createViewsAndAnimations() {
        ObjectAnimator animation = ObjectAnimator.ofFloat(this, "translationY", 2500);
        animation.addListener(this);
        animation.setDuration(2000);
        animation.addUpdateListener(this);
        animation.start();
    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {

    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {

    }
}
