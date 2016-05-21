package com.semdog.spacerace.misc;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.universe.Mass;
import com.semdog.spacerace.universe.Planet;

/**
 * Created by sam on 2016/05/01.
 *
 * A test mass to work on collision physics and all that jazz
 */
public class Squar extends Mass {

    public Squar(float x, float y, float dx, float dy, float mass, Planet environment) {
        super(x, y, dx, dy, mass, 100, 100, environment);
    }

    @Override
    protected float getImpactThreshold() {
        return 0;
    }

    @Override
    public void debugRender(ShapeRenderer renderer) {
        renderer.setColor(new Color(hashCode()));
        renderer.rect(x - getWidth() / 2, y - getHeight() / 2, getWidth(), getHeight());
    }

    @Override
    protected float getWidth() {
        return 100;
    }

    @Override
    protected float getHeight() {
        return 100;
    }

    @Override
    protected void hitPlayer(Player player) {
        // TODO Auto-generated method stub

    }
}
