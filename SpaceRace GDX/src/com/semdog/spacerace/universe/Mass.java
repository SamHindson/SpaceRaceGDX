package com.semdog.spacerace.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.semdog.spacerace.misc.OrbitalHelper;
import com.semdog.spacerace.misc.Tools;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.vehicles.DebrisPiece;
import com.semdog.spacerace.vehicles.Ship;
import com.semdog.spacerace.weapons.Bullet;

/**
 * A class which allows objects to express properties of masses found in the real world (i.e. velocity, mass, inertia, collisions)
 */

public abstract class Mass implements Disposable {

    protected static Color[] orbitColors = {Color.WHITE, Color.LIGHT_GRAY, Color.GRAY, Color.DARK_GRAY,};

    protected Vector2 position, velocity;
    protected float angle;
    protected float width, height;
    protected boolean onGround, alive = true;
    protected Planet environment;
    protected boolean shouldCollide;
    protected float currentHealth, maxHealth;
    protected float age;
    protected float ouchTime;
    protected String id;
    Rectangle bounds;
    private float mass;
    private boolean gravityEnabled = true, physicsEnabled = true;
    private boolean orbiting = false;

    protected Mass(float x, float y, float dx, float dy, float mass, float width, float height, Planet environment,
                   String id) {
        position = new Vector2(x, y);
        velocity = new Vector2(dx, dy);
        this.mass = mass;
        this.environment = environment;
        this.id = id;

        this.width = width;
        this.height = height;

        shouldCollide = true;

        Universe.currentUniverse.requestMass(this);

        if (width == 0 || height == 0) {
            Gdx.app.error("Mass", "Warning! Mass created with a zero width or height. What the hell mate?");
        }

        bounds = new Rectangle(x, y, width, height);
    }

    protected Mass(float x, float y, float dx, float dy, float mass, Planet environment, String id) {
        this(x, y, dx, dy, mass, 1, 1, environment, id);
    }

    public Mass(float x, float y, float dx, float dy, float mass, float width, float height, String id) {
        this(x, y, dx, dy, mass, width, height, null, id);
    }

    private static Color getOrbitColor(Mass mass) {
        Color color = Color.GRAY;

        if (mass instanceof Ship) {
            if (((Ship) mass).hasPilot()) {
                return Color.WHITE;
            } else {
                return Color.GRAY;
            }
        } else if (mass instanceof Grenade) {
            return Color.LIGHT_GRAY;
        } else if (mass instanceof DebrisPiece) {
            return Color.DARK_GRAY;
        }

        return color;
    }

    public float getMass() {
        return mass;
    }

    public void setPhysicsEnabled(boolean physicsEnabled) {
        this.physicsEnabled = physicsEnabled;
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
        ouchTime = 1;
        Universe.currentUniverse.playSound("shiphurt", position.x, position.y, 1.f);
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

    public void update(float dt, Array<Planet> gravitySources) {
        if (physicsEnabled) {
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
                boolean foundPlanet = false;
                for (int i = 0; i < gravitySources.size; i++) {
                    Planet planet = gravitySources.get(i);
                    if (inRange(planet)) {
                        foundPlanet = true;

                        if (!onGround) {
                            if (planet != environment) {
                                setEnvironment(planet);
                            }

                            float force = (float) (Universe.GRAVITY * planet.getMass() / Math.pow(distance(planet), 2));
                            float ax = -force * MathUtils.cos(angle);
                            float ay = -force * MathUtils.sin(angle);

                            velocity.x += ax * dt;
                            velocity.y += ay * dt;
                            break;
                        }
                    }
                }

                if (!foundPlanet) {
                    environment = null;
                }
            }

            position.x += velocity.x * dt;
            position.y += velocity.y * dt;

            bounds.setPosition(position.x - getWidth() / 2, position.y - getHeight() / 2);

            if (ouchTime > 0) {
                ouchTime -= dt * 5f;
            } else {
                ouchTime = 0;
            }
        }
    }

    private void addMomentum(Vector2 velocity, float p) {
        float vx = velocity.x;
        float vy = velocity.y;
        this.velocity.add(vx * p / mass, vy * p / mass);
    }

    protected void setEnvironment(Planet planet) {
        environment = planet;
    }

    protected abstract float getImpactThreshold();

    /**
     * A method that does some useful stuff regarding the mass, like checking
     * whether it's on the planet surface and whether it's got its stuff loaded
     */
    public void checkState() {
        onSurface(environment);
    }

    private boolean onSurface(Planet planet) {
        if (environment != null) {
            if (!onGround) {
                if (distance(planet) <= planet.getRadius() + getHeight() / 2.f) {
                    float speed = Vector2.dst(0, 0, velocity.x, velocity.y);
                    handlePlanetCollision(speed, true);
                } else {
                    onGround = false;
                }
            } else {
                if (distance(planet) >= planet.getRadius() + getHeight() / 2.f) {
                    onGround = false;
                }
            }
        }
        return onGround;
    }

    protected void handlePlanetCollision(float speed, boolean withPlanet) {
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

    public Rectangle getCollisionBounds() {
        return bounds;
    }

    public Rectangle getBounds() {
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

    private boolean inRange(Planet planet) {
        return distance(planet) < planet.getSOI();
    }

    protected float distance(Planet planet) {
        return Vector2.dst(position.x, position.y, planet.getX(), planet.getY());
    }

    public void debugRender(ShapeRenderer renderer) {
        renderer.setColor(getOrbitColor(this));

        if (environment != null) {
            if (getEccentricity() < 1 && getApogee() < environment.getSOI()) {
                float semiminor = getSemiminor();
                float semimajor = getSemimajor();
                float f = (float) Math.sqrt(semimajor * semimajor - semiminor * semiminor);

                renderer.identity();
                renderer.translate(environment.getPosition().x, environment.getPosition().y, 0);
                renderer.rotate(0, 0, 1, getOrbitAngle() * MathUtils.radiansToDegrees + 90);
                try {
                    renderer.ellipse((-semiminor), (-semimajor - f), semiminor * 2, semimajor * 2);
                } catch (Exception e) {
                    Gdx.app.error("Mass", "There was some sort of error while trying to render that orbit. Oops");
                }
                renderer.identity();
                renderer.setColor(Color.WHITE);
            } else {
                // TODO someday figure out how to draw proper hyperbolic / parabolic orbits
                float fx = position.x + velocity.x * 100;
                float fy = position.y + velocity.y * 100;
                renderer.line(position.x, position.y, fx, fy);
            }
        } else {
            float fx = position.x + velocity.x * 100;
            float fy = position.y + velocity.y * 100;
            renderer.line(position.x, position.y, fx, fy);
        }
    }

    protected void findEnvironment() {
        for (Planet planet : Universe.currentUniverse.getPlanets()) {
            if (planet.inRange(position.x, position.y)) {
                environment = planet;
                return;
            }
        }
    }

    public void render(SpriteBatch batch) {
    }

    public void checkCollisions(Array<Mass> masses) {
        for (int u = 0; u < masses.size; u++) {
            Mass m = masses.get(u);

            if (!m.equals(this)) {
                if (Intersector.overlaps(getCollisionBounds(), m.getCollisionBounds()) && m.shouldCollide && shouldCollide) {
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

    protected void handleMassCollision(Mass mass) {

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
        if (Intersector.overlaps(getCollisionBounds(), player.getCollisionBounds())) {
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

    public void setPosition(float x, float y) {
        position.set(x, y);
    }
}
