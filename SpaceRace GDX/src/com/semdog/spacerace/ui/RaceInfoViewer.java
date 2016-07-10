package com.semdog.spacerace.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.misc.FontManager;
import com.semdog.spacerace.races.Race;
import com.semdog.spacerace.screens.SingleplayerMenu;

public class RaceInfoViewer implements Disposable {
    private Race race;

    private String title, info;
    private float timeLimit, bestTime;

    private BitmapFont titleFont, descriptionFont;
    private Color borderColor;
    private Button launchButton;

    private float x, y, width, height;

    private boolean showing = false;

    public RaceInfoViewer(SingleplayerMenu container, float x, float y, float width, float height) {
        this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

        titleFont = FontManager.getFont("fipps-36");
        descriptionFont = FontManager.getFont("inconsolata-28");

        borderColor = Colors.P_BLUE;

        launchButton = new Button("Launch", false, x + width - 80, y + 35, 140, 50, () -> container.loadRace(race));
        launchButton.setColors(Colors.UI_GREEN, Colors.UI_WHITE);
    }
	
	public void draw(SpriteBatch batch) {
		batch.setColor(borderColor);
        batch.draw(Art.get("pixel_white"), x, y, width, height);
        batch.setColor(Color.BLACK);
        batch.draw(Art.get("pixel_white"), x + 5, y + 5, width - 10, height - 10);

        if (showing) {
            titleFont.draw(batch, title, x + 20, y + height - 20);

            descriptionFont.setColor(Color.WHITE);
            descriptionFont.draw(batch, info, x + 20, y + height - titleFont.getCapHeight() - 40, width - 40, 9, true);

            descriptionFont.setColor(Colors.P_PINK);
            descriptionFont.draw(batch, "Time Limit:", x + 20, y + 110);
            descriptionFont.setColor(Colors.P_GREEN);
            descriptionFont.draw(batch, String.format("%.1f", timeLimit) + "s", x + 20, y + 85);

            descriptionFont.setColor(Colors.P_RED);
            descriptionFont.draw(batch, "Best Time:", x + 20, y + 60);
            descriptionFont.setColor(Colors.P_BLUE);
            descriptionFont.draw(batch, String.format("%.1f", bestTime) + "s", x + 20, y + 35);

            launchButton.draw(batch);
        }
    }

    public void update(float dt) {
        launchButton.update(dt);
    }

    public void setRace(Race race) {
        this.race = race;
        setInfo(race.getName(), race.getDescription(), race.getTimeLimit(), race.getBestTime());
    }

    private void setInfo(String title, String info, float timeLimit, float bestTime) {
        showing = true;
        this.title = title;
        this.info = info;
        this.timeLimit = timeLimit;
        this.bestTime = bestTime;
    }

    @Override
    public void dispose() {
    	
    }
}
