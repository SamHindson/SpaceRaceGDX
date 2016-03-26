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
import com.semdog.spacerace.vehicles.Ship;
import com.semdog.spacerace.vehicles.SmallBombarder;
import com.semdog.spacerace.weapons.Bullet;

public class Universe {
	public static final float GRAVITY = 50f;
	public static Universe currentUniverse;

	private Array<Planet> planets;
	private Array<Mass> masses;

	private Array<Effect> effects;
	private Array<Bullet> bullets;

	private Player player;
	private Ship crapDude;

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
		crapDude = new SmallBombarder(100, 600, planets.get(0));
		player.setShip(crapDude);

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
		for (Mass mass : masses) {
			mass.update(dt, planets);
			mass.checkCollisions(player);
		}

		for (Effect effect : effects) {
			effect.update(dt);
		}

		for (Bullet bullet : bullets) {
			bullet.update(dt, planets);
		}
		
		if(Gdx.input.getInputProcessor().scrolled(1)) {
			camera.zoom += 0.2f;
		} else if(Gdx.input.getInputProcessor().scrolled(-1)) {
			camera.zoom -= 0.2f;
		} 

		desiredRot = player.getAngle() % MathUtils.PI2;
		float da = (desiredRot - cameraRot) / 10.f;
		cameraRot += da;
		camera.rotate(-da * MathUtils.radiansToDegrees);

		player.update(dt, camera);
		camera.position.set(player.getFX(), player.getFY(), 0);
		camera.update();
		
		universeBatch.setProjectionMatrix(camera.combined);
		universeShapeRenderer.setProjectionMatrix(camera.combined);
		stars.setOriginCenter();
		stars.setPosition(player.getFX() - Gdx.graphics.getWidth() * 10, player.getFY() - Gdx.graphics.getHeight() * 10);
	}

	public void render() {
		universeBatch.begin();
		
		stars.draw(universeBatch);

		for (Effect effect : effects) {
			effect.render(universeBatch);
		}

		for (Mass mass : masses) {
			mass.render(universeBatch);
		}

		player.draw(universeBatch);
		universeBatch.end();

		universeShapeRenderer.begin(ShapeType.Filled);
		for (Planet planet : planets) {
			planet.draw(universeShapeRenderer);
		}

		for (Bullet bullet : bullets) {
			bullet.draw(universeShapeRenderer);
		}
		
		crapDude.debugRender(universeShapeRenderer);

		player.debugDraw(universeShapeRenderer);
		universeShapeRenderer.end();
	}

	public void finalizeState() {
		for (Effect effect : effects) {
			if (!effect.isAlive()) {
				System.out.println("remong a dead mamn! ");
				System.out.println(effect);
				effects.removeValue(effect, true);
				break;
			}
		}

		for (Mass mass : masses) {
			if (!mass.alive()) {
				masses.removeValue(mass, true);
				break;
			}
		}

		for (Bullet bullet : bullets) {
			if (!bullet.alive()) {
				bullets.removeValue(bullet, true);
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
