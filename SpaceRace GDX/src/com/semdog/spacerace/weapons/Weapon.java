package com.semdog.spacerace.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.MathUtils;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.players.VitalSigns;
import com.semdog.spacerace.players.Vitality;
import com.semdog.spacerace.universe.Universe;

public abstract class Weapon implements Vitality {
    protected String name;
    protected boolean automatic;
	protected float fireDelay, currentWait;
	protected int damage;
	protected Player owner;
	protected float aimAngle;
    protected int clipSize, ammoleft;
    private boolean justFired = false;

	public Weapon(String name, int clipSize, boolean automatic, float fireDelay, int damage, String fireSound) {
		this.name = name;
		this.clipSize = clipSize;
		this.ammoleft = clipSize;
		this.automatic = automatic;
		this.fireDelay = fireDelay;
		this.damage = damage;
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
    public VitalSigns.Type getType() {
        return VitalSigns.Type.CONTINUOUS;
    }

    public void pickup(Player owner) {
        this.owner = owner;
    }

	public void update(float dt, float a) {
		aimAngle = a;
        currentWait += dt;

        if (ammoleft > 0) {
            if (automatic) {
                if (Gdx.input.isButtonPressed(Buttons.LEFT) && currentWait > fireDelay) {
                    currentWait = 0;
                    fire();
                }
            } else {
                if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
                    if (!justFired && ammoleft > 0) {
                        justFired = true;
                        fire();
                    }
                } else {
                    justFired = false;
                }
            }
        }
    }

    public void reset() {
        ammoleft = clipSize;
    }

	protected void fire() {
		float ax = owner.getX() + 10 * MathUtils.cos(aimAngle);
		float ay = owner.getY() + 10 * MathUtils.sin(aimAngle);
		Universe.currentUniverse.addBullet(new Bullet(ax, ay, owner.getDX(), owner.getDY(), aimAngle, damage));
		ammoleft--;
	}
}
