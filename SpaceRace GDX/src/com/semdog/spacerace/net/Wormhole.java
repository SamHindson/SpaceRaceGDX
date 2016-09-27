package com.semdog.spacerace.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.semdog.spacerace.RaceGame;
import com.semdog.spacerace.graphics.Colors;
import com.semdog.spacerace.net.entities.*;
import com.semdog.spacerace.net.scoring.ScoreSheet;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.players.Team;
import com.semdog.spacerace.screens.MultiplayerMenu;
import com.semdog.spacerace.ui.Notification;
import com.semdog.spacerace.universe.MultiplayerUniverse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Wormhole extends Listener {
    private Client client;
    private MultiplayerUniverse universe;
    private int clientID;

    private RaceGame game;

    public Wormhole(MultiplayerUniverse universe, RaceGame game) throws IOException {
        this.universe = universe;

        client = new Client();

        //  Here we tell the Kryo server what kind of information we'll be using it to send
        Kryo kryo = client.getKryo();
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
        kryo.register(MassSpawnRequest.class);
        kryo.register(MassSpawnRequest.class);
        kryo.register(MassKillRequest.class);
        kryo.register(MassMap.class);
        kryo.register(MassState.class);
        kryo.register(MassEvent.class);

        client.start();
        client.connect(5000, MultiplayerMenu.serverAddress, 13377, 24488);
        client.addListener(this);

        clientID = client.getID();

        this.game = game;
    }

    public int getID() {
        return clientID;
    }

    @Override
    public void disconnected(Connection connection) {
        universe.dispose();
        Notification.show("Connection to Server Lost!", "Aw snap!", Colors.P_BLUE, () -> {
            Notification.showing = false;
        });
        Gdx.app.postRunnable(() -> {
            game.changeScreen("menu");
        });
    }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof NewPlayer) {
            universe.createPuppet(((NewPlayer) object).getId(), ((NewPlayer) object).getPlayer());
        } else if (object instanceof UniverseState) {
            UniverseState state = (UniverseState) object;
            for (int r = 0; r < state.getPlanets().length; r++) {
                universe.createPlanet(state.getPlanets()[r]);
            }
            universe.sortCollectibles();
            for (int t = 0; t < state.getPlayers().length; t++) {
                universe.createPuppet(state.getPlayerIDs()[t], state.getPlayers()[t]);
            }
            universe.sortTeams();
        } else if (object instanceof PlayerState) {
            PlayerState state = (PlayerState) object;
            switch (state.getCategory()) {
                case PlayerState.SETPOS:
                    universe.setPuppetPosition(state.getId(), state.getInformation()[0], state.getInformation()[1]);
                    universe.setPlayerAnimState(state.getId(), state.getInformation()[2]);
                    universe.setEnvironmentPosition(state.getId(), state.getInformation()[3], state.getInformation()[4]);
                    break;
                case PlayerState.ENVSTATE:
                    universe.setEnvironmentPosition(state.getId(), state.getInformation()[0], state.getInformation()[1]);
                    break;
                case PlayerState.LIFE:
                    universe.setAlive(state.getId(), state.getInformation()[0]);
                    break;
            }
        } else if (object instanceof BulletRequest) {
            BulletRequest request = (BulletRequest) object;
            universe.addBullet(request.getOwnerID(), request.getX(), request.getY(), request.getDx(), request.getDy(), request.getDamage());
        } else if (object instanceof PlayerDisconnect) {
            if(connection.getID() != ((PlayerDisconnect) object).getId()) universe.removePuppet(((PlayerDisconnect) object).getId());
        } else if (object instanceof MassSpawnRequest) {
            System.out.println("New mass was approved.");
            universe.createMass(((MassSpawnRequest) object));
        } else if (object instanceof MassKillRequest) {
            universe.killMass((((MassKillRequest) object)).getId());
        } else if (object instanceof ScoreSheet) {
            System.out.println("wew");
            universe.setScores((ScoreSheet) object);
        } else if (object instanceof MassMap) {
            MassMap massMap = (MassMap) object;
            for (Map.Entry<Integer, MassState> entry : massMap.getMassStates().entrySet()) {
                universe.setMassPosition(entry.getKey(), entry.getValue().getX(), entry.getValue().getY());
            }
        } else if (object instanceof MassEvent) {
            MassEvent massEvent = (MassEvent) object;
            int type = massEvent.getEvent();
            int id = massEvent.getMassID();
            switch (type) {
                case MassEvent.PLANET_COLLIDE:
                    universe.planetCollision(id);
                    break;
            }
        }
    }

    public void registerPlayer(Player player) {
        VirtualPlayer player2 = new VirtualPlayer(player.getName(), client.getID(), player.getTeam(), player.getX(), player.getY(), player.getEnvironmentX(), player.getEnvironmentY());
        NewPlayer request = new NewPlayer(player2);
        request.setId(client.getID());
        client.sendTCP(request);
    }

    public void sendPlayerState(int category, float... params) {
        client.sendTCP(new PlayerState(clientID, category, params));
    }

    public void sendBulletRequest(int ownerID, float x, float y, float dx, float dy, int damage) {
        client.sendTCP(new BulletRequest(ownerID, x, y, dx, dy, damage));
    }

    public void requestVirtualMass(MassSpawnRequest massSpawnRequest) {
        client.sendTCP(massSpawnRequest);
    }

    public void close() {
        client.close();
        try {
            client.dispose();
        } catch (IOException e) {
            Gdx.app.error("Wormhole", "That client refuses to die!");
            e.printStackTrace();
        }
    }

    public void killMass(int id) {
        client.sendTCP(new MassKillRequest(id));
    }

    public void endConnection() {
        client.sendTCP(new PlayerDisconnect(clientID));
    }
}
