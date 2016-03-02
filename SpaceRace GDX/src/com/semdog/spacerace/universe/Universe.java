package com.semdog.spacerace.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.graphics.effects.Explosion;
import com.semdog.spacerace.players.Player;

public class Universe {
	
	public static final float GRAVITY = 50f;

	private Array<Planet> planets;
	private Array<Mass> masses;
	
	private Array<Explosion> explosions;
	
	private Player player;

	private SpriteBatch universeBatch;
	private ShapeRenderer universeShapeRenderer;
	
	private OrthographicCamera camera;

	public Universe() {
		Mass.initiate(this);
		
		planets = new Array<>();
		masses = new Array<>();
		
		explosions = new Array<>();

		planets.add(new Planet(0, 0, 500));
		
		player = new Player(0, 600, planets.get(0));
		
		universeBatch = new SpriteBatch();
		universeShapeRenderer = new ShapeRenderer();
		
		camera = new OrthographicCamera(800, 600);
		camera.position.set(0, 0, 0);
		camera.zoom = 0.5f;
		
		universeBatch.setProjectionMatrix(camera.combined);
		
		camera.update();
		
		Pixmap pm = new Pixmap(16, 16, Format.RGBA8888);
		pm.setColor(Color.RED);
		pm.drawRectangle(0, 0, 16, 16);
		Gdx.input.setCursorImage(pm, 0, 0);
		pm.dispose();
	}

	public void tick(float dt) {
		for(Mass mass : masses) {
			mass.update(dt, planets);
		}
		
		for(Explosion explosion : explosions) {
			explosion.update(dt);
		}
		
		camera.zoom = 1f;

		player.update(dt, camera);
		camera.position.set(player.getX(), player.getY(), 0);
		camera.update();
		universeBatch.setProjectionMatrix(camera.combined);
		universeShapeRenderer.setProjectionMatrix(camera.combined);
	}

	public void render() {
		universeShapeRenderer.begin(ShapeType.Filled);
		for (Planet planet : planets) {
			planet.draw(universeShapeRenderer);
		}
		universeShapeRenderer.end();
		
		universeBatch.begin();
		for(Mass mass : masses) {
			mass.debugRender(universeBatch);
		}
		
		for(Explosion explosion : explosions) {
			explosion.draw(universeBatch);
		}
		
		player.draw(universeBatch);
		universeBatch.end();
	}
	
	public void addMass(Mass what) {
		masses.add(what);
	}

	public void addExplosion(float x, float y, int magnitude) {
		/*hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah
		hahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahahah*/
		// TODO remove this shit someday
		
		explosions.add(new Explosion(x, y, magnitude));
	}

	public void killMass(Grenade grenade) {
		
	}

}
