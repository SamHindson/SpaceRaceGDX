package com.semdog.spacerace.screens;

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
import com.semdog.spacerace.ui.Notification;
import com.semdog.spacerace.ui.TitleCard;

/**
 * This screen is where the user will be able to change their preferred key bindings.
 *
 * @author Sam
 */

public class KeysScreen extends RaceScreen implements InputProcessor {
    private SpriteBatch batch;
    private TitleCard titleCard;
    private BitmapFont subtitleFont, categoryFont;
    private Button doneButton;
    private String[] primaries = {"Move/Rotate Left", "Move/Rotate Right", "Jump", "Toggle Sprint", "RCS_Up", "RCS_Down", "RCS_Left", "RCS_Right"};
    private String[] secondaries = {"Activate", "Throw Grenade", "Goggles", "Camera Lock", "Engines", "Pause"};
    private int[] primaryKeyCodes, secondaryKeyCodes;
    private Button[] primaryChangers, secondaryChangers;
    private boolean listening, listeningPrimary;
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
            exit();
        });
        doneButton.setColors(Colors.P_BLUE, Colors.UI_WHITE);

        // This section instiantiates a whole bunch of arrays that are used in the process of changing the key bindings.
        // They are in two separate sections to increase usability.
        primaryChangers = new Button[primaries.length];
        String[] primaryKeys = new String[primaries.length];
        primaryKeyCodes = new int[primaries.length];
        int a = 0;

        for (String name : primaries) {
            final int i = a;
            String split = name.split(" ")[name.split(" ").length - 1].toUpperCase();
            String key = split;
            int keyCode = SettingsManager.getKey(key);
            String keyName = Input.Keys.toString(SettingsManager.getKey(key));

            primaryKeys[a] = keyName;
            primaryChangers[a] = new Button(keyName, false, Gdx.graphics.getWidth() * 0.3f + 65, Gdx.graphics.getHeight() * 0.7f + categoryFont.getCapHeight() - 40 * a - 10, 100, 40, () -> {
                // When the button is pressed, the InputProcessor will await the  next keystroke with baited breath.
                // When it receives this signal, that keystroke is recorded.
                listen(i);
                listeningPrimary = true;
            });
            primaryChangers[a].setColors(Color.CLEAR, Colors.P_PINK);
            primaryChangers[a].setBorders(false);
            primaryKeyCodes[a] = keyCode;
            a++;
        }

        secondaryChangers = new Button[secondaries.length];
        String[] secondaryKeys = new String[secondaries.length];
        secondaryKeyCodes = new int[secondaries.length];
        int b = 0;

        for (String name : secondaries) {
            final int i = b;
            String key = name.split(" ")[name.split(" ").length - 1].toUpperCase();
            int keyCode = SettingsManager.getKey(key);
            String keyName = Input.Keys.toString(SettingsManager.getKey(key));

            secondaryKeys[b] = keyName;
            secondaryChangers[b] = new Button(keyName, false, Gdx.graphics.getWidth() * 0.6f + 65, Gdx.graphics.getHeight() * 0.7f + categoryFont.getCapHeight() - 40 * b - 10, 100, 40, () -> {
                listen(i);
                listeningPrimary = false;
            });
            secondaryChangers[b].setColors(Color.CLEAR, Colors.P_PINK);
            secondaryChangers[b].setBorders(false);
            secondaryKeyCodes[b] = keyCode;
            b++;
        }

        // Allows this class' InputProcessor to handle keystrokes
        Gdx.input.setInputProcessor(this);
    }

    /**
     * Begins listening for a keystroke, whose value will be recorded as 'i'
     */
    private void listen(int i) {
        listening = true;
        listeningIndex = i;
    }

    private void saveSettings() {
        SettingsManager.setKeys(primaries, primaryKeyCodes);
        SettingsManager.setKeys(secondaries, secondaryKeyCodes);
        SettingsManager.writeKeys();
    }

    @Override
    public void update(float dt) {
        doneButton.update(dt);

        if (listening) {
            if (listeningPrimary) {
                primaryChangers[listeningIndex].setColors(Color.CLEAR, Colors.getRandomPlanetColor());
            } else {
                secondaryChangers[listeningIndex].setColors(Color.CLEAR, Colors.getRandomPlanetColor());
            }
        }

        for (Button button : primaryChangers) {
            button.update(dt);
        }

        for (Button button : secondaryChangers) {
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

        for (int x = 0; x < primaries.length; x++) {
            categoryFont.setColor(Colors.UI_WHITE);
            categoryFont.draw(batch, primaries[x].replaceAll("_", " "), 0, Gdx.graphics.getHeight() * 0.7f + categoryFont.getCapHeight() - 40 * x, Gdx.graphics.getWidth() * 0.3f, 2, false);
        }

        for (int x = 0; x < secondaries.length; x++) {
            categoryFont.setColor(Colors.UI_WHITE);
            categoryFont.draw(batch, secondaries[x].replaceAll("_", " "), 0, Gdx.graphics.getHeight() * 0.7f + categoryFont.getCapHeight() - 40 * x, Gdx.graphics.getWidth() * 0.6f, 2, false);
        }

        for (Button button : primaryChangers) {
            button.draw(batch);
        }

        for (Button button : secondaryChangers) {
            button.draw(batch);
        }

        drawTitle(batch);

        batch.end();
    }

    /**
     * When a key is pressed, record that keystroke
     */
    @Override
    public boolean keyDown(int keycode) {
        if (listening) {
            if (checkConflicts(keycode)) {
                listening = false;
                if (listeningPrimary) primaryChangers[listeningIndex].setColors(Color.CLEAR, Colors.P_PINK);
                else secondaryChangers[listeningIndex].setColors(Color.CLEAR, Colors.P_PINK);
                return false;
            }
            if (listeningPrimary) {
                primaryKeyCodes[listeningIndex] = keycode;
                primaryChangers[listeningIndex].setText(Input.Keys.toString(keycode));
                primaryChangers[listeningIndex].setColors(Color.CLEAR, Colors.P_BLUE);
                listening = false;
            } else {
                secondaryKeyCodes[listeningIndex] = keycode;
                secondaryChangers[listeningIndex].setText(Input.Keys.toString(keycode));
                secondaryChangers[listeningIndex].setColors(Color.CLEAR, Colors.P_BLUE);
                listening = false;
            }
        }
        return false;
    }

    private boolean checkConflicts(int keycode) {
        if (listeningPrimary) if (keycode == primaryKeyCodes[listeningIndex]) return false;
        else if (keycode == secondaryKeyCodes[listeningIndex]) return false;

        for (int i = 0; i < primaryKeyCodes.length; i++) {
            int k = primaryKeyCodes[i];
            if (k == keycode) {
                Notification.show("Conflicting keys!\nThe key " + Input.Keys.toString(keycode) + " is already in use by: " + primaries[i], "Okie dokie", Colors.P_GRAY, () -> {
                    listening = false;
                    Notification.showing = false;
                });
                return true;
            }
        }
        for (int i = 0; i < secondaryKeyCodes.length; i++) {
            int k = secondaryKeyCodes[i];
            if (k == keycode) {
                Notification.show("Conflicting keys!\n" + Input.Keys.toString(keycode) + " is already in use by: " + secondaries[i], "Okie dokie", Colors.P_GRAY, () -> {
                    listening = false;
                    Notification.showing = false;
                });
                return true;
            }
        }
        return false;
    }

    @Override
    public void exit() {
        game.changeScreen("settings");
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