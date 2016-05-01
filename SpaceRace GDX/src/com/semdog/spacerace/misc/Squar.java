package com.semdog.spacerace.misc;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.semdog.spacerace.universe.Mass;
import com.semdog.spacerace.universe.Planet;

/**
 * Created by sam on 2016/05/01.
 */
public class Squar extends Mass {

    public Squar(float x, float y, float dx, float dy, float mass, Planet environment) {
        super(x, y, dx, dy, mass, 5, 5, environment);
    }

    @Override
    public void debugRender(ShapeRenderer renderer) {
        renderer.rect(x, y, 5, 5);
    }

    @Override
    protected float getWidth() {
        return 0;
    }

    @Override
    protected float getHeight() {
        return 0;
    }
}
