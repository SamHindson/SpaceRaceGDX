package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.ui.*;

/**
 * Created by Sam on 2016/07/08.
 */

public class BugReportScreen extends RaceScreen {

    private static final String[] SCREENS = {
            "Main Menu", "Singleplayer Menu", "Ingame", "Settings", "Key Bindings", "Help", "Bug Report"
    };
    private SpriteBatch batch;
    private TitleCard titleCard;
    private BitmapFont subtitleFont;
    private Button abandonButton, sendButton;
    private BitmapFont mainFont;
    private TextInput input;
    private CyclableText screen, rating;

    public BugReportScreen(RaceGame raceGame) {
        super(raceGame);

        titleCard = new TitleCard(TitleCard.SMALL, 5, Gdx.graphics.getHeight() - 5);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("assets/fonts/Fipps-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        subtitleFont = generator.generateFont(parameter);
        generator.dispose();

        FreeTypeFontGenerator generator2 = new FreeTypeFontGenerator(
                Gdx.files.internal("assets/fonts/OldSansBlack.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter2 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter2.size = 24;
        mainFont = generator2.generateFont(parameter2);
        mainFont.setColor(Colors.UI_WHITE);
        generator2.dispose();

        setTitle("Let's Squash Some Bugs!");

        batch = new SpriteBatch();

        abandonButton = new Button("Abandon", false, Gdx.graphics.getWidth() / 2 - 80, Gdx.graphics.getHeight() * 0.05f, 140, 50, () -> {
            game.changeScreen("menu");
        });
        abandonButton.setColors(Colors.UI_RED, Colors.UI_WHITE);

        sendButton = new Button("Send", false, Gdx.graphics.getWidth() / 2 + 80, Gdx.graphics.getHeight() * 0.05f, 140, 50, this::sendBug);
        sendButton.setColors(Colors.UI_PURPLE, Colors.UI_WHITE);

        input = new TextInput(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() * 0.15f, Gdx.graphics.getWidth() / 3f, 250, 101);
        Gdx.input.setInputProcessor(input);

        Object[] ratings = new Object[10];

        for (int r = 0; r < 10; r++) {
            ratings[r] = r + 1;
        }

        screen = new CyclableText(Gdx.graphics.getWidth() * 0.5f + 40, Gdx.graphics.getHeight() * 0.675f, SCREENS);
        rating = new CyclableText(Gdx.graphics.getWidth() * 0.5f + 40, Gdx.graphics.getHeight() * 0.625f, ratings);
    }

    private void sendBug() {
        String url = "https://docs.google.com/forms/d/1v4Tnlhe0LSgkCJq9x312P4goUZUOb5e5tVyQrgHL-PU/formResponse";
        String content = "entry.1035838169=" +
                ((String) screen.getValue()).replace(" ", "+") + "&entry.1476923378=" + rating.getValue() +
                "&entry.1007846955=" + input.getText().replace(" ", "+");

        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(url);
        request.setContent(content);
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int cod = httpResponse.getStatus().getStatusCode();

                if (cod == 200) {
                    Notification.show("Your bug has been reported!", "Nice!", Colors.UI_GREEN, () -> {
                        Notification.showing = false;
                        game.changeScreen("menu");
                    });
                } else {

                    Notification.show("There was an error sending your bug report! Try report it through the Bug Report Scre... Oh wait", "Retry", "Abandon", Colors.UI_BLUE, Colors.UI_RED, () -> {
                        Notification.showing = false;
                        sendBug();
                    }, () -> {
                        Notification.showing = false;
                        game.changeScreen("menu");
                    });
                }
            }

            @Override
            public void failed(Throwable t) {
                System.err.println("EEk");
            }

            @Override
            public void cancelled() {
                System.out.println("aww");
            }
        });
    }

    @Override
    public void update(float dt) {
        sendButton.update(dt);
        abandonButton.update(dt);

        screen.update(dt);
        rating.update(dt);
    }

    @Override
    public void render() {
        batch.begin();
        sendButton.draw(batch);
        abandonButton.draw(batch);

        subtitleFont.setColor(Colors.P_PINK);
        subtitleFont.draw(batch, "Bugs!", 100, Gdx.graphics.getHeight() - 90);

        drawTitle(batch);
        titleCard.draw(batch);

        input.draw(batch);

        mainFont.draw(batch, "Which screen was it on?", 0, Gdx.graphics.getHeight() * 0.7f, Gdx.graphics.getWidth() / 2, 2, false);
        mainFont.draw(batch, "On a scale of 1 to 10, how bad was it?", 0, Gdx.graphics.getHeight() * 0.65f, Gdx.graphics.getWidth() / 2, 2, false);
        mainFont.draw(batch, "Describe it here, in 100 characters.", 0, Gdx.graphics.getHeight() * 0.55f, Gdx.graphics.getWidth(), 1, false);

        rating.draw(batch);
        screen.draw(batch);

        batch.end();
    }
}
