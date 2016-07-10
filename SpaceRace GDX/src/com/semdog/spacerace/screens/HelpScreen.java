package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Array;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.ui.*;

/**
 * Help help I'm being repressed
 */

public class HelpScreen extends RaceScreen implements ListViewListener {

    private SpriteBatch spriteBatch;
    private HelpSection helpSection;
    private ListView itemChooser;

    private TitleCard titleCard;
    private BitmapFont subtitleFont;

    private Button backButton, bugButton;

    private Array<Texture> diagrams;

    public HelpScreen(RaceGame game) {
        super(game);

        titleCard = new TitleCard(TitleCard.SMALL, 5, Gdx.graphics.getHeight() - 5);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("assets/fonts/Fipps-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        subtitleFont = generator.generateFont(parameter);
        generator.dispose();

        setTitle("Select a Help Topic!");

        spriteBatch = new SpriteBatch();
        helpSection = new HelpSection(350, Gdx.graphics.getHeight() * 0.1f, (Gdx.graphics.getWidth() - 100 - 250 - 100), Gdx.graphics.getHeight() * 0.7f);
        helpSection.setHelpID(0);
        itemChooser = new ListView(100, Gdx.graphics.getHeight() * 0.1f, 250, Gdx.graphics.getHeight() * 0.7f, Colors.UI_BLUE,
                Colors.UI_TEAL, 6);
        itemChooser.setTitles(helpSection.getTitles());
        itemChooser.setListener(this);

        backButton = new Button("Got it!", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.05f, 140, 50, () -> {
            game.changeScreen("menu");
        });
        backButton.setColors(Colors.UI_YELLOW, Colors.UI_BLUE);

        bugButton = new Button("Report a Bug", false, Gdx.graphics.getWidth() * 0.85f, Gdx.graphics.getHeight() * 0.05f, 200, 50, () -> {
            game.changeScreen("bug");
        });
        bugButton.setColors(Colors.UI_RED, Colors.UI_WHITE);
    }

    @Override
    public void update(float dt) {
        itemChooser.update(dt);
        helpSection.update(dt);
        backButton.update(dt);
        bugButton.update(dt);
    }

    @Override
    public void render() {
        spriteBatch.begin();
        helpSection.draw(spriteBatch);
        itemChooser.draw(spriteBatch);
        drawTitle(spriteBatch);
        subtitleFont.setColor(Colors.P_GRAY);
        subtitleFont.draw(spriteBatch, "Help", 100, Gdx.graphics.getHeight() - 90);
        titleCard.draw(spriteBatch);
        backButton.draw(spriteBatch);
        bugButton.draw(spriteBatch);
        spriteBatch.end();
    }

    @Override
    public void itemSelected(int index) {
        helpSection.setHelpID(index);
    }
}
