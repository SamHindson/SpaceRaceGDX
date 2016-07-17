package com.semdog.spacerace.net;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.semdog.spacerace.net.entities.BulletRequest;
import com.semdog.spacerace.net.entities.VirtualPlanet;
import com.semdog.spacerace.net.entities.VirtualPlayer;
import com.semdog.spacerace.net.entities.VirtualUniverse;
import com.semdog.spacerace.net.scoring.ScoreSheet;
import com.semdog.spacerace.players.Team;

import java.io.IOException;
import java.util.HashMap;

public class SpaceRaceServer extends Listener implements Runnable {

    private VirtualUniverse universe;
    private Server server;

    private SpaceRaceServer() throws IOException {
        universe = new VirtualUniverse();
        universe.addPlanet("Mors", 0, 0, 200);
        universe.addPlanet("Dagobah", 1200, 0, 200);
        universe.addPlanet("Xen", 1200, 1200, 200);
        universe.addPlanet("Hillbrow", 0, 1200, 200);
        universe.addPlanet("Toilet", 600, 600, 165);

        server = new Server();
        server.bind(13377, 24488);

        Kryo kryo = server.getKryo();
        kryo.register(VirtualUniverse.class);
        kryo.register(VirtualPlayer.class);
        kryo.register(VirtualPlanet.class);
        kryo.register(VirtualPlayer[].class);
        kryo.register(VirtualPlanet[].class);
        kryo.register(Integer.class);
        kryo.register(UniverseState.class);
        kryo.register(NewPlayer.class);
        kryo.register(Vector2.class);
        kryo.register(Integer.class);
        kryo.register(PlayerPosition.class);
        kryo.register(String.class);
        kryo.register(int[].class);
        kryo.register(int.class);
        kryo.register(float.class);
        kryo.register(float[].class);
        kryo.register(PlayerState.class);
        kryo.register(BulletRequest.class);
        kryo.register(PlayerDisconnect.class);
        kryo.register(Team.class);
        kryo.register(ScoreSheet.class);
        kryo.register(HashMap.class);

        server.addListener(this);
        server.start();

        Thread physicsThread = new Thread(this);
        physicsThread.start();
    }

    public static void main(String[] args) {
        try {
            new SpaceRaceServer();
        } catch (IOException e) {
            System.err.println("Could not start server!");
            e.printStackTrace();
        }
    }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof NewPlayer) {
            NewPlayer newboy = (NewPlayer) object;
            connection.sendTCP(universe.getState());
            server.sendToAllExceptTCP(connection.getID(), newboy);
            universe.addPlayer(newboy.getId(), newboy.getPlayer());
        } else if (object instanceof PlayerState) {
            PlayerState state = (PlayerState) object;
            if (state.getCategory() == PlayerState.SETPOS) {
                universe.setPlayerPosition(state.id, state.getInformation()[0], state.getInformation()[1]);
                server.sendToAllExceptTCP(connection.getID(), object);
            } else if (state.getCategory() == PlayerState.LIFE) {
                universe.setLifeInfo(state.id, state.getInformation()[0]);
                server.sendToAllExceptTCP(connection.getID(), object);
            } else if (state.getCategory() == PlayerState.BULLETKILL) {
                universe.killPoint(connection.getID(), (int) state.getInformation()[0]);
                ScoreSheet scoreSheet = new ScoreSheet();
                scoreSheet.players = universe.getPlayers();
                server.sendToAllTCP(scoreSheet);
            }
        } else if (object instanceof BulletRequest) {
            server.sendToAllTCP(object);
        } else if (object instanceof MassRequest) {
            // TODO work out multiplayer physics
            //universe.addMass();
        }
    }

    @Override
    public void disconnected(Connection connection) {
        server.sendToAllExceptTCP(connection.getID(), new PlayerDisconnect(connection.getID()));
        universe.removePlayer(connection.getID());
    }

    @Override
    public void run() {
        while (true) {
            universe.update(0.016f);

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
