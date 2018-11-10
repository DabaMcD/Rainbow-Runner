package com.rainbowrunner.rainbowrunner.rainbowrunner;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import java.util.ArrayList;

class Triangle {
    ArrayList<Vector2> points;
    private Vector2 velocity;
    private float angle;
    private double rotAmount;
    private int fillColor;
    private Paint paint;

    Triangle(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 v, double rot, int c) {
        points = new ArrayList<>();
        points.add(p0);
        points.add(p1);
        points.add(p2);
        velocity = v;
        angle = 0;
        rotAmount = rot;
        fillColor = c;
        paint = new Paint();
        paint.setAntiAlias(true);
    }
    void Update() {
        angle += rotAmount;
        for (int i = 0; i < points.size(); i ++){
            points.get(i).Add(velocity);
        }
        paint = new Paint();
    }
    void Draw(Canvas canvas) {
        paint.setColor(fillColor);
        float offsetX = 0;
        float offsetY = 0;
        for (int i = 0; i < points.size(); i++){
            offsetX += points.get(i).x;
            offsetY += points.get(i).y;
        }
        offsetX /= points.size();
        offsetY /= points.size();

        canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.rotate(angle);
        canvas.translate(-offsetX, -offsetY);

        Path trianglePath = new Path();
        trianglePath.setFillType(Path.FillType.EVEN_ODD);
        trianglePath.moveTo(points.get(0).x, points.get(0).y);
        trianglePath.lineTo(points.get(1).x, points.get(1).y);
        trianglePath.lineTo(points.get(2).x, points.get(2).y);
        trianglePath.close();

        canvas.drawPath(trianglePath, paint);
        canvas.restore();
    }
}
