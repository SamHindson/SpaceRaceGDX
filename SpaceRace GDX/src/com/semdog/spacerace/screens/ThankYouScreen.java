package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.ui.Button;
import com.semdog.spacerace.ui.TitleCard;

/**
 * Created by Sam on 2016/07/05.
 * <p>
 * A little screen (that can be accessed by finding the secret button on the Main Menu) where I (the developer) can
 * thank players for playing SpaceRace.
 */
public class ThankYouScreen extends RaceScreen {

    private SpriteBatch batch;

    private TitleCard titleCard;

    private Button neatoButton;

    private BitmapFont font;

    private String thankYou = "Developed by Sam Hindson\nCopyright Flaming Trousers Studios 2016\n\nThank you for playing! :)";

    private Texture texture;

    public ThankYouScreen(RaceGame game) {
        super(game);

        FreeTypeFontGenerator generator1 = new FreeTypeFontGenerator(
                Gdx.files.internal("assets/fonts/Consolas.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 26;
        font = generator1.generateFont(parameter);
        generator1.dispose();

        batch = new SpriteBatch();

        titleCard = new TitleCard(TitleCard.BIG, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.85f);

        neatoButton = new Button("Neato!", false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.1f, 150, 75, () -> {
            game.changeScreen("menu");
        });
        neatoButton.setColors(Colors.P_BLUE, Colors.UI_WHITE);

        texture = new Texture(Gdx.files.internal("assets/thanks.png"));
    }

    @Override
    public void update(float dt) {
        neatoButton.update(dt);
    }

    @Override
    public void render() {
        batch.begin();
        titleCard.draw(batch);
        neatoButton.draw(batch);
        font.draw(batch, thankYou, Gdx.graphics.getWidth() * 0.3f, Gdx.graphics.getHeight() * 0.6f, Gdx.graphics.getWidth() * 0.4f, 1, true);
        batch.draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }
}
