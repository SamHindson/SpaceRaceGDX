package com.semdog.spacerace.collectables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.universe.Collideable;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Trackable;
import com.semdog.spacerace.universe.Universe;

/**
 * The abstract Collectible class, which all collectibles extend. Collectibles
 * can be collected by the player and each provides some sort of effect.
 *
 * @author Sam
 */

public abstract class Collectible implements Trackable {

    public static final byte PLAYER = 0x01;            //	Collision mask for Player
    public static final byte SHIP = 0x10;            //	Collision mask for Ships
    protected boolean respawnable = true;
    private float x;
    private float y;
    private float width;
    private float height;
    private float h;
    private float a;
    private int target;
    private Sprite sprite;
    private Rectangle bounds;
    private Planet environment;
    private float originalHeight;
    private float angle;
    private Color color;
    private String textureName;
    private ParticleEffect particleEffect;
    private boolean active = true;
    private float cooldown;

    Collectible(float h, float a, float width, float height, String textureName, int target) {
        this.h = h;
        this.a = a;
        this.width = width;
        this.height = height;
        this.target = target;
        this.textureName = textureName;

        sprite = new Sprite(Art.get(textureName));
        sprite.setOriginCenter();
        sprite.setSize(width, height);

        color = Art.getAccent(textureName);
        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("assets/effects/weaponpickup.p"), Gdx.files.internal("assets/effects"));
        particleEffect.start();
        particleEffect.getEmitters().first().getTint().setColors(new float[]{color.r, color.g, color.b});
    }

    /**
     * Sets the planet on which the collectible resides and allows it to bob up
     * and down very slightly.
     */
    public void setEnvironment(Planet planet) {
        environment = planet;

        x = environment.getX() + (environment.getRadius() + h) * MathUtils.cos(a);
        y = environment.getY() + (environment.getRadius() + h) * MathUtils.sin(a);

        bounds = new Rectangle(x, y, width, height);

        angle = MathUtils.atan2(y - planet.getY(), x - planet.getX());
        originalHeight = Vector2.dst(x, y, planet.getX(), planet.getY());

        sprite.setOriginCenter();
        sprite.setRotation(angle * MathUtils.radiansToDegrees - 90);
        sprite.setPosition(x, y);
    }

    /**
     * Lets the collectible do whatever it wants to do each frame
     */
    public void update(float dt, Array<Collideable> collideables) {
        if (environment != null) {
            float bob = MathUtils.sin(Universe.currentUniverse.getAge()) * 3;
            x = environment.getX() + (originalHeight + bob) * MathUtils.cos(angle);
            y = environment.getY() + (originalHeight + bob) * MathUtils.sin(angle);
            particleEffect.setPosition(x, y);
        }

        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
        particleEffect.update(dt);

        if (particleEffect.isComplete()) particleEffect.start();

        /* Loops through all the collideables (i.e. players, ships) the universe
        has given it and checks whether they collide.*/
        for (int q = 0; q < collideables.size; q++) {
            /*This simple bitmasking method sees whether the Collideable is of
            the right variant (i.e. player, or space ship).
            If this is compatible with what the Collectible is meant to
            effect (i.e. the check produces a non-zero result) the collision
            goes ahead.*/
            Collideable collideable = collideables.get(q);
            int cID = collideable.getType();
            int check = cID & target;
            if (check != 0 && collideable.canCollect(this)) {
                if (collideable.getCollisionBounds().overlaps(bounds)) {
                    get(collideable);
                    collideable.collectCollectible(this);
                    active = false;
                    cooldown = 0;
                }
            }
        }

        //  If the collectible is allowed to respawn, it does so after 10 seconds
        if (!active && respawnable) {
            cooldown += dt;

            if (cooldown > 10) {
                Universe.currentUniverse.playSound("boop", x, y, 0.75f);
                active = true;
            }
        }
    }

    public void draw(SpriteBatch batch) {
        particleEffect.draw(batch);
        if (active) sprite.draw(batch);
    }

    //  If the collectible should do something when collected, this is what is to be executed.
    protected abstract void get(Collideable collideable);

    @Override
    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

    @Override
    public Color getGizmoColor() {
        return color;
    }

    @Override
    public String getGizmoLabel() {
        return textureName;
    }
}
