package com.example.ikefluxa.rainbowrunner;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

public class MainActivity extends AppCompatActivity {
    GameScreen gameScreen;
    Thread mainThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setScreenDims();

        Constants.groundHeight = (Screen.height / 5) * 3;
        Constants.cmndrSize = Screen.width / 3;
        Constants.blockSize = Constants.cmndrSize / 15;
        Constants.jumpVelocity = -Constants.blockSize * 2.06;
        Constants.psSize = Constants.cmndrSize;
        Constants.launchVelocityY = -Constants.blockSize * 1.35;
        Constants.moveSpeedX = (int) (Constants.cmndrSize / 18.75);
        Constants.InitializeGame(this);

        gameScreen = findViewById(R.id.gameScreen);
        Touch.setTouchListener(gameScreen);
        createAndStartMainThread();
    }
    private void createAndStartMainThread() {
        mainThread = new Thread() {
            public void run() {
                while (true) {
                    long bob = System.currentTimeMillis();
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                gameScreen.draw();
                                Touch.justTouched ++;
                            }
                        });
                        if(bob - System.currentTimeMillis() + 30 > 0) {
                            Thread.sleep(bob - System.currentTimeMillis() + 30);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        mainThread.start();
    }
    private void setScreenDims() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Screen.width = size.x;
        Screen.height = size.y - getStatusBarHeight();
    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
