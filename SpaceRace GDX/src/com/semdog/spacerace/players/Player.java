package com.semdog.spacerace.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.semdog.spacerace.collectables.*;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.io.SettingsManager;
import com.semdog.spacerace.misc.OrbitalHelper;
import com.semdog.spacerace.misc.Tools;
import com.semdog.spacerace.net.entities.MassSpawnRequest;
import com.semdog.spacerace.players.VitalSigns.Type;
import com.semdog.spacerace.universe.*;
import com.semdog.spacerace.vehicles.Ship;
import com.semdog.spacerace.weapons.Weapon;

/**
 * The Player class allows users to interact with the in-game universe.
 *
 * @author Sam
 */

public class Player implements Collideable, Disposable, Trackable {

    protected Team team;
    protected float health = 200;
    protected boolean lefting = false, righting = false;
    protected boolean alive = true;
    protected String name = (String) Tools.decide("MarlonBrando24", "TARS", "Dave", "ChrissyNolan159485", "Bang Di");
    protected Sprite jetpack;
    protected Vector2 position;
    protected Rectangle bounds;
    protected VitalSigns primarySigns;
    protected Vitality vHealth, vGrenades, vJetpack;
    private Planet home;
    private Planet environment;
    private Array<Planet> visitedPlanets;
    private int grenadeCount;
    private int toastCount;
    private float angle;
    private float animTime = 0f;
    private float jetpackFuel = 100;
    private boolean onGround = false;
    private boolean sprinting = false;
    private boolean pilotingShip = false;
    private boolean flipped = false;
    private boolean boarding;
    private Ship ship;
    private Ship boardingShip;
    private Animation animation;
    private Sprite idleTexture;
    private ParticleEffect jetpackEffect;
    private Vector2 velocity;
    private Vector2 ghostPosition;
    private Weapon weapon;
    private HUD hud;

    public Player(float x, float y, Planet planet) {
        environment = planet;
        position = new Vector2(x, y);
        ghostPosition = new Vector2();
        velocity = new Vector2();

        TextureAtlas textureAtlas = new TextureAtlas("assets/graphics/runboy.atlas");
        animation = new Animation(1 / 30f, textureAtlas.getRegions());
        idleTexture = new Sprite(Art.get("idledude"));
        jetpack = new Sprite(Art.get("pinkbp"));

        bounds = new Rectangle(x - 10, y - 10, 20, 20);

        // Players always have grenades.
        grenadeCount = 5;

        primarySigns = new VitalSigns();

        // Instead of manually creating new classes for each vitality, it is
        // easier to create them at run time in the form of anonymous classes
        vHealth = new Vitality() {

            @Override
            public String getID() {
                return "playerhealth";
            }

            @Override
            public String getDisplayName() {
                return "Health";
            }

            @Override
            public Type getValueType() {
                return Type.CONTINUOUS;
            }

            @Override
            public float getValue() {
                return health;
            }

            @Override
            public float getMaxValue() {
                return 200;
            }

            @Override
            public Color getColor() {
                return Colors.V_PLAYERHEALTH;
            }
        };

        vJetpack = new Vitality() {

            @Override
            public Type getValueType() {
                return Type.CONTINUOUS;
            }

            @Override
            public String getDisplayName() {
                return "Jetpack";
            }

            @Override
            public float getValue() {
                return jetpackFuel;
            }

            @Override
            public float getMaxValue() {
                return 100;
            }

            @Override
            public String getID() {
                return "jetpack";
            }

            @Override
            public Color getColor() {
                return Colors.UI_GREEN;
            }
        };

        vGrenades = new Vitality() {

            @Override
            public String getID() {
                return "grenades";
            }

            @Override
            public String getDisplayName() {
                return "Grenades";
            }

            @Override
            public Type getValueType() {
                return Type.DISCRETE;
            }

            @Override
            public float getValue() {
                return grenadeCount;
            }

            @Override
            public float getMaxValue() {
                return 5;
            }

            @Override
            public Color getColor() {
                return Colors.V_PLAYERGRENADES;
            }
        };

        visitedPlanets = new Array<>();

        jetpackEffect = new ParticleEffect();
        jetpackEffect.load(Gdx.files.internal("assets/effects/jetpack.p"), Gdx.files.internal("assets/effects"));
        jetpackEffect.setPosition(x, y);
        jetpackEffect.allowCompletion();

        team = Team.BLUE;
    }

    /**
     * Allows the player to do things every frame.
     */
    public void update(float dt, boolean controllable, Array<Planet> planets, boolean lockedCamera) {
        if (alive) {
            if (pilotingShip && controllable) {
                ship.updateControls(dt);

                if (Gdx.input.isKeyJustPressed(SettingsManager.getKey("ACTIVATE"))) {
                    exitShip();
                }
            } else {
                //  Scans for nearby planets. If you're near enough, you are considered a citizen thereof.
                for (int r = 0; r < planets.size; r++) {
                    Planet planet = planets.get(r);
                    float d = Vector2.dst(planet.getX(), planet.getY(), position.x, position.y);
                    if (d < planet.getSOI()) {
                        if (environment != planet)
                            setEnvironment(planet);
                        break;
                    }
                }

                //  Sets the jetpack position
                jetpackEffect.setPosition(position.x + 2 * MathUtils.cos(getAngleAroundEnvironment()),
                        position.y + 2 * MathUtils.sin(getAngleAroundEnvironment()));
                jetpackEffect.update(dt);

                //  Pulls you down onto the planet surface if need be.
                if (environment != null) {
                    if (home == null) {
                        home = environment;
                    }

                    float distance = Vector2.dst(getEnvironmentX(), getEnvironmentY(), position.x, position.y);
                    angle = MathUtils.atan2(position.y - getEnvironmentY(), position.x - getEnvironmentX());
                    if (!onGround) {
                        float force = (float) (Universe.GRAVITY * environment.getMass() / Math.pow(distance, 2));
                        float ax = -force * MathUtils.cos(angle);
                        float ay = -force * MathUtils.sin(angle);

                        velocity.x += ax * dt;
                        velocity.y += ay * dt;

                        if (distance < environment.getRadius() + 10) {
                            float vx = -velocity.x * MathUtils.cos(angle);
                            float vy = -velocity.y * MathUtils.sin(angle);
                            vx = vx < 0 ? 0 : vx;
                            vy = vy < 0 ? 0 : vy;
                            float v = Vector2.len(vx, vy);
                            if (v > 200) {
                                doDamage((v / getImpactThreshold()) * 50, DamageCause.FALLING);
                            }
                            onGround = true;
                            position.x = getEnvironmentX() + (environment.getRadius() + 10) * MathUtils.cos(angle);
                            position.y = getEnvironmentY() + (environment.getRadius() + 10) * MathUtils.sin(angle);

                            if (!visitedPlanets.contains(environment, true)) {
                                visitedPlanets.add(environment);
                                hud.showNotification("Visited Planet " + environment.getID());
                            }
                        }
                    } else {
                        //  If you're on the floor already, stop moving or you'll go right through the planet!
                        velocity.set(Vector2.Zero);
                        //  Adds the planet to the list of places you've visited
                        if (!visitedPlanets.contains(environment, true)) {
                            visitedPlanets.add(environment);
                            hud.showNotification("Visited Planet " + environment.getID());
                        }
                    }

                    onGround = distance < environment.getRadius() + 11;
                }

                if (controllable) {
                    sprinting = Gdx.input.isKeyPressed(SettingsManager.getKey("SPRINT"));

                    float wx, wy;
                    if (Gdx.input.isKeyPressed(SettingsManager.getKey("LEFT"))) {
                        animTime += dt;
                        if (onGround) {
                            // Move anti-clockwise around planet
                            float speed = sprinting ? -200 : -100;
                            wx = speed * MathUtils.cos(angle - MathUtils.PI / 2.f);
                            wy = speed * MathUtils.sin(angle - MathUtils.PI / 2.f);

                            velocity.add(wx, wy);
                        }
                        lefting = true;
                        righting = false;
                    } else if (Gdx.input.isKeyPressed(SettingsManager.getKey("RIGHT"))) {
                        animTime += dt;
                        if (onGround) {
                            // Move clockwise around planet
                            float speed = sprinting ? 200 : 100;
                            wx = speed * MathUtils.cos(angle - MathUtils.PI / 2.f);
                            wy = speed * MathUtils.sin(angle - MathUtils.PI / 2.f);

                            velocity.add(wx, wy);
                        }
                        righting = true;
                        lefting = false;
                    } else {
                        animTime = 0;
                        lefting = righting = false;
                    }

                    //  Handles jetpack movement.
                    if (jetpackFuel > 0) {
                        boolean rcsIng = false;
                        float effectAngle = 0;
                        if (!onGround) {
                            if (Gdx.input.isKeyPressed(SettingsManager.getKey("RCS_UP"))) {
                                Universe.currentUniverse.loopSound("rcs", position.x, position.y, 1);

                                float rx = 150 * MathUtils.cos(angle);
                                float ry = 150 * MathUtils.sin(angle);

                                velocity.add(rx * dt, ry * dt);

                                onGround = false;
                                rcsIng = true;

                                jetpackFuel -= dt * 10;
                            }

                            if (Gdx.input.isKeyPressed(SettingsManager.getKey("RCS_DOWN"))) {
                                Universe.currentUniverse.loopSound("rcs", position.x, position.y, 1);

                                float rx = -150 * MathUtils.cos(angle);
                                float ry = -150 * MathUtils.sin(angle);

                                velocity.add(rx * dt, ry * dt);

                                onGround = false;
                                rcsIng = true;
                                jetpackFuel -= dt * 10;

                                effectAngle = 180;
                            }

                            if (Gdx.input.isKeyPressed(SettingsManager.getKey("RCS_LEFT"))) {
                                Universe.currentUniverse.loopSound("rcs", position.x, position.y, 1);

                                float rx = 150 * MathUtils.cos(angle + MathUtils.PI / 2.f);
                                float ry = 150 * MathUtils.sin(angle + MathUtils.PI / 2.f);

                                velocity.add(rx * dt, ry * dt);

                                onGround = false;
                                rcsIng = true;
                                jetpackFuel -= dt * 10;

                                effectAngle = 90;
                            }

                            if (Gdx.input.isKeyPressed(SettingsManager.getKey("RCS_RIGHT"))) {
                                Universe.currentUniverse.loopSound("rcs", position.x, position.y, 1);

                                float rx = -150 * MathUtils.cos(angle + MathUtils.PI / 2.f);
                                float ry = -150 * MathUtils.sin(angle + MathUtils.PI / 2.f);

                                velocity.add(rx * dt, ry * dt);

                                onGround = false;
                                rcsIng = true;
                                jetpackFuel -= dt * 10;

                                effectAngle = 270;
                            }

                            jetpackEffect.getEmitters().get(0).getAngle()
                                    .setHigh(getAngle() * MathUtils.radiansToDegrees - 90 + MathUtils.random(-15, 15)
                                            + effectAngle);

                            if (!rcsIng) {
                                Universe.currentUniverse.stopSound("rcs");
                                jetpackEffect.allowCompletion();
                            } else {
                                jetpackEffect.start();
                            }
                        } else {
                            jetpackEffect.allowCompletion();
                            Universe.currentUniverse.stopSound("rcs");
                        }
                    } else {
                        jetpackEffect.allowCompletion();
                        if (primarySigns.has("jetpack")) {
                            primarySigns.removeItems("jetpack");
                            Universe.currentUniverse.stopSound("rcs");
                        }
                    }

                    if (Gdx.input.isKeyJustPressed(SettingsManager.getKey("JUMP")) && onGround) {
                        // Works out which direction is up and shoots the player in said direction
                        float jx = 100 * MathUtils.cos(angle);
                        float jy = 100 * MathUtils.sin(angle);

                        velocity.add(jx, jy);

                        onGround = false;

                        Universe.currentUniverse.playUISound("jump");
                    }

                    if (boarding && Gdx.input.isKeyJustPressed(SettingsManager.getKey("ACTIVATE"))) {
                        setShip(boardingShip);
                        boardingShip = null;

                        onGround = false;

                        Universe.currentUniverse.playUISound("ingress");
                    }
                }

                position.add(velocity.x * dt, velocity.y * dt);

                bounds.setPosition(position.x - 10, position.y - 10);

                //  Aiming
                float ax1 = Gdx.input.getX();
                float ay1 = Gdx.input.getY();

                float cx = Gdx.graphics.getWidth() / 2;
                float cy = Gdx.graphics.getHeight() / 2;

                float a;

                //  The aim angle depends on whether or not the camera is locked.
                if (lockedCamera) {
                    a = -MathUtils.atan2(cy - ay1, cx - ax1) + MathUtils.PI / 2.f + getAngleAroundEnvironment();
                } else {
                    a = MathUtils.atan2(cy - ay1, ax1 - cx);
                }

                flipped = !(a > getAngleAroundEnvironment() && a < getAngleAroundEnvironment() + MathUtils.PI);

                if (weapon != null && controllable)
                    weapon.update(dt, a);

                //  If we need to throw a grenade, we do
                if (Gdx.input.isKeyJustPressed(SettingsManager.getKey("GRENADE")) && grenadeCount > 0 && controllable) {
                    float gx = position.x + 30 * MathUtils.cos(a);
                    float gy = position.y + 30 * MathUtils.sin(a);

                    float gdx = 150 * MathUtils.cos(a) + velocity.x;
                    float gdy = 150 * MathUtils.sin(a) + velocity.y;

                    if (Universe.currentUniverse instanceof MultiplayerUniverse)
                        ((MultiplayerUniverse) Universe.currentUniverse).requestMass(new MassSpawnRequest(MassSpawnRequest.GRENADE, gx, gy, gdx, gdy, 3.5f));
                    else
                        new Grenade(gx, gy, gdx, gdy, 420);

                    grenadeCount--;
                }
            }
        }

        /*if (environment != null)
            if (Vector2.dst(position.x, position.y, getEnvironmentX(), getEnvironmentY()) < environment.getRadius() + 10 && onGround) {
                onGround = true;
                position.set(getEnvironmentX() + (environment.getRadius() + 10) * MathUtils.cos(angle), getEnvironmentY() + (environment.getRadius() + 10) * MathUtils.sin(angle));
            }*/
    }

    /**
     * Renders the player
     */
    public void draw(SpriteBatch batch) {
        if (!pilotingShip) {
            if (alive) {
                float m = sprinting ? 2 : 1;

                jetpackEffect.draw(batch);

                jetpack.setSize(20, 20);
                jetpack.setPosition(position.x - 10, position.y - 10);
                jetpack.setOriginCenter();
                jetpack.setRotation(getAngle() * MathUtils.radiansToDegrees);
                jetpack.draw(batch);

                if (righting) {
                    batch.draw(animation.getKeyFrame(animTime * m, true), position.x - 10, position.y - 10, 10, 10, 20,
                            20, 1, 1, getAngle() * MathUtils.radiansToDegrees);
                    jetpack.setFlip(true, false);
                } else if (lefting) {
                    batch.draw(animation.getKeyFrame(animTime * m, true), position.x - 10, position.y - 10, 10, 10, 20,
                            20, -1, 1, getAngle() * MathUtils.radiansToDegrees);
                    jetpack.setFlip(false, false);
                } else {
                    jetpack.setFlip(flipped, false);
                    idleTexture.setSize(20, 20);
                    idleTexture.setFlip(flipped, false);
                    idleTexture.setPosition(position.x - 10, position.y - 10);
                    idleTexture.setOriginCenter();
                    idleTexture.setRotation(getAngle() * MathUtils.radiansToDegrees);
                    idleTexture.draw(batch);
                }

                if (weapon != null)
                    weapon.draw(batch);
            }
        }
    }

    public void debugDraw(ShapeRenderer sr) {
        if (pilotingShip)
            return;

        sr.setColor(team.getColor());
        sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    /**
     * Throws the player into the universe
     */
    public void spawn(float x, float y, Array<Planet> planets) {
        position.set(x, y);
        velocity.set(Vector2.Zero);
        alive = true;

        health = 200;
        grenadeCount = 5;
        jetpackFuel = 100;

        primarySigns.addItems(vGrenades, vHealth, vJetpack);

        if (weapon != null)
            weapon.reset();

        //  Checks for nearby planets
        for (Planet planet : planets) {
            if (planet.inRange(x, y)) {
                environment = planet;
                return;
            }
        }
    }

    /**
     * Ouch!
     */
    public void doDamage(float amount, DamageCause cause) {
        if (alive) {
            health -= amount;

            Universe.currentUniverse.playUISound("playerhit" + Tools.decide(1, 2, 3, 4, 5));

            if (health < 0) {
                die(cause);
            }
        }
    }

    /**
     * r.i.p
     */
    protected void die(DamageCause damageCause) {
        if (alive) {
            ghostPosition.set(getX(), getY());
            alive = false;
            ship = null;

            Universe.currentUniverse.playerKilled(damageCause);
            Universe.currentUniverse.stopSound("rcs");

            if (pilotingShip) {
                primarySigns.removeItems("shipfuel");
                pilotingShip = false;
            }
        }
    }

    @Override
    public void collectCollectible(Collectible collectible) {
        hud.showNotification("You collected " + collectible.getGizmoLabel());
    }

    public void replenishHealth() {
        health = 200;
    }

    public void replenishAmmo() {
        weapon.reset();
    }

    public void setBoarding(boolean boarding, Ship boardingShip) {
        if (!this.boarding && boarding) {
            hud.makeToast(boardingShip.isEnterable() ? "Press E to board" : "Press E to flip", 2f, Colors.UI_WHITE);
            this.boarding = true;
        }

        if (!boarding) {
            hud.hideToast();
        }

        this.boarding = boarding;
        this.boardingShip = boardingShip;
    }

    public void setHud(HUD hud) {
        this.hud = hud;
    }

    public VitalSigns getPrimarySigns() {
        return pilotingShip ? ship.getVitalSigns() : primarySigns;
    }

    public boolean isPilotingShip() {
        return pilotingShip;
    }

    private void setShip(Ship ship) {
        // If the ship is upside down, flip it over and try again.
        if (!ship.isEnterable()) {
            ship.flip();
            return;
        }

        this.ship = ship;
        pilotingShip = true;
        boarding = false;
        ship.setPilot(this);
        Universe.currentUniverse.stopSound("rcs");
        hud.hideToast();
        hud.showNotification("Boarded ship " + ship.getID());
    }

    public void exitShip() {
        pilotingShip = false;
        float shipAngle = ship.getAngleAroundEnvironment();
        position.x = ship.getX() + 30 * MathUtils.sin(shipAngle);
        position.y = ship.getY() + 30 * MathUtils.cos(shipAngle);
        velocity.x = ship.getDx();
        velocity.y = ship.getDy();
        ship.setPilot(null);
        ship = null;
        Universe.currentUniverse.playUISound("egress");
        hud.showNotification("Alighted Ship");
    }

    public void enteredOrbit() {
        hud.showNotification("Attained orbit around " + environment.getID());
    }

    public void exitedOrbit() {
        hud.showNotification("Exited orbit around " + environment.getID());
    }

    public boolean isOnGround() {
        return pilotingShip ? ship.isOnGround() : onGround;
    }

    public boolean isAlive() {
        return alive;
    }

    public Team getTeam() {
        return team;
    }

    public Vector2 getPosition() {
        return pilotingShip ? ship.getPosition() : position;
    }

    public Vector2 getVelocity() {
        return pilotingShip ? ship.getVelocity() : velocity;
    }

    public void addSpeed(Vector2 added) {
        velocity.add(added);
    }

    public void kickBack(float angle, float force) {
        velocity.add(force * MathUtils.sin(angle), force * MathUtils.cos(angle));
    }

    public float getX() {
        return (pilotingShip) ? ship.getX() : (position.x);
    }

    public float getY() {
        return (pilotingShip) ? ship.getY() : (position.y);
    }

    public float getFX() {
        return alive ? (pilotingShip) ? ship.getFX() : (position.x) : ghostPosition.x;
    }

    public float getFY() {
        return alive ? (pilotingShip) ? ship.getFY() : (position.y) : ghostPosition.y;
    }

    public float getDX() {
        return velocity.x;
    }

    public float getDY() {
        return velocity.y;
    }

    public float getWeaponX() {
        return position.x + MathUtils.cos(angle) * 2.f + 1.5f;
    }

    public float getWeaponY() {
        return position.y + MathUtils.sin(angle) * 2.f + 1.5f;
    }

    public Rectangle getCollisionBounds() {
        return bounds;
    }

    public String getName() {
        return name;
    }

    public float getAngle() {
        return (pilotingShip && ship != null) ? ship.getAngle() : (angle - MathUtils.PI / 2);
    }

    public float getAngleAroundEnvironment() {
        return (pilotingShip && ship != null) ? ship.getAngleAroundEnvironment() : (angle);
    }

    public void orbit(float direction) {
        angle = MathUtils.atan2(position.y - getEnvironmentY(), position.x - getEnvironmentX());
        float v = (float) Math.sqrt(
                Universe.GRAVITY * environment.getMass() / position.sub(getEnvironmentX(), getEnvironmentY()).len())
                * -direction;
        velocity.x = MathUtils.sin(angle) * v;
        velocity.y = MathUtils.cos(angle) * v;
    }

    private float getImpactThreshold() {
        return 200;
    }

    public boolean isBoarding() {
        return boarding;
    }

    public float getEnvironmentX() {
        return environment != null ? environment.getX() : 0;
    }

    public float getEnvironmentY() {
        return environment != null ? environment.getY() : 0;
    }

    public String getEnvironmentID() {
        return (environment != null) ? environment.getID() : "???";
    }

    @Override
    public boolean canCollect(Collectible collectible) {
        if (collectible instanceof Health)
            return alive && health < 200;

        if (collectible instanceof WeaponPickup) {
            return Gdx.input.isKeyJustPressed(Keys.E);
        }

        if (collectible instanceof Toast) {
            return true;
        }

        if (collectible instanceof Ammo) {
            return weapon.getAmmoLeft() < weapon.getMaxAmmo();
        }

        return true;
    }

    @Override
    public int getType() {
        return Collectible.PLAYER;
    }

    public String getShipID() {
        return pilotingShip ? ship.getID() : "???";
    }

    public float getPerigee() {
        return pilotingShip ? ship.getPerigee()
                : OrbitalHelper.computeOrbit(position, environment.getPosition(), velocity, environment.getMass())[6];
    }

    public float getApogee() {
        return pilotingShip ? ship.getApogee()
                : OrbitalHelper.computeOrbit(position, environment.getPosition(), velocity, environment.getMass())[5];
    }

    public Planet getEnvironment() {
        return environment;
    }

    public void setEnvironment(Planet planet) {
        environment = planet;
        hud.showNotification("Entered gravitational influence of " + planet.getID());
    }

    public Array<Planet> getVisitedPlanets() {
        return visitedPlanets;
    }

    public float getVisitedPlanetsNo() {
        return visitedPlanets.size;
    }

    public void addToast() {
        toastCount++;
    }

    public float getToastCount() {
        return toastCount;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
        weapon.pickup(this);
        primarySigns.addItems(weapon);
        hud.showNotification("Picked up " + weapon.getName());
    }

    public boolean isFlipped() {
        return flipped;
    }

    public float getAnimState() {
        return lefting ? -1 : righting ? 1 : 0;
    }

    public boolean isHome() {
        return environment.equals(home);
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
    public void dispose() {
        for (TextureRegion region : animation.getKeyFrames()) {
            region.getTexture().dispose();
        }

        jetpackEffect.dispose();
    }
}
