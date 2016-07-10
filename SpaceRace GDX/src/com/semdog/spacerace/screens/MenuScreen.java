package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.audio.SoundManager;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.ui.Button;
import com.semdog.spacerace.ui.Notification;
import com.semdog.spacerace.ui.TitleCard;

public class MenuScreen extends RaceScreen {
	
	//TODO fix the horrendous button colors
	
	private SpriteBatch batch;
	
	private Button button1, button2, button3, button4, button5, secretButton;
	private TitleCard titleCard;

	public MenuScreen(RaceGame game) {
		super(game);
		
		batch = new SpriteBatch();

        button1 = new Button("Singleplayer", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.55f, 200, 60, () -> game.changeScreen("playmenu"));
        button1.setColors(Colors.P_YELLOW, Colors.UI_BLUE);

        button2 = new Button("Multiplayer", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.55f - 61, 200, 60, () -> System.out.println("Dogs!"));
        button2.setColors(Colors.UI_BLUE, Colors.P_YELLOW);

        button3 = new Button("Settings", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.55f - 122, 200, 60, () -> {
            game.changeScreen("settings");
        });
        button3.setColors(Colors.UI_YELLOW, Colors.UI_WHITE);

        button4 = new Button("Help", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.55f - 183, 200, 60, () -> {
            game.changeScreen("help");
        });
        button4.setColors(Colors.UI_GRAY, Colors.UI_WHITE);

        button5 = new Button("Quit", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.55f - 244, 200, 60, () -> {
            SoundManager.stopMusic("menu");

            Notification.show("Really quit?", "Yeah...", "No!", Colors.UI_RED, Colors.UI_GREEN, () -> {
                Gdx.app.exit();
            }, () -> {
                Notification.showing = false;
            });
        });
        button5.setColors(Colors.UI_BLUE, Colors.UI_WHITE);

        secretButton = new Button("???", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.9f, 400, 300, () -> {
            game.changeScreen("thankyou");
        });
        secretButton.setColors(Color.ORANGE, Color.GREEN);
		
		titleCard = new TitleCard(1, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.85f);

        if (!SoundManager.isMusicPlaying("menu"))
            SoundManager.playMusic("menu", true);
    }

	@Override
	public void update(float dt) {
		button1.update(dt);
		button2.update(dt);
		button3.update(dt);
		button4.update(dt);
		button5.update(dt);
		secretButton.update(dt);
        button1.setColors(Colors.P_YELLOW, Colors.UI_WHITE);
        button2.setColors(Colors.P_RED, Colors.UI_WHITE);
        button3.setColors(Colors.P_PURPLE, Colors.UI_WHITE);
        button4.setColors(Colors.P_GRAY, Colors.UI_WHITE);
        button5.setColors(Colors.P_BLUE, Colors.UI_WHITE);

    }

	@Override
	public void render() {
		batch.begin();
		
		titleCard.draw(batch);
		
		button1.draw(batch);
		button2.draw(batch);
		button3.draw(batch);
		button4.draw(batch);
		button5.draw(batch);
		
		batch.end();
		
	}

	@Override
	public void dispose() {
        super.dispose();
        batch.dispose();
	}
	
}
