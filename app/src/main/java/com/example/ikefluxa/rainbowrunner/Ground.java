package com.example.ikefluxa.rainbowrunner;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class Ground {
    private Vector2 position,
        lastPosition;
    private float width,
        height;
    private Paint paint;

    Ground(int x, int width) {
        position = new Vector2(x, Constants.groundHeight);
        lastPosition = position.Clone();
        this.width = width;
        height = Screen.height - position.y;
        paint = new Paint();
    }

    public void Update(){
        lastPosition = position.Clone();
        position.x -= Constants.moveSpeedX;
    }

    public void Draw(Canvas canvas){
        paint.setColor(Constants.black);
        canvas.drawRect(position.x, position.y, width, height, paint);
        paint.setColor(Color.rgb(117, 117, 117));
        canvas.drawRect(position.x, position.y, width, (float) (height * 0.05), paint);
        paint.setColor(Color.rgb(171, 171, 171));
        canvas.drawRect(position.x, position.y, width, (float) (height * 0.015), paint);
    }
}
