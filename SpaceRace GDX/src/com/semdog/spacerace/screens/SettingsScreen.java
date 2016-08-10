package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.audio.SoundManager;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.io.SettingsManager;
import com.semdog.spacerace.misc.FontManager;
import com.semdog.spacerace.ui.Button;
import com.semdog.spacerace.ui.CyclableText;
import com.semdog.spacerace.ui.Notification;
import com.semdog.spacerace.ui.TitleCard;

/**
 * A screen where users can change their game settings.
 *
 * @author Sam
 */

public class SettingsScreen extends RaceScreen {

    private BitmapFont subtitleFont, categoryFont;
    private SpriteBatch batch;
    private TitleCard titleCard;

    private CyclableText fullscreen, postprocessing, resolution, master, sfx, music;
    private Button keysButton, doneButton;

    public SettingsScreen(RaceGame game) {
        super(game);
        batch = new SpriteBatch();

        titleCard = new TitleCard(TitleCard.SMALL, 5, Gdx.graphics.getHeight() - 5);
        subtitleFont = FontManager.getFont("fipps-18");
        categoryFont = FontManager.getFont("fipps-20");
        categoryFont.setColor(Colors.UI_WHITE);

        Object[] volumes = new Object[11];

        for (int r = 10; r >= 0; r--) {
            volumes[r] = r * 10;
        }

        fullscreen = new CyclableText(Gdx.graphics.getWidth() / 2 + 25, Gdx.graphics.getHeight() * 0.7f, true, false);
        postprocessing = new CyclableText(Gdx.graphics.getWidth() / 2 + 25, Gdx.graphics.getHeight() * 0.7f - 50, true, false);
        resolution = new CyclableText(Gdx.graphics.getWidth() / 2 + 25, Gdx.graphics.getHeight() * 0.7f - 100, "1280x720", "1600x900", "1920x1080");
        master = new CyclableText(Gdx.graphics.getWidth() / 2 + 25, Gdx.graphics.getHeight() * 0.7f - 150, volumes);
        sfx = new CyclableText(Gdx.graphics.getWidth() / 2 + 25, Gdx.graphics.getHeight() * 0.7f - 200, volumes);
        music = new CyclableText(Gdx.graphics.getWidth() / 2 + 25, Gdx.graphics.getHeight() * 0.7f - 250, volumes);

        postprocessing.setOnChangeEvent(() ->
                SettingsManager.setPostprocessing((boolean) postprocessing.getValue()));

        music.setOnChangeEvent(() -> SoundManager.setMusicVolume(((Integer) (music.getValue()))));
        music.setWrappable(false);

        master.setOnChangeEvent(() -> SoundManager.setMasterVolume(((Integer) (master.getValue()))));
        master.setWrappable(false);

        sfx.setOnChangeEvent(() -> {
            SoundManager.setSfxVolume(((Integer) (sfx.getValue())));
            SoundManager.playSound("weaponget", 1, 0);
        });
        sfx.setWrappable(false);

        fullscreen.setValue(SettingsManager.isFullscreen());
        postprocessing.setValue(SettingsManager.isPostprocessing());
        resolution.setValue(SettingsManager.getResolution());
        master.setValue((int) SettingsManager.getMaster());
        sfx.setValue((int) SettingsManager.getSfx());
        music.setValue((int) SettingsManager.getMusic());

        keysButton = new Button("[...]", false, Gdx.graphics.getWidth() / 2 + 50, Gdx.graphics.getHeight() * 0.7f - 350, 50, 20, () -> {
            saveSettings();
            game.changeScreen("keys");
        });
        keysButton.setColors(Color.BLACK, Colors.P_PINK);

        doneButton = new Button("Done", false, Gdx.graphics.getWidth() / 2, 50, 140, 50, () -> {
            saveSettings();
            Notification.resetValues();
            exit();
        });
        doneButton.setColors(Colors.P_BLUE, Colors.UI_WHITE);
    }

    private void saveSettings() {
        SettingsManager.setFullscreen((boolean) fullscreen.getValue());
        SettingsManager.setPostprocessing((boolean) postprocessing.getValue());
        SettingsManager.setMaster((int) master.getValue() * 1.f);
        SettingsManager.setSfx((int) sfx.getValue() * 1.f);
        SettingsManager.setMusic((int) music.getValue() * 1.f);
        SettingsManager.setResolution((String) resolution.getValue());

        // Sets the resolution of the game window.
        Gdx.graphics.setWindowedMode(SettingsManager.getWidth(), SettingsManager.getHeight());

        game.changeResolution();

        //  If Fullscreen is enabled, make it so
        if (SettingsManager.isFullscreen())
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());

        SettingsManager.writeSettings();
    }

    @Override
    public void update(float dt) {
        fullscreen.update(dt);
        postprocessing.update(dt);
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

        categoryFont.setColor(Colors.UI_WHITE);

        categoryFont.draw(batch, "Fullscreen", 0, Gdx.graphics.getHeight() * 0.7f + categoryFont.getCapHeight() + 10, Gdx.graphics.getWidth() * 0.5f - 25, 2, false);
        categoryFont.draw(batch, "Arcadiafication", 0, Gdx.graphics.getHeight() * 0.7f + categoryFont.getCapHeight() - 50 + 10, Gdx.graphics.getWidth() * 0.5f - 25, 2, false);
        categoryFont.draw(batch, "Resolution", 0, Gdx.graphics.getHeight() * 0.7f + categoryFont.getCapHeight() - 100 + 10, Gdx.graphics.getWidth() * 0.5f - 25, 2, false);
        categoryFont.draw(batch, "Master Volume", 0, Gdx.graphics.getHeight() * 0.7f + categoryFont.getCapHeight() - 150 + 10, Gdx.graphics.getWidth() * 0.5f - 25, 2, false);
        categoryFont.draw(batch, "SFX Volume", 0, Gdx.graphics.getHeight() * 0.7f + categoryFont.getCapHeight() - 200 + 10, Gdx.graphics.getWidth() * 0.5f - 25, 2, false);
        categoryFont.draw(batch, "Music Volume", 0, Gdx.graphics.getHeight() * 0.7f + categoryFont.getCapHeight() - 250 + 10, Gdx.graphics.getWidth() * 0.5f - 25, 2, false);
        categoryFont.draw(batch, "Key Bindings", 0, Gdx.graphics.getHeight() * 0.7f + categoryFont.getCapHeight() - 350 - 10, Gdx.graphics.getWidth() * 0.5f - 25, 2, false);

        fullscreen.draw(batch);
        postprocessing.draw(batch);
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

    @Override
    public void exit() {
        game.changeScreen("menu");
    }
}