package com.example.ikefluxa.rainbowrunner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class GameScreen extends View {
    int counter;
    Paint paint;
    public GameScreen(Context context) {
        super(context);
        constructor();
    }

    public GameScreen(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        constructor();
    }

    public GameScreen(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        constructor();
    }

    private void constructor() {
        counter = 0;
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        counter ++;
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.BLUE);
        paint.setTextSize(Screen.height / 10);
        canvas.drawText(String.valueOf(counter), Screen.width / 2, Screen.height / 2, paint);
    }

    public void draw() {
        invalidate();
        requestLayout();
    }
}
