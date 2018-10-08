package com.example.ikefluxa.rainbowrunner;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class StartButton {
    private Vector2 position;
    private int width,
        height,
        originX,
        originY;
    private int fillColor,
        strokeColor;
    private Paint paint;

    public StartButton(float x, float y) {
        position = new Vector2(x, y);
        width = 90;
        height = 50;
        originX = this.width / 2;
        originY = this.height / 2;
        fillColor = Color.rgb(255, 0, 0);
        strokeColor = Color.rgb(255, 0, 0);
        paint = new Paint();
    }

    public void update() {
        int color = Color.rgb(255, 221, 0);
        if (Touch.x >= position.x - originX && Touch.x < position.x + width - originX && Touch.y >= position.y - originY && Touch.y < position.y + height - originY){
            fillColor = Constants.black;
            strokeColor = Color.rgb(255, 255, 255);
            if (Touch.isTouching){
                // ResetGame(); // About to define
            }
        } else {
            fillColor = Color.rgb(255, 255, 255);
            strokeColor = Constants.black;
        }
    }

    public void draw(Canvas canvas) {
        // Fill
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(fillColor);
        canvas.drawRect(position.x - originX, position.y - originY, width, height, paint);

        // Stroke
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(strokeColor);
        paint.setStrokeWidth(8);
        canvas.drawRect(position.x - originX, position.y - originY, width, height, paint);

        // Text
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(strokeColor);
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Start", position.x, position.y - paint.getTextSize(), paint);
    }
}
