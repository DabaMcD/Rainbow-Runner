package com.rainbowrunner.rainbowrunner.rainbowrunner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

class Constants {
    // General game variables
    static final int black = Color.rgb(16, 9, 20);
    static final int bgColor = Color.rgb(174, 231, 245);
    static int groundHeight;
    static final int lossTimerMax = 60;
    static final int flashes = 4;
    static final double scoreIncrement = 1d / 50d;
    static int cmndrSize = 150;
    static int blockSize = 10;
    private static final ArrayList<Integer> colors = new ArrayList<>(Arrays.asList(
            Color.rgb(255, 204, 0),
            Color.rgb(240, 142, 29),
            Color.rgb(58, 186, 169),
            Color.rgb(230, 97, 139),
            Color.rgb(95, 44, 189)
    ));
    static int moveSpeedX = 8;
    static double launchVelocityY = -blockSize * 1.35;
    static final double launchScale = 0.137;
    static final double jumpScale = 0.080;
    static double jumpVelocity = -blockSize * 2.06;
    static int psSize = 150;
    static final ArrayList<String> obstacleTypes = new ArrayList<>(Arrays.asList(
            "jump",
            "kick",
            "slide",
            "launch"
    ));
    private static final int obstacleSpawnMax = 70;
    static final int obstacleSpawnMin = 22;
    static final int launchTimerMax = 40;
    static final ArrayList<Integer> obstacleColors = new ArrayList<>(Arrays.asList(
            Color.rgb(224, 42, 18),
            Color.rgb(191, 61, 224),
            Color.rgb(22, 88, 196),
            Color.rgb(255, 105, 173)
    ));
    static final int goldPoints = 100;
    private static Paint paint = new Paint();
    private static int ctrlButtonHeight;
    private static int ctrlButtonStrokeWidth;

    static void InitializeGame(Context context) {
        if(!scoreFileExists(context)) {
            writeScore(context, 0);
        }
        GameVals.highScore = readScore(context);
        ctrlButtonHeight = (Screen.height - groundHeight) / 3;
        ctrlButtonStrokeWidth = ctrlButtonHeight / 12;

        GameVals.startButton = new StartButton();
        InitializeGround();
        GameVals.cmndrVideo = new CommanderVideo();
        GameVals.pSys = new ParticleSystem(GameVals.cmndrVideo, GameVals.cmndrVideo.position.x, GameVals.cmndrVideo.position.y, psSize, colors, GameVals.pLife);
        GameVals.obstacles = new ObstacleCollection();
        GameVals.gold = new GoldCollection();
        GameVals.bgText = new BgText();
    }
    private static void InitializeGround() {
        GameVals.ground = new ArrayList<>();
        GameVals.ground.add(new Ground(0, Screen.width + GameVals.obstacleSpawnInterval * moveSpeedX));
    }
    static void LoseGame() {
        GameVals.cmndrVideo.Kill();
        GameVals.highScore = (int) Math.round(GameVals.score > GameVals.highScore ? GameVals.score : GameVals.highScore);
        GameVals.gameState = GameStates.loss;
    }
    private static void writeScore(Context context, int score) {
        context.deleteFile("highscore.txt");
        File path = context.getFilesDir();
        File file = new File(path, "highscore.txt");
        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(String.valueOf(score).getBytes());
            stream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    private static int readScore(Context context) {
        String text = "";
        FileInputStream fis;
        try {
            fis = context.openFileInput("highscore.txt");
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            text = new String(buffer);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(text);
    }
    private static boolean scoreFileExists(Context context) {
        File path = context.getFilesDir();
        File file = new File(path, "highscore.txt");
        return file.exists() && !file.isDirectory();
    }
    static void ResetGame(Context context) {
        if(GameVals.score > readScore(context)) {
            writeScore(context, (int) Math.round(GameVals.score));
        }

        GameVals.score = 0;
        GameVals.lossTimer = 0;
        GameVals.launchTimer = 0;
        GameVals.gold.Reset();
        GameVals.obstacles.Reset();
        GameVals.obstacleTimer = 0;
        GameVals.obstacleSpawnInterval = obstacleSpawnMax; // start at lowest level
        GameVals.cmndrVideo.Reset();
        GameVals.pSys.Reset();
        InitializeGround();
        GameVals.bgText.Reset();
        GameVals.gameState = GameStates.playing;
    }
    static void CheckGroundCollision() {
        for (int i = 0; i < GameVals.ground.size(); i++){
            Ground grnd = GameVals.ground.get(i);
            if (Overlap(GameVals.cmndrVideo, grnd)){
                Collide(GameVals.cmndrVideo, grnd);
                GameVals.cmndrVideo.GroundCollision();
            }
        }
    }
    private static void Collide(CommanderVideo obj1, Ground obj2) {
        float overlapX;
        float overlapY;
        if (obj1.lastPosition.x < obj2.lastPosition.x){
            overlapX = obj1.position.x + obj1.width - obj2.position.x;
        } else {
            overlapX = obj2.position.x + obj2.width - obj1.position.x;
        }
        if (obj1.lastPosition.y < obj2.lastPosition.y){
            overlapY = obj1.position.y + obj1.height - obj2.position.y;
        } else {
            overlapY = obj2.position.y + obj2.height - obj1.position.y;
        }

        if (overlapX < overlapY){
            // obj1 right of obj2
            if (obj2.lastPosition.x + obj2.width <= obj1.lastPosition.x && obj2.position.x + obj2.width > obj1.position.x){
                obj1.position.x += overlapX;
            }
            // obj1 left of obj2
            if (obj1.lastPosition.x + obj1.width <= obj2.lastPosition.x && obj1.position.x + obj1.width > obj2.position.x){
                obj1.position.x -= overlapX;
            }
        }
        else {
            // obj1 above obj2
            if (obj1.lastPosition.y + obj1.height <= obj2.lastPosition.y && obj1.position.y + obj1.height > obj2.position.y){
                obj1.position.y -= overlapY;
                obj1.isTouchingBottom = true;
            }
            // obj1 below obj2
            if (obj2.lastPosition.y + obj2.height <= obj1.lastPosition.y && obj2.position.y + obj2.height > obj1.position.y){
                obj1.position.y += overlapY;
            }
        }
    }
    private static boolean Overlap(CommanderVideo obj1, Ground obj2) {
        return obj1.position.x < obj2.position.x + obj2.width &&
                obj1.position.x + obj1.width > obj2.position.x &&
                obj1.position.y < obj2.position.y + obj2.height &&
                obj1.position.y + obj1.height > obj2.position.y;
    }
    static boolean Overlap(Obstacle obj1, CollisionRectObj obj2) {
        return obj1.position.x < obj2.position.x + obj2.width &&
                obj1.position.x + obj1.width > obj2.position.x &&
                obj1.position.y < obj2.position.y + obj2.height &&
                obj1.position.y + obj1.height > obj2.position.y;
    }
    static boolean Overlap(CollisionRectObj obj1, CollisionRectObj obj2) {
        return obj1.position.x < obj2.position.x + obj2.width &&
                obj1.position.x + obj1.width > obj2.position.x &&
                obj1.position.y < obj2.position.y + obj2.height &&
                obj1.position.y + obj1.height > obj2.position.y;
    }
    static boolean CheckBounds(GoldParticle obj) {
        return (obj.position.x + obj.width < 0 || obj.position.x > Screen.width ||
                obj.position.y + obj.height < 0 || obj.position.y > Screen.height);
    }
    static boolean IsKeyDown(String key) {
        if(Touch.isTouching) {
            switch (key) {
                case "jump":
                    return Touch.y > Screen.height - ctrlButtonHeight * 2 &&
                        Touch.y < Screen.height - ctrlButtonHeight &&
                        Touch.x < Screen.width / 2;
                case "slide":
                    return Touch.y > Screen.height - ctrlButtonHeight &&
                            Touch.x < Screen.width / 2;
                case "kick":
                    return Touch.y > Screen.height - ctrlButtonHeight * 2 &&
                            Touch.y < Screen.height - ctrlButtonHeight &&
                            Touch.x > Screen.width / 2;
                case "launch":
                    return Touch.y > Screen.height - ctrlButtonHeight &&
                            Touch.x > Screen.width / 2;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }
    static boolean IsKeyPressed(String key) {
        if(Touch.isTouching && Touch.justTouched < 2) {
            switch (key) {
                case "jump":
                    return Touch.y > Screen.height - ctrlButtonHeight * 2 &&
                            Touch.y < Screen.height - ctrlButtonHeight &&
                            Touch.x < Screen.width / 2;
                case "slide":
                    return Touch.y > Screen.height - ctrlButtonHeight &&
                            Touch.x < Screen.width / 2;
                case "kick":
                    return Touch.y > Screen.height - ctrlButtonHeight * 2 &&
                            Touch.y < Screen.height - ctrlButtonHeight &&
                            Touch.x > Screen.width / 2;
                case "launch":
                    return Touch.y > Screen.height - ctrlButtonHeight &&
                            Touch.x > Screen.width / 2;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }
    static int ColorWithAlpha(int c, int a) {
        int r = Color.red(c);
        int g = Color.green(c);
        int b = Color.blue(c);
        return Color.argb(a, r, g, b);
    }
    static int lerpColor(int a, int b, double phase) {
        int red = (int) (Color.red(a) * phase + Color.red(b) * (1 - phase));
        int green = (int) (Color.green(a) * phase + Color.green(b) * (1 - phase));
        int blue = (int) (Color.blue(a) * phase + Color.blue(b) * (1 - phase));
        return Color.rgb(red, green, blue);
    }
    static void UpdateGround() {
        for (int i = GameVals.ground.size() - 1; i >= 0 ; i--){
            Ground g = GameVals.ground.get(i);
            g.Update();
            // remove offscreen
            if (g.position.x + g.width < 0){
                GameVals.ground.remove(i);
            }
        }
    }
    static void DrawGround(Canvas canvas) {
        for (int i = 0; i < GameVals.ground.size(); i++){
            GameVals.ground.get(i).Draw(canvas);
        }
    }
    static void DrawScore(Canvas canvas) {
        // draw score
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(ctrlButtonHeight / 4);
        paint.setColor(bgColor);
        String txt;
        float x, y;
        // score
        x = Screen.width / 4;
        y = groundHeight + ctrlButtonHeight / 2;
        paint.setTextAlign(Paint.Align.CENTER);
        txt = "Score: " + Math.round(GameVals.score);
        canvas.drawText(txt, x, y + paint.getTextSize() / 2, paint);
        // high score
        paint.setTextAlign(Paint.Align.CENTER);
        txt = "High Score: " + Math.round(GameVals.highScore);
        x = Screen.width / 4 * 3;
        canvas.drawText(txt, x, y + paint.getTextSize() / 2, paint);
    }
    static void DrawControls(Canvas canvas) {
        paint.setAntiAlias(true);
        paint.setTextSize(ctrlButtonHeight / 2);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        int x, y, w = Screen.width / 2;

        // Jump button
        x = 0;
        y = Screen.height - ctrlButtonHeight * 2;
        paint.setColor(obstacleColors.get(0));
        if(IsKeyDown(obstacleTypes.get(0))) {
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(x, y, x + w, y + ctrlButtonHeight, paint);
            paint.setColor(black);
        } else {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(ctrlButtonStrokeWidth);
            canvas.drawRect(x + ctrlButtonStrokeWidth / 2, y + ctrlButtonStrokeWidth / 2, x + w - ctrlButtonStrokeWidth / 2, y + ctrlButtonHeight - ctrlButtonStrokeWidth / 2, paint);
            paint.setStyle(Paint.Style.FILL);
        }
        canvas.drawText(obstacleTypes.get(0).toUpperCase(), x + w / 2, y + ctrlButtonHeight / 2 + paint.getTextSize() / 3, paint);

        // Kick button
        x = Screen.width / 2;
        y = Screen.height - ctrlButtonHeight * 2;
        paint.setColor(obstacleColors.get(1));
        if(IsKeyDown(obstacleTypes.get(1))) {
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(x, y, x + w, y + ctrlButtonHeight, paint);
            paint.setColor(black);
        } else {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(ctrlButtonStrokeWidth);
            canvas.drawRect(x + ctrlButtonStrokeWidth / 2, y + ctrlButtonStrokeWidth / 2, x + w - ctrlButtonStrokeWidth / 2, y + ctrlButtonHeight - ctrlButtonStrokeWidth / 2, paint);
            paint.setStyle(Paint.Style.FILL);
        }
        canvas.drawText(obstacleTypes.get(1).toUpperCase(), x + w / 2, y + ctrlButtonHeight / 2 + paint.getTextSize() / 3, paint);

        // Slide button
        x = 0;
        y = Screen.height - ctrlButtonHeight;
        paint.setColor(obstacleColors.get(2));
        if(IsKeyDown(obstacleTypes.get(2))) {
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(x, y, x + w, y + ctrlButtonHeight, paint);
            paint.setColor(black);
        } else {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(ctrlButtonStrokeWidth);
            canvas.drawRect(x + ctrlButtonStrokeWidth / 2, y + ctrlButtonStrokeWidth / 2, x + w - ctrlButtonStrokeWidth / 2, y + ctrlButtonHeight - ctrlButtonStrokeWidth / 2, paint);
            paint.setStyle(Paint.Style.FILL);
        }
        canvas.drawText(obstacleTypes.get(2).toUpperCase(), x + w / 2, y + ctrlButtonHeight / 2 + paint.getTextSize() / 3, paint);

        // Launch button
        x = Screen.width / 2;
        y = Screen.height - ctrlButtonHeight;
        paint.setColor(obstacleColors.get(3));
        if(IsKeyDown(obstacleTypes.get(3))) {
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(x, y, x + w, y + ctrlButtonHeight, paint);
            paint.setColor(black);
        } else {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(ctrlButtonStrokeWidth);
            canvas.drawRect(x + ctrlButtonStrokeWidth / 2, y + ctrlButtonStrokeWidth / 2, x + w - ctrlButtonStrokeWidth / 2, y + ctrlButtonHeight - ctrlButtonStrokeWidth / 2, paint);
            paint.setStyle(Paint.Style.FILL);
        }
        canvas.drawText(obstacleTypes.get(3).toUpperCase(), x + w / 2, y + ctrlButtonHeight / 2 + paint.getTextSize() / 3, paint);
    }
}
