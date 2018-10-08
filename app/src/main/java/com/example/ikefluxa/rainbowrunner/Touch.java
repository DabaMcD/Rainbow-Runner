package com.example.ikefluxa.rainbowrunner;

import android.view.MotionEvent;
import android.view.View;

public class Touch {
    public static float x;
    public static float y;
    public static boolean isTouching = false;

    public static void setTouchListener(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = event.getX();
                        y = event.getY();
                        isTouching = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        x = event.getX();
                        y = event.getY();
                        isTouching = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        x = event.getX();
                        y = event.getY();
                        isTouching = false;
                        break;
                }
                return true;
            }
        });
    }
}
