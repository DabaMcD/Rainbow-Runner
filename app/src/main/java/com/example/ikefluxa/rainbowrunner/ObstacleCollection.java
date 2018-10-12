package com.example.ikefluxa.rainbowrunner;

import android.graphics.Canvas;
import java.util.ArrayList;

class ObstacleCollection {
    private ArrayList<Obstacle> obstacles;
    private ArrayList<FireParticleSystem> pSystems;
    private ArrayList<Triangle> debris;
    
    ObstacleCollection() {
        obstacles = new ArrayList<>();
        pSystems = new ArrayList<>();
        debris = new ArrayList<>();
    }
    void Reset() {
        obstacles = new ArrayList<>();
        pSystems = new ArrayList<>();
        debris = new ArrayList<>();
    }
	private void SpawnDebris(Obstacle rect, CollisionRectObj kickRect) {
        float x = rect.position.x;
        float x2 = (float) (rect.position.x + rect.width);
        float y = kickRect.position.y + kickRect.height/2;
        float breakDist;
        double min = 1.0;
        int max = 5;
        double scaleX = 1.85;
        double closeScaleX = 0.85;
        double closeRot = 0.065;
        double farRot = 0.055;

        // top left
        breakDist = (float) (Math.random() * (max - min) + min);
        debris.add(
                new Triangle(
                        new Vector2(rect.position.x - breakDist, rect.position.y - breakDist),
                        new Vector2((float) (rect.position.x + rect.width - breakDist), rect.position.y - breakDist),
                        new Vector2(x - breakDist, y - breakDist),
                        new Vector2((float) (Constants.moveSpeedX * scaleX * closeScaleX), 0),
                        -Math.PI * closeRot,
                        rect.color));
        // top right
        breakDist = (float) (Math.random() * (max - min) + min);
        debris.add(
                new Triangle(
                        new Vector2((float) (rect.position.x + rect.width + breakDist), rect.position.y - breakDist),
                        new Vector2(x + breakDist, y - breakDist),
                        new Vector2(x2 + breakDist, y - breakDist),
                        new Vector2((float) (Constants.moveSpeedX * scaleX), 0),
                        -Math.PI * farRot,
                        rect.color));
        // bottom right
        breakDist = (float) (Math.random() * (max - min) + min);
        debris.add(
                new Triangle(
                        new Vector2(x + breakDist, y + breakDist),
                        new Vector2(x2 + breakDist, y + breakDist),
                        new Vector2((float) (rect.position.x + rect.width + breakDist), (float) (rect.position.y + rect.height + breakDist)),
                        new Vector2((float) (Constants.moveSpeedX * scaleX), 0),
                        Math.PI * farRot,
                        rect.color));
        // bottom left
        breakDist = (float) (Math.random() * (max - min) + min);
        debris.add(
                new Triangle(
                        new Vector2(rect.position.x - breakDist, (float) (rect.position.y + rect.height + breakDist)),
                        new Vector2((float) (rect.position.x + rect.width - breakDist), (float) (rect.position.y + rect.height + breakDist)),
                        new Vector2(x - breakDist, y + breakDist),
                        new Vector2((float) (Constants.moveSpeedX * scaleX * closeScaleX), 0),
                        Math.PI * closeRot,
                        rect.color));
    }
	void CheckCollision() {
        for (int i = obstacles.size() - 1; i >= 0; i--) {
            CommanderVideo c = GameVals.cmndrVideo;
            Obstacle o = obstacles.get(i);
            // launch
            if (Constants.obstacleTypes.get(o.type).equals("launch")) {
                if(c.CanLaunch()) {
                    double halfX = c.position.x + c.width / 2;
                    if (halfX >= o.position.x && halfX < o.position.x + o.width) {
                        o.position.y -= o.height;
                        c.Launch();
                    }
                }
            }
            // collision, change game state to loss
            else {
                // kick check
                if (c.isKicking && Constants.obstacleTypes.get(o.type).equals("kick") && Constants.Overlap(o, c.KickRect())) {
                    Obstacle temporaryObstacle = obstacles.get(i);
                    obstacles.remove(i);
                    SpawnDebris(temporaryObstacle, c.KickRect());
                    continue;
                }
                // collision
                if (Constants.Overlap(o, c.CollisionRect())) {
                    Constants.LoseGame();
                    return;
                }
            }
        }
    }
	void Update() {
        // update obstacles
        for (int i = obstacles.size() - 1; i >= 0; i --) {
            Obstacle o = obstacles.get(i);
            o.Update();
            // check bounds
            if (o.position.x + o.width < 0) {
                obstacles.remove(i);
            }
        }
        // update timer
        if (GameVals.launchTimer <= 0) {
            GameVals.obstacleTimer += 1;
        } else {
            GameVals.launchTimer --;
        }

        // spawn new obstacle on timer complete
        if (GameVals.obstacleTimer >= GameVals.obstacleSpawnInterval) {
            GameVals.obstacleTimer = 0;
            if (GameVals.obstacleSpawnInterval > Constants.obstacleSpawnMin) {
                GameVals.obstacleSpawnInterval --;
            }
            // pick obstacle type
            // spawn launches less frequently
            int obIndex = 0;
            int launchRatio = 3;
            int total = (launchRatio - 1) * (Constants.obstacleTypes.size() - 1) + Constants.obstacleTypes.size();
            double rand = Math.random() * total;
            for (int i = 0; i < Constants.obstacleTypes.size(); i ++) {
                if (rand < (i + 1) * launchRatio) {
                    obIndex = i;
                    break;
                }
            }

            //obIndex = 1; // debug obstacle type
            Obstacle o = new Obstacle(obIndex);
            obstacles.add(o);

            // additional spawning events
            switch (Constants.obstacleTypes.get(obIndex)) {
                case "launch":
                    // ground
                    GameVals.launchTimer = Constants.launchTimerMax;
                    GameVals.ground.add(new Ground(Screen.width, (int) (o.width * 2)));
                    GameVals.ground.add(new Ground(Screen.width + Constants.launchTimerMax * Constants.moveSpeedX, GameVals.obstacleSpawnInterval * Constants.moveSpeedX));
                    // gold
                    if (Math.random() < 0.5) {
                        GameVals.gold.Add(new Gold(Screen.width + Constants.moveSpeedX * Constants.launchTimerMax / 2, Constants.groundHeight - Constants.cmndrSize * 7 / 4));
                    }
                    break;
                case "slide":
                    // gold
                    if (Math.random() < 0.33) {
                        o = obstacles.get(obstacles.size() - 1);
                        Gold g = new Gold((float) (o.position.x + o.width), Constants.groundHeight - Constants.cmndrSize);
                        float offset = (float) ((Constants.obstacleSpawnMin * Constants.moveSpeedX - o.width - g.width) / 2);
                        g.position.x += offset;
                        GameVals.gold.Add(g);
                    }
                case "jump":
                    pSystems.add(new FireParticleSystem(o.position.x, o.position.y, (float) o.width, o));
                    // prime the fire/generate particles
                    for (int i = 0; i < 10; i++) {
                        pSystems.get(pSystems.size() - 1).Update();
                    }
                    break;
            }
            // ground for non launch obstacles
            if (!Constants.obstacleTypes.get(obIndex).equals("launch")) {
                GameVals.ground.add(new Ground(Screen.width, GameVals.obstacleSpawnInterval * Constants.moveSpeedX));
            }
        }

        // update particle systems
        for (int i = pSystems.size() - 1; i >= 0; i --) {
            FireParticleSystem p = pSystems.get(i);
            p.Update();
            // remove parentless
            if (obstacles.indexOf(p.parent) < 0) {
                pSystems.remove(i);
            }
        }
        
        // update debris	
        for (int i = debris.size() - 1; i >= 0; i--) {
            Triangle d = debris.get(i);
            d.Update();
            // remove offscreen debris
            if (d.points.get(0).x > Screen.width * 2) {
                debris.remove(i);
            }
        }
    }
	void Draw(Canvas canvas) {
        // draw particle systems
        for (int i = 0; i < pSystems.size(); i++) {
            pSystems.get(i).Draw(canvas);
        }

        // draw obstacles
        for (int i = 0; i < obstacles.size(); i++) {
            obstacles.get(i).Draw(canvas);
        }

        // draw debris	
        for (int i = 0; i < debris.size(); i++) {
            debris.get(i).Draw(canvas);
        }
    }
}
