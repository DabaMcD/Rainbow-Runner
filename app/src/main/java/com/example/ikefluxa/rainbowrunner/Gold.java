package com.example.ikefluxa.rainbowrunner;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

class Gold {
    Vector2 position;
    float width,
            height;
    private float scaleX,
            sideScaleX,
            frontScaleX,
            w,
            sideWidth,
            frontWidth,
            scaleY,
            frontScaleY,
            topScaleY,
            h,
            frontHeight,
            topHeight;
    private Paint paint;

    Gold(float x, float y) {
        position = new Vector2(x, y);
        width = Constants.blockSize * 6;
        height = (float) (Constants.blockSize * 3.1);

        scaleX = 11;
        sideScaleX = 8;
        frontScaleX = scaleX - sideScaleX;
        w = width / scaleX;
        sideWidth = w * sideScaleX;
        frontWidth = w * frontScaleX;
        scaleY = 4;
        frontScaleY = 2;
        topScaleY = (scaleY - frontScaleY) / 2;
        h = height / scaleY;
        frontHeight = h * frontScaleY;
        topHeight = h * topScaleY;
        paint = new Paint();
    }
    CollisionRectObj CollisionRect() {
        return new CollisionRectObj(position.Clone(), width, height);
    }
    void Update() {
        position.x -= Constants.moveSpeedX;
    }
    void DrawCollisionRect(Canvas canvas) {
        paint.setColor(Color.argb(100, 255, 0, 0));
        CollisionRectObj cr = CollisionRect();
        canvas.drawRect(cr.position.x, cr.position.y, cr.width + cr.position.x, cr.height + cr.position.y, paint);
    }
    void Draw(Canvas canvas) {
        float x = position.x;
        float y = position.y;

        // top / base
        paint.setColor(Color.rgb(255, 213, 0));
        y += topHeight;
        Path top = new Path();
        top.moveTo(x, y);
        y += frontHeight;
        top.lineTo(x, y);
        y += topHeight;
        x += frontWidth;
        top.lineTo(x, y);
        x += sideWidth;
        y -= topHeight;
        top.lineTo(x, y);
        y -= frontHeight;
        top.lineTo(x, y);
        x -= frontWidth;
        y -= topHeight;
        top.lineTo(x, y);
        top.close();

        canvas.drawPath(top, paint);

        // front
        paint.setColor(Color.rgb(255, 170, 0));
        Path front = new Path();
        x -= sideWidth;
        y += topHeight;
        front.moveTo(x, y);
        y += frontHeight;
        front.lineTo(x, y);
        x += frontWidth;
        y += topHeight;
        front.lineTo(x, y);
        y -= frontHeight;
        front.lineTo(x, y);
        front.close();

        canvas.drawPath(front, paint);

        // side
        paint.setColor(Color.rgb(255, 132, 0));
        Path side = new Path();
        side.moveTo(x, y);
        y += frontHeight;
        side.lineTo(x, y);
        x += sideWidth;
        y -= topHeight;
        side.lineTo(x, y);
        y -= frontHeight;
        side.lineTo(x, y);
        side.close();

        canvas.drawPath(side, paint);

        // todo: the above most probably doesn't work.
    }
}
