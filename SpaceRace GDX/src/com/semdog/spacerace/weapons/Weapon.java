package com.semdog.spacerace.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.universe.Universe;

public abstract class Weapon {
	protected String name;
	protected int clipSize, ammoleft;
	protected boolean automatic;
	protected float fireDelay, currentWait;
	protected int damage;

	protected Sound fireSound;

	private boolean justFired = false;
	
	protected Player owner;

	public Weapon(String name, int clipSize, boolean automatic, float fireDelay, int damage, String fireSound) {
		this.name = name;
		this.clipSize = clipSize;
		this.ammoleft = clipSize;
		this.automatic = automatic;
		this.fireDelay = fireDelay;
		this.damage = damage;

		this.fireSound = Gdx.audio.newSound(Gdx.files.internal("assets/audio/" + fireSound + ".ogg"));
	}
	
	public void pickup(Player owner) {
		this.owner = owner;
	}

	public void update(float dt) {
		if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
			if (!justFired && ammoleft > 0) {
				justFired = true;
				fire();
			}
		} else {
			justFired = false;
		}
	}

	private void fire() {
		float a = MathUtils.atan2((Gdx.graphics.getHeight() - Gdx.input.getY()) - Gdx.graphics.getHeight() / 2, Gdx.input.getX() - Gdx.graphics.getWidth() / 2);
		fireSound.play();
		Universe.currentUniverse.addBullet(new Bullet(owner.getX(), owner.getY(), a, damage));
		ammoleft--;
	}
}
