package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.misc.Squar;
import com.semdog.spacerace.universe.Universe;

/**
 * Created by sam on 2016/05/01.
 *
 * A scren
 */

public class TestScren extends RaceScreen {

    private Universe uni;

    public TestScren(RaceGame game) {
        super(game);
        System.out.println("Universe,,,");
        uni = new Universe();

        for (float k = 0; k < 20.f; k++) {
            float a = MathUtils.PI2 * (k / 20.f);
            float x = 500 * MathUtils.cos(a);
            float y = 500 * MathUtils.sin(a);
            float s = MathUtils.random(1, 100);
            new Squar(x, y, -x / 10.f, -y / 10.f, s * 2, null);
        }
    }

    @Override
    public void update(float dt) {
        uni.tick(dt);
        uni.tickPhysics(dt);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
                | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
        Gdx.gl20.glClearColor(0, 0, 0, 1f);
        uni.render();
    }

    @Override
    public void dispose() {

    }
}
