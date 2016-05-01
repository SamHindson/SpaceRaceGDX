package com.semdog.spacerace;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.semdog.spacerace.screens.MenuScreen;
import com.semdog.spacerace.screens.PlayScreen;
import com.semdog.spacerace.screens.RaceScreen;
import com.semdog.spacerace.screens.TestScren;

public class RaceGame extends ApplicationAdapter {
	
	private RaceScreen screen;
	
	@Override
	public void create () {
		screen = new TestScren(this);
	}

	@Override
	public void render () {
		screen.update(Gdx.graphics.getDeltaTime());
		screen.render();
	}

	public void changeScreen(String name) {
		if(name.equals("play")) {
			screen.dispose();
			screen = new PlayScreen(this);
		} else if(name.equals("menu")) {
			screen.dispose();
			screen = new MenuScreen(this);
		}
	}
}
