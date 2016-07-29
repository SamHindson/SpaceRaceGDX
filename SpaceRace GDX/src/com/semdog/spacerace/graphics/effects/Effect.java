package com.semdog.spacerace.graphics.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

/**
 * An abstract class which all in-game effects are instances of.
 * This provides a handy way of keeping track of all the effects in the universe.
 */

public abstract class Effect implements Disposable {
    float x;
    float y;
    private boolean loaded = false;

    /**
     * Because effects can sometimes be created outside of the main game thread (e.g. during a multiplayer match where it is created from the Kryo thread), the constructor must not load the assets.
     * This method is then implemented by all inheritors.
     */
    void load() {
        loaded = true;
    }

    /**
     * Updates the effect in question.
     *
     * @param dt delta time
     */
    public void update(float dt) {
        if (!loaded) load();
    }

    /**
     * Draws the effect.
     * @param batch The SpriteBatch into which the effect must be drawn
     */
    public abstract void render(SpriteBatch batch);

    /**
     * Different effects will have different criteria for being alive. Some may expire after a certain amount of time, some might expire when they touch something else, etc. This is why it is safer for inheritors to decide for themselves when they are no longer active.
     * @return
     */
    public abstract boolean isAlive();

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
