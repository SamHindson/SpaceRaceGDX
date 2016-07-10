package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.io.SettingsManager;
import com.semdog.spacerace.ui.Button;
import com.semdog.spacerace.ui.CyclableText;
import com.semdog.spacerace.ui.Notification;
import com.semdog.spacerace.ui.TitleCard;

public class SettingsScreen extends RaceScreen {

    private BitmapFont subtitleFont, categoryFont;
    private SpriteBatch batch;
    private TitleCard titleCard;

    private CyclableText fullscreen, resolution, master, sfx, music;
    private Button keysButton, doneButton;

    public SettingsScreen(RaceGame game) {
        super(game);
        batch = new SpriteBatch();

        titleCard = new TitleCard(TitleCard.SMALL, 5, Gdx.graphics.getHeight() - 5);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("assets/fonts/Fipps-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        subtitleFont = generator.generateFont(parameter);

        parameter.size = 20;
        categoryFont = generator.generateFont(parameter);
        categoryFont.setColor(Colors.UI_WHITE);

        generator.dispose();

        Object[] volumes = new Object[11];

        for (int r = 10; r >= 0; r--) {
            volumes[r] = r * 10;
        }

        fullscreen = new CyclableText(Gdx.graphics.getWidth() / 2 + 25, Gdx.graphics.getHeight() * 0.7f, true, false);
        resolution = new CyclableText(Gdx.graphics.getWidth() / 2 + 25, Gdx.graphics.getHeight() * 0.7f - 40, "1280x720", "1600x900", "1920x1080");
        master = new CyclableText(Gdx.graphics.getWidth() / 2 + 25, Gdx.graphics.getHeight() * 0.7f - 80, volumes);
        sfx = new CyclableText(Gdx.graphics.getWidth() / 2 + 25, Gdx.graphics.getHeight() * 0.7f - 120, volumes);
        music = new CyclableText(Gdx.graphics.getWidth() / 2 + 25, Gdx.graphics.getHeight() * 0.7f - 160, volumes);

        fullscreen.setValue(SettingsManager.isFullscreen());
        resolution.setValue(SettingsManager.getResolution());
        master.setValue((int) SettingsManager.getMaster());
        sfx.setValue((int) SettingsManager.getSfx());
        music.setValue((int) SettingsManager.getMusic());

        keysButton = new Button("[...]", false, Gdx.graphics.getWidth() / 2 + 50, Gdx.graphics.getHeight() * 0.7f - 290, 50, 20, () -> {
            game.changeScreen("keys");
        });
        keysButton.setColors(Color.BLACK, Colors.P_PINK);

        doneButton = new Button("Done", false, Gdx.graphics.getWidth() / 2, 50, 140, 50, () -> {
            saveSettings();
            Notification.resetValues();
            game.changeScreen("menu");
        });
        doneButton.setColors(Colors.P_BLUE, Colors.UI_WHITE);
    }

    private void saveSettings() {
        SettingsManager.setFullscreen((boolean) fullscreen.getValue());
        SettingsManager.setMaster((int) master.getValue() * 1.f);
        SettingsManager.setSfx((int) sfx.getValue() * 1.f);
        SettingsManager.setMusic((int) music.getValue() * 1.f);
        SettingsManager.setResolution((String) resolution.getValue());

        Gdx.graphics.setDisplayMode(SettingsManager.getWidth(), SettingsManager.getHeight(), SettingsManager.isFullscreen());

        SettingsManager.writeSettings();
    }

    @Override
    public void update(float dt) {
        fullscreen.update(dt);
        resolution.update(dt);
        master.update(dt);
        sfx.update(dt);
        music.update(dt);

        keysButton.update(dt);
        doneButton.update(dt);
    }

    @Override
    public void render() {
        batch.begin();
        titleCard.draw(batch);
        subtitleFont.setColor(Colors.P_PINK);
        subtitleFont.draw(batch, "Settings", 100, Gdx.graphics.getHeight() - 90);

        categoryFont.draw(batch, "Fullscreen", 0, Gdx.graphics.getHeight() * 0.7f + categoryFont.getCapHeight(), Gdx.graphics.getWidth() * 0.5f - 25, 2, false);
        categoryFont.draw(batch, "Resolution", 0, Gdx.graphics.getHeight() * 0.7f + categoryFont.getCapHeight() - 40, Gdx.graphics.getWidth() * 0.5f - 25, 2, false);
        categoryFont.draw(batch, "Master Volume", 0, Gdx.graphics.getHeight() * 0.7f + categoryFont.getCapHeight() - 80, Gdx.graphics.getWidth() * 0.5f - 25, 2, false);
        categoryFont.draw(batch, "SFX Volume", 0, Gdx.graphics.getHeight() * 0.7f + categoryFont.getCapHeight() - 120, Gdx.graphics.getWidth() * 0.5f - 25, 2, false);
        categoryFont.draw(batch, "Music Volume", 0, Gdx.graphics.getHeight() * 0.7f + categoryFont.getCapHeight() - 160, Gdx.graphics.getWidth() * 0.5f - 25, 2, false);
        categoryFont.draw(batch, "Key Bindings", 0, Gdx.graphics.getHeight() * 0.7f + categoryFont.getCapHeight() - 300, Gdx.graphics.getWidth() * 0.5f - 25, 2, false);

        fullscreen.draw(batch);
        resolution.draw(batch);
        master.draw(batch);
        sfx.draw(batch);
        music.draw(batch);

        keysButton.draw(batch);
        doneButton.draw(batch);
        batch.end();
    }

    @Override
    public void dispose() {

    }

}
