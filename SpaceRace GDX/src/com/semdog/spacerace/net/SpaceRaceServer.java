package com.semdog.spacerace.net;

import java.io.IOException;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class SpaceRaceServer extends Listener {

	public static void main(String[] args) {
		try {
			new SpaceRaceServer();
		} catch (IOException e) {
			System.err.println("Could not start server!");
			e.printStackTrace();
		}
	}

	private VirtualUniverse universe;
	private Server server;

	public SpaceRaceServer() throws IOException {
		universe = new VirtualUniverse();
		universe.addPlanet("Macky D's", 0, 0, 200);
		universe.addPlanet("Wimpy", 1500, 0, 200);

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

		server.addListener(this);
		server.start();
	}

	@Override
	public void received(Connection connection, Object object) {
		if (object instanceof NewPlayer) {
			NewPlayer newboy = (NewPlayer) object;
			System.out.println("New boy approaching!");
			System.out.println("His id is " + connection.getID());

			connection.sendTCP(universe.getState());
			server.sendToAllExceptTCP(connection.getID(), (NewPlayer) object);

			universe.addPlayer(newboy.getId(), newboy.getPlayer());
		} else if (object instanceof PlayerState) {
			PlayerState state = (PlayerState)object;
			server.sendToAllExceptTCP(connection.getID(), (PlayerState) object);
			if (state.getCategory() == PlayerState.SETPOS) {
				universe.setPlayerPosition(state.id, state.getInformation()[0], state.getInformation()[1]);
			} else if (state.getCategory() == PlayerState.LIFE) {
				//universe.setPlayerDead(state.id);
				//	TODO handle score changes
			}
		} else if (object instanceof BulletRequest) {
			server.sendToAllTCP(object);
		}
	}
}
