package com.semdog.spacerace.vehicles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Universe;

/**
 * A space ship that can crash into planets ridiculously fast yet leave the player intact.
 *
 * @author Sam
 */

public class Needle extends Ship {

    public Needle(float x, float y, String id) {
        super(x, y, 32, 32, 2500, 250, "needle", id);

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("assets/effects/needle.p"), Gdx.files.internal("assets/effects"));
        particleEffect.setPosition(x, y);
        particleEffect.allowCompletion();

        pCooldown = 0.025f;

        setMaxHealth(1500);
    }

    @Override
    public void update(float dt, Array<Planet> gravitySources) {
        super.update(dt, gravitySources);
        particleEffect.setPosition(position.x, position.y);
        particleEffect.getEmitters().get(0).getAngle().setHigh(getAngle() * MathUtils.radiansToDegrees - 90);
        particleEffect.update(dt);
    }

    @Override
    public void updateControls(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            r += dt * 150;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            r -= dt * 150;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W) && currentFuel > 1) {
            currentFuel -= power * dt;
            velocity.x -= getCurrentPower() * dt * MathUtils.sin(r * MathUtils.degreesToRadians);
            velocity.y += getCurrentPower() * dt * MathUtils.cos(r * MathUtils.degreesToRadians);
            particleEffect.start();
            Universe.currentUniverse.loopSound("needle", position.x, position.y, -1f);
            Universe.currentUniverse.setCameraShake(10);
        } else {
            particleEffect.allowCompletion();
            Universe.currentUniverse.stopSound("needle");
        }
    }

    @Override
    protected void explode(DamageCause cause) {
        rud();
        velocity.set(Vector2.Zero);
        Universe.currentUniverse.stopSound("needle");
        if (pilot != null) {
            pilot.exitShip();
        }
    }

    @Override
    public void firePrimary() {

    }

    @Override
    public void fireSecondary() {

    }

    @Override
    protected float getImpactThreshold() {
        return 300;
    }

    @Override
    public void dispose() {
        particleEffect.dispose();
    }

    @Override
    public String getGizmoLabel() {
        return "Needle";
    }
}
