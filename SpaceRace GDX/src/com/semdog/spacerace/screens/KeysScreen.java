package com.semdog.spacerace.screens;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.io.SettingsManager;
import com.semdog.spacerace.misc.FontManager;
import com.semdog.spacerace.ui.Button;
import com.semdog.spacerace.ui.TitleCard;

/**
 * Created by Sam on 2016/07/05.
 * <p>
 * This screen is where the user will be able to change their preferred key bindings.
 */
public class KeysScreen extends RaceScreen implements InputProcessor {
    private SpriteBatch batch;

    private TitleCard titleCard;
    private BitmapFont subtitleFont, categoryFont;
    private Button doneButton;

    private String[] titles, keys;
    private int[] keyCodes;
    private Button[] keyChangers;

    private boolean listening;
    private int listeningIndex;

    public KeysScreen(RaceGame game) {
        super(game);

        titleCard = new TitleCard(TitleCard.SMALL, 5, Gdx.graphics.getHeight() - 5);
        subtitleFont = FontManager.getFont("fipps-18");
        categoryFont = FontManager.getFont("fipps-20");

        setTitle("Click on a key to change it.");

        titleFont.setColor(Colors.UI_BLUE);

        batch = new SpriteBatch();

        doneButton = new Button("Done", false, Gdx.graphics.getWidth() / 2, 50, 140, 50, () -> {
            saveSettings();
            game.changeScreen("settings");
        });
        doneButton.setColors(Colors.P_BLUE, Colors.UI_WHITE);

        titles = new String[SettingsManager.getKeys().entrySet().size()];
        keys = new String[titles.length];
        keyCodes = new int[titles.length];
        keyChangers = new Button[keys.length];

        int x = 0;
        for (Map.Entry<String, Integer> entry : SettingsManager.getKeys().entrySet()) {
            final int i = x;

            titles[x] = entry.getKey().charAt(0) + entry.getKey().substring(1).toLowerCase();
            keys[x] = Input.Keys.toString(entry.getValue());
            keyChangers[x] = new Button(keys[x], false, Gdx.graphics.getWidth() * 0.5f + 65, Gdx.graphics.getHeight() * 0.7f + categoryFont.getCapHeight() - 35 * x - 15, 100, 40, () -> {
                listen(i);
            });
            keyChangers[x].setColors(Color.CLEAR, Colors.P_PINK);

            keyCodes[x] = entry.getValue();
            x++;
        }

        Gdx.input.setInputProcessor(this);
    }

    private void listen(int i) {
        listening = true;
        listeningIndex = i;
    }

    private void saveSettings() {
        SettingsManager.setKeys(titles, keyCodes);
    }

    @Override
    public void update(float dt) {
        doneButton.update(dt);

        if (listening) {
            keyChangers[listeningIndex].setColors(Color.CLEAR, Colors.getRandom());
        }

        for (Button button : keyChangers) {
            button.update(dt);
        }
    }

    @Override
    public void render() {
        batch.begin();
        titleCard.draw(batch);
        subtitleFont.setColor(Colors.P_PURPLE);
        subtitleFont.draw(batch, "Keys", 100, Gdx.graphics.getHeight() - 90);
        doneButton.draw(batch);

        for (int x = 0; x < titles.length; x++) {
            categoryFont.setColor(Colors.UI_WHITE);
            categoryFont.draw(batch, titles[x], 0, Gdx.graphics.getHeight() * 0.7f + categoryFont.getCapHeight() - 35 * x, Gdx.graphics.getWidth() * 0.5f - 25, 2, false);
        }

        for (Button button : keyChangers) {
            button.draw(batch);
        }

        drawTitle(batch);

        batch.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (listening) {
            keyCodes[listeningIndex] = keycode;
            keys[listeningIndex] = Input.Keys.toString(keycode);
            keyChangers[listeningIndex].setText(keys[listeningIndex]);
            listening = false;
        }
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
        return false;
    }
}
