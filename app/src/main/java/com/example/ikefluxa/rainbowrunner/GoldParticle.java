package com.example.ikefluxa.rainbowrunner;

import android.graphics.Canvas;
import android.graphics.Paint;

import static com.example.ikefluxa.rainbowrunner.GameVals.g;

class GoldParticle {
    Vector2 position;
    private Vector2 velocity;
    double width,
            height,
            life;
    private int fillColor;
    private Paint paint;

    GoldParticle(double px, double py, double vx, double vy, double width, double height, int c, double life) {
        position = new Vector2((float) px, (float) py);
        velocity = new Vector2((float) vx, (float) vy);
        this.width = width;
        this.height = height;
        fillColor = c;
        this.life = life;
        paint = new Paint();
    }
    void Update() {
        velocity.y += g * 0.1;
        position.Add(velocity);
        life --;
    }
    void Draw(Canvas canvas) {
        paint.setColor(fillColor);
        canvas.drawRect(position.x, position.y, (float) width + position.x, (float) height + position.y, paint);
    }
}
