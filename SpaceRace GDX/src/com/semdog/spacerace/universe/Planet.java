package com.semdog.spacerace.universe;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.semdog.spacerace.graphics.Colors;

/**
 * An object which players can walk around, much like a normal planet, albeit
 * much smaller.
 */

public class Planet implements Disposable, Trackable {

    private Vector2 position;
    private float radius;
    private float mass;

    private String id;

    private Color color;

    private int dustBalls;
    private float[] ballX, ballY, ballR;
    private Color[] dustColors;

    public Planet(String id, float x, float y, float radius) {
        position = new Vector2(x, y);
        this.radius = radius;

        this.id = id;

        mass = radius * radius * 5f;

        color = Colors.getRandomPlanetColor();

        if (radius == 0.1f)
            return;

        dustBalls = 100;
        ballX = new float[dustBalls];
        ballY = new float[dustBalls];
        ballR = new float[dustBalls];
        dustColors = new Color[dustBalls];

        for (int j = 0; j < dustBalls; j++) {
            do {
                ballX[j] = MathUtils.random(-radius, radius);
                ballY[j] = MathUtils.random(-radius, radius);
                ballR[j] = MathUtils.random(10, 100);
            } while (Vector2.dst(0, 0, ballX[j], ballY[j]) + ballR[j] > radius);

            dustColors[j] = new Color(color.r + MathUtils.random(0.05f), color.g + MathUtils.random(0.05f), color.b + MathUtils.random(0.05f), 1.f);
        }
    }

    public void draw(ShapeRenderer shapeRenderer, boolean goggles) {
        shapeRenderer.set(ShapeType.Filled);
        if (goggles)
            shapeRenderer.setColor(new Color(color).mul(0.75f));
        else
            shapeRenderer.setColor(color);

        shapeRenderer.circle(position.x, position.y, radius, 100);

        if (dustBalls != 0 && !goggles)
            for (int h = 0; h < dustBalls; h++) {
                shapeRenderer.setColor(dustColors[h]);
                shapeRenderer.circle(position.x + ballX[h], position.y + ballY[h], ballR[h]);
            }
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getSOI() {
        return radius * 3;
    }

    public float getMass() {
        return mass;
    }

    public float getRadius() {
        return radius;
    }

    public float getGravity(float distance) {
        return ((mass) * (Universe.GRAVITY)) / (float) Math.pow(distance, 2);
    }

    public Color getColor() {
        return color;
    }

    public boolean inRange(float x2, float y2) {
        return Vector2.dst(position.x, position.y, x2, y2) < getSOI();
    }

    public String getID() {
        return id;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void debugRender(ShapeRenderer shapeRenderer) {
        shapeRenderer.set(ShapeType.Filled);
        shapeRenderer.setColor(new Color(color).mul(0.3f));
        shapeRenderer.circle(position.x, position.y, getSOI());
    }

    @Override
    public void dispose() {
        color = null;
        ballX = ballY = ballR = null;
        position = null;
        id = null;
        dustColors = null;
    }

    @Override
    public Color getGizmoColor() {
        return Colors.UI_WHITE;
    }

    @Override
    public String getGizmoLabel() {
        return id;
    }
}
