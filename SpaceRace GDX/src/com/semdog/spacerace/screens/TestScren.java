package com.semdog.spacerace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.misc.Squar;
import com.semdog.spacerace.universe.Universe;

/**
 * Created by sam on 2016/05/01.
 */
public class TestScren extends RaceScreen {

    Universe uni;

    public TestScren(RaceGame game) {
        super(game);
        uni = new Universe();
        uni.addMass(new Squar(-50, 0, 10, 0, 100, null));
        uni.addMass(new Squar(50, 0, -20, 0, 100, null));
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
