package com.example.ikefluxa.rainbowrunner;

import android.graphics.Canvas;
import android.graphics.Color;
import java.util.ArrayList;

class GoldParticleSystem {
    Vector2 position;
    private ArrayList<GoldParticle> particles;
    private float pSize,
            pLife,
            spawnCount;
    
    GoldParticleSystem(float x, float y) {
        particles = new ArrayList<>();
        position = new Vector2(x, y);
        pSize = Constants.blockSize * 4;
        pLife = 20;
        spawnCount = 12;
    }
    void Reset() {
        particles = new ArrayList<>();
    }
    void SpawnParticles() {
        // new particles
        for (int i = 0; i < spawnCount; i++){
            double size = pSize + Math.random() * Constants.blockSize;
            double a = Math.random() * Math.PI * 2 - Math.PI;
            double speed = (Math.random() * pSize * 13 + pSize * 4) / size;
            double vx = Math.cos(a) * speed;
            double vy = Math.sin(a) * speed - pSize * 0.09;
            double g = Math.random() * 30 + 220;
            double b = Math.random() * 50 + 50;
            int c = Color.argb(180, 255, (int) g, (int) b);
            double l = pLife * size / pSize;
            particles.add(
                    new GoldParticle(
                            position.x - size/2,
                            position.y - size/2,
                            vx,
                            vy,
                            size,
                            size,
                            c,
                            l));
        }
    }
    void Update() {
        if (particles.size() == 0){
            return;
        }
        // update each particle's life
        for (int i = particles.size() - 1; i >= 0; i --){
            GoldParticle p = particles.get(i);
            p.Update();
            // remove dead particles
            if (p.life <= 0 || Constants.CheckBounds(p)){
                particles.remove(i);
            }
        }
    }
    void Draw(Canvas canvas) {
        for (int i = 0; i < particles.size(); i ++){
            particles.get(i).Draw(canvas);
        }
    }
}
