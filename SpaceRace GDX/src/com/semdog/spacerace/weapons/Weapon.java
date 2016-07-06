package com.semdog.spacerace.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.MathUtils;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.players.VitalSigns;
import com.semdog.spacerace.players.Vitality;
import com.semdog.spacerace.universe.Universe;

public abstract class Weapon implements Vitality {
    protected String name, fireSound;
    protected boolean automatic;
	protected float fireDelay, currentWait;
	protected int damage;
	protected Player owner;
	protected float aimAngle;
    protected int clipSize, ammoleft;
    protected float inacurracy;
    private boolean justFired = false;

	public Weapon(String name, int clipSize, boolean automatic, float fireDelay, int damage, String fireSound, float inaccuracy) {
		this.name = name;
		this.clipSize = clipSize;
		this.ammoleft = clipSize;
		this.automatic = automatic;
		this.fireDelay = fireDelay;
		this.damage = damage;
		this.inacurracy = inaccuracy;
		this.fireSound = fireSound;
		currentWait = fireDelay;
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

    public void reset() {
        ammoleft = clipSize;
    }

	protected void fire() {
        Universe.currentUniverse.playSound(fireSound, owner.getX(), owner.getY(), 0.3f);
		float inaccuracy = MathUtils.random(-inacurracy, inacurracy);
		float ax = owner.getX() + 10 * MathUtils.cos(aimAngle);
		float ay = owner.getY() + 10 * MathUtils.sin(aimAngle);
		Universe.currentUniverse.addBullet(new Bullet(ax, ay, owner.getDX(), owner.getDY(), aimAngle, damage, inaccuracy));
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
}
