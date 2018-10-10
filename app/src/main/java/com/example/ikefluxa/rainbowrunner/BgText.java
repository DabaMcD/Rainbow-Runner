package com.example.ikefluxa.rainbowrunner;

import android.graphics.Paint;

class BgText {
    private int txtSize,
            colorIndex,
            baseVelocityX,
            txtWidth,
            counter,
            counterMax;
    private boolean max,
            isVisible;
    private String startText, maxText, txt;
    BgText() {
        txtSize = height / 2;
        position = new Vector2(width, height / 2 - txtSize / 2);
        colors = [color(255, 160, 80, 150), color(255, 140, 170, 150), color(245, 220, 90, 150)];
        colorIndex = 0;
        max = false; // at max level
        startText = "BEGIN";
        maxText = "MAX LEVEL";
        txt = startText;
        baseVelocityX = Constants.moveSpeedX * 0.65;
        velocity = new Vector2(baseVelocityX, 0);
        paint = new Paint;
        paint.setTextSize(txtSize)
        txtWidth = textWidth(txt);
        isVisible = true;
        counter = 0;
        counterMax = 40;
    }
    Reset = function(){
        position = new Vector2(width, height/2 - txtSize/2);
        velocity = new Vector2(baseVelocityX, 0);
        textSize(txtSize);
        txt = startText;
        txtWidth = textWidth(txt);
        isVisible = true;
        max = false;
    };
    
    Update = function(){
        // movement
        if (!max && obstacleSpawnInterval === obstacleSpawnMin){
            max = true;
            position.x = width;
            txt = maxText;
            textSize(txtSize);
            txtWidth = textWidth(txt);
            velocity.x = baseVelocityX;
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
            colorIndex = (colorIndex + 1) % colors.length;
        }
    };
    
    Draw = function(){
        if (!isVisible){
            return;
        }
        var c = lerpColor(colors[colorIndex], colors[(colorIndex + 1) % colors.length], counter/counterMax);
        fill(c);
        noStroke();
        textSize(txtSize);
        textAlign(LEFT, TOP);
        text(txt, position.x, position.y);
    };
}
