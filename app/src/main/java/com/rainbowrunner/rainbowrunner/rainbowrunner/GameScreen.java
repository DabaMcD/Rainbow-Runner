package com.rainbowrunner.rainbowrunner.rainbowrunner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class GameScreen extends View {
    private Paint paint;
    private Context context;

    public GameScreen(Context context) {
        super(context);
        constructor(context);
    }
    public GameScreen(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        constructor(context);
    }
    public GameScreen(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        constructor(context);
    }
    private void constructor(Context context) {
        this.context = context;
        paint = new Paint();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(Constants.bgColor);
        canvas.drawRect(-1, -1, Screen.width + 1, Screen.height + 1, paint);
        switch(GameVals.gameState){
            case "menu":
                // update
                GameVals.cmndrVideo.Update();
                GameVals.pSys.Update();
                GameVals.startButton.Update(context);

                // draw
                GameVals.pSys.Draw(canvas);
                GameVals.cmndrVideo.Draw(canvas);
                Constants.DrawGround(canvas);
                Constants.DrawControls(canvas);
                GameVals.startButton.Draw(canvas);
                Constants.DrawScore(canvas);
                break;
            case "playing":
                // update
                GameVals.score += Constants.scoreIncrement;
                GameVals.bgText.Update();
                Constants.UpdateGround();
                GameVals.cmndrVideo.Update();
                GameVals.gold.Update();
                GameVals.obstacles.Update();
                GameVals.obstacles.CheckCollision();
                GameVals.pSys.Update();

                // draw
                GameVals.bgText.Draw(canvas);
                GameVals.gold.Draw(canvas);
                GameVals.pSys.Draw(canvas);
                GameVals.cmndrVideo.Draw(canvas);
                Constants.DrawGround(canvas);
                Constants.DrawControls(canvas);
                GameVals.obstacles.Draw(canvas);
                Constants.DrawScore(canvas);
                if (GameVals.cmndrVideo.isAlive){
                    GameVals.gold.DrawPSys(canvas);
                }
                break;
            case "loss":
                // commander video flashes
                if (GameVals.lossTimer % Math.round(Constants.lossTimerMax / Constants.flashes / 2) == 0){
                    GameVals.cmndrVideo.isVisible = !GameVals.cmndrVideo.isVisible;
                }
                GameVals.lossTimer++;

                // draw
                GameVals.bgText.Draw(canvas);
                GameVals.gold.Draw(canvas);
                GameVals.pSys.Draw(canvas);
                GameVals.cmndrVideo.Draw(canvas);
                Constants.DrawGround(canvas);
                Constants.DrawControls(canvas);
                GameVals.obstacles.Draw(canvas);
                Constants.DrawScore(canvas);

                // end loss state
                if (GameVals.lossTimer >= Constants.lossTimerMax){
                    Constants.ResetGame(context);
                }
        }
    }
    void draw() {
        invalidate();
        requestLayout();
    }
}
