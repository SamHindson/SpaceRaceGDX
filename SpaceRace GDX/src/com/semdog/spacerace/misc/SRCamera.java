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
    private float lastRotation;

    public SRCamera(int w, int h) {
        super(w, h);
    }

    public void setRotation(float rotation) {

        if (Math.abs(rotation - currentRotation) > 345) {
            float rAmount = (rotation - currentRotation);
            currentRotation += rAmount;
            rotate(rAmount);
        } else {
            float rAmount = (rotation - currentRotation) / 10.f;
            currentRotation += rAmount;
            rotate(rAmount);
        }
    }
}
