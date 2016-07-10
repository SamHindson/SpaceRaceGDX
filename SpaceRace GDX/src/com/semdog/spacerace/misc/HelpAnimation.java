package com.semdog.spacerace.misc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.graphics.Colors;

/**
 * Created by Sam on 2016/07/08.
 */

public class HelpAnimation implements Disposable {
    private Texture[] frames;
    private int frame;
    private float frameTime;

    public HelpAnimation(int id) {
        FileHandle[] files = Gdx.files.internal("assets/help/animations/" + id).list();
        frames = new Texture[files.length];

        for (int u = 0; u < frames.length; u++) {
            frames[u] = new Texture(files[u]);
        }
    }

    public void update(float dt) {
        frameTime += dt;
        if (frameTime >= 0.15f) {
            frameTime = 0;
            frame++;

            if (frame == frames.length) {
                frame = 0;
            }
        }
    }

    public void reset() {
        frame = 0;
    }

    public void draw(SpriteBatch batch, float x, float y, float width, float height) {
        //System.out.println("wow ethan");
        batch.setColor(Colors.P_BLUE);
        batch.draw(Art.get("pixel_white"), x, y, width, height);
        batch.setColor(Color.WHITE);
        batch.draw(frames[frame], x + 5, y + 5, width - 10, height - 10);
    }

    public void dispose() {
        for (Texture texture : frames) {
            texture.dispose();
        }
    }
}
