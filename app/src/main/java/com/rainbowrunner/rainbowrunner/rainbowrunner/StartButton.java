package com.rainbowrunner.rainbowrunner.rainbowrunner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class StartButton {
    private Paint paint;

    StartButton() {
        paint = new Paint();
    }
    void Update(Context context) {
        if (Touch.y < Constants.groundHeight){
            if (Touch.isTouching){
                 Constants.ResetGame(context);
            }
        }
    }
    void Draw(Canvas canvas) {
        paint.setAntiAlias(true);
        paint.setTextSize(Screen.height / 12);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Start", Screen.width / 2, Screen.height / 5 + paint.getTextSize() / 3, paint);
    }
}
