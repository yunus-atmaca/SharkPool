package com.sharkpool21game;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;

public class Shark extends AppCompatImageView implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {

    private boolean isLeft;
    private final int translationY;
    private int parentNumber;

    private SharkListener listener;
    public Shark(Context context, int translationY, int id, SharkListener listener, int parentNumber){
        super(context);

        this.listener = listener;
        this.translationY = translationY + 2*dpToPx(120);
        this.parentNumber = parentNumber;

        isLeft = true;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = dpToPx(-120);

        setImageResource(R.drawable.ic_shark_left);
        setId(id);
        setLayoutParams(params);
    }

    /*public Shark(Context context, AttributeSet attrs) {
        super(context, attrs);
        isLeft = true;
        setImageResource(R.drawable.ic_shark_left);
        //init();
    }*/

    public void startAnim(){
        setAnim();
    }

    private void setAnim(){
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
        ObjectAnimator animation = ObjectAnimator.ofFloat(this, "translationY", translationY);
        animation.addListener(this);
        animation.setDuration(5000);
        animation.addUpdateListener(this);
        animation.start();
    }

    private int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {
        this.listener.onAnimEnd(getId(), this, parentNumber);
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

    public interface SharkListener {
        void onAnimEnd(int id, Shark shark, int parent);
    }
}
