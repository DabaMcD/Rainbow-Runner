package com.example.ikefluxa.rainbowrunner;

import android.graphics.Color;

class Constants {
    // General game variables
    static final int black = Color.rgb(16, 9, 20);
    static final int bgColor = Color.rgb(174, 231, 245);

    // Loss

    // Score

    // Commander video

    // Movement

    // Particles

    // Obstacles
    static final int obstacleSpawnMax = 70;

    // Gold

    static void InitializeGame() {
        GameVals.startButton = new StartButton(Screen.width / 2, Screen.height / 6);
        InitializeGround();
        GameVals.cmndrVideo = new CommanderVideo();
        GameVals.pSys = new ParticleSystem(GameVals.cmndrVideo, GameVals.cmndrVideo.position.x, GameVals.cmndrVideo.position.y, psSize, colors, pLife);
        GameVals.obstacles = new ObstacleCollection();
        GameVals.gold = new GoldCollection();
        GameVals.bgText = new BgText();
    }

    static void InitializeGround() {
//        ground = [new Ground(0, SCREEN_WIDTH + obstacleSpawnInterval * moveSpeedX)];
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
