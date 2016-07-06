package com.semdog.spacerace.misc;

import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * A custom camera class designed to help the fact that the standard LibGDX Orthographic Camera does not have a
 * 'setRotation' method. Nobody seems to know why this is the case, so instead of asking questions I'd rather just
 * do it myself.
 * <p>
 * TODO fix the flipping out bug!!!
 */

public class SRCamera extends OrthographicCamera {
    private float currentRotation;

    public SRCamera(int w, int h) {
        super(w, h);
    }

    public void setRotation(float rotation) {
        if (rotation < 0) {
            rotation += 360;
        }
        System.out.println(rotation);
        float desiredRotation = rotation;
        rotate((desiredRotation - currentRotation) / 2.f);
        currentRotation += (desiredRotation - currentRotation) / 2.f;
    }
}
