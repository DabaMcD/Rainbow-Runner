package com.example.ikefluxa.rainbowrunner;

import android.graphics.Canvas;
import android.graphics.Paint;

class Particle {
    private Vector2 position;
    private float color,
            width;
    private double height,
            life;
    private Paint paint;

    Particle(float x, double y, float width, double height, int clr, double life) {
        this.position = new Vector2(x, (float) y);
        this.width = width;
        this.height = height;
        this.color = clr;
        this.life = Math.max(1, life);
        paint = new Paint();
    }
    void Update() {
        life -= 0.04;
        position.x -= Constants.moveSpeedX;
    }
    void Draw(double totalLife, Canvas canvas) {
        int c = Constants.ColorWithAlpha((int) color, (int) (Math.round((life/totalLife)*205)+50));
        paint.setColor(c);
        canvas.drawRect(position.x, position.y, width + position.x, (float) height + position.y, paint);
    }
}
