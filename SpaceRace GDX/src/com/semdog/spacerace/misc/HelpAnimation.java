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
 * A small class set to hold animation frames of an animation found in the Help Section.
 * <p>
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
        for (FileHandle fileHandle : files) {
            if (!fileHandle.extension().equals("jpg")) continue;
            int frameNo = Integer.parseInt(fileHandle.nameWithoutExtension()) - 1;
            frames[frameNo] = new Texture(fileHandle);
        }
    }

    /**
     * Updates the animation
     */
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

    /**
     * Draws the frame that is to be displayed at the moment
     */
    public void draw(SpriteBatch batch, float x, float y, float width, float height) {
        batch.setColor(Colors.P_BLUE);
        batch.draw(Art.get("pixel_white"), x, y, width, height);
        batch.setColor(Color.WHITE);

        /* If someone has added other files to the program it handles the error by redrawing the previous frame. */
        if (frames[frame] == null) batch.draw(frames[frame - 1], x + 5, y + 5, width - 10, height - 10);
        else batch.draw(frames[frame], x + 5, y + 5, width - 10, height - 10);
        /* TODO there must be a better way... */
    }

    /**
     * For once we can actually dispose of the textures, because they are not just pointers to Textures in the Art HashMap.
     */
    public void dispose() {
        for (Texture texture : frames) {
            texture.dispose();
        }
    }
}
