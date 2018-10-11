package com.example.ikefluxa.rainbowrunner;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import static com.example.ikefluxa.rainbowrunner.GameVals.obstacleSpawnInterval;

class BgText {
    private int txtSize;
    private double baseVelocityX,
            txtWidth;
    private boolean max,
            isVisible;
    private String startText,
            maxText,
            txt;
    private Vector2 position,
            velocity;
    private Paint paint;

    BgText() {
        txtSize = Screen.height / 2;
        position = new Vector2(Screen.width, Screen.height / 2 - txtSize / 2);
        max = false; // at max level
        startText = "BEGIN";
        maxText = "MAX LEVEL";
        txt = startText;
        baseVelocityX = Constants.moveSpeedX * 0.65;
        velocity = new Vector2((float) baseVelocityX, 0);
        paint = new Paint();
        paint.setTextSize(txtSize);
        txtWidth = paint.measureText(txt);
        isVisible = true;
    }
    void Reset() {
        position = new Vector2(Screen.width, Screen.height/2 - txtSize/2);
        velocity = new Vector2((float) baseVelocityX, 0);
        paint.setTextSize(txtSize);
        txt = startText;
        txtWidth = paint.measureText(txt);
        isVisible = true;
        max = false;
    }
    void Update() {
        // movement
        if (!max && obstacleSpawnInterval == Constants.obstacleSpawnMin){
            max = true;
            position.x = Screen.width;
            txt = maxText;
            paint.setTextSize(txtSize);
            txtWidth = paint.measureText(txt);
            velocity.x = (float) baseVelocityX;
            isVisible = true;
        }
        position.Sub(velocity);
        if (position.x + txtWidth < 0){
            isVisible = false;
        }
    }
    void Draw(Canvas canvas) {
        if (!isVisible){
            return;
        }
        paint.setColor(Color.argb(150, 100, 100, 100));
        paint.setTextSize(txtSize);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(txt, position.x, position.y + paint.getTextSize(), paint);
    }
}
