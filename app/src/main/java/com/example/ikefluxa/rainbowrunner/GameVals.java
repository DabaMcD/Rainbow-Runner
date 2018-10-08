package com.example.ikefluxa.rainbowrunner;

class GameVals {
    // General game variables
    static String gameState = GameStates.menu;
    static StartButton startButton;
    static BgText bgText;

    // Loss
    static int lossTimer = 0;

    // Score
    static int score = 0;

    // Commander video
    static CommanderVideo cmndrVideo;

    // Movement

    // Particles
    static ParticleSystem pSys;

    // Obstacles
    static ObstacleCollection obstacles;
    static int launchTimer = 0;
    static int obstacleTimer = 0;
    static int obstacleSpawnInterval = 70;

    // Gold
    static GoldCollection gold;
}
