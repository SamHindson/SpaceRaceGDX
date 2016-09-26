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
 *
 * @author Sam
 */

public class Planet implements Disposable, Trackable {

    private Vector2 position;
    private float radius;
    private float mass;

    private String id;

    private Color color;

    private int specks;
    private float[] speckX, speckY, speckR, speckM, speckW;

    public Planet(String id, float x, float y, float radius, Color color, int seed) {
        position = new Vector2(x, y);
        this.radius = radius;
        this.color = color;
        this.id = id;

        mass = radius * radius * 5f;

        //  Makes sure the specks aren't drawn if it is a debug planet.
        if (radius == 0.1f) {
            specks = 0;
            return;
        }

        specks = 200;
        speckX = new float[specks];
        speckY = new float[specks];
        speckR = new float[specks];
        speckW = new float[specks];
        speckM = new float[specks];

        float maxSize = 0.5f * (radius - 50);

        MathUtils.random.setSeed(seed);

        for (int j = 0; j < specks; j++) {
            float size = MathUtils.random(1, maxSize);
            float angle = MathUtils.random(MathUtils.PI2);
            float distance = MathUtils.random(radius - size * (float) Math.sqrt(2));

            speckX[j] = position.x + distance * MathUtils.cos(angle);
            speckY[j] = position.y + distance * MathUtils.sin(angle);
            speckR[j] = MathUtils.random(MathUtils.PI2);
            speckM[j] = MathUtils.random(0.5f, 1);
            speckW[j] = size;
        }
    }

    public Planet(String id, float x, float y, float radius) {
        this(id, x, y, radius, Colors.getRandomPlanetColor(), MathUtils.random(0xFFFFFF));
    }

    public void draw(ShapeRenderer shapeRenderer, boolean goggles) {
        shapeRenderer.set(ShapeType.Filled);
        if (goggles)
            shapeRenderer.setColor(new Color(color).mul(0.75f));
        else
            shapeRenderer.setColor(color);

        shapeRenderer.circle(position.x, position.y, radius, 100);
        shapeRenderer.setColor(new Color(color).mul(0.75f));
        shapeRenderer.circle(position.x, position.y, radius - 5, 100);

        //  TODO figure out a new planet rendering strategy.
        //  In my defense, what do 8-bit planets look like??? They can't be round ???

        shapeRenderer.set(ShapeType.Filled);
        for (int i = 0; i < specks; i++) {
            shapeRenderer.setColor(new Color(color).mul(speckM[i]));
            shapeRenderer.identity();
            shapeRenderer.translate(speckX[i], speckY[i], 0);
            shapeRenderer.rotate(0, 0, 1, speckR[i] * MathUtils.radiansToDegrees);
            shapeRenderer.rect(0, 0, speckW[i], speckW[i]);
            shapeRenderer.identity();
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
        position = null;
        id = null;
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
