package com.semdog.spacerace.universe;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.effects.Explosion;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.players.Player;

/**
 * A mass which is exploded by the player for fun and profit.
 */

public class Box extends Mass {
    private Sprite sprite, silhouette;

    public Box(float x, float y, String id) {
        super(x, y, 0, 0, 100, 25, 25, null, id);

        sprite = new Sprite(Art.get("box"));
        sprite.setOriginCenter();
        sprite.setSize(width, height);

        silhouette = new Sprite(Art.get("box_sil"));
        silhouette.setOriginCenter();
        silhouette.setSize(width, height);

        setMaxHealth(150);

        Universe.currentUniverse.addBox(this);
    }

    @Override
    public void update(float dt, Array<Planet> gravitySources) {
        super.update(dt, gravitySources);
        sprite.setOriginCenter();
        silhouette.setOriginCenter();
        sprite.setPosition(position.x - width / 2, position.y - height / 2);

        sprite.setRotation(angle * MathUtils.radiansToDegrees);

        if (ouchTime > 0) {
            silhouette.setRotation(angle * MathUtils.radiansToDegrees);
            silhouette.setPosition(position.x - width / 2, position.y - height / 2);
            ouchTime -= dt * 5f;
        } else {
            ouchTime = 0;
        }

        if (onGround) {
            position.set(environment.getX() + (environment.getRadius() + width / 2) * MathUtils.cos(angle), environment.getY() + (environment.getRadius() + height / 2) * MathUtils.sin(angle));
        }
    }

    @Override
    public void doDamage(float amount, DamageCause reason) {
        super.doDamage(amount, reason);
    }

    @Override
    public void die(DamageCause reason) {
        super.die(reason);
        Universe.currentUniverse.addEffect(new Explosion(position.x, position.y));
        Universe.currentUniverse.killBox(this);
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        sprite.draw(batch);

        if (ouchTime > 0) {
            silhouette.setAlpha(MathUtils.random(0.5f, 1));
            silhouette.draw(batch);
        }
    }

    @Override
    protected float getImpactThreshold() {
        return 500;
    }

    @Override
    protected void hitPlayer(Player player) {

    }

    @Override
    public void dispose() {

    }
}
