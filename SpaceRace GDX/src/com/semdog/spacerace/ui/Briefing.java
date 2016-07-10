package com.semdog.spacerace.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Sam on 2016/07/10.
 */
public class Briefing extends Overlay {

    private String title, briefing;
    private Button go;

    public Briefing(String title, String briefing) {
        super();
        this.title = title;
        this.briefing = briefing;
        
        // wew
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {

    }
}
