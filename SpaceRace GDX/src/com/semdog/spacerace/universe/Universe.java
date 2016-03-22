package com.semdog.spacerace.universe;

import com.badlogic.gdx.Gdx;
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
import com.semdog.spacerace.graphics.effects.Explosion;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.weapons.Bullet;

public class Universe {
	public static final float GRAVITY = 50f;
	public static Universe currentUniverse;

	private Array<Planet> planets;
	private Array<Mass> masses;

	private Array<Explosion> explosions;
	private Array<Bullet> bullets;

	private Player player;

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

		explosions = new Array<>();

		bullets = new Array<>();

		planets.add(new Planet(0, 0, 500));

		player = new Player(0, 600, planets.get(0));

		universeBatch = new SpriteBatch();
		universeShapeRenderer = new ShapeRenderer();

		camera = new OrthographicCamera(800, 600);
		camera.position.set(0, 0, 0);
		camera.zoom = 0.5f;

		Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth() * 2, Gdx.graphics.getHeight() * 2, Format.RGBA4444);
		pixmap.setColor(Color.BLACK);
		pixmap.fill();

		for (int k = 0; k < 1000; k++) {
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

		camera.update();
	}

	public void tick(float dt) {
		for (Mass mass : masses) {
			mass.update(dt, planets);
			mass.checkCollisions(player);
		}

		for (Explosion explosion : explosions) {
			explosion.update(dt);
		}

		for (Bullet bullet : bullets) {
			bullet.update(dt, planets);
		}

		camera.zoom = 1.2f;

		desiredRot = player.getAngle() % MathUtils.PI2;
		float da = (desiredRot - cameraRot) / 10.f;
		cameraRot += da;
		camera.rotate(-da * MathUtils.radiansToDegrees);

		player.update(dt, camera);
		camera.position.set(player.getX(), player.getY(), 0);
		camera.update();
		
		universeBatch.setProjectionMatrix(camera.combined);
		universeShapeRenderer.setProjectionMatrix(camera.combined);
		stars.setOriginCenter();
		stars.setPosition(player.getX() - Gdx.graphics.getWidth(), player.getY() - Gdx.graphics.getHeight());
	}

	public void render() {
		universeBatch.begin();
		
		stars.draw(universeBatch);

		for (Explosion explosion : explosions) {
			explosion.draw(universeBatch);
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

		player.debugDraw(universeShapeRenderer);
		universeShapeRenderer.end();
	}

	public void finalizeState() {
		for (Explosion explosion : explosions) {
			if (!explosion.alive()) {
				explosions.removeValue(explosion, true);
				break;
			}
		}

		for (Mass mass : masses) {
			if (mass.isDead()) {
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

	public void addExplosion(float x, float y, int magnitude) {
		/*
		 * hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		 * hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		 * hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		 * hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		 * hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		 * hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		 * hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		 * hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		 * hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		 * hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		 * hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		 * hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		 * hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		 * hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		 * hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		 * hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		 * hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		 * hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		 * hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		 * hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		 */
		// TODO remove this shit someday

		explosions.add(new Explosion(x, y, magnitude));
	}

	public void playerKilled(Player player, String cause) {
		player.die("elephants");
	}

	public void addBullet(Bullet bullet) {
		bullets.add(bullet);
	}
}
