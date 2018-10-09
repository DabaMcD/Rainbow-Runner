package com.example.ikefluxa.rainbowrunner;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class FireParticle {
    private Vector2 position,
            velocity;
    float life;
    private float width,
            startingLife,
            height;
    private int startColor,
            endColor;
    private Paint paint;

    FireParticle(float px, float py, float vx, float vy, float width, float height, float life) {
        position = new Vector2(px, py);
        velocity = new Vector2(vx, vy);
        this.width = width;
        this.height = height;
        startColor = Color.rgb(250, 130, 15);
        endColor = Constants.obstacleColors.get(Constants.obstacleTypes.indexOf("jump"));
        this.life = life;
        startingLife = life;
        paint = new Paint();
    }
    void Update() {
        position.Add(velocity);
        life--;
    }
    void Draw(float x, Canvas canvas) {
        int c = Constants.lerpColor(endColor, startColor, life/startingLife);
        paint.setColor(c);
        canvas.drawRect(position.x + x, position.y, width, height, paint);
    }
}
