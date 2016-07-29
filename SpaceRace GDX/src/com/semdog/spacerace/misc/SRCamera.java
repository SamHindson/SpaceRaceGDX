package com.semdog.spacerace.misc;

import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * A custom camera class designed to help the fact that the standard LibGDX
 * Orthographic Camera does not have a 'setRotation' method. Nobody seems to
 * know why this is the case, so instead of asking questions I'd rather just do
 * it myself.
 */

public class SRCamera extends OrthographicCamera {
    private float currentRotation;

    public SRCamera(int width, int height) {
        super(width, height);
    }

    public void setRotation(float rotation) {
        if (Math.abs(rotation - currentRotation) > 345) {
            // If there is a jarring difference between current and desired rotations, the camera will not interpolate the rotation and will just snap to whatever is asked of it for fear of making users dizzy
            float rAmount = (rotation - currentRotation);
            currentRotation += rAmount;
            rotate(rAmount);
        } else {
            // Otherwise, we smoothly rotate to the desired rotation.
            float rAmount = (rotation - currentRotation) / 20.f;
            currentRotation += rAmount;
            rotate(rAmount);
        }
    }
}
