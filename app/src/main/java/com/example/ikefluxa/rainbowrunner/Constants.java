package com.example.ikefluxa.rainbowrunner;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Arrays;

class Constants {
    // General game variables
    static final int black = Color.rgb(16, 9, 20);
    static final int bgColor = Color.rgb(174, 231, 245);
    static int groundHeight;
    static final int scoreTextSize = 22;
    static final int ctrlTextSize = 18;

    // Loss
    static final int lossTimerMax = 60;
    static final int flashes = 4;

    // Score
    static final float scoreIncrement = 1 / 50;

    // Commander video
    static final int cmndrSize = 150;
    static final int blockSize = 10;
    static final ArrayList<Integer> colors = new ArrayList<>(Arrays.asList(
            Color.rgb(255, 204, 0),
            Color.rgb(240, 142, 29),
            Color.rgb(58, 186, 169),
            Color.rgb(230, 97, 139),
            Color.rgb(95, 44, 189)
    ));

    // Movement
    static final int moveSpeedX = 8;
    static final double launchVelocityY = -blockSize * 1.35;
    static final double launchScale = 0.137;
    static final double jumpScale = 0.080;
    static final double jumpVelocity = -blockSize * 2.06;

    // Particles
    static final int psSize = 150;

    // Obstacles
    static final ArrayList<String> obstacleTypes = new ArrayList<>(Arrays.asList(
            "jump",
            "kick",
            "slide",
            "launch"
    ));
    static final int obstacleSpawnMax = 70;
    static final int obstacleSpawnMin = 22;
    static final int launchTimerMax = 40;
    static final ArrayList<Integer> obstacleColors = new ArrayList<>(Arrays.asList(
            Color.rgb(224, 42, 18),
            Color.rgb(191, 61, 224),
            Color.rgb(22, 88, 196),
            Color.rgb(255, 105, 173)
    ));

    // Gold
    static final int goldPoints = 100;

    // Other
    private static Paint paint = new Paint();

    static void InitializeGame() {
        GameVals.startButton = new StartButton(Screen.width / 2, Screen.height / 6);
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
        GameVals.highScore = GameVals.score > GameVals.highScore ? GameVals.score : GameVals.highScore;
        GameVals.gameState = GameStates.loss;
    }
    static void ResetGame() {
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
    static void Collide(CommanderVideo obj1, Ground obj2) {
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
    static boolean Overlap(CommanderVideo obj1, Ground obj2) {
        if (obj1.position.x < obj2.position.x + obj2.width &&
                obj1.position.x + obj1.width > obj2.position.x &&
                obj1.position.y < obj2.position.y + obj2.height &&
                obj1.position.y + obj1.height > obj2.position.y){
            return true;
        }
        return false;
    } // todo: check for occurrences and make multiple functions taking in different types according to the uses
    static boolean Overlap(Obstacle obj1, CollisionRectObj obj2) {
        if (obj1.position.x < obj2.position.x + obj2.width &&
                obj1.position.x + obj1.width > obj2.position.x &&
                obj1.position.y < obj2.position.y + obj2.height &&
                obj1.position.y + obj1.height > obj2.position.y){
            return true;
        }
        return false;
    }
    static boolean Overlap(CollisionRectObj obj1, Obstacle obj2) {
        if (obj1.position.x < obj2.position.x + obj2.width &&
                obj1.position.x + obj1.width > obj2.position.x &&
                obj1.position.y < obj2.position.y + obj2.height &&
                obj1.position.y + obj1.height > obj2.position.y){
            return true;
        }
        return false;
    }
    static boolean Overlap(CollisionRectObj obj1, CollisionRectObj obj2) {
        if (obj1.position.x < obj2.position.x + obj2.width &&
                obj1.position.x + obj1.width > obj2.position.x &&
                obj1.position.y < obj2.position.y + obj2.height &&
                obj1.position.y + obj1.height > obj2.position.y){
            return true;
        }
        return false;
    }
    static boolean CheckBounds(GoldParticle obj) {
        return (obj.position.x + obj.width < 0 || obj.position.x > Screen.width ||
                obj.position.y + obj.height < 0 || obj.position.y > Screen.height);
    }
    static boolean IsKeyDown(String key) {
        if(Touch.isTouching) {
            if (key.equals("jump")) {
                return Touch.y > Constants.groundHeight &&
                        Touch.y < Constants.groundHeight + (Screen.height - Constants.groundHeight) / 2 &&
                        Touch.x < Screen.width / 2;
            } else if (key.equals("slide")) {
                return Touch.y > Constants.groundHeight + (Screen.height - Constants.groundHeight) / 2 &&
                        Touch.x < Screen.width / 2;
            } else if (key.equals("kick")) {
                return Touch.y > Constants.groundHeight &&
                        Touch.y < Constants.groundHeight + (Screen.height - Constants.groundHeight) / 2 &&
                        Touch.x > Screen.width / 2;
            } else if (key.equals("launch")) {
                return Touch.y < Constants.groundHeight + (Screen.height - Constants.groundHeight) / 2 &&
                        Touch.x > Screen.width / 2;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    static boolean IsKeyPressed(String key) {
        if(Touch.isTouching && Touch.justTouched < 2) {
            if (key.equals("jump")) {
                return Touch.y > Constants.groundHeight &&
                        Touch.y < Constants.groundHeight + (Screen.height - Constants.groundHeight) / 2 &&
                        Touch.x < Screen.width / 2;
            } else if (key.equals("slide")) {
                return Touch.y > Constants.groundHeight + (Screen.height - Constants.groundHeight) / 2 &&
                        Touch.x < Screen.width / 2;
            } else if (key.equals("kick")) {
                return Touch.y > Constants.groundHeight &&
                        Touch.y < Constants.groundHeight + (Screen.height - Constants.groundHeight) / 2 &&
                        Touch.x > Screen.width / 2;
            } else if (key.equals("launch")) {
                return Touch.y > Constants.groundHeight + (Screen.height - Constants.groundHeight) / 2 &&
                        Touch.x > Screen.width / 2;
            } else {
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
    } // todo: a color problem might be here
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
        paint.setTextSize(scoreTextSize);
        String txt;
        float x, y;
        paint.setColor(Color.rgb(255, 255, 255));
        paint.setStrokeWidth(4);
        // score
        x = 10;
        y = Screen.height - 25;
        paint.setTextAlign(Paint.Align.LEFT);
        txt = "Score: " + Math.round(GameVals.score);
        canvas.drawText(txt, x, y, paint);
        // high score
        paint.setTextAlign(Paint.Align.RIGHT);
        txt = "High Score: " + Math.round(GameVals.highScore);
        x = Screen.width - 10 - paint.measureText(txt);
        canvas.drawText(txt, x, y, paint);
    }
    static void DrawControls(Canvas canvas) {
//        int txtSize = ctrlTextSize;
//        float x, y, txtWidth;
//        String txt, ctrlTxt;
//        int bufferX = 10;
//        int bufferY = 18;
//        int obIndex = 0;
//        int ctrlOffset = 11;
//        int rectOffsetX = 8;
//        int rectOffsetY = 5;
//        paint.setTextAlign(Paint.Align.LEFT);
//        paint.setTextSize(txtSize);

        // todo: draw controls here
    }
}
