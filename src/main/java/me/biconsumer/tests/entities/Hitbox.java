package me.biconsumer.tests.entities;

public class Hitbox {

    private final float width;
    private final float height;
    private final float eye;

    public Hitbox(float width, float height, float eye) {
        this.width = width;
        this.height = height;
        this.eye = eye;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getEye() {
        return eye;
    }
}