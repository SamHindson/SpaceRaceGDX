package com.semdog.spacerace.universe;

import java.util.HashMap;

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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.collectables.Collectable;
import com.semdog.spacerace.graphics.effects.Effect;
import com.semdog.spacerace.graphics.effects.Explosion;
import com.semdog.spacerace.players.DamageCause;
import com.semdog.spacerace.players.HUD;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.ui.Overlay;
import com.semdog.spacerace.ui.RaceEndScreen;
import com.semdog.spacerace.vehicles.Ship;
import com.semdog.spacerace.weapons.Bullet;

public class Universe {
	public static final float GRAVITY = 30f;
	public static Universe currentUniverse;

	private float age;
	private float timeLeft;
	private float countdown = 10;
	private boolean countingDown = true;
	private boolean playerEnabled;

	private Array<Planet> planets;
	private Array<Mass> masses;
	private Array<Ship> ships;
	private Array<Effect> effects;
	private Array<Bullet> bullets;
	private Array<Collectable> collectables;
	private Array<Collideable> collideables;

	private GoalChecker goalChecker;
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

	@SuppressWarnings("unused")
	private float zoom;
	private float desiredZoom, deltaZoom;
	private float cameraShake = 0;

	private boolean isLoading = true;
	
	@SuppressWarnings("unused")
	private boolean suddenDeath;

	private Overlay raceEnd;

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
		pixmap.setColor(Color.DARK_GRAY);
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

		goalChecker = new GoalChecker();

		raceEnd = new RaceEndScreen();
		playerEnabled = false;
		
		collectables = new Array<>();
		
		collideables = new Array<>();
		collideables.add(player);
	}

	public GoalChecker getGoalChecker() {
		return goalChecker;
	}

	public boolean isLoading() {
		return isLoading;
	}
	
	private Mass getMass(int i) {
		try {
			return masses.get(i);
		} catch(Exception e) {
			Gdx.app.error("Universe", "Mass is not good!");
			return null;
		}
	}

	public void tick(float dt) {
		age += dt;
		
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
			getMass(i).checkCollisions(masses);

			if (!player.isPilotingShip())
				getMass(i).checkPlayerCollision(player);
		}

		for (int i = 0; i < masses.size; i++) {
			Mass mass = getMass(i);
			for (int j = 0; j < bullets.size; j++) {
				Bullet bullet = bullets.get(j);
				if (mass.getBounds().contains(bullet.getX(), bullet.getY())) {
					mass.doDamage(bullet.getDamage(), DamageCause.BULLET);
					bullet.die();
				}
			}
		}

		for (int i = 0; i < masses.size; i++) {
			getMass(i).checkState();
		}
		
		for(int i = 0; i < collectables.size; i++) {
			collectables.get(i).update(collideables, dt);
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
		
		followingPlayer = true;

		if (followingPlayer) {
			desiredCX = player.getFX();
			desiredCY = player.getFY();
		} else {
			desiredCX = getMass(0).getX();
			desiredCY = getMass(0).getY();
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
			if (cameraShake > 5)
				cameraShake = 5;
			cameraShake -= dt * 1.5f;
		} else {
			cameraShake = 0;
		}

		goalChecker.update(player);

		if (goalChecker.isVictory()) {
			playerEnabled = false;
			raceEnd.setText("Victory!", "Race completed in ample time.");
			raceEnd.setShowing(true);
			hud.hideAll();
		}

		raceEnd.update(dt);
		
		if(countingDown) {
			countdown -= dt * 5;
			hud.setText("Get ready!", "[" + (int)countdown + "]");
			
			if(countdown <= 0) {
				countingDown = false;
				playerEnabled = true;
				hud.hideMessage();
				hud.showStats();
				hud.showTimer();
			}
		} else {
			timeLeft -= dt;
			hud.setCountdownValue((int)timeLeft);
			
			if(timeLeft <= 0) {
				playerEnabled = false;
				raceEnd.setText("Failure!", "You're not a clever smart boy.");
				raceEnd.setShowing(true);
			}
		}
	}

	public void tickPhysics(float dt) {
		for (int i = 0; i < masses.size; i++) {
			masses.get(i).update(dt, planets);
		}

		for (Bullet bullet : bullets) {
			bullet.updatePhysics(dt, planets);
		}

		player.update(dt, camera, playerEnabled, planets);
	}

	public void setPlayerEnabled(boolean playerEnabled) {
		this.playerEnabled = playerEnabled;
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
		
		for(int i = 0; i < collectables.size; i++) {
			collectables.get(i).draw(universeBatch);
		}

		universeBatch.end();
		universeShapeRenderer.setAutoShapeType(true);
		universeShapeRenderer.begin();
		for (int i = 0; i < bullets.size; i++) {
			bullets.get(i).draw(universeShapeRenderer);
		}
		for (int i = 0; i < planets.size; i++) {
			planets.get(i).draw(universeShapeRenderer);
		}
		for (int i = 0; i < masses.size; i++) {
			getMass(i).debugRender(universeShapeRenderer);
		}
		planets.get(0).draw(universeShapeRenderer);
		player.debugDraw(universeShapeRenderer);
		universeShapeRenderer.end();

		hudBatch.begin();
		if (!goalChecker.isVictory()) {
			hud.draw(hudBatch);
		}
		raceEnd.draw(hudBatch);
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

			for (int i = 0; i < masses.size; i++) {
				Mass mass = masses.get(i);
				float distance = Vector2.dst(mass.getX(), mass.getY(), ((Explosion) effect).getX(),
						((Explosion) effect).getY());
				if (distance < 300) {
					float damage = 500.f / (0.1f * distance + 1) - distance * 0.017f;
					mass.doDamage(damage, DamageCause.EXPLOSION);
				}
			}

			float distance = Vector2.dst(player.getFX(), player.getFY(), ((Explosion) effect).getX(),
					((Explosion) effect).getY());
			if (distance < 300) {
				float damage = 500.f / (0.1f * distance + 1) - distance * 0.017f;
				player.doDamage(damage, DamageCause.EXPLOSION);
				cameraShake = 5 / (0.01f * distance + 1);
			}
		}
	}

	public void playerKilled(Player player, DamageCause cause) {
		player.die();
		hud.setDead(cause, true);
		hud.displayMessage();
	}

	public void addBullet(Bullet bullet) {
		bullets.add(bullet);
	}

	public Player getPlayer() {
		return player;
	}

	public void respawnPlayer() {
		hud.showStats();
		switch (player.getTeam()) {
		case PINK:
			player.spawn(0, 550, planets);
		case BLUE:
			break;
		default:
			break;
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

	public void playerHurt(Player player, float amount, DamageCause cause) {
		player.doDamage(amount, cause);
	}

	public void createPlanet(String id, float x, float y, float radius) {
		planets.add(new Planet(id, x, y, radius));
		
		for(Collectable collectable : collectables) {
			collectable.reposition(planets);
		}
	}
	
	public float getAge() {
		return age;
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
			load("healthget.wav");

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

	public void setCameraShake(float cameraShake) {
		this.cameraShake = cameraShake;
	}

	public void setTimeLimit(float timeLimit) {
		timeLeft = timeLimit;
		hud.displayMessage();
	}

	public void setSuddenDeath(boolean suddenDeath) {
		this.suddenDeath = suddenDeath;
	}

	public void killCollectible(Collectable collectable) {
		collectables.removeValue(collectable, true);
	}

	public void addCollectable(Collectable collectable) {
		collectable.reposition(planets);
		collectables.add(collectable);
	}
}
