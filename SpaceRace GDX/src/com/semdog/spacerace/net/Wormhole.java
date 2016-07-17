package com.semdog.spacerace.net;

import java.io.IOException;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.semdog.spacerace.players.Player;
import com.semdog.spacerace.universe.MultiplayerUniverse;

public class Wormhole extends Listener {
	private Client client;
	private MultiplayerUniverse universe;
	private int clientID;
	
	public Wormhole(MultiplayerUniverse universe) throws IOException {
		this.universe = universe;
		
		client = new Client();
		
		System.out.println("Connecting...");
		
		Log.TRACE();
		
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
		kryo.register(PlayerPosition.class);
		kryo.register(String.class);
		kryo.register(int[].class);
		kryo.register(int.class);
		kryo.register(float.class);
		kryo.register(float[].class);
		kryo.register(PlayerState.class);		
		kryo.register(BulletRequest.class);
		kryo.register(PlayerDisconnect.class);
		
		client.start();
		client.connect(5000, "192.168.0.4", 13377, 24488);
		client.addListener(this);
		
		Log.INFO();
		
		System.out.println("Cool!");
		
		clientID = client.getID();
		System.out.println("My id is " + clientID);
	}
	
	public int getID() {
		return clientID;
	}
	
	@Override
	public void received(Connection connection, Object object) {
		if(object instanceof NewPlayer) {
			universe.createPuppet(((NewPlayer)object).getId(), ((NewPlayer)object).getPlayer());
		} else if(object instanceof UniverseState) {
			UniverseState state = (UniverseState)object;
						
			for(int r = 0; r < state.getPlanets().length; r++) {
				universe.createPlanet(state.getPlanets()[r]);
			}
			
			for(int t = 0; t < state.getPlayers().length; t++) {
				universe.createPuppet(state.getPlayerIDs()[t], state.getPlayers()[t]);
			}
		} else if(object instanceof PlayerState) {
			PlayerState state = (PlayerState)object;
			
			switch(state.getCategory()) {
			case PlayerState.ANIMSTATE:
				universe.setPlayerAnimState(state.id, state.getInformation()[0]);
				break;
			case PlayerState.SETPOS:
				universe.setPuppetPosition(state.id, state.getInformation()[0], state.getInformation()[1]);
				break;
			case PlayerState.ENVSTATE:
				universe.setEnvironmentPosition(state.id, state.getInformation()[0], state.getInformation()[1]);
				break;
			case PlayerState.LIFE:
				universe.setAlive(state.id, state.getInformation()[0]);
				break;
			case PlayerState.BULLETKILL:
				universe.killPoint(state.id, state.getInformation()[0]);
				break;
			}
		} else if(object instanceof BulletRequest) {
			BulletRequest request = (BulletRequest)object;
			universe.addBullet(connection.getID(), request.getX(), request.getY(), request.getDx(), request.getDy(), request.getDamage());
		} else if(object instanceof PlayerDisconnect) {
			universe.removePuppet(((PlayerDisconnect)object).getId());
		}
	}

	public void registerPlayer(Player player) {
		VirtualPlayer player2 = new VirtualPlayer(player.getName(), clientID, 0, player.getX(), player.getY());
		NewPlayer request = new NewPlayer(player2);
		request.setId(clientID);
		client.sendTCP(request);
	}
	
	public void sendPlayerState(int category, float...params) {
		client.sendTCP(new PlayerState(clientID, category, params));
	}

	public void sendBulletRequest(float x, float y, float dx, float dy, int damage) {
		client.sendTCP(new BulletRequest(x, y, dx, dy, damage));
	}
}
