package com.semdog.spacerace.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.semdog.spacerace.collectables.Collectible;
import com.semdog.spacerace.collectables.Health;
import com.semdog.spacerace.collectables.Toast;
import com.semdog.spacerace.collectables.WeaponPickup;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.io.SettingsManager;
import com.semdog.spacerace.misc.OrbitalHelper;
import com.semdog.spacerace.misc.Tools;
import com.semdog.spacerace.players.VitalSigns.Type;
import com.semdog.spacerace.universe.Collideable;
import com.semdog.spacerace.universe.Grenade;
import com.semdog.spacerace.universe.Planet;
import com.semdog.spacerace.universe.Universe;
import com.semdog.spacerace.vehicles.Ship;
import com.semdog.spacerace.weapons.Weapon;

public class Player implements Collideable, Disposable {

    private Team team = Team.PINK;

    private float health = 200;

    private Planet environment;
    private float angle;

    private boolean onGround = false;
    private boolean lefting = false, righting = false;
    private boolean sprinting = false;
    private boolean alive = true;

    private boolean pilotingShip = false;
    private Ship ship;

    private Animation animation;
    private TextureRegion idleTexture;
    private float animTime = 0f;

    private Vector2 position, velocity;

    private Rectangle bounds;

    private Weapon weapon;
    private float da;
    private boolean boarding;

    private Ship boardingShip;

    private VitalSigns primarySigns;

    private Array<Planet> visitedPlanets;

    private int grenadeCount;

    private int toastCount;
    private HUD hud;

    public Player(float x, float y, Planet planet) {
        environment = planet;
        position = new Vector2(x, y);
        velocity = new Vector2();
        Gdx.app.log("Player", "Player created at " + position);
        TextureAtlas textureAtlas = new TextureAtlas("assets/graphics/runboy.atlas");
        animation = new Animation(1 / 30f, textureAtlas.getRegions());
        idleTexture = new TextureRegion(new Texture(Gdx.files.internal("assets/graphics/idledude.png")));
        bounds = new Rectangle(x - 10, y - 10, 20, 20);

        grenadeCount = 5;
        primarySigns = new VitalSigns();
        Vitality vHealth = new Vitality() {

            @Override
            public String getID() {
                return "playerhealth";
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

        Vitality vAmmo = new Vitality() {

            @Override
            public String getID() {
                return "ammo";
            }

            @Override
            public Type getValueType() {
                return Type.CONTINUOUS;
            }

            @Override
            public float getValue() {
                return weapon.getAmmoLeft();
            }

            @Override
            public float getMaxValue() {
                return weapon.getMaxAmmo();
            }

            @Override
            public Color getColor() {
                return weapon.getColor();
            }
        };

        Vitality vGrenades = new Vitality() {

            @Override
            public String getID() {
                return "grenades";
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

        primarySigns.addItems(vHealth, vGrenades);
        visitedPlanets = new Array<>();
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

    public void setShip(Ship ship) {
        pilotingShip = true;
        boarding = false;
        this.ship = ship;
        ship.setPilot(this);
    }

    public Team getTeam() {
        return team;
    }

    public Vector2 getPosition() {
        return pilotingShip ? ship.getPosition() : position;
    }

    public void update(float dt, boolean controllable, Array<Planet> planets) {
        if (alive) {
            if (pilotingShip && controllable) {
                ship.updateControls(dt);

                if (Gdx.input.isKeyJustPressed(SettingsManager.getKey("ACTIVATE"))) {
                    exitShip();
                }
            } else {
                animTime += dt;

                for (int r = 0; r < planets.size; r++) {
                    Planet planet = planets.get(r);
                    float d = Vector2.dst(planet.getX(), planet.getY(), position.x, position.y);
                    if (d < planet.getSOI()) {
                        environment = planet;
                        break;
                    }
                }

                if (environment != null) {
                    float distance = Vector2.dst(environment.getX(), environment.getY(), position.x, position.y);
                    angle = MathUtils.atan2(position.y - environment.getY(), position.x - environment.getX());
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
                            position.x = environment.getX() + (environment.getRadius() + 10) * MathUtils.cos(angle);
                            position.y = environment.getY() + (environment.getRadius() + 10) * MathUtils.sin(angle);

                            if (!visitedPlanets.contains(environment, true)) {
                                visitedPlanets.add(environment);
                            }
                        }
                    } else {
                        velocity.set(Vector2.Zero);
                    }
                    onGround = distance < environment.getRadius() + 10;
                }

                sprinting = Gdx.input.isKeyPressed(SettingsManager.getKey("SPRINT"));

                float wx;
                float wy;
                if (Gdx.input.isKeyPressed(SettingsManager.getKey("LEFT")) && controllable) {
                    // Move anti-clockwise around planet
                    float speed = sprinting ? -300 : -100;
                    wx = speed * MathUtils.cos(angle - MathUtils.PI / 2.f);
                    wy = speed * MathUtils.sin(angle - MathUtils.PI / 2.f);
                    lefting = true;
                    righting = false;
                } else if (Gdx.input.isKeyPressed(SettingsManager.getKey("RIGHT")) && controllable) {
                    // Move clockwise around planet
                    float speed = sprinting ? 300 : 100;
                    wx = speed * MathUtils.cos(angle - MathUtils.PI / 2.f);
                    wy = speed * MathUtils.sin(angle - MathUtils.PI / 2.f);
                    righting = true;
                    lefting = false;
                } else {
                    // Bly stil
                    wx = wy = 0;
                    lefting = righting = false;
                }

                if (Gdx.input.isKeyJustPressed(SettingsManager.getKey("JUMP")) && onGround && controllable) {
                    // Jump!

                    // Works out which direction is up and shoots the player in said direction
                    float jx = 5500 * MathUtils.cos(angle);
                    float jy = 5500 * MathUtils.sin(angle);

                    velocity.add(jx * dt, jy * dt);

                    onGround = false;

                    Universe.currentUniverse.playUISound("jump");
                }

                if (boarding && Gdx.input.isKeyJustPressed(SettingsManager.getKey("ACTIVATE")) && controllable) {
                    pilotingShip = true;
                    setShip(boardingShip);
                    boardingShip = null;

                    onGround = false;

                    Universe.currentUniverse.playUISound("ingress");
                }

                position.add(velocity.x * dt, velocity.y * dt);
                position.add(wx * dt, wy * dt);

                bounds.setPosition(position.x - 10, position.y - 10);

                // AIMING
                float ax1 = Gdx.input.getX();
                float ay1 = Gdx.input.getY();

                float a = -MathUtils.atan2(ay1 - (Gdx.graphics.getHeight() / 2), ax1 - (Gdx.graphics.getWidth() / 2)) + angle
                        - MathUtils.PI / 2;

                if (weapon != null && controllable)
                    weapon.update(dt, a);

                if (Gdx.input.isKeyJustPressed(SettingsManager.getKey("GRENADE")) && grenadeCount > 0 && controllable) {
                    float gx = position.x + 30 * MathUtils.cos(a);
                    float gy = position.y + 30 * MathUtils.sin(a);

                    float gdx = 200 * MathUtils.cos(a) + velocity.x;
                    float gdy = 200 * MathUtils.sin(a) + velocity.y;

                    new Grenade(gx, gy, gdx, gdy, 10, environment, "grenade");

                    grenadeCount--;
                }
            }
        }
    }

    private float getImpactThreshold() {
        return 200;
    }

    public boolean isOnGround() {
        return pilotingShip ? ship.isOnGround() : onGround;
    }

    public void draw(SpriteBatch batch) {
        if (!pilotingShip) {
            if (alive) {
                float m = sprinting ? 3 : 1;
                if (righting)
                    batch.draw(animation.getKeyFrame(animTime * m, true), position.x - 10, position.y - 10, 10, 10, 20,
                            20, 1, 1, getAngle() * MathUtils.radiansToDegrees);
                else if (lefting)
                    batch.draw(animation.getKeyFrame(animTime * m, true), position.x - 10, position.y - 10, 10, 10, 20,
                            20, -1, 1, getAngle() * MathUtils.radiansToDegrees);
                else
                    batch.draw(idleTexture, position.x - 10, position.y - 10, 10, 10, 20, 20, 1, 1,
                            getAngle() * MathUtils.radiansToDegrees);
            }
        }
    }

    public void debugDraw(ShapeRenderer sr) {
        sr.setColor(Color.BLUE);
        sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public float getX() {
        return (pilotingShip && ship != null) ? ship.getX() : (position.x + velocity.x * Gdx.graphics.getDeltaTime());
    }

    public float getY() {
        return (pilotingShip && ship != null) ? ship.getY() : (position.y + velocity.y * Gdx.graphics.getDeltaTime());
    }

    public float getFX() {
        return (pilotingShip && ship != null) ? ship.getFX() : (position.x + velocity.x * Gdx.graphics.getDeltaTime());
    }

    public float getFY() {
        return (pilotingShip && ship != null) ? ship.getFY() : (position.y + velocity.y * Gdx.graphics.getDeltaTime());
    }

    public float getDa() {
        return da;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void die() {
        alive = false;
        ship = null;

        if (pilotingShip) {
            primarySigns.removeItems("shipfuel");
            pilotingShip = false;
        }
    }

    public float getAngle() {
        return (pilotingShip && ship != null) ? ship.getAngle() : (angle - MathUtils.PI / 2) % MathUtils.PI2;
    }

    public float getDX() {
        return velocity.x;
    }

    public float getDY() {
        return velocity.y;
    }

    public void spawn(float x, float y, Array<Planet> planets) {
        position.set(x, y);
        velocity.set(Vector2.Zero);
        alive = true;

        Gdx.app.log("Player", "Player respawned at " + position);

        health = 200;
        grenadeCount = 5;

        if (weapon != null)
            weapon.reset();

        for (Planet planet : planets) {
            if (planet.inRange(x, y)) {
                environment = planet;
                return;
            }
        }
    }

    public void setBoarding(boolean boarding, Ship boardingShip) {
        this.boarding = boarding;
        this.boardingShip = boardingShip;
    }

    public boolean isBoarding() {
        return boarding;
    }

    public void doDamage(float amount, DamageCause cause) {
        if (alive) {
            health -= amount;

            Universe.currentUniverse.playUISound("playerhit" + Tools.decide(1, 2, 3, 4, 5));

            if (health < 0) {
                System.out.println("Player is dead!");
                health = 0;
                alive = false;
                Universe.currentUniverse.playerKilled(this, cause);
            }
        }
    }

    public Vector2 getVelocity() {
        return pilotingShip ? ship.getVelocity() : velocity;
    }

    public String getEnvironmentID() {
        return (environment != null) ? environment.getID() : "???";
    }

    public boolean isAlive() {
        return alive;
    }

    @Override
    public void collectCollectible(Collectible collectible) {
        // Yeah baby!
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

        return true;
    }

    @Override
    public String getType() {
        return "player";
    }

    public void replenishHealth() {
        health = 200;
    }

    public String getShipID() {
        return pilotingShip ? ship.getID() : "???";
    }

    public void addSpeed(Vector2 added) {
        velocity.add(added);
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
    }

    public void kickBack(float angle, float force) {
        velocity.add(force * MathUtils.sin(angle), force * MathUtils.cos(angle));
    }

    public Array<Planet> getVisitedPlanets() {
        return visitedPlanets;
    }

    public float getVisitedPlanetsNo() {
        return visitedPlanets.size;
    }

    @Override
    public void dispose() {
        idleTexture.getTexture().dispose();

        for (TextureRegion region : animation.getKeyFrames()) {
            region.getTexture().dispose();
        }
    }

    public void addToast() {
        toastCount++;
        hud.showNotification("Toast", "You got toast!");
    }

    public void orbit(float direction) {
        angle = MathUtils.atan2(position.y - environment.getY(), position.x - environment.getX());
        float v = (float) Math.sqrt(Universe.GRAVITY * environment.getMass() / position.sub(environment.getX(), environment.getY()).len()) * -direction;
        velocity.x = MathUtils.sin(angle) * v;
        velocity.y = MathUtils.cos(angle) * v;
    }

    public float getToastCount() {
        return toastCount;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
        weapon.pickup(this);
        primarySigns.addItems(weapon);
        hud.showNotification("Equipment", "Picked up " + weapon.getName());
    }
}
