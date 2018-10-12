package com.example.ikefluxa.rainbowrunner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class StartButton {
    private Vector2 position;
    private int width,
        height,
        originX,
        originY;
    private int fillColor,
        strokeColor;
    private Paint paint;

    StartButton(float x, float y) {
        position = new Vector2(x, y);
        width = Screen.width / 6;
        height = Screen.width / 12;
        originX = this.width / 2;
        originY = this.height / 2;
        fillColor = Color.rgb(255, 0, 0);
        strokeColor = Color.rgb(255, 0, 0);
        paint = new Paint();
    }
    void Update(Context context) {
        if (Touch.x >= position.x - originX && Touch.x < position.x + width - originX && Touch.y >= position.y - originY && Touch.y < position.y + height - originY){
            fillColor = Constants.black;
            strokeColor = Color.rgb(255, 255, 255);
            if (Touch.isTouching){
                 Constants.ResetGame(context); // About to define
            }
        } else {
            fillColor = Color.rgb(255, 255, 255);
            strokeColor = Constants.black;
        }
    }
    void Draw(Canvas canvas) {
        // Fill
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(fillColor);
        canvas.drawRect(position.x - originX, position.y - originY, width + position.x - originX, height + position.y - originY, paint);

        // Stroke
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(strokeColor);
        paint.setStrokeWidth(8);
        canvas.drawRect(position.x - originX, position.y - originY, width + position.x - originX, height + position.y - originY, paint);

        // Text
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(strokeColor);
        paint.setTextSize(width / 3);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Start", position.x, position.y + paint.getTextSize() / 3, paint);
    }
}
