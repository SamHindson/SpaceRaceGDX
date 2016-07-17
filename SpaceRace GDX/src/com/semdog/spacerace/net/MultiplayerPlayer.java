package com.semdog.spacerace.net;

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

public class MultiplayerPlayer extends Player {

    private Wormhole wormhole;

    public MultiplayerPlayer(String name, Wormhole wormhole, float x, float y, Planet planet) {
        super(x, y, planet);
        this.wormhole = wormhole;
        this.name = name;
        primarySigns.addItems(vGrenades, vHealth, vJetpack);
    }

    public void setTeam(Team team) {
        this.team = team;
        System.out.println("Set my team to " + team);
        jetpack = new Sprite(Art.get(team == Team.PINK ? "pinkbp" : "bluebp"));
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

    @Override
    public void spawn(float x, float y, Array<Planet> planets) {
        super.spawn(x, y, planets);
        wormhole.sendPlayerState(PlayerState.LIFE, 1);
    }

    public void checkCollisions(Array<Bullet> bullets) {
        if (!alive) return;

        for (Bullet bullet : bullets) {
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

                    if (!alive) {
                        wormhole.sendPlayerState(PlayerState.BULLETKILL, bullet.getOwnerID());
                    }

                    return;
                }
            }
        }
    }

    private void hitByBullet(int damage, int ownerID) {
        if (alive) {
            health -= damage;
            if (health <= 0) {
                alive = false;
                wormhole.sendPlayerState(PlayerState.BULLETKILL, ownerID);
                die(DamageCause.BULLET);
            }
        }
    }

    @Override
    public void die(DamageCause damageCause) {
        super.die(damageCause);
        wormhole.sendPlayerState(PlayerState.LIFE, -1);
    }
}