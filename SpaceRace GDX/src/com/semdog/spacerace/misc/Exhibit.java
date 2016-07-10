package com.semdog.spacerace.misc;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.Colors;

/**
 * Created by Sam on 2016/07/09.
 */
public class Exhibit {
    private String[] textures;
    private float width, height;
    private float padding;
    private float itemWidth;

    public Exhibit(float width, float height, String... textures) {
        this.textures = textures;
        this.width = width;
        this.height = height;

        padding = height * 0.1f;

        itemWidth = height - 2 * padding;
    }

    public void draw(SpriteBatch batch, float x, float y) {
        itemWidth = height - 4 * padding;
        float k = width / (textures.length + 1);
        /*batch.setColor(Colors.P_PINK);
        batch.draw(Art.get("pixel_white"), x, y, width, height);
        batch.setColor(new Color(0x8F9190FF));
        batch.draw(Art.get("pixel_white"), x + 5, y + 5, width - 10, height - 10);*/
        float silSize = itemWidth * 1.1f;
        float silOffs = itemWidth * 0.05f;
        for (int e = 0; e < textures.length; e++) {
            batch.setColor(Colors.UI_YELLOW);
            batch.draw(Art.get(textures[e] + "_sil"), x + k * (e + 1) - itemWidth / 2 - silOffs, y + padding * 2 - silOffs, silSize, silSize);
            batch.setColor(Color.WHITE);
            batch.draw(Art.get(textures[e]), x + k * (e + 1) - itemWidth / 2, y + padding * 2, itemWidth, itemWidth);
        }
    }
}
