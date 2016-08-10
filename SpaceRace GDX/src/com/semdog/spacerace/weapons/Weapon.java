package com.semdog.spacerace.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.players.VitalSigns;
import com.semdog.spacerace.players.Vitality;
import com.semdog.spacerace.universe.Universe;

/**
 * An abstract class which encompasses the nature of weapons in SpaceRace.
 *
 * @author Sam
 */

public abstract class Weapon implements Vitality {
    String fireSound;
    Player owner;
    float aimAngle;
    int ammoleft;
    private String name;
    private boolean automatic;
    private float fireDelay;
    private float currentWait;
    private int damage;
    private int clipSize;
    private float inacurracy;
    private Sprite sprite;
    private boolean justFired = false;

    Weapon(String name, int clipSize, boolean automatic, float fireDelay, int damage, String fireSound, float inaccuracy) {
        this.name = name;
        this.clipSize = clipSize;
        this.ammoleft = clipSize;
        this.automatic = automatic;
        this.fireDelay = fireDelay;
        this.damage = damage;
        this.inacurracy = inaccuracy;
        this.fireSound = fireSound;
        currentWait = fireDelay;

        sprite = new Sprite(Art.get(name));
        sprite.setScale(0.4f);
    }

    @Override
    public float getValue() {
        return ammoleft;
    }

    @Override
    public float getMaxValue() {
        return clipSize;
    }

    @Override
    public VitalSigns.Type getValueType() {
        return VitalSigns.Type.CONTINUOUS;
    }

    public void pickup(Player owner) {
        this.owner = owner;
    }

    // TODO fix the horrible weapon placement
    public void update(float dt, float a) {
        aimAngle = a;
        currentWait += dt;

        boolean flipped = !owner.isFlipped();

        float sin = MathUtils.sin(owner.getAngleAroundEnvironment());
        float cos = MathUtils.sin(owner.getAngle());

        sprite.setFlip(flipped, false);
        sprite.setPosition(owner.getX() - (flipped ? sprite.getWidth() * 0.4f * sin : 0), owner.getY() - (flipped ? sprite.getWidth() * 0.4f * cos : 0));
        sprite.setOrigin(0, 0);
        sprite.setRotation(owner.getAngle() * MathUtils.radiansToDegrees);

        if (ammoleft > 0) {
            if (automatic) {
                if (Gdx.input.isButtonPressed(Buttons.LEFT) && currentWait > fireDelay) {
                    currentWait = 0;
                    fire();
                }
            } else {
                if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
                    if (!justFired && ammoleft > 0 && currentWait > fireDelay) {
                        justFired = true;
                        fire();
                        currentWait = 0;
                    }
                } else {
                    justFired = false;
                }
            }
        }
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public void reset() {
        ammoleft = clipSize;
    }

    void fire() {
        Universe.currentUniverse.playSound(fireSound, owner.getX(), owner.getY(), 0.3f);
        float inaccuracy = MathUtils.random(-inacurracy, inacurracy);
        float ax = owner.getWeaponX() + 30 * MathUtils.cos(aimAngle);
        float ay = owner.getWeaponY() + 30 * MathUtils.sin(aimAngle);
        /*
         * System.out.println(ax); System.out.println(ay);
		 * System.out.println(owner.getDX()); System.out.println(owner.getDY());
		 * System.out.println(aimAngle); System.out.println(damage);
		 * System.out.println(inaccuracy);
		 */
        Universe.currentUniverse.requestBullet(new Bullet(ax, ay, owner.getDX(), owner.getDY(), aimAngle, damage, inaccuracy));
        ammoleft--;
    }

    public int getAmmoLeft() {
        return ammoleft;
    }

    public float getMaxAmmo() {
        return clipSize;
    }

    @Override
    public String getID() {
        return "ammo";
    }

    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return "Ammo";
    }
}
