package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.esotericsoftware.kryonet.Client;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.io.SettingsManager;
import com.semdog.spacerace.misc.FontManager;
import com.semdog.spacerace.ui.Button;
import com.semdog.spacerace.ui.TextInput;
import com.semdog.spacerace.ui.TitleCard;

import java.net.InetAddress;

/**
 * Created by Sam on 2016/07/17.
 */
public class MultiplayerMenu extends RaceScreen {

    public static InetAddress serverAddress;
    private Button joinButton, abandonButton;
    private boolean canJoin;
    private BitmapFont notifierFont;
    private TextInput nameInput;
    private TitleCard titleCard;
    private BitmapFont subtitleFont;
    private SpriteBatch batch;
    private String text = "Searching for a local server...";

    public MultiplayerMenu(RaceGame game) {
        super(game);

        titleCard = new TitleCard(TitleCard.SMALL, 5, Gdx.graphics.getHeight() - 5);
        subtitleFont = FontManager.getFont("fipps-18");
        notifierFont = FontManager.getFont("inconsolata-32");
        notifierFont.setColor(Colors.UI_WHITE);

        setTitle("Join a Multiplayer Game");

        batch = new SpriteBatch();

        abandonButton = new Button("Abandon", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.05f, 140, 50, () -> game.changeScreen("menu"));
        abandonButton.setColors(Colors.UI_RED, Colors.UI_WHITE);

        joinButton = new Button("Join!", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.05f + 60, 140, 50, this::joinGame);
        joinButton.setColors(Colors.UI_GREEN, Colors.UI_WHITE);

        nameInput = new TextInput(Gdx.graphics.getWidth() * 0.4f, Gdx.graphics.getHeight() * 0.4f, Gdx.graphics.getWidth() * 0.2f, 100, 17);
        Gdx.input.setInputProcessor(nameInput);

        canJoin = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Client client = new Client();
                InetAddress address = client.discoverHost(24488, 5000);

                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if (address == null) text = "Could not find any servers!";
                        else {
                            serverAddress = address;
                            text = "Found a server!\nEnter your name.";
                            canJoin = true;
                        }
                    }
                });
            }
        }).start();
    }

    private void joinGame() {
        if (nameInput.getText() == null) return;
        SettingsManager.setName(nameInput.getText());
        game.changeScreen("multiplay");
    }

    @Override
    public void update(float dt) {
        abandonButton.update(dt);

        if (canJoin) {
            joinButton.update(dt);
        }
    }

    @Override
    public void render() {
        batch.begin();
        notifierFont.draw(batch, text, 0, Gdx.graphics.getHeight() * 0.666f, Gdx.graphics.getWidth(), Align.center, false);
        abandonButton.draw(batch);

        if (canJoin) {
            nameInput.draw(batch);
            joinButton.draw(batch);
        }

        drawTitle(batch);
        titleCard.draw(batch);

        subtitleFont.setColor(Color.GREEN);
        subtitleFont.draw(batch, "Multiplayer", 100, Gdx.graphics.getHeight() - 90);

        batch.end();
    }

    @Override
    public void exit() {

    }
}
