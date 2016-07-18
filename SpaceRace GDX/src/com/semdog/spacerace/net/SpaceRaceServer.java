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

/**
 * The server code exists here.
 */

public class SpaceRaceServer extends Listener {

    private VirtualUniverse universe;
    private Server server;

    private SpaceRaceServer() throws IOException {
        //  Create a standard universe with four planets
        universe = new VirtualUniverse();
        universe.addPlanet("Mors", 0, 0, 200);
        universe.addPlanet("Dagobah", 1200, 0, 200);
        universe.addPlanet("Xen", 1200, 1200, 200);
        universe.addPlanet("Hillbrow", 0, 1200, 200);
        universe.addPlanet("Toilet", 600, 600, 165);

        //  Creates a new Kryo server
        server = new Server();

        //  Binds to TCP Port 13377 and UDP Port 24488
        server.bind(13377, 24488);

        //  Register the classes that will be sent over the Kryo
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
    /** This is called whenever the server receives something from a client */
    public void received(Connection connection, Object object) {
        if (object instanceof NewPlayer) {
            //  A new player has joined the game.
            //  Get the player's information
            NewPlayer newboy = (NewPlayer) object;
            //  Send the Universe's current state straight back to that connection
            connection.sendTCP(universe.getState());
            //  Informs other players of new arrival
            server.sendToAllExceptTCP(connection.getID(), newboy);
            //  Adds the player to the virtual universe
            universe.addPlayer(newboy.getId(), newboy.getPlayer());
        } else if (object instanceof PlayerState) {
            //  A player has just informed us of his state.
            PlayerState state = (PlayerState) object;
            //  Decide what to do based on what category the state holds
            if (state.getCategory() == PlayerState.SETPOS) {
                //  If the player moved, inform the universe
                universe.setPlayerPosition(state.getId(), state.getInformation()[0], state.getInformation()[1]);
                //  'sendToAllExceptTCP' is used because the client sending this already knows that their player has moved
                server.sendToAllExceptTCP(connection.getID(), object);
            } else if (state.getCategory() == PlayerState.LIFE) {
                //  If the player's life state has changed, inform everybody
                universe.setLifeInfo(state.getId(), state.getInformation()[0]);
                //  'sendToAllExceptTCP' is used because the client sending this already knows that their player is alive/dead.
                server.sendToAllExceptTCP(connection.getID(), object);
            } else if (state.getCategory() == PlayerState.BULLETKILL) {
                //  Inform the universe that somebody was killed by a bullet
                universe.killPoint(connection.getID(), (int) state.getInformation()[0]);
                //  Get the updated scores of the players and send it to everyone
                ScoreSheet scoreSheet = new ScoreSheet();
                scoreSheet.players = universe.getPlayers();
                server.sendToAllTCP(scoreSheet);
            }
        } else if (object instanceof BulletRequest) {
            //  Tell all clients to spawn a bullet
            server.sendToAllTCP(object);
        }

        // TODO work out multiplayer physics
    }

    @Override
    /** Removes the player from the universe players list */
    public void disconnected(Connection connection) {
        server.sendToAllExceptTCP(connection.getID(), new PlayerDisconnect(connection.getID()));
        universe.removePlayer(connection.getID());
    }
}
