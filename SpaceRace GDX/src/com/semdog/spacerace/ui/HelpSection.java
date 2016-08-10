package com.semdog.spacerace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.misc.Exhibit;
import com.semdog.spacerace.misc.FontManager;
import com.semdog.spacerace.misc.HelpAnimation;
import com.semdog.spacerace.misc.HelpLoader;

/**
 * A UI element which shows a help topic in the Help Screen.
 * Renders text, images, exhibits and animations.
 *
 * @author Sam
 */

public class HelpSection implements Disposable {
    //	TODO do away with this needing to be static
    private static Array<HelpAnimation> animations;
    private static Array<Texture> images;
    private BitmapFont titleFont, descriptionFont;
    private Color borderColor;
    private float x, y, width, height;
    private int helpID = 5;
    private HelpLoader loader;
    private Array<Exhibit> exhibits;

    public HelpSection(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        titleFont = FontManager.getFont("fipps-36");
        descriptionFont = FontManager.getFont(Gdx.graphics.getWidth() > 1280 ? "inconsolata-24" : "inconsolata-18");
        descriptionFont.setColor(Colors.UI_WHITE);

        exhibits = new Array<>();
        exhibits.add(new Exhibit(width - 40, descriptionFont.getLineHeight() * 5, "rubbish", "needle", "runt"));
        exhibits.add(new Exhibit(width - 40, descriptionFont.getLineHeight() * 5, "carbine", "smg", "shotgun", "rocketlauncher"));
        exhibits.add(new Exhibit(width - 40, descriptionFont.getLineHeight() * 5, "toast", "health", "ammo", "fuel"));

        borderColor = Colors.P_YELLOW;

        loader = new HelpLoader();
    }

    public static void initialize() {
        animations = new Array<>();

        for (int w = 0; w < 4; w++) {
            animations.add(new HelpAnimation(w));
        }

        images = new Array<>();
        images.add(new Texture(Gdx.files.internal("assets/help/images/img0.jpg")));
        images.add(new Texture(Gdx.files.internal("assets/help/images/img1.jpg")));
    }

    public void update(float dt) {
        for (HelpAnimation animation : animations) {
            animation.update(dt);
        }
    }

    public void draw(SpriteBatch batch) {
        batch.setColor(borderColor);
        batch.draw(Art.get("pixel_white"), x, y, width, height);
        batch.setColor(Color.BLACK);
        batch.draw(Art.get("pixel_white"), x + 5, y + 5, width - 10, height - 10);

        titleFont.draw(batch, loader.getItem(helpID).getTitle(), x + 20, y + height - 20);

        GlyphLayout glyphLayout = new GlyphLayout();
        float lineHeight = descriptionFont.getLineHeight();
        float xx = x + 20;
        float yy = y + height - titleFont.getLineHeight();

        //	Parses each word from the Help file and decides what to do with it.
        for (String word : loader.getItem(helpID).getSplit()) {
            switch (word) {
                case "*": // If it's an asterisk, we add a new line.
                    xx = x + 20;
                    yy -= lineHeight;
                    continue;
                case "[blue]": // If it's a color in square brackets, make the font that color
                    descriptionFont.setColor(Colors.UI_BLUE);
                    continue;
                case "[red]":
                    descriptionFont.setColor(Colors.UI_RED);
                    continue;
                case "[white]":
                    descriptionFont.setColor(Colors.UI_WHITE);
                    continue;
                case "[green]":
                    descriptionFont.setColor(Colors.UI_GREEN);
                    continue;
                case "[yellow]":
                    descriptionFont.setColor(Colors.UI_YELLOW);
                    continue;
            }

            glyphLayout.setText(descriptionFont, word + "?");

            if (word.contains("[anim")) {
                //	If it's an animation, fetch the referenced animation
                int id = Integer.parseInt(word.substring(5, 6));
                float h = (width - 40) * 350f / 1280f;
                animations.get(id).draw(batch, x + 20, yy - h, (width - 40), h);
                yy -= h - 15;
                continue;
            } else if (word.contains("[exh")) {
                //	If it's an exhibit, fetch the referenced exhibit
                int id = Integer.parseInt(word.substring(4, 5));
                float h = descriptionFont.getLineHeight() * 6;
                exhibits.get(id).draw(batch, x + 20, yy - h);
                yy -= h;
                continue;
            } else if (word.contains("[img")) {
                //	If it's an image, fetch the referenced image
                int id = Integer.parseInt(word.substring(4, 5));
                float h = descriptionFont.getLineHeight() * 10;
                float w = h * 128f / 50f;
                batch.setColor(Color.WHITE);
                batch.draw(images.get(id), x + width / 2 - w / 2, yy - h, w, h);
                yy -= h;
                continue;
            }

            descriptionFont.draw(batch, word, xx, yy);    // Draw the word

            if (xx + glyphLayout.width + 5 - x > width - 150) {
                // If we need to, add another line
                xx = x + 20;
                yy -= lineHeight;
            } else {
                xx += glyphLayout.width;
            }
        }
    }

    public String[] getTitles() {
        return loader.getTitles();
    }

    public void setHelpID(int helpID) {
        this.helpID = helpID;
    }

    @Override
    public void dispose() {
        for (HelpAnimation animation : animations) {
            animation.dispose();
        }
    }

    public boolean[] getCompleted() {
        return new boolean[100];
    }
}
