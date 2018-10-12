package com.example.ikefluxa.rainbowrunner;

class CollisionRectObj {
    public Vector2 position;
    public float width,
            height;

    CollisionRectObj(Vector2 position, float width, float height) {
        this.position = position;
        this.width = width;
        this.height = height;
    }
}
