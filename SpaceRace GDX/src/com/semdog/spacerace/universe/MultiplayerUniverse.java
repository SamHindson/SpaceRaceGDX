package com.semdog.spacerace.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.graphics.effects.Effect;
import com.semdog.spacerace.graphics.effects.Explosion;
import com.semdog.spacerace.io.SettingsManager;
import com.semdog.spacerace.misc.FontManager;
import com.semdog.spacerace.misc.SRCamera;
import com.semdog.spacerace.misc.Tools;
import com.semdog.spacerace.net.MultiplayerPlayer;
import com.semdog.spacerace.net.PlayerState;
import com.semdog.spacerace.net.Wormhole;
import com.semdog.spacerace.net.entities.MassSpawnRequest;
import com.semdog.spacerace.net.entities.Puppet;
import com.semdog.spacerace.net.entities.VirtualPlanet;
import com.semdog.spacerace.net.entities.VirtualPlayer;
import com.semdog.spacerace.net.scoring.MetaPlayer;
import com.semdog.spacerace.net.scoring.ScoreSheet;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.players.HUD;
import com.semdog.spacerace.players.Team;
import com.semdog.spacerace.screens.PlayScreen;
import com.semdog.spacerace.ui.Notification;
import com.semdog.spacerace.ui.RaceEndScreen;
import com.semdog.spacerace.ui.Scores;
import com.semdog.spacerace.weapons.Bullet;
import com.semdog.spacerace.weapons.Carbine;
import com.semdog.spacerace.weapons.SMG;
import com.semdog.spacerace.weapons.Shotgun;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * An extended universe that communicates with a server for updates on positions
 * of masses, bullets and other players for use in Multiplayer.
 *
 * @author Sam
 */

public class MultiplayerUniverse extends Universe {

    private Wormhole wormhole;

    private HashMap<Integer, Puppet> puppets;
    private HashMap<Integer, Mass> masses;

    private float networkTime;

    private HashMap<Integer, MetaPlayer> pinks, blues;
    private Scores scores;
    private boolean scoresShowing = false;

    public MultiplayerUniverse(PlayScreen _container, String playerName) {
        super();

        container = _container;

        currentUniverse = this;

        trackables = new Array<>();
        planets = new Array<>();
        puppets = new HashMap<>();
        collideables = new Array<>();
        bullets = new Array<>();
        effects = new Array<>();
        masses = new HashMap<>();

        pinks = new HashMap<>();
        blues = new HashMap<>();
        scores = new Scores();

        hudBatch = new SpriteBatch();

        camera = new SRCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 0, 0);
        camera.zoom = 0.5f;

        universeBatch = new SpriteBatch();
        universeBatch.setProjectionMatrix(camera.combined);
        universeShapeRenderer = new ShapeRenderer();

        Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth() * 6, Gdx.graphics.getWidth() * 6, Format.RGBA4444);
        pixmap.setColor(new Color(0, 0, 0.05f, 1f));
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
        activated = true;

        goalChecker = new GoalChecker();
        raceEnd = new RaceEndScreen();

        playerEnabled = true;

        collideables.add(player);

        exiting = false;

        computerFont = FontManager.getFont("mohave-18-italic");

        try {
            wormhole = new Wormhole(this);
        } catch (IOException ioe) {
            Notification.show("Could not connect to multiplayer service! Try again some other time.", "Aww :(", Colors.UI_BLUE, () -> Notification.showing = false);
            container.exit();
        }

        spawnX = 0;
        spawnY = 350;

        player = new MultiplayerPlayer(playerName, wormhole, spawnX, spawnY, null);
        ((MultiplayerPlayer) player).setTeam(decideTeam());
        MetaPlayer metaPlayer = new MetaPlayer();
        metaPlayer.setTeam(player.getTeam());
        metaPlayer.setName(player.getName());


        if (metaPlayer.getTeam() == Team.PINK) {
            pinks.put(wormhole.getID(), metaPlayer);
        } else {
            blues.put(wormhole.getID(), metaPlayer);
        }

        wormhole.registerPlayer(player);
        hud = new HUD(player);
        player.setHud(hud);
        if (MathUtils.randomBoolean(1 / 3f)) player.setWeapon(new SMG());
        else if (MathUtils.randomBoolean(1 / 3f)) player.setWeapon(new Shotgun());
        else player.setWeapon(new Carbine());

        trackables.add(player);

        hud.showStats();
    }

    private Team decideTeam() {
        return pinks.size() > blues.size() ? Team.BLUE : pinks.size() < blues.size() ? Team.PINK : (Team) Tools.decide(Team.PINK, Team.BLUE);
    }

    @Override
    public void tick(float dt) {
        age += dt;

        networkTime += dt;

        if (wormhole != null && player != null) {
            float networkTickTime = 1 / 30f;
            if (networkTime > networkTickTime) {
                wormhole.sendPlayerState(PlayerState.SETPOS, player.getX(), player.getY());
            }
        }

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

            if (!scoresShowing && Gdx.input.isKeyPressed(Keys.TAB)) {
                scoresShowing = true;
            } else if (scoresShowing && !Gdx.input.isKeyPressed(Keys.TAB)) {
                scoresShowing = false;
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
        }

        for (int i = 0; i < bullets.size; i++) {
            if (bullets.get(i) != null)
                bullets.get(i).checkCollisions(planets);
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

        stars.setPosition(player.getFX() - stars.getWidth() / 2, player.getFY() - stars.getHeight() / 2);

        if (cameraShake > 0) {
            if (cameraShake > 5)
                cameraShake = 5;
            cameraShake -= dt * 1.5f;
        } else {
            cameraShake = 0;
        }

        if (injuryAlpha > 0) {
            injuryAlpha *= 0.5f;

            if (injuryAlpha < 0.05f)
                injuryAlpha = 0;
        }

        for (Effect effect : effects) {
            effect.update(dt);
        }

        player.update(dt, playerEnabled, planets, lockedRotation);
        ((MultiplayerPlayer) player).checkCollisions(bullets);

        for (Map.Entry<Integer, Puppet> puppet : puppets.entrySet()) {
            if (!puppet.getValue().isLoaded())
                puppet.getValue().load();

            puppet.getValue().update(dt);
        }
    }

    @Override
    public void respawnPlayer() {
        spawnX = (int) Tools.decide(0, 1200);
        spawnY = (int) Tools.decide(350, 850);

        super.respawnPlayer();

        if (MathUtils.randomBoolean(1 / 3f)) player.setWeapon(new SMG());
        else if (MathUtils.randomBoolean(1 / 3f)) player.setWeapon(new Shotgun());
        else player.setWeapon(new Carbine());
    }

    @Override
    public void tickPhysics(float dt) {
        for (int i = 0; i < bullets.size; i++) {
            if (bullets.get(i) != null) {
                bullets.get(i).updatePhysics(dt);
            }
        }

        for (Map.Entry<Integer, Mass> entry : masses.entrySet()) {
            entry.getValue().update(dt, planets);
            if (!entry.getValue().alive) {
                wormhole.killMass(entry.getKey());
                break;
            }
        }
    }

    public void setMassPosition(int id, float x, float y) {
        if (!masses.containsKey(id)) return;
        masses.get(id).setPosition(x, y);
    }

    @Override
    public void finalizeState() {
        for (int i = 0; i < bullets.size; i++) {
            if (!bullets.get(i).alive()) {
                bullets.removeValue(bullets.get(i), true);
                break;
            }
        }

        for (int i = 0; i < effects.size; i++) {
            if (!effects.get(i).isAlive()) {
                effects.get(i).dispose();
                effects.removeValue(effects.get(i), true);
                break;
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        wormhole.close();
    }

    @Override
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

        for (Map.Entry<Integer, Mass> entry : masses.entrySet()) {
            entry.getValue().render(universeBatch);
        }

        player.draw(universeBatch);

        for (Map.Entry<Integer, Puppet> puppet : puppets.entrySet()) {
            puppet.getValue().draw(universeBatch);
        }

        for (int i = 0; i < effects.size; i++) {
            effects.get(i).render(universeBatch);
        }
        universeBatch.end();
        universeShapeRenderer.begin(gogglesActive ? ShapeType.Line : ShapeType.Filled);
        Gdx.gl20.glLineWidth(gogglesActive ? 2 : 1);

        for (int i = 0; i < bullets.size; i++) {
            bullets.get(i).draw(universeShapeRenderer);
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

        raceEnd.draw(hudBatch);

        hudBatch.setColor(1f, 1f, 1f, injuryAlpha);
        hudBatch.draw(Art.get("pixel_white"), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudBatch.setColor(Color.WHITE);

        hud.draw(hudBatch);

        if (scoresShowing) {
            scores.draw(hudBatch, pinks, blues);
        }

        hudBatch.end();
    }

    public void createPuppet(int id, VirtualPlayer player) {
        Puppet puppet = new Puppet(player);
        puppets.put(id, puppet);
        trackables.add(puppet);

        MetaPlayer metaPlayer = new MetaPlayer();
        metaPlayer.setName(player.getName());
        metaPlayer.setTeam(puppet.getTeam());

        if (metaPlayer.getTeam() == Team.PINK) pinks.put(player.getId(), metaPlayer);
        else blues.put(player.getId(), metaPlayer);
    }

    public void createPlanet(VirtualPlanet planet) {
        Planet planet2 = new Planet(planet.getID(), planet.getX(), planet.getY(), planet.getRadius());
        planets.add(planet2);
        trackables.add(planet2);
    }

    public void setPuppetPosition(int id, float x, float y) {
        if (puppets.containsKey(id)) {
            puppets.get(id).setPosition(x, y);
        }
    }

    public void setPlayerAnimState(int id, float direction) {
        if (puppets.containsKey(id)) {
            puppets.get(id).setAnimState(direction);
        }
    }

    public void setEnvironmentPosition(int id, float f, float g) {
        if (puppets.containsKey(id)) {
            puppets.get(id).setEnvironmentPosition(f, g);
        }
    }

    @Override
    public void requestBullet(Bullet bullet) {
        wormhole.sendBulletRequest(wormhole.getID(), bullet.getX(), bullet.getY(), bullet.getDx(), bullet.getDy(), bullet.getDamage());
    }

    public void addBullet(int ownerID, float x, float y, float dx, float dy, int damage) {
        super.requestBullet(new Bullet(ownerID, x, y, dx, dy, damage));
    }

    public void setAlive(int id, float f) {
        puppets.get(id).setAlive(f > 0);
    }

    @Override
    public void spawnPlayer(float x, float y) {
        super.spawnPlayer(x, y);
        wormhole.sendPlayerState(PlayerState.LIFE, 1);
    }

    public void removePuppet(int id) {
        if (puppets.get(id).getTeam() == Team.PINK) pinks.remove(id);
        else blues.remove(id);

        puppets.remove(id);
    }

    public void requestMass(MassSpawnRequest what) {
        wormhole.requestVirtualMass(what);
    }

    public void createMass(MassSpawnRequest request) {
        switch (request.getType()) {
            case MassSpawnRequest.GRENADE:
                Grenade newGrenade = new Grenade(request.getX(), request.getY(), request.getDX(), request.getDY());
                newGrenade.setControllerID(request.getId());
                masses.put(request.getId(), newGrenade);
        }
    }

    public void addEffect(Effect effect) {
        effects.add(effect);

        if (effect instanceof Explosion) {
            if (cameraShake < 5) {
                /*for (int i = 0; i < masses.size(); i++) {
                    Mass mass = masses.get(i);
                    float distance = Vector2.dst(mass.getX(), mass.getY(), effect.getX(), effect.getY());
                    if (distance < 300) {
                        float damage = 500.f / (0.1f * distance + 1) - distance * 0.017f;

                        if (mass.isAlive())
                            mass.doDamage(damage, DamageCause.EXPLOSION);
                    }
                }*/

                float distance = Vector2.dst(player.getFX(), player.getFY(), effect.getX(), effect.getY());
                if (distance < 300) {
                    float damage = 500.f / (0.1f * distance + 1) - distance * 0.017f;
                    playerHurt(player, damage, DamageCause.EXPLOSION);
                    cameraShake = 5 / (0.01f * distance + 1);
                }
            }
        }
    }

    public void setScores(ScoreSheet sheet) {
        for (Map.Entry<Integer, VirtualPlayer> entry : sheet.players.entrySet()) {
            int p = entry.getKey();
            if (pinks.containsKey(p)) pinks.get(p).setScore(entry.getValue().getScore());
            else if (blues.containsKey(p)) blues.get(p).setScore(entry.getValue().getScore());
        }
    }

    public void killMass(int id) {
        masses.remove(id);
    }
}