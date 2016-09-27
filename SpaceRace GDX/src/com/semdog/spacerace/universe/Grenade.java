package com.semdog.spacerace.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.graphics.effects.Explosion;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.players.Player;

/**
 * A mass spawned by the player that detonates after a set amount of time, or on
 * contact with masses/other players.
 *
 * @author Sam
 */

public class Grenade extends Mass implements Trackable {

    private boolean exploded = false, bouncing = true;
    private float timer = 0;
    private float beepTimer = 0;
    private int owner;

    private Sprite sprite;
    private ParticleEffect trail;

    public Grenade(float x, float y, float dx, float dy, int owner) {
        super(x, y, dx, dy, 2, 2, 10, null, "grenade");

        bounds = new Rectangle(x, y, 1, 1);

        trail = new ParticleEffect();
        trail.setPosition(x, y);
        sprite = new Sprite(Art.get("grenade"));
        sprite.setSize(2, 2);
        sprite.setOriginCenter();
    }

    @Override
    protected void load() {
        super.load();
        trail.load(Gdx.files.internal("assets/effects/grenadetrail.p"), Gdx.files.internal("assets/effects"));
    }

    @Override
    public void update(float dt, Array<Planet> gravitySources) {
        super.update(dt, gravitySources);

        trail.setPosition(position.x, position.y);
        trail.update(dt);

        sprite.rotate(dt * 2000);
        sprite.setPosition(position.x, position.y);

        beepTimer += dt;
        timer += dt;

        if (beepTimer > 0.5f) {
            beepTimer = 0;
            Universe.currentUniverse.playSound("grenadebeep", position.x, position.y, -0.9f);
        }
        bounds.set(position.x, position.y, 1, 1);

        if (timer > 3.5f && !(Universe.currentUniverse instanceof MultiplayerUniverse)) {
            explode();
        }
    }

    @Override
    protected void handleMassCollision(Mass mass) {
        explode();
    }

    @Override
    protected float getImpactThreshold() {
        return 0;
    }

    public void render(SpriteBatch batch) {
        trail.draw(batch);
        sprite.draw(batch);
    }

    @Override
    protected void handlePlanetCollision(float speed, boolean e) {
        if (!exploded) {
            exploded = true;
            Universe.currentUniverse.addEffect(new Explosion(position.x, position.y));
            die(DamageCause.EXPLOSION);
        }
    }

    @Override
    protected void hitPlayer(Player player) {
        if (!exploded) {
            Universe.currentUniverse.playerKilled(DamageCause.EXPLOSION);
            explode();
        }
    }

    public void explode() {
        exploded = true;
        Universe.currentUniverse.addEffect(new Explosion(position.x, position.y));
        die(DamageCause.EXPLOSION);
    }

    @Override
    public boolean isAlive() {
        return !exploded;
    }

    public float getWidth() {
        return 1;
    }

    public float getHeight() {
        return 1;
    }

    @Override
    public void die(DamageCause reason) {
        super.die(reason);
    }

    @Override
    public void dispose() {
        Gdx.app.postRunnable(() -> trail.dispose());
    }

    @Override
    public Color getGizmoColor() {
        return Colors.P_RED;
    }

    @Override
    public String getGizmoLabel() {
        return "LIVE GRENADE!";
    }
}