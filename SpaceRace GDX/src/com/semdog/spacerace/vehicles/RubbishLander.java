package com.semdog.spacerace.vehicles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Universe;

/**
 * The worst ship in the game.
 * Nobody uses it if they can avoid it
 */

public class RubbishLander extends Ship {

    public RubbishLander(float x, float y, String id) {
        super(x, y, 32, 32, 3000, 200, "rubbish4", id);

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("assets/effects/smallflame.p"), Gdx.files.internal("assets/effects"));
        particleEffect.setPosition(x, y);
        particleEffect.allowCompletion();
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
        if (Gdx.input.isKeyPressed(Keys.A)) {
            r += dt * 60f;
        }
        if (Gdx.input.isKeyPressed(Keys.D)) {
            r -= dt * 60f;
        }

        if (Gdx.input.isKeyPressed(Keys.W) && currentFuel > 0) {
            currentFuel -= power * dt;
            velocity.x -= getCurrentPower() * dt * MathUtils.sin(r * MathUtils.degreesToRadians);
            velocity.y += getCurrentPower() * dt * MathUtils.cos(r * MathUtils.degreesToRadians);
            particleEffect.start();
            Universe.currentUniverse.setCameraShake(1);
            Universe.currentUniverse.loopSound("rubbish", position.x, position.y, 1f);
        } else {
            particleEffect.allowCompletion();
            Universe.currentUniverse.stopSound("rubbish");
        }
    }

    @Override
    public void die(DamageCause reason) {
        super.die(reason);
        Universe.currentUniverse.stopSound("rubbish");
    }

    @Override
    protected float getImpactThreshold() {
        return 350;
    }

    @Override
    public void firePrimary() {
        System.out.println("No weapons mate");
    }

    @Override
    public void fireSecondary() {
        System.out.println("Weren't you listening");
    }

    @Override
    public void dispose() {
        particleEffect.dispose();
    }

    @Override
    public String getGizmoLabel() {
        return "Lander";
    }
}
