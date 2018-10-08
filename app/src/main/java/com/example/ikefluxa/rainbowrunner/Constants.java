package com.example.ikefluxa.rainbowrunner;

import android.graphics.Color;
import java.util.ArrayList;
import java.util.Arrays;

class Constants {
    // General game variables
    static final int black = Color.rgb(16, 9, 20);
    static final int bgColor = Color.rgb(174, 231, 245);
    static final int groundHeight = Math.round((Screen.height / 4) * 3);

    // Loss

    // Score

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
    static final int obstacleSpawnMax = 70;
    static final int obstacleSpawnMin = 22;

    // Gold

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
        gameState = GameStates.loss;
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

    static boolean Overlap(CollisionRectObj obj1, CollisionRectObj obj2) {
        if (obj1.position.x < obj2.position.x + obj2.width &&
                obj1.position.x + obj1.width > obj2.position.x &&
                obj1.position.y < obj2.position.y + obj2.height &&
                obj1.position.y + obj1.height > obj2.position.y){
            return true;
        }
        return false;
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
                return Touch.y < Constants.groundHeight + (Screen.height - Constants.groundHeight) / 2 &&
                        Touch.x > Screen.width / 2;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
