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
                            }
                        });
                        Thread.sleep(bob - System.currentTimeMillis() + 30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        mainThread.start();
    }

    private void setScreenDims() {
        Display display = getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Screen.width = size.x;
        Screen.height = size.y;
    }
}
