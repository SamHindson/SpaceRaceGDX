package com.semdog.spacerace.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.semdog.spacerace.audio.SoundManager;
import com.semdog.spacerace.collectables.Collectible;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.graphics.effects.Effect;
import com.semdog.spacerace.graphics.effects.Explosion;
import com.semdog.spacerace.io.SettingsManager;
import com.semdog.spacerace.misc.FontManager;
import com.semdog.spacerace.misc.SRCamera;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.players.HUD;
import com.semdog.spacerace.players.LifeAndDeath;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.races.RaceManager;
import com.semdog.spacerace.screens.PlayScreen;
import com.semdog.spacerace.ui.Overlay;
import com.semdog.spacerace.ui.RaceEndScreen;
import com.semdog.spacerace.vehicles.Ship;
import com.semdog.spacerace.weapons.Bullet;

/**
 * The container for everything that is ingame.
 */

public class Universe implements Disposable {
    public static final float GRAVITY = 30f;
    public static Universe currentUniverse;
    static PlayScreen container;
    static boolean exiting;
    protected Array<Mass> masses;
    float fcx = 0;
    float fcy = 0;
    float spawnX, spawnY;
    float age;
    boolean playerEnabled;
    Array<Planet> planets;
    Array<Effect> effects;
    Array<Bullet> bullets;
    Array<Collideable> collideables;
    Array<Trackable> trackables;
    GoalChecker goalChecker;
    HUD hud;
    Player player;
    SpriteBatch hudBatch;
    SpriteBatch universeBatch;
    BitmapFont computerFont;
    ShapeRenderer universeShapeRenderer;
    SRCamera camera;
    Sprite stars;
    float cameraX;
    float cameraY;
    boolean lockedRotation = false;
    boolean followingPlayer = true;
    float injuryAlpha;
    float zoom;
    float desiredZoom;
    float cameraShake = 0;
    boolean isLoading = true;
    Overlay raceEnd;
    boolean gogglesActive = false;
    boolean activated = false;
    private float timeLeft;
    private float timeLimit;
    private float raceEndDelay;
    private float countdown = 10;
    private boolean suddenDeath = false;
    private boolean terminated = false;
    private boolean won = false;
    private boolean countingDown = true;
    private Array<Ship> ships;
    private Array<Collectible> collectibles;
    private Array<Box> boxes;
    private boolean shownEnd = false;

    /**
     * Default constructor for inheritors.
     */
    public Universe() {

    }

    public Universe(PlayScreen _container) {
        container = _container;

        currentUniverse = this;

        planets = new Array<>();
        masses = new Array<>();
        ships = new Array<>();
        boxes = new Array<>();
        effects = new Array<>();
        bullets = new Array<>();
        collectibles = new Array<>();
        collideables = new Array<>();
        trackables = new Array<>();

        player = new Player(0, 0, null);
        trackables.add(player);

        hud = new HUD(player);

        hudBatch = new SpriteBatch();

        camera = new SRCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 0, 0);
        camera.zoom = 0.5f;

        universeBatch = new SpriteBatch();
        universeBatch.setProjectionMatrix(camera.combined);
        universeShapeRenderer = new ShapeRenderer();

        Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth() * 5, Gdx.graphics.getWidth() * 5, Format.RGBA4444);
        pixmap.setColor(new Color(0.05f, 0.05f, 0.05f, 1f));
        pixmap.fill();

        for (int k = 0; k < 5000; k++) {
            float i = MathUtils.random();
            float x = MathUtils.random(pixmap.getWidth());
            float y = MathUtils.random(pixmap.getHeight());
            float s = MathUtils.random(2) + 1;
            pixmap.setColor(i, i, i, 1);
            pixmap.fillCircle((int) x, (int) y, (int) s);
        }

        stars = new Sprite(new Texture(pixmap));
        pixmap.dispose();
        stars.setOriginCenter();

        Gdx.input.setInputProcessor(new InputManager());
        camera.zoom = 1.5f;
        camera.update();

        isLoading = false;

        goalChecker = new GoalChecker();
        raceEnd = new RaceEndScreen();

        playerEnabled = false;

        collideables.add(player);

        exiting = false;

        player.setHud(hud);
        computerFont = FontManager.getFont("mohave-18-italic");
    }

    public static void reset() {
        container.bigBang(false);
    }

    public static void transcend() {
        exiting = true;
        SoundManager.stopMusic("victory");
        SoundManager.stopMusic("failure");
        currentUniverse.dispose();
        container.setMarkedForDestruction(true);
        container.getGame().changeScreen("playmenu");
    }

    public static boolean isExiting() {
        return exiting;
    }

    public boolean isSuddenDeath() {
        return suddenDeath;
    }

    public void setSuddenDeath(boolean suddenDeath) {
        this.suddenDeath = suddenDeath;
    }

    public void setLockedRotation(boolean lockedRotation) {
        this.lockedRotation = lockedRotation;
    }

    public boolean isPlayerHome() {
        return player.isHome();
    }

    public float getShipCount() {
        return ships.size;
    }

    public Array<Planet> getPlanets() {
        return planets;
    }

    public GoalChecker getGoalChecker() {
        return goalChecker;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void tick(float dt) {
        age += dt;

        if (desiredZoom >= 5) {
            desiredZoom = 5;
        } else if (desiredZoom <= 0.25f) {
            desiredZoom = 0.25f;
        }

        if (activated) {
            if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
                desiredZoom = 1;
            }

            float deltaZoom = (desiredZoom - camera.zoom) * dt * 5;

            zoom += deltaZoom;
            camera.zoom += deltaZoom;

            if (!gogglesActive && Gdx.input.isKeyPressed(SettingsManager.getKey("GOGGLES"))) {
                gogglesActive = true;
                playUISound("goggleson");
            } else if (gogglesActive && !Gdx.input.isKeyPressed(SettingsManager.getKey("GOGGLES"))) {
                gogglesActive = false;
                playUISound("gogglesoff");
            }

            if (Gdx.input.isKeyJustPressed(SettingsManager.getKey("LOCK"))) {
                lockedRotation = !lockedRotation;
                hud.makeToast("Camera " + (lockedRotation ? "locked" : "unlocked"), 0.5f, Colors.UI_BLUE);
            }

            if (lockedRotation)
                camera.setRotation(-player.getAngleAroundEnvironment() * MathUtils.radiansToDegrees + 90);
            else
                camera.setRotation(0);

            hud.update(dt);

            if (countingDown) {
                countdown -= dt * 5;
                hud.setText("Get ready!", "[" + (int) countdown + "]");

                if (countdown <= 0) {
                    countingDown = false;
                    playerEnabled = true;
                    hud.hideMessage();
                    hud.showStats();
                    hud.showTimer();
                }
            } else if (!terminated) {
                timeLeft -= dt;
                hud.setCountdownValue((int) timeLeft);

                if (timeLeft <= 0 && !won) {
                    raceEndDelay += dt;
                    terminated = true;
                    if (!shownEnd) {
                        terminateRace(false, null);
                    }
                }
            } else {
                raceEnd.update(dt);
            }

            goalChecker.update(this);

            if (goalChecker.isVictory()) {
                playerEnabled = false;
                raceEndDelay += dt;

                if (!shownEnd) {
                    shownEnd = true;

                    float time = timeLimit - timeLeft;

                    boolean best = time < RaceManager.getCurrentRace().getBestTime();

                    RaceManager.getCurrentRace().setCompleted(true);

                    if (best) RaceManager.setNewBestTime(time);
                    
                    String timeString = timeLeft < 10 ? String.format("%.2f", timeLeft) : (int) timeLeft + "";
                    String text = "Race completed with " + timeString + "s left!" + (best ? "\nNew Record!" : "");
                    raceEnd.setColor(best ? Colors.UI_GREEN : Colors.UI_WHITE);
                    raceEnd.setText("Victory!", text);
                    SoundManager.stopMusic("oxidiser");
                    SoundManager.playMusic("victory", false);

                    won = true;

                    hud.hideAll();
                    raceEnd.setShowing(true);
                }
            }

            if (injuryAlpha > 0) {
                injuryAlpha *= 0.5f;

                if (injuryAlpha < 0.05f)
                    injuryAlpha = 0;
            }

        }

        for (int i = 0; i < bullets.size; i++) {
            if (bullets.get(i) != null)
                bullets.get(i).checkCollisions(planets);
        }

        for (int i = 0; i < masses.size; i++) {
            masses.get(i).checkCollisions(masses);

            if (!player.isPilotingShip() && masses.get(i) != null)
                masses.get(i).checkPlayerCollision(player);
        }

        for (int i = 0; i < masses.size; i++) {
            Mass mass = masses.get(i);
            for (int j = 0; j < bullets.size; j++) {
                Bullet bullet = bullets.get(j);
                if (mass.contians(bullet)) {
                    mass.doDamage(bullet.getDamage(), DamageCause.BULLET);
                    bullet.die();
                }
            }
        }

        for (int i = 0; i < masses.size; i++) {
            masses.get(i).checkState();
        }

        for (int i = 0; i < collectibles.size; i++) {
            collectibles.get(i).update(dt, collideables);
        }

        for (Effect effect : effects) {
            effect.update(dt);
        }

        float desiredCX;
        float desiredCY;

        if (followingPlayer) {
            desiredCX = player.getFX();
            desiredCY = player.getFY();
        } else {
            desiredCX = fcx;
            desiredCY = fcy;
        }

        cameraX += (desiredCX - cameraX) / 15.f;
        cameraY += (desiredCY - cameraY) / 15.f;

        camera.position.set(cameraX + MathUtils.random() * cameraShake, cameraY + MathUtils.random() * cameraShake, 0);
        camera.update();

        universeBatch.setProjectionMatrix(camera.combined);
        universeShapeRenderer.setProjectionMatrix(camera.combined);
        stars.setOriginCenter();
        stars.setPosition(player.getFX() - stars.getWidth() / 2, player.getFY() - stars.getHeight() / 2);

        boolean f = true;
        if (!player.isPilotingShip()) {
            for (int k = 0; k < ships.size; k++) {
                Ship ship = ships.get(k);

                if (Vector2.dst(player.getX(), player.getY(), ship.getX(), ship.getY()) < 75) {
                    player.setBoarding(true, ship);
                    f = false;
                    break;
                }
            }

            if (f) player.setBoarding(false, null);
        }
        if (cameraShake > 0) {
            if (cameraShake > 5)
                cameraShake = 5;
            cameraShake -= dt * 1.5f;
        } else {
            cameraShake = 0;
        }

        if (raceEndDelay > 1.3f) {
            raceEnd.update(dt);
        }
    }

    public float getBoxCount() {
        return boxes.size;
    }

    public void addBox(Box box) {
        boxes.add(box);
    }

    public boolean isPlayerAlive() {
        return player.isAlive();
    }

    public float getPlayerVisitedPlanetsNo() {
        return player.getVisitedPlanetsNo();
    }

    public float getPlayerToastCount() {
        return player.getToastCount();
    }

    public void tickPhysics(float dt) {
        for (int i = 0; i < masses.size; i++) {
            masses.get(i).update(dt, planets);
        }

        for (int i = 0; i < bullets.size; i++) {
            if (bullets.get(i) != null) {
                bullets.get(i).updatePhysics(dt);
            }
        }

        player.update(dt, playerEnabled, planets, lockedRotation);
    }

    public void setPlayerEnabled(boolean playerEnabled) {
        this.playerEnabled = playerEnabled;
    }

    public boolean isPlayerOrbiting() {
        return player.getPerigee() > planets.get(0).getRadius();
    }

    public void render() {
        universeShapeRenderer.setAutoShapeType(true);
        universeShapeRenderer.begin();
        for (Planet planet : planets) {
            planet.debugRender(universeShapeRenderer);
        }
        universeShapeRenderer.end();

        universeBatch.begin();

        if (!gogglesActive)
            stars.draw(universeBatch);

        for (int i = 0; i < effects.size; i++) {
            effects.get(i).render(universeBatch);
        }

        for (int i = 0; i < masses.size; i++) {
            masses.get(i).render(universeBatch);
        }

        player.draw(universeBatch);

        for (int i = 0; i < collectibles.size; i++) {
            collectibles.get(i).draw(universeBatch);
        }

        universeBatch.end();
        universeShapeRenderer.begin(gogglesActive ? ShapeType.Line : ShapeType.Filled);
        Gdx.gl20.glLineWidth(gogglesActive ? 2 : 1);
        for (int i = 0; i < bullets.size; i++) {
            bullets.get(i).draw(universeShapeRenderer);
        }

        if (gogglesActive) {
            for (int i = 0; i < masses.size; i++)
                masses.get(i).debugRender(universeShapeRenderer);

            player.debugDraw(universeShapeRenderer);
        }

        for (int i = 0; i < planets.size; i++) {
            planets.get(i).draw(universeShapeRenderer, gogglesActive);
        }
        universeShapeRenderer.end();

        hudBatch.begin();

        if (gogglesActive) {
            for (Trackable trackable : trackables) {
                Vector3 playerScreenPos = camera.project(new Vector3(trackable.getPosition(), 0));
                String label = trackable.getGizmoLabel();
                float w = new GlyphLayout(computerFont, label).width;
                computerFont.setColor(trackable.getGizmoColor());
                computerFont.draw(hudBatch, label, playerScreenPos.x - w / 2, playerScreenPos.y - 10);
            }
        }

        if (!goalChecker.isVictory()) {
            hud.draw(hudBatch);
        }
        raceEnd.draw(hudBatch);

        hudBatch.setColor(1f, 1f, 1f, injuryAlpha);
        hudBatch.draw(Art.get("pixel_white"), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudBatch.setColor(Color.WHITE);

        hudBatch.end();
    }

    public void finalizeState() {
        for (int i = 0; i < effects.size; i++) {
            if (!effects.get(i).isAlive()) {
                effects.get(i).dispose();
                effects.removeValue(effects.get(i), true);
                break;
            }
        }

        for (int i = 0; i < masses.size; i++) {
            if (!masses.get(i).isAlive()) {
                if (masses.get(i) instanceof Ship)
                    ships.removeValue((Ship) masses.get(i), true);
                if (masses.get(i) instanceof Trackable)
                    trackables.removeValue((Trackable) masses.get(i), true);

                masses.removeValue(masses.get(i), true);
                break;
            }
        }

        for (int i = 0; i < bullets.size; i++) {
            if (!bullets.get(i).alive()) {
                bullets.removeValue(bullets.get(i), true);
                break;
            }
        }
    }

    public void requestMass(Mass what) {
        masses.add(what);

        if (what instanceof Trackable) {
            if (!trackables.contains((Trackable) what, true)) {
                trackables.add((Trackable) what);
            }
        }
    }

    public void addShip(Ship ship) {
        ships.add(ship);
        collideables.add(ship);
    }

    public void addEffect(Effect effect) {
        effects.add(effect);

        if (effect instanceof Explosion) {
            if (cameraShake < 5) {
                for (int i = 0; i < masses.size; i++) {
                    Mass mass = masses.get(i);
                    float distance = Vector2.dst(mass.getX(), mass.getY(), effect.getX(), effect.getY());
                    if (distance < 300) {
                        float damage = 500.f / (0.1f * distance + 1) - distance * 0.017f;

                        if (mass.isAlive())
                            mass.doDamage(damage, DamageCause.EXPLOSION);
                    }
                }

                float distance = Vector2.dst(player.getFX(), player.getFY(), effect.getX(), effect.getY());
                if (distance < 300) {
                    float damage = 500.f / (0.1f * distance + 1) - distance * 0.017f;
                    playerHurt(player, damage, DamageCause.EXPLOSION);
                    cameraShake = 5 / (0.01f * distance + 1);
                }
            }
        }
    }

    public void playerKilled(Player player, DamageCause cause) {
        hud.setDead(cause, !suddenDeath);
        hud.displayMessage();

        if (suddenDeath && !won) {
            terminateRace(true, cause);
        }
    }

    private void terminateRace(boolean died, DamageCause cause) {
        terminated = true;
        playerEnabled = false;
        shownEnd = true;
        hud.hideAll();
        SoundManager.stopMusic("oxidiser");
        SoundManager.playMusic("failure2", false);
        raceEnd.setText("Failure!", died ? cause.getDetails() : LifeAndDeath.getRandomFailure());
        raceEnd.setShowing(true);
    }

    public boolean isPlayerPilotingShip() {
        return player.isPilotingShip();
    }

    public void requestBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    public Player getPlayer() {
        return player;
    }

    public void respawnPlayer() {
        hud.showStats();
        player.spawn(spawnX, spawnY, planets);
    }

    public void spawnPlayer(float x, float y) {
        player.spawn(x, y, planets);
        spawnX = x;
        spawnY = y;
    }

    public void playUISound(String name) {
        SoundManager.playSound(name, 3f, 0);
    }

    public void loopSound(String name, float x, float y, float volume) {
        float v = 20f / (Vector2.dst(x, y, player.getX(), player.getY()) - 5);

        float u = x - player.getX();
        float pan = (float) Math.atan(u);

        SoundManager.loopSound(name, v + volume, pan);
    }

    public void stopSound(String name) {
        SoundManager.stopSound(name);
    }

    public void playSound(String name, float x, float y, float volume) {
        float d = Vector2.dst(x, y, player.getX(), player.getY());

        if (d < 500) {
            float v = 3f / (d + 3) + volume;

            float u = x - player.getX();
            float pan = (float) Math.atan(u);

            SoundManager.playSound(name, v, pan);
        }
    }

    public void playerHurt(Player player, float amount, DamageCause cause) {
        injuryAlpha = amount / 200.f;
        player.doDamage(amount, cause);
    }

    public void createPlanet(String id, float x, float y, float radius) {
        Planet planet = new Planet(id, x, y, radius);
        planets.add(planet);
        trackables.add(planet);
    }

    public float getAge() {
        return age;
    }

    public void setCameraShake(float cameraShake) {
        this.cameraShake = cameraShake;
    }

    public void setTimeLimit(float timeLimit) {
        timeLeft = timeLimit;
        this.timeLimit = timeLimit;
        hud.displayMessage();
    }

    public void killCollectible(Collectible collectible) {
        collectibles.removeValue(collectible, true);
        trackables.removeValue(collectible, true);
    }

    public void addCollectable(Collectible collectible, int planet) {
        collectible.setEnvironment(planets.get(planet));
        collectibles.add(collectible);
        trackables.add(collectible);
    }

    @Override
    public void dispose() {
        Gdx.app.log("Universe", "Disposing of all sorts of stuff");
        for (Planet planet : planets) {
            planet.dispose();
        }
        planets.clear();

        for (Mass mass : masses) {
            mass.dispose();
        }
        masses.clear();
        bullets.clear();

        collectibles.clear();

        for (Ship ship : ships) {
            ship.dispose();
        }
        ships.clear();

        for (Effect effect : effects) {
            effect.dispose();
        }

        player.dispose();

        stars.getTexture().dispose();

        universeBatch.dispose();
        hudBatch.dispose();
        universeShapeRenderer.dispose();
    }

    public void killBox(Box box) {
        boxes.removeValue(box, true);
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    class InputManager implements InputProcessor {

        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {

            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(int amount) {
            if (!activated)
                return false;

            desiredZoom += 0.25f * amount;
            return false;
        }

    }
}
