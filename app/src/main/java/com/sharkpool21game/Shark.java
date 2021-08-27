package com.sharkpool21game;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;

public class Shark extends AppCompatImageView implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {

    private boolean isLeft;
    private final int translationY;
    private int parentNumber;

    private SharkListener listener;
    private boolean isEating;
    private ObjectAnimator animation;
    private Float yPos;
    public Shark(Context context, int translationY, int id, SharkListener listener, int parentNumber){
        super(context);

        this.listener = listener;
        this.translationY = translationY + 2*dpToPx(120);
        this.parentNumber = parentNumber;
        isEating = false;
        animation = null;
        yPos = 0F;

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

    public void startAnim(){
        setAnim();
    }

    public void eatBall(float x, float y){
        isEating = true;
        if(animation != null)
            animation.cancel();

        setImageResource(R.drawable.ic_shark_attack);

        new Handler().postDelayed(() -> {
            setImageResource(R.drawable.ic_shark_left);
            isLeft = true;

            createViewsAndAnimations();
            isEating = false;
            this.listener.onEndEating();
        },200);
    }

    private void setAnim(){
        new CountDownTimer(20000, 500) {
            public void onTick(long millisUntilFinished) {
                if(isEating)
                    return;

                setImageResource(isLeft ? R.drawable.ic_shark_right : R.drawable.ic_shark_left);
                isLeft = !isLeft;
            }

            public void onFinish() {

            }
        }.start();

        createViewsAndAnimations();
    }

    private void createViewsAndAnimations() {
        animation = ObjectAnimator.ofFloat(this, "translationY", translationY);
        animation.addListener(this);
        //animation.setDuration(5000);
        animation.setDuration(getDuration());
        animation.addUpdateListener(this);
        animation.start();
    }

    private int getDuration() {
        return (Math.round(translationY - yPos) * 5000) / translationY;
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
        if(isEating)
            return;
        if(animation != null) {
            animation.cancel();
            animation = null;
        }
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
        yPos = (Float) animation.getAnimatedValue();
        listener.onSharkMovement(this);
    }

    public interface SharkListener {
        void onAnimEnd(int id, Shark shark, int parent);
        void onSharkMovement(Shark shark);
        void onEndEating();
    }
}
