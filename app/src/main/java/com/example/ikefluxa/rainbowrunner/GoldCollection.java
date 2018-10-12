package com.example.ikefluxa.rainbowrunner;

import android.graphics.Canvas;

import java.util.ArrayList;

class GoldCollection {
    private ArrayList<Gold> gold;
    private GoldParticleSystem goldPSys;
    GoldCollection() {
        gold = new ArrayList<>();
        goldPSys = new GoldParticleSystem(0, 0);
    }
    void Reset() {
        gold = new ArrayList<>();
        goldPSys.Reset();
    }
    void Add(Gold g) {
        gold.add(g);
    }
    private void CheckGoldCollision(){
        for (int i = gold.size() - 1; i >= 0; i--){
            Gold g = gold.get(i);
            if (Constants.Overlap(g.CollisionRect(), GameVals.cmndrVideo.CollisionRect())){
                GameVals.score += Constants.goldPoints;

                // temp gold particle solution
                goldPSys.position = g.position.Clone();
                goldPSys.position.y += g.height / 2;
                goldPSys.SpawnParticles();

                gold.remove(i);
            }
        }
    } // give points for gold collected
    void Update() {
        for (int i = gold.size() - 1; i >= 0 ; i --){
            Gold g = gold.get(i);
            g.Update();
            if (g.position.x + g.width < 0){
                gold.remove(i);
            }
        }
        CheckGoldCollision();
        goldPSys.Update();
    }
    void Draw(Canvas canvas) {
        for (int i = 0; i < gold.size(); i++){
            gold.get(i).Draw(canvas);
        }
    }
    void DrawPSys(Canvas canvas) {
        goldPSys.Draw(canvas);
    }
}
