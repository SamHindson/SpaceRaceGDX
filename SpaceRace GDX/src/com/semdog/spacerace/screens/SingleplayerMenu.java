package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.misc.FontManager;
import com.semdog.spacerace.races.Race;
import com.semdog.spacerace.races.RaceManager;
import com.semdog.spacerace.ui.*;

/**
 * A screen where players can choose a singleplayer race to play.
 * Contains a custom ListView and a specially formatted information display area.
 */

public class SingleplayerMenu extends RaceScreen implements ListViewListener {

    private SpriteBatch batch;
    private TitleCard titleCard;
    private BitmapFont subtitleFont;
    private ListView raceChooser;
    private RaceInfoViewer raceViewer;
    private Button abandonButton;

    public SingleplayerMenu(RaceGame game) {
        super(game);

        titleCard = new TitleCard(TitleCard.SMALL, 5, Gdx.graphics.getHeight() - 5);
        subtitleFont = FontManager.getFont("fipps-18");

        setTitle("Select a Race!");

        batch = new SpriteBatch();

        raceChooser = new ListView(100, Gdx.graphics.getHeight() * 0.1f, 250, Gdx.graphics.getHeight() * 0.7f, new Color(1.f, 0, 110f / 255f, 1),
                new Color(178f / 255f, 0, 1, 1), 8);
        raceChooser.setTitles(RaceManager.getRaceTitles(), RaceManager.getCompleted());
        raceChooser.setListener(this);

        raceViewer = new RaceInfoViewer(this, 350, Gdx.graphics.getHeight() * 0.1f, (Gdx.graphics.getWidth() - 100 - 250 - 100), Gdx.graphics.getHeight() * 0.7f);

        abandonButton = new Button("Abandon", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.05f, 140, 50, () -> game.changeScreen("menu"));
        abandonButton.setColors(Colors.UI_RED, Colors.UI_WHITE);

        if (RaceManager.getCurrentRace() != null) {
            raceViewer.setRace(RaceManager.getCurrentRace());
            raceChooser.setPosition(RaceManager.getCurrentRace().getName());
        }
    }

    @Override
    public void update(float dt) {
        raceChooser.update(dt);
        raceViewer.update(dt);
        abandonButton.update(dt);
    }

    @Override
    public void render() {
        batch.begin();

        titleCard.draw(batch);
        subtitleFont.setColor(Colors.P_BLUE);
        subtitleFont.draw(batch, "Singleplayer", 100, Gdx.graphics.getHeight() - 90);
        raceChooser.draw(batch);
        raceViewer.draw(batch);
        abandonButton.draw(batch);
        drawTitle(batch);

        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
    }

    @Override
    public void itemSelected(int index) {
        raceViewer.setRace(RaceManager.getRace(index));
    }

    //	Called when a race is selected
    public void loadRace(Race race) {
        RaceManager.setCurrentRace(race);
        game.changeScreen("play");
    }

    @Override
    public void exit() {
        game.changeScreen("menu");
    }
}
