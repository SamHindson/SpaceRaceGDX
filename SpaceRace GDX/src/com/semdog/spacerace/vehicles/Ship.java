package com.semdog.spacerace.vehicles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.collectables.Collectible;
import com.semdog.spacerace.collectables.Fuel;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.graphics.effects.Explosion;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.players.VitalSigns;
import com.semdog.spacerace.players.VitalSigns.Type;
import com.semdog.spacerace.players.Vitality;
import com.semdog.spacerace.universe.*;

/***
 * The ship class, which all of the in-game vehicles derive from.
 * TODO optimize the ship classes, because they function in mostly the same way.
 *
 * @author Sam
 */

public abstract class Ship extends Mass implements Collideable, Trackable {

    Player pilot;
    float currentFuel;
    float r;
    float width;
    float power;
    float pCooldown;
    float pRest;
    float sRest;
    VitalSigns vitalSigns;
    ParticleEffect particleEffect;
    private Sprite sprite;
    private Sprite silhouette;
    private float totalFuel;
    private float height;
    private float rotationalVelocity;
    private float beepTime;
    private boolean boostActive = false;
    private float boostTime = 30;
    private float boostRemaining;
    private Vitality vBoost;
    private boolean initialized;
    private boolean enterable;
    private boolean flipping;

    public Ship(float x, float y, float w, float h, float fuel, float power, String textureName, String id) {
        super(x, y, 0, 0, 5000, w, h, null, id);

        totalFuel = currentFuel = fuel;

        this.power = power;

        sprite = new Sprite(Art.get(textureName));
        silhouette = new Sprite(Art.get(textureName + "_sil"));
        sprite.setOriginCenter();
        width = sprite.getWidth();
        height = sprite.getHeight();

        Universe.currentUniverse.addShip(this);

        beepTime = MathUtils.random() * 5;

        this.id = id;

        vitalSigns = new VitalSigns();
        Vitality vFuel = new Vitality() {
            @Override
            public Type getValueType() {
                return Type.CONTINUOUS;
            }

            @Override
            public String getDisplayName() {
                return "Fuel";
            }

            @Override
            public float getValue() {
                return currentFuel;
            }

            @Override
            public float getMaxValue() {
                return totalFuel;
            }

            @Override
            public Color getColor() {
                return Colors.V_FUEL;
            }

            @Override
            public String getID() {
                return "fuel";
            }
        };

        Vitality vHealth = new Vitality() {
            @Override
            public Type getValueType() {
                return Type.CONTINUOUS;
            }

            @Override
            public String getDisplayName() {
                return "Integrity";
            }

            @Override
            public float getValue() {
                return currentHealth;
            }

            @Override
            public float getMaxValue() {
                return maxHealth;
            }

            @Override
            public Color getColor() {
                return Colors.V_SHIPHEALTH;
            }

            @Override
            public String getID() {
                return "shiphealth";
            }
        };

        vBoost = new Vitality() {
            @Override
            public Type getValueType() {
                return Type.CONTINUOUS;
            }

            @Override
            public String getDisplayName() {
                return "Boost";
            }

            @Override
            public float getValue() {
                return boostRemaining;
            }

            @Override
            public float getMaxValue() {
                return boostTime;
            }

            @Override
            public Color getColor() {
                return Colors.V_BOOST;
            }

            @Override
            public String getID() {
                return "boost";
            }
        };

        vitalSigns.addItems(vHealth, vFuel);
    }

    @Override
    protected void enteredOrbit() {
        if (pilot != null) {
            pilot.enteredOrbit();
        }
    }

    @Override
    protected void exitedOrbit() {
        if (pilot != null) {
            pilot.exitedOrbit();
        }
    }

    @Override
    public void update(float dt, Array<Planet> gravitySources) {
        super.update(dt, gravitySources);
        sprite.setRotation(r);
        sprite.setPosition(position.x - width / 2, position.y - height / 2);

        if (boostActive) {
            boostRemaining -= dt;

            if (boostRemaining <= 0) {
                setBoostActive(false);
            }
        }

        if (pilot == null) {
            beepTime += dt;

            if (onGround)
                rotationalVelocity = 0;

            r += rotationalVelocity * dt;

            if (beepTime > 5f) {
                beepTime = 0;
                Universe.currentUniverse.playSound("neet", position.x, position.y, 0.5f);
            }

            enterable = Math.abs(getAngle() - getAngleAroundEnvironment() + MathUtils.PI / 2) < 0.05f || !onGround;

            if (onGround && flipping) {
                r = getAngleAroundEnvironment() * MathUtils.radiansToDegrees - 90;
                enterable = true;
                flipping = false;
            }
        }

        if (ouchTime > 0) {
            silhouette.setRotation(r);
            silhouette.setPosition(position.x - width / 2, position.y - height / 2);
        }
    }

    float getCurrentPower() {
        if (boostActive)
            return power + 300;
        else
            return power;
    }

    @Override
    protected void setEnvironment(Planet planet) {
        super.setEnvironment(planet);

        if (!initialized) {
            initialized = true;
            r = (-MathUtils.PI / 2 + MathUtils.atan2(position.y - environment.getY(), position.x - environment.getX())) * MathUtils.radiansToDegrees;
        }

        if (pilot != null) {
            pilot.setEnvironment(planet);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        particleEffect.draw(batch);
        sprite.draw(batch);

        //  If it has just been hurt, make it look so
        if (ouchTime > 0) {
            silhouette.setAlpha(MathUtils.random(0.5f, 1));
            silhouette.draw(batch);
        }
    }

    @Override
    public void dispose() {
        particleEffect.dispose();
    }

    public abstract void updateControls(float dt);

    public float getAngle() {
        return r * MathUtils.degreesToRadians;
    }

    public float getAngleAroundEnvironment() {
        return environment != null ? MathUtils.atan2(position.y - environment.getY(), position.x - environment.getX()) : 0;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void die(DamageCause reason) {
        super.die(reason);
        alive = false;
        explode(reason);
    }

    private void setBoostActive(boolean boostActive) {
        this.boostActive = boostActive;

        if (boostActive) {
            vitalSigns.addItems(vBoost);
            boostRemaining = 30;
        } else {
            vitalSigns.removeItems("boost");
        }
    }

    public boolean isEnterable() {
        return enterable;
    }

    @Override
    protected void handlePlanetCollision(float speed, boolean v) {
        super.handlePlanetCollision(speed, v);
    }

    void explode(DamageCause cause) {
        Universe.currentUniverse.addEffect(new Explosion(position.x, position.y));

        if (pilot != null) {
            Universe.currentUniverse.playerKilled(cause);
        }

        rud();
    }

    /**
     * This code is executed when the ship undergoes a RUD (Rapid Unscheduled Disassembly)
     */
    private void rud() {
        for (int k = 0; k < 30; k++) {
            new DebrisPiece(position.x, position.y, velocity.x, velocity.y, environment, this);
        }
    }

    @Override
    public int getType() {
        return Collectible.SHIP;
    }

    @Override
    protected void hitPlayer(Player player) {
        player.doDamage(new Vector2(getVelocity()).sub(player.getVelocity().x, player.getVelocity().y).len(), DamageCause.SHIP);
        player.addSpeed(velocity);
    }

    public abstract void firePrimary();

    public void flip() {
        flipping = true;
    }

    public void setPilot(Player pilot) {
        this.pilot = pilot;

        rotationalVelocity = 0;

        if (pilot == null) {
            playerExited();
        }
    }

    void playerExited() {

    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public float getAltitude() {
        return Vector2.dst(position.x, position.y, environment.getX(), environment.getY());
    }

    public String getID() {
        return id;
    }

    @Override
    protected void handleMassCollision(Mass mass) {
        super.handleMassCollision(mass);

        if (mass instanceof Grenade) {
            ((Grenade) mass).explode();
        }
    }

    public VitalSigns getVitalSigns() {
        return vitalSigns;
    }

    public boolean hasPilot() {
        return pilot != null;
    }

    void orbit(float direction) {
        if (environment == null)
            findEnvironment();
        angle = MathUtils.atan2(position.y - environment.getY(), position.x - environment.getX());
        float v = (float) Math.sqrt(Universe.GRAVITY * environment.getMass() / distance(environment)) * -direction;
        velocity.x = MathUtils.sin(angle) * v;
        velocity.y = MathUtils.cos(angle) * v;
    }

    public float getFX() {
        return position.x;
    }

    public float getFY() {
        return position.y;
    }

    public void replenishFuel() {
        currentFuel = totalFuel;
    }

    @Override
    public void collectCollectible(Collectible collectible) {
    }

    @Override
    public Color getGizmoColor() {
        return pilot != null ? Color.CLEAR : Colors.P_WHITE;
    }

    public void addToast() {
        if (pilot != null)
            pilot.addToast();
    }

    @Override
    public void doDamage(float amount, DamageCause reason) {
        super.doDamage(amount, reason);

        if (pilot == null && !onGround) {
            rotationalVelocity = MathUtils.random(-amount, amount);
        }
    }

    @Override
    public boolean canCollect(Collectible collectible) {
        return !(collectible instanceof Fuel) || currentFuel < totalFuel;
    }

    @Override
    public Rectangle getBounds() {
        return super.getBounds();
    }
}
