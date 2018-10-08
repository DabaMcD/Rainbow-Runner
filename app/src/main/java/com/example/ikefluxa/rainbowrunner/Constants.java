package com.example.ikefluxa.rainbowrunner;

import android.graphics.Color;

import java.lang.reflect.Array;
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
    static final ArrayList<Integer> colors = new ArrayList<>(Arrays.asList(
            Color.rgb(255, 204, 0),
            Color.rgb(240, 142, 29),
            Color.rgb(58, 186, 169),
            Color.rgb(230, 97, 139),
            Color.rgb(95, 44, 189)
    ));

    // Movement
    static final int moveSpeedX = 8;

    // Particles
    static final int psSize = Constants.cmndrSize;

    // Obstacles
    static final int obstacleSpawnMax = 70;

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
}
