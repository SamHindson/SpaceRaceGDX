package com.semdog.spacerace.graphics.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

/**
 * An abstract class which all in-game effects are instances of.
 * This provides a handy way of keeping track of all the effects in the universe.
 *
 * @author Sam
 */

public abstract class Effect implements Disposable {
    float x;
    float y;
    private boolean loaded = false;

    void load() {
        loaded = true;
    }

    public void update(float dt) {
        if (!loaded) load();
    }

    public abstract void render(SpriteBatch batch);

    public abstract boolean isAlive();

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
