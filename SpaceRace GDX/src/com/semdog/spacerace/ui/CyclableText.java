package com.semdog.spacerace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.semdog.spacerace.graphics.Colors;

/**
 * Created by Sam on 2016/07/05.
 */
public class CyclableText {
    private static BitmapFont font;
    private static float height;

    static {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("assets/fonts/Fipps-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        font = generator.generateFont(parameter);
        generator.dispose();

        font.setColor(Colors.P_ORANGE);

        height = font.getCapHeight();
    }

    private Object[] options;
    private int currentIndex;
    private float x, y;
    private float tx, ty;
    private Button forward, back;

    public CyclableText(float x, float y, Object... options) {
        this.x = x;
        this.y = y;
        this.options = options;

        back = new Button("<", false, x + height / 2, y + height / 2, height, height, this::back);
        forward = new Button(">", false, x + height / 2 + 100, y + height / 2, height, height, this::forward);

        back.setColors(Color.BLACK, Colors.P_PINK);
        forward.setColors(Color.BLACK, Colors.P_PINK);

        currentIndex = 0;

        rearrange();
    }

    private void back() {
        currentIndex--;

        if (currentIndex == -1)
            currentIndex = options.length - 1;

        rearrange();
    }

    private void forward() {
        currentIndex++;

        if (currentIndex == options.length)
            currentIndex = 0;

        rearrange();
    }

    private void rearrange() {
        GlyphLayout glyphLayout = new GlyphLayout(font, options[currentIndex].toString());
        tx = x + height + 10;
        ty = y + height;
        forward.setPosition(x + height * 1.5f + glyphLayout.width + 20, y + height / 2);
    }

    public void update(float dt) {
        back.update(dt);
        forward.update(dt);
    }

    public void draw(SpriteBatch batch) {
        back.draw(batch);
        font.draw(batch, options[currentIndex] instanceof Boolean ? booleanToEnglish((boolean) options[currentIndex]) : options[currentIndex].toString(), tx, ty);
        forward.draw(batch);
    }

    private String booleanToEnglish(boolean b) {
        return b ? "Yes" : "No";
    }

    public Object getValue() {
        return options[currentIndex];
    }

    public void setValue(Object value) {
        for (int q = 0; q < options.length; q++)
            if (options[q].equals(value))
                currentIndex = q;

        rearrange();
    }
}
