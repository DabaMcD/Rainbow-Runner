package com.example.ikefluxa.rainbowrunner;

import android.graphics.Canvas;
import android.graphics.Paint;

class Obstacle {
    int type;
    double width, 
            height;
    Vector2 position;
    int color;
    private Paint paint;
    
    Obstacle(int t) {
        type = t;
        position = new Vector2(0, 0);
        width = 0;
        height = 0;
        switch (Constants.obstacleTypes.get(t)) {
            case "jump":
                width = Constants.blockSize * 3.2;
                height = Constants.blockSize * 3.2;
                position.y = (float) (Constants.groundHeight - height);
                color = Constants.obstacleColors.get(t);
                break;
            case "kick":
                width = Constants.blockSize * 2;
                height = Constants.cmndrSize;
                position.y = (float) (Constants.groundHeight - height);
                color = Constants.obstacleColors.get(t);
                break;
            case "slide":
                width = Constants.blockSize * 10;
                height = Constants.groundHeight - Constants.blockSize * 6;
                position.y = 0;
                color = Constants.obstacleColors.get(t);
                break;
            case "launch":
                width = Constants.blockSize * 5;
                height = Constants.blockSize * 1.5;
                position.y = Constants.groundHeight;
                color = Constants.obstacleColors.get(t);
                break;
        }
        position.x = Screen.width;
        width = Math.round(width);
        height = Math.round(height);
        paint = new Paint();
    }
    void Update() {
        position.x -= Constants.moveSpeedX;
    }
    void Draw(Canvas canvas) {
        paint.setColor(color);
        canvas.drawRect(position.x, position.y, (float) width + position.x, (float) height + position.y, paint);
    }
}
