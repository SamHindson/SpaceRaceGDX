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
 * A small class set to hold animation frames of an animation found in the Help
 * Section.
 *
 * Not exceptionally efficient. TODO: Phase out in later releases
 *
 * @author Sam
 */

public class HelpAnimation implements Disposable {
    /**
     * The frame textures
     */
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

    /** Updates the animation */
    public void update(float dt) {
        frameTime += dt;

        //  If the frame has been show for more than 0.15s, go to the next.
        if (frameTime >= 0.15f) {
            frameTime = 0;
            frame++;

            //  If the previous frame was the last one, wrap back around.
            if (frame == frames.length) {
                frame = 0;
            }
        }
    }

    /** Draws the frame that is to be displayed at the moment */
    public void draw(SpriteBatch batch, float x, float y, float width, float height) {
        batch.setColor(Colors.P_BLUE);
        batch.draw(Art.get("pixel_white"), x, y, width, height);
        batch.setColor(Color.WHITE);
        batch.draw(frames[frame], x + 5, y + 5, width - 10, height - 10);
    }

    /** For once we can actually dispose of the textures, because they are not  pointers to Textures in the Art HashMap. */
    public void dispose() {
        for (Texture texture : frames) {
            texture.dispose();
        }
    }
}
