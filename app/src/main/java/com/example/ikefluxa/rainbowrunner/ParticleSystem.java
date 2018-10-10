package com.example.ikefluxa.rainbowrunner;

import android.graphics.Canvas;

import java.util.ArrayList;

class ParticleSystem {
    private CommanderVideo parent;
    private double x,
            size,
            pLife,
            trailLength,
            y;
    private ArrayList<Integer> pColors;
    private Vector2 position;
    private ArrayList<ArrayList<Particle>> particles;

    ParticleSystem(CommanderVideo parent, float x, float y, float size, ArrayList<Integer>  pColors, float pLife) {
        this.parent = parent;
        particles = new ArrayList<>();
        position = new Vector2(x, y);
        this.size = size;
        this.pColors = pColors;
        this.pLife = pLife;
        trailLength = (float) Math.ceil(position.x / Constants.moveSpeedX);
        Reset();
    }
    void Reset() {
        // initialize particles
        for (int i = 0; i < pColors.size(); i++){
            if(particles.size() > i) {
                particles.remove(i);
            }
            particles.add(i, new ArrayList<Particle>());
        }
    }
    void Update() {
        float growth = Constants.blockSize * 4;
        if (size < parent.height){
            size += growth;
            if (size > parent.height){
                size = parent.height;
            }
        } else if (size > parent.height){
            size -= growth;
            if (size < parent.height){
                size = parent.height;
            }
        }
        position.x = parent.position.x;
        position.y = (float) (parent.position.y + parent.height - size);

        // update each particle's life
        for (int i = 0; i < particles.size(); i++){
            for (int j = 0; j < particles.get(i).size(); j++){
                particles.get(i).get(j).Update();
            }
        }

        // new particles
        // set size for new particle
        double zs = Math.floor(size / pColors.size());
        // set remainder to compensate overall length
        double zr = size - zs * pColors.size();
        // spawn new particles for each array in particles
        for (int i = 0; i < particles.size(); i++){
            ArrayList<Particle> zp = particles.get(i);
            double zy = position.y + i * zs;
            // add remainer to last particle length
            if (i == particles.size() - 1 ){zs += zr;}
            float zw = Constants.moveSpeedX + parent.position.x - parent.lastPosition.x;
            zp.add(
                    new Particle(
                            position.x - zw,
                            zy,
                            zw,
                            zs,
                            pColors.get(i),
                            pLife));
            // remove excess particles
            if (zp.size() > trailLength){
                zp.remove(0);
            }
        }
    }
    void Draw(Canvas canvas) {
        for (int i = 0; i < particles.size(); i++){
            for (int j = 0; j < particles.get(i).size(); j++){
                particles.get(i).get(j).Draw(pLife, canvas);
            }
        }
    }
}
