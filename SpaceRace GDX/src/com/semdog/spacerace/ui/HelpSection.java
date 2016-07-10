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
 * TODO write stuff
 */

public class HelpSection implements Disposable {
    private String title, info;
    private BitmapFont titleFont, descriptionFont;
    private Color borderColor;

    private float x, y, width, height;
    private int helpID = 5;

    private HelpLoader loader;

    private Array<HelpAnimation> animations;
    private Array<Exhibit> exhibits;
    private Array<Texture> images;

    public HelpSection(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        titleFont = FontManager.getFont("fipps-36");
        descriptionFont = FontManager.getFont("inconsolata-28");
        descriptionFont.setColor(Colors.UI_WHITE);

        borderColor = Colors.P_YELLOW;

        loader = new HelpLoader();

        animations = new Array<>();

        for (int w = 0; w < 4; w++) {
            animations.add(new HelpAnimation(w));
        }

        exhibits = new Array<>();
        exhibits.add(new Exhibit(width - 40, descriptionFont.getLineHeight() * 5, "rubbish", "needle", "runt"));
        exhibits.add(new Exhibit(width - 40, descriptionFont.getLineHeight() * 5, "carbine", "smg", "shotgun", "rocketlauncher"));
        exhibits.add(new Exhibit(width - 40, descriptionFont.getLineHeight() * 5, "toast", "health", "ammo", "fuel"));

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

        for (String thing : loader.getItem(helpID).getSplit()) {
            switch (thing) {
                case "*":
                    xx = x + 20;
                    yy -= lineHeight;
                    continue;
                case "[blue]":
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

            glyphLayout.setText(descriptionFont, thing + "?");

            if (thing.contains("[anim")) {
                int id = Integer.parseInt(thing.substring(5, 6));
                float h = (width - 40) * 350f / 1280f;
                animations.get(id).draw(batch, x + 20, yy - h, (width - 40), h);
                yy -= h;
                continue;
            } else if (thing.contains("[exh")) {
                int id = Integer.parseInt(thing.substring(4, 5));
                float h = descriptionFont.getLineHeight() * 6;
                exhibits.get(id).draw(batch, x + 20, yy - h);
                yy -= h;
                continue;
            } else if (thing.contains("[img")) {
                int id = Integer.parseInt(thing.substring(4, 5));
                float h = descriptionFont.getLineHeight() * 10;
                float w = h * 128f / 50f;
                batch.setColor(Color.WHITE);
                batch.draw(images.get(id), x + width / 2 - w / 2, yy - h, w, h);
                yy -= h;
                continue;
            }

            descriptionFont.draw(batch, thing, xx, yy);
            if (xx + glyphLayout.width + 5 - x > width - 150) {
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
}
