package com.semdog.spacerace.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.misc.FontManager;

/**
 * A UI element which displays a set of options, the chosen of which can be selected by navigating forward and
 * back through the options.
 *
 * @author Sam
 */

public class CyclableText {
    private BitmapFont font;
    private float height;

    private int currentIndex;
    private float x, y;

    private boolean wrappable = true;

    private Object[] options;
    private Button forward, back;
    private Event onChangeEvent;

    public CyclableText(float x, float y, Object... options) {
        this.x = x;
        this.y = y;
        this.options = options;

        font = FontManager.getFont("fipps-20");
        height = font.getLineHeight();
        float width = new GlyphLayout(font, "Bug Report [br]").width;
        back = new Button("<", false, x + height / 2, y + height / 2, height, height, this::back);
        forward = new Button(">", false, x + height / 2 + width, y + height / 2, height, height, this::forward);
        back.setColors(Color.BLACK, Colors.P_PINK);
        forward.setColors(Color.BLACK, Colors.P_PINK);
        currentIndex = 0;

        back.setBeeps(false);
        forward.setBeeps(false);
    }

    public void update(float dt) {
        back.update(dt);
        forward.update(dt);
    }

    public void draw(SpriteBatch batch) {
        back.draw(batch);
        font.setColor(Colors.P_ORANGE);
        font.draw(batch, options[currentIndex] instanceof Boolean ? booleanToEnglish((boolean) options[currentIndex]) : options[currentIndex].toString(), x + height, y + height - font.getLineHeight() / 4);
        forward.draw(batch);
    }

    public Object getValue() {
        return options[currentIndex];
    }

    public void setValue(Object value) {
        for (int q = 0; q < options.length; q++)
            if (options[q].equals(value))
                currentIndex = q;
    }

    public void setOnChangeEvent(Event onChangeEvent) {
        this.onChangeEvent = onChangeEvent;
    }

    public void setWrappable(boolean wrappable) {
        this.wrappable = wrappable;
    }

    private void back() {
        currentIndex--;

        if (currentIndex == -1)
            currentIndex = wrappable ? options.length - 1 : 0;

        if (onChangeEvent != null)
            onChangeEvent.execute();
    }

    private void forward() {
        currentIndex++;

        if (currentIndex == options.length)
            currentIndex = wrappable ? 0 : options.length - 1;

        if (onChangeEvent != null)
            onChangeEvent.execute();
    }

    /**
     * Makes boolean values more readable.
     *
     * @param b The boolean
     * @return yes if true, no if not
     */
    private String booleanToEnglish(boolean b) {
        return b ? "Yes" : "No";
    }
}
