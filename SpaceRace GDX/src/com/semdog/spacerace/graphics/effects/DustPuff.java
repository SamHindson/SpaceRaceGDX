package com.semdog.spacerace.graphics.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * The DustPuff appears on the surface of the planet when something hits it.
 * Purely aesthetic.
 *
 * @author Sam
 */

public class DustPuff extends Effect {
    private ParticleEffect effect;
    private Color color;

    public DustPuff(float x, float y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;

        effect = new ParticleEffect();
    }

    @Override
    public void load() {
        super.load();
        effect.load(Gdx.files.internal("assets/effects/dustpuff.p"), Gdx.files.internal("assets/effects"));
        effect.getEmitters().get(0).getTint().setColors(new float[]{
                color.r, color.g, color.b, 1, color.r, color.g, color.b, 1
        });
        effect.setPosition(x, y);
        effect.start();
    }

    public void render(SpriteBatch batch) {
        effect.draw(batch);
    }

    public void update(float dt) {
        super.update(dt);
        effect.update(dt);
    }

    /**
     * Dust puffs expire when their particle effect is done emitting.
     */
    @Override
    public boolean isAlive() {
        return !effect.isComplete();
    }

    /**
     * Gets rid of the effect.
     */
    @Override
    public void dispose() {
        effect.dispose();
    }
}
