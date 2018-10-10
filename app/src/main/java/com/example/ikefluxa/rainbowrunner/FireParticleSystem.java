package com.example.ikefluxa.rainbowrunner;

import android.graphics.Canvas;

import java.util.ArrayList;

class FireParticleSystem {
    public Obstacle parent;
    private ArrayList<FireParticle> particles;
    private Vector2 position;
    private double pSize;
    private float pLife, width;
    private int spawnCount;

    FireParticleSystem(float x, float y, float w, Obstacle p) {
        particles = new ArrayList<>();
        position = new Vector2(x, y);
        pSize = Constants.blockSize * 0.55;
        pLife = 20;
        spawnCount = 1;
        width = w;
        parent = p;
    }
    void SpawnParticles() {
        // new particles
        for (int i = 0; i < spawnCount; i++){
            double vy = Math.random() * 1.7 - 2;
            particles.add(
                    new FireParticle(
                            (float) (Math.random() * (width - pSize)),
                            position.y,
                            0,
                            (float) vy,
                            (float) pSize,
                            (float)pSize,
                            pLife)
            );
        }
    }
    void Update() {
        //position = new Vector2(mouseX, mouseY);
        if (parent != null){
            position = parent.position.Clone();
        }
        SpawnParticles();
        // update each particle's life
        for (int i = particles.size() - 1; i >= 0; i --){
            FireParticle p = particles.get(i);
            p.Update();
            // remove dead particles
            if (p.life <= 0){
                particles.remove(i);
            }
        }
    }
    void Draw(Canvas canvas) {
        for (int i = 0; i < particles.size(); i++){
            particles.get(i).Draw(position.x, canvas);
        }
    }
}
