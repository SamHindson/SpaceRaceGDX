package com.semdog.spacerace.net;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.weapons.Bullet;

public class MultiplayerPlayer extends Player {

	private Wormhole wormhole;

	public MultiplayerPlayer(Wormhole wormhole, float x, float y, Planet planet) {
		super(x, y, planet);
		this.wormhole = wormhole;
		primarySigns.addItems(vGrenades, vHealth, vJetpack);
	}

	public void update(float dt, boolean controllable, com.badlogic.gdx.utils.Array<Planet> planets, boolean lockedCamera) {
		super.update(dt, controllable, planets, lockedCamera);

		if (!getVelocity().equals(Vector2.Zero)) {
			wormhole.sendPlayerState(PlayerState.SETPOS, position.x, position.y);
		}

		wormhole.sendPlayerState(PlayerState.ANIMSTATE, lefting ? -1 : righting ? 1 : 0);
	}

	@Override
	public void setEnvironment(Planet planet) {
		super.setEnvironment(planet);
		wormhole.sendPlayerState(PlayerState.ENVSTATE, planet.getX(), planet.getY());
	}
	
	public void checkCollisions(Array<Bullet> bullets) {
		for(Bullet bullet : bullets) {
			float accuracy = 5;

			float ox = bullet.getX();
			float oy = bullet.getY();

			float fx = bullet.getX() + (bullet.getDx() * 0.01f);
			float fy = bullet.getY() + (bullet.getDy() * 0.01f);

			for (float i = 0; i <= accuracy; i++) {
				float px = MathUtils.lerp(ox, fx, i / accuracy);
				float py = MathUtils.lerp(oy, fy, i / accuracy);

				if (bounds.contains(px, py)) {
					doDamage(bullet.getDamage(), DamageCause.BULLET);
					bullet.die();
				}
			}
		}
	}
	
	@Override
	public void die(DamageCause damageCause) {
		super.die(damageCause);
		wormhole.sendPlayerState(PlayerState.LIFE, -1);
	}
}