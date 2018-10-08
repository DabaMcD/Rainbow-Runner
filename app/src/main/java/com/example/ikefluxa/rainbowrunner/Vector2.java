package com.example.ikefluxa.rainbowrunner;

public class Vector2 {
    float x, y;
    public Vector2(float x, float y){
        this.x = x;
        this.y = y;
    }
    public void Add(Vector2 vector2) {
        x += vector2.x;
        y += vector2.y;
    }
    public void Sub(Vector2 vector2) {
        x -= vector2.x;
        y -= vector2.y;
    }
    public void Scale(double constant) {
        x *= constant;
        y *= constant;
    }
    public void Normalize() {
        // cannot normalize zero vector
        if (x == 0 && y == 0){
            return;
        }
        double hyp = Math.sqrt(x * x + y * y);
        hyp = 1/hyp;
        x *= hyp;
        y *= hyp;
    }
    public double GetLength() {
        return Math.sqrt(x * x + y * y);
    }
    public double GetDistance(Vector2 v) {
        Vector2 a = new Vector2(this.x, this.y);
        a.Sub(v);
        return a.GetLength();
    }
    public Vector2 Clone() {
        return new Vector2(this.x, this.y);
    }
    public boolean Equals(Vector2 vector2) {
        return x == vector2.x && y == vector2.y;
    }
    public Vector2 Zero() {
        return new Vector2(0, 0);
    }
    public float Dot(Vector2 v) {
        return x * v.x + y * v.y;
    }
}
