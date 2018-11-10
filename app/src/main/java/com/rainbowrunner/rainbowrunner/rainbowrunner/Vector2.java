package com.rainbowrunner.rainbowrunner.rainbowrunner;

class Vector2 {
    float x, y;

    public Vector2(float x, float y){
        this.x = x;
        this.y = y;
    }
    void Add(Vector2 vector2) {
        x += vector2.x;
        y += vector2.y;
    }
    void Sub(Vector2 vector2) {
        x -= vector2.x;
        y -= vector2.y;
    }
    Vector2 Clone() {
        return new Vector2(this.x, this.y);
    }
}
