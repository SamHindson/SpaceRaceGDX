package com.semdog.spacerace.net.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.semdog.spacerace.collectables.Collectible;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.graphics.effects.DustPuff;
import com.semdog.spacerace.players.Team;
import com.semdog.spacerace.universe.Collideable;
import com.semdog.spacerace.universe.Trackable;
import com.semdog.spacerace.universe.Universe;

/**
 * An object seen in the multiplayer game as the other players currently on the server.
 * Named puppet because there is someone else controlling them.
 *
 * @author Sam
 */

public class Puppet implements Trackable, Collideable {
    private String name;
    private Vector2 position;
    private Team team;
    private Rectangle bounds;
    private Animation animation;
    private Sprite idleTexture, jetpack;

    private boolean lefting, righting;
    private boolean flipped;
    private boolean loaded = false;
    private boolean alive = true;

    private float animTime;
    private float environmentX, environmentY;

    public Puppet(VirtualPlayer player) {
        position = new Vector2(player.getX(), player.getY());
        team = player.getTeam();
        name = player.getName();

        environmentX = player.getEnvironmentX();
        environmentY = player.getEnvironmentY();

        bounds = new Rectangle(position.x - 10, position.y - 10, 20, 20);
    }

    public void setAlive(boolean alive) {
        this.alive = alive;

        if (!alive) {
            Universe.currentUniverse.addEffect(new DustPuff(position.x, position.y, Colors.P_RED));
        }
    }

    public Team getTeam() {
        return team;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void load() {
        loaded = true;
        TextureAtlas textureAtlas = new TextureAtlas("assets/graphics/runboy.atlas");
        animation = new Animation(1 / 30f, textureAtlas.getRegions());
        idleTexture = new Sprite(Art.get("idledude"));
        jetpack = new Sprite(Art.get(team == Team.PINK ? "pinkbp" : "bluebp"));
        jetpack.setSize(20, 20);
        jetpack.setOriginCenter();
        idleTexture.setSize(20, 20);
        idleTexture.setFlip(flipped, false);
        idleTexture.setOriginCenter();
    }

    @Override
    public Color getGizmoColor() {
        return team.getColor();
    }

    @Override
    public String getGizmoLabel() {
        return name;
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position2) {
        position = position2;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public void setAnimState(float dir) {
        lefting = dir == 1;
        righting = dir == -1;
    }

    public void draw(SpriteBatch batch) {
        if (alive) {
            if (!loaded) {
                load();
                loaded = true;
            }
            jetpack.draw(batch);

            if (lefting) {
                batch.draw(animation.getKeyFrame(animTime, true), position.x - 10, position.y - 10, 10, 10, 20, 20, 1, 1, getAngle() * MathUtils.radiansToDegrees);
            } else if (righting) {
                batch.draw(animation.getKeyFrame(animTime, true), position.x - 10, position.y - 10, 10, 10, 20, 20, -1, 1, getAngle() * MathUtils.radiansToDegrees);
            } else {
                idleTexture.draw(batch);
            }
        }
    }

    private float getAngle() {
        return MathUtils.atan2(position.y - getEnvironmentY(), position.x - getEnvironmentX()) - MathUtils.PI / 2.f;
    }

    public void update(float dt) {
        if (!loaded) load();

        jetpack.setFlip(lefting, false);
        bounds.setPosition(position.x - 10, position.y - 10);

        animTime += dt;

        jetpack.setPosition(position.x - 10, position.y - 10);
        jetpack.setRotation(getAngle() * MathUtils.radiansToDegrees);
        idleTexture.setPosition(position.x - 10, position.y - 10);
        idleTexture.setRotation(getAngle() * MathUtils.radiansToDegrees);
    }

    public void setEnvironmentPosition(float x, float y) {
        environmentX = x;
        environmentY = y;
    }

    private float getEnvironmentX() {
        return environmentX;
    }

    private float getEnvironmentY() {
        return environmentY;
    }

    @Override
    public void collectCollectible(Collectible collectible) {

    }

    public void setTeam(Team team) {
        if(this.team.equals(team)) return;
        this.team = team;
        jetpack = new Sprite(Art.get(team == Team.PINK ? "pinkbp" : "bluebp"));jetpack.setSize(20, 20);
        jetpack.setOriginCenter();
        idleTexture.setSize(20, 20);
        idleTexture.setFlip(flipped, false);
        idleTexture.setOriginCenter();
    }

    @Override
    public Rectangle getCollisionBounds() {
        return bounds;
    }

    @Override
    public boolean canCollect(Collectible collectible) {
        return true;
    }

    @Override
    public int getType() {
        return Collectible.PLAYER;
    }
}
