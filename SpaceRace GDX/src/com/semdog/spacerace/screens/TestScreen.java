package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.graphics.Colors;

/**
 * Test scren please ignroe
 */

public class TestScreen extends RaceScreen {

    float ax = 150;
    float bx = 250;
    float by = 250;
    private ShapeRenderer renderer;
    private float ay = 0;

    public TestScreen(RaceGame game) {
        super(game);

        renderer = new ShapeRenderer();
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyPressed(Keys.W)) {
            ay += dt * 100;
        } else if (Gdx.input.isKeyPressed(Keys.S)) {
            ay -= dt * 100;
        }
    }

    @Override
    public void render() {
        Gdx.gl20.glLineWidth(2);
        renderer.begin(ShapeType.Line);
        renderer.setColor(Colors.P_PINK);
        //renderer.curve(100, 500, 100, 500 + ay, Gdx.graphics.getWidth() - 100, 500 + ay, Gdx.graphics.getWidth() - 100, 500, 500);
        //renderer.curve(100, 500, 100, 500 - ay, Gdx.graphics.getWidth() - 100, 500 - ay, Gdx.graphics.getWidth() - 100, 500, 50);
        renderer.polyline(createArc(45, ay, 200, 200, 100, Gdx.input.getX() - 100, Gdx.graphics.getHeight() - Gdx.input.getY()));
        renderer.end();
    }

    private float[] createArc(float StartAngle, float SweepAngle, int PointsInArc, int ellipseWidth, int ellipseHeight, int xOffset, int yOffset) {
        if (PointsInArc < 0)
            PointsInArc = 0;

        if (PointsInArc > 360)
            PointsInArc = 360;

        float[] points = new float[PointsInArc * 2];
        int xo;
        int yo;
        float degs;
        double rads;

        //could have WidthRadius and HeightRadius be parameters, but easier
        // for maintenance to have the diameters sent in instead, matching closer
        // to DrawEllipse and similar methods
        double radiusW = (double) ellipseWidth / 2.0;
        double radiusH = (double) ellipseHeight / 2.0;

        for (int p = 0; p < PointsInArc * 2; p += 2) {
            degs = StartAngle + ((SweepAngle / PointsInArc) * p);

            rads = (degs * (Math.PI / 180));

            xo = (int) Math.round(radiusW * Math.sin(rads));
            yo = (int) Math.round(radiusH * Math.cos(rads));

            xo += (int) Math.round(radiusW) + xOffset;
            yo = (int) Math.round(radiusH) - yo + yOffset;

            points[p] = xo;
            points[p + 1] = yo;
        }

        return points;
    }

    @Override
    public void exit() {
        // TODO Auto-generated method stub

    }

}
