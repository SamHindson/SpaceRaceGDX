package com.semdog.spacerace.net.entities;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.misc.OrbitalHelper;
import com.semdog.spacerace.misc.Tools;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.universe.Universe;
import com.semdog.spacerace.weapons.Bullet;

import java.util.ArrayList;

/**
 *
 */

public abstract class VirtualMass {
    protected Vector2 position, velocity;
    protected float angle;
    protected float width, height;
    protected boolean onGround, alive = true;
    protected VirtualPlanet environment;
    protected boolean shouldCollide;
    protected float currentHealth, maxHealth;
    protected float age;
    protected String id;
    Rectangle bounds;
    private float mass;
    private boolean gravityEnabled = true;
    private boolean orbiting = false;

    protected VirtualMass(float x, float y, float dx, float dy, float mass, float width, float height, VirtualPlanet environment,
                          String id) {
        position = new Vector2(x, y);
        velocity = new Vector2(dx, dy);
        this.mass = mass;
        this.environment = environment;
        this.id = id;

        this.width = width;
        this.height = height;

        shouldCollide = true;

        VirtualUniverse.currentUniverse.addMass(this);

        bounds = new Rectangle(x, y, width, height);
    }

    protected VirtualMass(float x, float y, float dx, float dy, float mass, VirtualPlanet environment, String id) {
        this(x, y, dx, dy, mass, 1, 1, environment, id);
    }

    public VirtualMass(float x, float y, float dx, float dy, float mass, float width, float height, String id) {
        this(x, y, dx, dy, mass, width, height, null, id);
    }

    protected void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        currentHealth = maxHealth;
    }

    public boolean isOnGround() {
        return environment != null && Vector2.dst(position.x, position.y, environment.getX(),
                environment.getY()) <= environment.getRadius() + 5;
    }

    public void doDamage(float amount, DamageCause reason) {
        if (currentHealth <= amount) {
            currentHealth = 0;
            alive = false;
            die(reason);
        } else {
            currentHealth -= amount;
        }
    }

    protected void enteredOrbit() {

    }

    protected void exitedOrbit() {

    }

    public void update(float dt, ArrayList<VirtualPlanet> gravitySources) {
        if (environment != null) {
            angle = MathUtils.atan2(position.y - environment.getY(), position.x - environment.getX());

            if (getPerigee() > environment.getRadius() && !orbiting) {
                orbiting = true;
                enteredOrbit();
            } else if (orbiting && getPerigee() < environment.getRadius()) {
                orbiting = false;
                exitedOrbit();
            }
        }

        age += dt;

        if (gravityEnabled) {
            boolean foundVirtualPlanet = false;
            for (int i = 0; i < gravitySources.size(); i++) {
                VirtualPlanet planet = gravitySources.get(i);
                if (inRange(planet))
                    foundVirtualPlanet = true;

                if (!onGround) {
                    if (planet != environment) {
                        setEnvironment(planet);
                    }

                    float force = (float) (Universe.GRAVITY * planet.getMass() / Math.pow(distance(planet), 2));
                    float ax = -force * MathUtils.cos(angle);
                    float ay = -force * MathUtils.sin(angle);

                    velocity.x += ax * dt;
                    velocity.y += ay * dt;

                    checkState();
                    break;
                } else {
                    velocity.set(0, 0);
                }
            }

            if (!foundVirtualPlanet) {
                environment = null;
            }
        }


        position.x += velocity.x * dt;
        position.y += velocity.y * dt;

        bounds.setPosition(position.x - getWidth() / 2, position.y - getHeight() / 2);
    }

    private void addMomentum(Vector2 velocity, float p) {
        float vx = velocity.x;
        float vy = velocity.y;
        this.velocity.add(vx * p / mass, vy * p / mass);
    }

    protected void setEnvironment(VirtualPlanet VirtualPlanet) {
        environment = VirtualPlanet;
    }

    protected abstract float getImpactThreshold();

    /**
     * A method that does some useful stuff regarding the mass, like checking
     * whether it's on the VirtualPlanet surface and whether it's got its stuff loaded
     */
    public void checkState() {
        onSurface(environment);
    }

    private boolean onSurface(VirtualPlanet VirtualPlanet) {
        if (environment != null) {
            if (!onGround) {
                if (distance(VirtualPlanet) <= VirtualPlanet.getRadius() + getHeight() / 2.f) {
                    float speed = Vector2.dst(0, 0, velocity.x, velocity.y);
                    handleVirtualPlanetCollision(speed, true);
                } else {
                    onGround = false;
                }
            } else {
                if (distance(VirtualPlanet) >= VirtualPlanet.getRadius() + getHeight() / 2.f) {
                    onGround = false;
                }
            }
        }
        return onGround;
    }

    protected void handleVirtualPlanetCollision(float speed, boolean withVirtualPlanet) {
        if (speed > getImpactThreshold()) {
            die(DamageCause.PLANET);
        } else if (speed > getImpactThreshold() * 0.5f) {
            float fraction = speed / getImpactThreshold() - 0.5f;
            doDamage(fraction * (maxHealth), DamageCause.PLANET);
        }
        onGround = true;
        velocity.x = velocity.y = 0;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getDx() {
        return velocity.x;
    }

    protected void setDx(float dx) {
        velocity.x = dx;
    }

    public float getDy() {
        return velocity.y;
    }

    protected void setDy(float dy) {
        velocity.y = dy;
    }

    private Rectangle getBounds() {
        return bounds;
    }

    public boolean isAlive() {
        return alive;
    }

    protected void die(DamageCause reason) {
        alive = false;
    }

    protected float getWidth() {
        return width;
    }

    protected float getHeight() {
        return height;
    }

    private float getAverageRadius() {
        return (getWidth() + getHeight()) / 4.f;
    }

    private boolean inRange(VirtualPlanet VirtualPlanet) {
        return distance(VirtualPlanet) < VirtualPlanet.getSOI();
    }

    protected float distance(VirtualPlanet VirtualPlanet) {
        return Vector2.dst(position.x, position.y, VirtualPlanet.getX(), VirtualPlanet.getY());
    }

    protected void findEnvironment() {
        for (VirtualPlanet planet : VirtualUniverse.currentUniverse.getPlanets()) {
            if (inRange(planet)) {
                environment = planet;
                return;
            }
        }
    }

    public void checkCollisions(Array<VirtualMass> masses) {
        for (int u = 0; u < masses.size; u++) {
            VirtualMass m = masses.get(u);

            if (!m.equals(this)) {
                if (Intersector.overlaps(getBounds(), m.getBounds()) && m.shouldCollide && shouldCollide) {
                    float v = Vector2.len(velocity.x - m.getDx(), velocity.y - m.getDy());
                    float d = Vector2.dst(position.x, position.y, m.getX(), m.getY());
                    float a1 = MathUtils.atan2(m.getY() - getY(), m.getX() - getX());
                    float a2 = -a1;
                    float r = (getRadius(a1) + m.getRadius(a2));
                    float i = (d - r) / 2.f;

                    handleMassCollision(m);

                    if (v > getImpactThreshold()) {
                        die(DamageCause.DEBRIS);
                    }

                    position.x += i * MathUtils.cos(a1);
                    position.y += i * MathUtils.sin(a1);

                    float p1 = mass * getVelocity().len();
                    float p2 = m.mass * m.getVelocity().len();

                    float pf = (p1 + p2) / 2.f;

                    velocity.x += (pf / mass) * -MathUtils.cos(a1);
                    velocity.x += (pf / mass) * -MathUtils.sin(a1);
                }
            }
        }
    }

    protected void handleMassCollision(VirtualMass mass) {

    }

    private float getRadius(float angle) {
        float ar = getAverageRadius();
        return ar * (float) Math.min(Math.abs(1.f / (Math.cos(angle))), Math.abs(1.f / (Math.sin(angle))));
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    protected abstract void hitPlayer(Player player);

    public void checkPlayerCollision(Player player) {
        if (Intersector.overlaps(getBounds(), player.getCollisionBounds())) {
            if (getVelocity().len() - player.getVelocity().len() > 50) {
                hitPlayer(player);
                Universe.currentUniverse.playSound("playerhit" + Tools.decide(1, 2, 3, 4, 5), position.x, position.y,
                        0);
            }
        }
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getPerigee() {
        return OrbitalHelper.computeOrbit(environment.getPosition(), getPosition(), getVelocity(),
                environment.getMass())[6];
    }

    public float getApogee() {
        return OrbitalHelper.computeOrbit(environment.getPosition(), getPosition(), getVelocity(),
                environment.getMass())[5];
    }

    private float getSemiminor() {
        return OrbitalHelper.computeOrbit(environment.getPosition(), getPosition(), getVelocity(),
                environment.getMass())[4];
    }

    private float getSemimajor() {
        return OrbitalHelper.computeOrbit(environment.getPosition(), getPosition(), getVelocity(),
                environment.getMass())[3];
    }

    private float getTrueAnomaly() {
        return OrbitalHelper.computeOrbit(environment.getPosition(), getPosition(), getVelocity(),
                environment.getMass())[7];
    }

    private float getOrbitAngle() {
        return getTrueAnomaly() - MathUtils.PI
                + MathUtils.atan2(position.y - environment.getPosition().y, position.x - environment.getPosition().x);
    }

    public boolean contians(Bullet bullet) {
        float accuracy = 5;

        float ox = bullet.getX();
        float oy = bullet.getY();

        float fx = bullet.getX() + (bullet.getDx() * 0.01f);
        float fy = bullet.getY() + (bullet.getDy() * 0.01f);

        for (float i = 0; i <= accuracy; i++) {
            float px = MathUtils.lerp(ox, fx, i / accuracy);
            float py = MathUtils.lerp(oy, fy, i / accuracy);

            if (bounds.contains(px, py)) {
                addMomentum(bullet.getVelocity(), bullet.getDamage() / 100.f);
                return true;
            }
        }

        return false;
    }

    private float getEccentricity() {
        return OrbitalHelper.computeOrbit(environment.getPosition(), getPosition(), getVelocity(),
                environment.getMass())[1];
    }
}
