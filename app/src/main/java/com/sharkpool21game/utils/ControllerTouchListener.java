package com.sharkpool21game.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class ControllerTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;
    private OnTouchListener listener;

    private boolean isUp;

    public ControllerTouchListener(Context context, OnTouchListener listener) {
        this.listener = listener;
        this.isUp = false;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent event) {
                isUp = false;
                return true;
            }

            @Override
            public void onLongPress(MotionEvent event) {
                if(!isUp){
                    listener.onLongPress();
                }
                super.onLongPress(event);
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            isUp = true;
            this.listener.onUp();
            return true;
        }
        return gestureDetector.onTouchEvent(motionEvent);
    }

    public interface OnTouchListener {
        void onUp();
        void onLongPress();
    }
}
