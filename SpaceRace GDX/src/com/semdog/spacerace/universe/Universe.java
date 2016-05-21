package com.semdog.spacerace.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.effects.Effect;
import com.semdog.spacerace.graphics.effects.Explosion;
import com.semdog.spacerace.players.DeathCause;
import com.semdog.spacerace.players.HUD;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.vehicles.Ship;
import com.semdog.spacerace.vehicles.SmallBombarder;
import com.semdog.spacerace.weapons.Bullet;

import java.util.HashMap;

public class Universe {
	public static final float GRAVITY = 50f;
	public static Universe currentUniverse;

	private Array<Planet> planets;
	private Array<Mass> masses;
	private Array<Ship> ships;
	private Array<Effect> effects;
	private Array<Bullet> bullets;

	private HUD hud;
	private Player player;

	private SoundManager soundManager;

	private SpriteBatch hudBatch;

	private SpriteBatch universeBatch;
	private ShapeRenderer universeShapeRenderer;

	private OrthographicCamera camera;
	private float cameraRot, desiredRot;
	
	private Sprite stars;

	private float cameraX, cameraY, desiredCX, desiredCY;
	private boolean followingPlayer = true, lockedRotation = true;

	private float zoom, desiredZoom, deltaZoom;
	private float cameraShake = 0;

	private boolean isLoading = true;

	public Universe() {
		currentUniverse = this;

		planets = new Array<>();
		masses = new Array<>();
		ships = new Array<>();

		effects = new Array<>();

		bullets = new Array<>();

		player = new Player(0, 0, null);
		
		hud = new HUD(player);

		hudBatch = new SpriteBatch();

		universeBatch = new SpriteBatch();
		universeShapeRenderer = new ShapeRenderer();

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0, 0, 0);
		camera.zoom = 0.5f;

		Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth() * 6, Gdx.graphics.getWidth() * 6, Format.RGBA4444);
		pixmap.setColor(Color.BLACK);
		pixmap.fill();

		for (int k = 0; k < 5000; k++) {
			float i = MathUtils.random();
			float x = MathUtils.random(pixmap.getWidth());
			float y = MathUtils.random(pixmap.getHeight());
			float s = MathUtils.random(2) + 1;
			pixmap.setColor(i, i, i, 1);
			pixmap.fillRectangle((int) x, (int) y, (int) s, (int) s);
		}

		stars = new Sprite(new Texture(pixmap));
		stars.setOriginCenter();

		universeBatch.setProjectionMatrix(camera.combined);

		Gdx.input.setInputProcessor(new InputManager());
		camera.zoom = 1.5f;
		camera.update();

		soundManager = new SoundManager();
		soundManager.init();

		isLoading = false;

        new SmallBombarder(200, 600);

        new UniverseLoader().load(this);
    }

	public boolean isLoading() {
		return isLoading;
	}

	public void tick(float dt) {
		if (desiredZoom >= 5) {
			desiredZoom = 5;
		} else if (desiredZoom <= 0.25f) {
			desiredZoom = 0.25f;
		}

		deltaZoom = (desiredZoom - camera.zoom) / 10.f;

		zoom += deltaZoom;
		camera.zoom += deltaZoom;

		for (int i = 0; i < bullets.size; i++) {
			bullets.get(i).checkCollisions(planets);
		}

		for (int i = 0; i < masses.size; i++) {
			masses.get(i).checkCollisions(masses);

			if (!player.isPilotingShip())
				masses.get(i).checkPlayerCollision(player);
		}

		for (int i = 0; i < masses.size; i++) {
			Mass mass = masses.get(i);
			for (int j = 0; j < bullets.size; j++) {
				Bullet bullet = bullets.get(j);
				if (mass.getBounds().contains(bullet.getX(), bullet.getY())) {
					mass.doDamage(bullet.getDamage(), DeathCause.BULLET);
					bullet.die();
				}
			}
		}

		for (int i = 0; i < masses.size; i++) {
			masses.get(i).checkState();
		}

		for (Effect effect : effects) {
			effect.update(dt);
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.C))
			lockedRotation = !lockedRotation;

		if (lockedRotation) {
			desiredRot = player.getAngle() % MathUtils.PI2;
		} else {
			desiredRot = 0;
		}

		float da = (desiredRot - cameraRot) / 10.f;
		cameraRot += da;
		camera.rotate(-da * MathUtils.radiansToDegrees);

		if (followingPlayer) {
			desiredCX = player.getFX();
			desiredCY = player.getFY();
		}

		cameraX += (desiredCX - cameraX) / 10.f;
		cameraY += (desiredCY - cameraY) / 10.f;

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

				if (Vector2.dst(player.getX(), player.getY(), ship.getX(), ship.getY()) < 50) {
					player.setBoarding(true, ship);
					f = false;
					break;
				}
			}

			if (f)
				player.setBoarding(false, null);
		}

		hud.update(dt);

		if (cameraShake > 0) {
			
			if(cameraShake > 5)
				cameraShake = 5;
			
			cameraShake -= dt * 1.5f;
		} else {
			cameraShake = 0;
		}
	}

	public void tickPhysics(float dt) {
		for (int i = 0; i < masses.size; i++) {
			masses.get(i).update(dt, planets);
			// masses.get(i).checkCollisions(player);
		}

		for (Bullet bullet : bullets) {
			bullet.updatePhysics(dt, planets);
		}

		player.update(dt, camera);
	}

	public void render() {
		universeBatch.begin();

		stars.draw(universeBatch);

		for (int i = 0; i < effects.size; i++) {
			effects.get(i).render(universeBatch);
		}

		for (int i = 0; i < masses.size; i++) {
			masses.get(i).render(universeBatch);
		}

		player.draw(universeBatch);

		universeBatch.end();

		universeShapeRenderer.begin(ShapeType.Filled);
		for (int i = 0; i < bullets.size; i++) {
			bullets.get(i).draw(universeShapeRenderer);
		}

		for (int i = 0; i < planets.size; i++) {
			planets.get(i).draw(universeShapeRenderer);
		}

		planets.get(0).draw(universeShapeRenderer);

		// crapDude.debugRender(universeShapeRenderer);

		player.debugDraw(universeShapeRenderer);

		/*
		 * for (int i = 0; i < masses.size; i++) {
		 * masses.get(i).debugRender(universeShapeRenderer); }
		 */
		universeShapeRenderer.end();

		hudBatch.begin();
		hud.draw(hudBatch);
		hudBatch.end();
	}

	public void finalizeState() {
		for (int i = 0; i < effects.size; i++) {
			if (!effects.get(i).isAlive()) {
				effects.removeValue(effects.get(i), true);
				break;
			}
		}

		for (int i = 0; i < masses.size; i++) {
			if (!masses.get(i).isAlive()) {
				if (masses.get(i) instanceof Ship)
					ships.removeValue((Ship) masses.get(i), true);

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

	public void addMass(Mass what) {
		masses.add(what);
	}

	public void addShip(Ship ship) {
		ships.add(ship);
	}

	public void addEffect(Effect effect) {
		effects.add(effect);

		if (effect instanceof Explosion && cameraShake < 5) {


            //cameraShake = 5;

            for (int i = 0; i < masses.size; i++) {
                Mass mass = masses.get(i);
                float distance = Vector2.dst(mass.getX(), mass.getY(), ((Explosion) effect).getX(),
                        ((Explosion) effect).getY());
                if (distance < 300) {
                    float damage = 500.f / (0.1f * distance + 1) - distance * 0.017f;
                    mass.doDamage(damage, DeathCause.EXPLOSION);
                }
            }

            float distance = Vector2.dst(player.getFX(), player.getFY(), ((Explosion) effect).getX(),
                    ((Explosion) effect).getY());
            if (distance < 300) {
                float damage = 500.f / (0.1f * distance + 1) - distance * 0.017f;
                player.doDamage(damage, DeathCause.EXPLOSION);
                System.out.println("Player distance:\t" + distance);
            }
        }
    }

	public void playerKilled(Player player, DeathCause cause) {
		player.die();
		hud.setDead(cause, true);
	}

	public void addBullet(Bullet bullet) {
		bullets.add(bullet);
	}

	public Player getPlayer() {
		return player;
	}

	public void respawnPlayer() {
		switch (player.getTeam()) {
		case PINK:
			player.spawn(0, 550, planets);
		}
	}
	
	public void spawnPlayer(float x, float y) {
		player.spawn(x, y, planets);
	}

	public void playUISound(String name) {
		soundManager.playSound(name, 0.3f, 0);
	}

	public void loopSound(String name, float x, float y, float volume) {
		float v = 20f / (Vector2.dst(x, y, player.getX(), player.getY()) - 5);

		float u = x - player.getX();
		float pan = (float) Math.atan(u);

		soundManager.loopSound(name, v + volume, pan);
	}

	public void stopSound(String name) {
		soundManager.stopSound(name);
	}

	public void playSound(String name, float x, float y, float volume) {
		float d = Vector2.dst(x, y, player.getX(), player.getY());

        if (d < 500) {
            float v = -0.002f * d + 1;

			float u = x - player.getX();
			float pan = (float) Math.atan(u);

			soundManager.playSound(name, v, pan);
		}
	}

	public void playerHurt(Player player, float amount, DeathCause cause) {
		player.doDamage(amount, cause);
	}

    public void createPlanet(float x, float y, float radius) {
        planets.add(new Planet(x, y, radius));
    }

	private class InputManager implements InputProcessor {

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
			desiredZoom += 0.25f * amount;
			return false;
		}

	}

	class SoundManager {
		HashMap<String, Sound> clips;
		HashMap<String, Long> looping;

		void init() {
			clips = new HashMap<>();
			load("explosion1.ogg");
			load("explosion2.ogg");
			load("explosion3.ogg");
			load("beep.wav");
			load("jump.ogg");
			load("egress.ogg");
			load("ingress.ogg");
			load("bulletground.wav");
			load("shrap1.ogg");
			load("shrap2.ogg");
			load("shrap3.ogg");
			load("runt.wav");
			load("carbine.wav");
            load("smg.wav");

			load("runtgun.wav");
			load("playerhit1.wav");
			load("playerhit2.wav");
			load("playerhit3.wav");
			load("playerhit4.wav");
			load("playerhit5.wav");

            load("neet.wav");

			looping = new HashMap<>();
		}

		void load(String name) {
			System.out.println(name);
			clips.put(name.split("[.]")[0], Gdx.audio.newSound(Gdx.files.internal("assets/audio/" + name)));
		}

		void playSound(String name, float volume, float pan) {
			if (clips.containsKey(name)) {
				clips.get(name).play(volume, 1, pan);
			} else {
				Gdx.app.error("Universe", "No spund: " + name);
			}
		}

		void loopSound(String name, float volume, float pan) {
			if (!looping.containsKey(name)) {
				if (clips.containsKey(name)) {
					looping.put(name, clips.get(name).loop(volume, 1, pan));
				} else {
					Gdx.app.error("Universe", "No! " + name);
				}
			}
		}

		void stopSound(String name) {
			if (looping.containsKey(name)) {
				clips.get(name).stop(looping.get(name));
				looping.remove(name);
			}
		}
	}
}
