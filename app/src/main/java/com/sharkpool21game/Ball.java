package com.sharkpool21game;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class Ball extends AppCompatImageView implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {

    private ObjectAnimator animation;
    private Float xPos;
    private onBallListener listener;
    public Ball(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        xPos = 0F;
        animation = null;
    }

    public void setListener(onBallListener listener){
        this.listener = listener;
    }

    public void start() {
        animation = new ObjectAnimator();
        animation.setTarget(this);
        animation.setPropertyName("translationX");
        animation.setFloatValues(1500);
        animation.addListener(this);
        animation.setDuration(getDuration());
        animation.addUpdateListener(this);
        animation.start();
    }

    private int getDuration() {
        //Log.d("getDuration-2", "" + (Math.round(1500 - xPos) * 1000 / 1500));
        return (Math.round(1500 - xPos) * 1000) / 1500;
    }

    public void stop() {
        if (animation != null) {
            animation.cancel();
            animation = null;
        }
    }

    @Override
    public void onAnimationStart(Animator animator) {
        Log.d("Ball-Test", "onAnimationStart");
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        Log.d("Ball-Test", "onAnimationEnd");
    }

    @Override
    public void onAnimationCancel(Animator animator) {
        Log.d("Ball-Test", "onAnimationCancel: " + xPos);
    }

    @Override
    public void onAnimationRepeat(Animator animator) {
        Log.d("Ball-Test", "onAnimationRepeat");
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        xPos = (Float) animation.getAnimatedValue();
        this.listener.onMove(xPos);
    }

    public interface onBallListener {
        void onMove(Float pos);
    }
}
