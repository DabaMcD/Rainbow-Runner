package com.rainbowrunner.rainbowrunner.rainbowrunner;

import java.util.ArrayList;

class GameVals {
    // General game variables
    static String gameState = GameStates.menu;
    static StartButton startButton;
    static BgText bgText;
    static ArrayList<Ground> ground;

    // Loss
    static int lossTimer = 0;

    // Score
    static double score = 0;
    static int highScore = 0;

    // Commander video
    static CommanderVideo cmndrVideo;

    // Movement
    static double g = Constants.blockSize * 0.245;

    // Particles
    static ParticleSystem pSys;
    static float pLife = 10 / 8;

    // Obstacles
    static ObstacleCollection obstacles;
    static int launchTimer = 0;
    static int obstacleTimer = 0;
    static int obstacleSpawnInterval = 22;

    // Gold
    static GoldCollection gold;
}
