package com.semdog.spacerace.collectables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.universe.Collideable;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Universe;

public abstract class Collectible implements Disposable {

    public static final byte PLAYER = 0x01;
    public static final byte SHIP = 0x10;

    protected float x;
    protected float y;
    protected float h, a;
    protected int target;

    float width;
    float height;
    Sprite sprite;
    private Rectangle bounds;
    private Planet environment;
    private float originalHeight;
    private float angle;

    private ParticleEffect particleEffect;

    Collectible(float h, float a, float width, float height, String textureName, int target) {
        this.h = h;
        this.a = a;
        this.width = width;
		this.height = height;

        sprite = new Sprite(Art.get(textureName));
        sprite.setOriginCenter();
        sprite.setSize(width, height);

        Color particleColor = Art.getAccent(textureName);
        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("assets/effects/weaponpickup.p"), Gdx.files.internal("assets/effects"));
        particleEffect.start();
        particleEffect.getEmitters().first().getTint().setColors(new float[]{particleColor.r, particleColor.g, particleColor.b});

        this.target = target;
    }

    protected void setEnvironment(Planet planet) {
        environment = planet;

        x = environment.getX() + (environment.getRadius() + h) * MathUtils.cos(a);
        y = environment.getY() + (environment.getRadius() + h) * MathUtils.sin(a);

        bounds = new Rectangle(x, y, width, height);

		angle = MathUtils.atan2(y - planet.getY(), x - planet.getX());
		originalHeight = Vector2.dst(x, y, planet.getX(), planet.getY());
        sprite.setRotation(angle * MathUtils.radiansToDegrees - 90);

        sprite.setOriginCenter();
        sprite.setPosition(x, y);
    }

    public void update(float dt, Array<Collideable> collideables) {
        if (environment != null) {
            float bob = MathUtils.sin(Universe.currentUniverse.getAge()) * 3;
            x = environment.getX() + (originalHeight + bob) * MathUtils.cos(angle);
			y = environment.getY() + (originalHeight + bob) * MathUtils.sin(angle);
            particleEffect.setPosition(x, y);
        }
        sprite.setOriginCenter();
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);

		for (int q = 0; q < collideables.size; q++) {
			Collideable collideable = collideables.get(q);
            int cID = collideable.getType();
            int check = cID & target;

            if (check != 0 && collideable.canCollect(this)) {
                if (collideable.getBounds().overlaps(bounds)) {
                    get(collideable);
					collideable.collectCollectible(this);
					Universe.currentUniverse.killCollectible(this);
				}
			}
		}
        particleEffect.update(dt);
    }

	public void draw(SpriteBatch batch) {
        particleEffect.draw(batch);
        sprite.draw(batch);
	}

    public void debugDraw(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    protected abstract void get(Collideable collideable);

	public void reposition(Array<Planet> planets) {
		for (Planet planet : planets) {
			if (Vector2.dst(x, y, planet.getX(), planet.getY()) < planet.getSOI()) {
				setEnvironment(planet);
				return;
			}
		}
	}

    @Override
    public void dispose() {
        bounds = null;
    }
}
