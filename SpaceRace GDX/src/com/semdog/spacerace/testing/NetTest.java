package com.semdog.spacerace.testing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.graphics.Art;
import com.semdog.spacerace.screens.RaceScreen;

/**
 * Created by Sam on 2016/07/05.
 */
public class NetTest extends RaceScreen {
    private String name;

    private Texture texture;
    private Sprite me;
    private HashMap<String, Sprite> dudes;
    private BitmapFont font;

    private SpriteBatch spriteBatch;

    private Client client;

    public NetTest(RaceGame game) {
        super(game);
        name = "Forrest";

        texture = Art.get("needle");

        me = new Sprite(texture);
        font = new BitmapFont();

        spriteBatch = new SpriteBatch();

        client = new Client();

        client.getKryo().register(String.class);
        client.getKryo().register(ConnectionEvent.class);
        client.getKryo().register(MoveRequest.class);
        client.getKryo().register(PlayerMoved.class);
        client.getKryo().register(ConnectionAccept.class);

        client.start();
        try {
            client.connect(5000, "192.168.0.3", 54555, 54777);
        } catch (IOException e) {
            e.printStackTrace();
        }

        dudes = new HashMap<>();

        ConnectionEvent connectionEvent = new ConnectionEvent();
        connectionEvent.who = name;
        client.sendTCP(connectionEvent);

        client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof PlayerMoved) {
                    String id = ((PlayerMoved) object).who;
                    Sprite duh = dudes.get(id);
                    PlayerMoved moo = (PlayerMoved) object;
                    duh.setPosition(moo.x, moo.y);
                } else if (object instanceof String) {
                    dudes.put((String) object, new Sprite(texture));
                } else if (object instanceof ConnectionAccept) {
                    dudes.put(name, new Sprite(texture));
                }
            }
        });
    }

    @Override
    public void update(float dt) {
        boolean sending = false;
        MoveRequest request = new MoveRequest();

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            sending = true;
            request.up = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            sending = true;
            request.right = true;
        }

        if (sending) {
            request.who = name;
            client.sendTCP(request);
        }
    }

    @Override
    public void render() {
        spriteBatch.begin();
        for (Map.Entry<String, Sprite> entry : dudes.entrySet()) {
            entry.getValue().draw(spriteBatch);
            font.draw(spriteBatch, entry.getKey() + "", entry.getValue().getX(), entry.getValue().getY());
        }
        spriteBatch.end();
    }
}
