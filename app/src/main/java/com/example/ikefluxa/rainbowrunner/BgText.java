package com.example.ikefluxa.rainbowrunner;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.ikefluxa.rainbowrunner.GameVals.obstacleSpawnInterval;

class BgText {
    private int txtSize,
            colorIndex,
            counter,
            counterMax;
    private double baseVelocityX,
            txtWidth;
    private boolean max,
            isVisible;
    private String startText,
            maxText,
            txt;
    private Vector2 position,
            velocity;
    private ArrayList<Integer> colors;
    private Paint paint;

    BgText() {
        txtSize = Screen.height / 2;
        position = new Vector2(Screen.width, Screen.height / 2 - txtSize / 2);
        colors = new ArrayList<>(Arrays.asList(
                Color.argb(150, 255, 160, 80),
                Color.argb(150, 255, 140, 170),
                Color.argb(150, 245, 220, 90)
        ));
        colorIndex = 0;
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
        counter = 0;
        counterMax = 40;
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

        // color

        counter++;
        if (counter >= counterMax){
            counter = 0;
            colorIndex = (colorIndex + 1) % colors.size();
        }
    }
    void Draw(Canvas canvas) {
        if (!isVisible){
            return;
        }
        int c = Constants.lerpColor(colors.get(colorIndex), colors.get((colorIndex + 1) % colors.size()), counter/counterMax);
        paint.setColor(c);
        paint.setTextSize(txtSize);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(txt, position.x, position.y - paint.getTextSize() * 5 / 6, paint); // todo: big text may not be vertically aligned
    }
}
