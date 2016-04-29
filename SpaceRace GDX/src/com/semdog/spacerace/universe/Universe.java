package com.semdog.spacerace.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
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
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.effects.Effect;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.vehicles.CrapLander;
import com.semdog.spacerace.vehicles.Ship;
import com.semdog.spacerace.vehicles.SmallBombarder;
import com.semdog.spacerace.weapons.Bullet;

public class Universe {
	public static final float GRAVITY = 50f;
	public static Universe currentUniverse;
	
	private float age = 0;

	private Array<Planet> planets;
	private Array<Mass> masses;

	private Array<Effect> effects;
	private Array<Bullet> bullets;

	private Player player;

	@SuppressWarnings("unused")
	private Ship testShip, testTarget;

	private SpriteBatch universeBatch;
	private ShapeRenderer universeShapeRenderer;

	private OrthographicCamera camera;
	private float cameraRot, desiredRot;

	private Sprite stars;

	public Universe() {
		Mass.initiate(this);

		currentUniverse = this;

		planets = new Array<>();
		masses = new Array<>();

		effects = new Array<>();

		bullets = new Array<>();

		planets.add(new Planet(0, 0, 500));

		player = new Player(0, 600, planets.get(0));
		testShip = new SmallBombarder(0, 600, planets.get(0));
		player.setShip(testShip);

		testTarget = new CrapLander(-600, 0, planets.get(0));

		universeBatch = new SpriteBatch();
		universeShapeRenderer = new ShapeRenderer();

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0, 0, 0);
		camera.zoom = 0.5f;

		Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth() * 20, Gdx.graphics.getHeight() * 20, Format.RGBA4444);
		pixmap.setColor(Color.BLACK);
		pixmap.fill();

		for (int k = 0; k < 100000; k++) {
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
	}

	public void tick(float dt) {
		age += dt;
		
		//Gdx.app.log("Universe", (int)age + "s");
		
		if (Gdx.input.getInputProcessor().scrolled(1)) {
			camera.zoom += 0.2f;
		} else if (Gdx.input.getInputProcessor().scrolled(-1)) {
			camera.zoom -= 0.2f;
		}
		
		for(int i = 0; i < bullets.size; i++) {
			bullets.get(i).checkCollisions(planets);
		}
		
		for (int i = 0; i < masses.size; i++) {
			masses.get(i).checkState();
		}

		for (Effect effect : effects) {
			effect.update(dt);
		}

		desiredRot = player.getAngle() % MathUtils.PI2;
		float da = (desiredRot - cameraRot) / 10.f;
		cameraRot += da;
		//camera.rotate(-da * MathUtils.radiansToDegrees);

		camera.position.set(player.getFX(), player.getFY(), 0);
		camera.update();

		universeBatch.setProjectionMatrix(camera.combined);
		universeShapeRenderer.setProjectionMatrix(camera.combined);
		stars.setOriginCenter();
		stars.setPosition(player.getFX() - Gdx.graphics.getWidth() * 10,
				player.getFY() - Gdx.graphics.getHeight() * 10);
	}

	public void tickPhysics(float dt) {
		player.update(dt, camera);

		for (int i = 0; i < masses.size; i++) {
			masses.get(i).update(dt, planets);
			masses.get(i).checkCollisions(player);
		}
		
		for (int i = 0; i < masses.size; i++) {
			masses.get(i).checkCollisions(masses);
		}

		for (Bullet bullet : bullets) {
			bullet.updatePhysics(dt, planets);
		}

		// Check collisions between bullets and masses
		for (Mass mass : masses) {
			for (Bullet bullet : bullets) {
				if (mass.getBounds().contains(bullet.getX(), bullet.getY())) {
					mass.doDamage(bullet.getDamage());
					bullet.die();
				}
			}
		}
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

		//crapDude.debugRender(universeShapeRenderer);

		player.debugDraw(universeShapeRenderer);

		/*for (int i = 0; i < masses.size; i++) {
			masses.get(i).debugRender(universeShapeRenderer);
		}*/
		universeShapeRenderer.end();
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

	public void addEffect(Effect effect) {
		effects.add(effect);
	}

	public void playerKilled(Player player, String cause) {
		player.die("elephants");
	}

	public void addBullet(Bullet bullet) {
		bullets.add(bullet);
	}

	class InputManager implements InputProcessor {

		@Override
		public boolean keyDown(int keycode) {
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean keyTyped(char character) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean scrolled(int amount) {
			camera.zoom += amount * 0.2f;
			return false;
		}

	}
}
