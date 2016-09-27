package com.semdog.spacerace.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.players.Team;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.weapons.Bullet;

/**
 * A class which extends the functionality of the standard Player by communicating with a Wormhole whenever its state
 * changes (i.e. starts walking, stops walking, jumps, etc.)
 * The Wormhole then processes this and notifies all other connected players to move the Puppet linked to this
 * particular player.
 *
 * @author Sam
 */

public class MultiplayerPlayer extends Player {

    private Wormhole wormhole;

    public MultiplayerPlayer(String name, Wormhole wormhole, float x, float y, Planet planet) {
        super(x, y, planet);
        this.wormhole = wormhole;
        this.name = name;
        primarySigns.addItems(vGrenades, vHealth, vJetpack);
    }

    /**
     * Sets the player's team and works out which backpack model to use
     */
    public void setTeam(Team team) {
        this.team = team;
        jetpack = new Sprite(Art.get(team == Team.PINK ? "pinkbp" : "bluebp"));
        wormhole.sendPlayerState(PlayerState.TEAMSET, team.equals(Team.PINK) ? 0 : 1);
    }

    public void update(float dt, boolean controllable, Array<Planet> planets, boolean lockedCamera) {
        super.update(dt, controllable, planets, lockedCamera);
        //  If the player is moving, tell the wormhole that.
        if (!getVelocity().equals(Vector2.Zero))
            wormhole.sendPlayerState(PlayerState.SETPOS, position.x, position.y, lefting ? -1 : righting ? 1 : 0, getEnvironmentX(), getEnvironmentY());
    }

    /**
     * Needed to tell the wormhole the rotation of the puppet
     */
    @Override
    public void setEnvironment(Planet planet) {
        super.setEnvironment(planet);
        wormhole.sendPlayerState(PlayerState.ENVSTATE, planet.getX(), planet.getY());
    }

    /**
     * Lets the wormhole know that the player is now alive
     */
    @Override
    public void spawn(float x, float y, Array<Planet> planets) {
        super.spawn(x, y, planets);
        wormhole.sendPlayerState(PlayerState.LIFE, 1);
    }

    /**
     * A multiplayer player must be able to be hit by bullets, so we check that here.
     */
    public void checkCollisions(Array<Bullet> bullets) {
        if (!alive) return;
        for (Bullet bullet : bullets) {
            float accuracy = 15;
            float ox = bullet.getX();
            float oy = bullet.getY();
            float fx = bullet.getX() + (bullet.getDx() * Gdx.graphics.getDeltaTime());
            float fy = bullet.getY() + (bullet.getDy() * Gdx.graphics.getDeltaTime());
            for (float i = 0; i <= accuracy; i++) {
                float px = MathUtils.lerp(ox, fx, i / accuracy);
                float py = MathUtils.lerp(oy, fy, i / accuracy);
                if (bounds.contains(px, py)) {
                    doDamage(bullet.getDamage(), DamageCause.BULLET);
                    bullet.die();
                    if (!alive) wormhole.sendPlayerState(PlayerState.BULLETKILL, bullet.getOwnerID());
                    return;
                }
            }
        }
    }

    /**
     * Lets the wormhole know that the player is now dead
     */
    @Override
    public void die(DamageCause damageCause) {
        super.die(damageCause);
        wormhole.sendPlayerState(PlayerState.LIFE, -1);
    }
}