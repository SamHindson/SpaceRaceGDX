package com.semdog.spacerace.testing;

import java.io.IOException;
import java.util.HashMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

/**
 * Created by Sam on 2016/07/05.
 */

public class TestServer {
    public static void main(String[] a) throws IOException {
        Server server = new Server();
        server.start();
        server.bind(54555, 54777);
        System.out.println("Server Running!");

        HashMap<String, Dude> dudes = new HashMap<>();

        server.getKryo().register(String.class);
        server.getKryo().register(ConnectionEvent.class);
        server.getKryo().register(MoveRequest.class);
        server.getKryo().register(PlayerMoved.class);
        server.getKryo().register(ConnectionAccept.class);

        server.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof ConnectionEvent) {
                    System.out.println("New Player Connected! wew");

                    Dude newDude = new Dude();
                    newDude.id = ((ConnectionEvent) object).who;
                    dudes.put(newDude.id, newDude);

                    System.out.println("NEWLY MAN: " + newDude.id);

                    server.sendToAllExceptTCP(connection.getID(), newDude.id);
                    connection.sendTCP(new ConnectionAccept());
                } else if (object instanceof MoveRequest) {
                    MoveRequest moved = (MoveRequest) object;
                    String who = moved.who;

                    if (moved.up) {
                        dudes.get(who).y++;
                    } else if (moved.down) {
                        dudes.get(who).y--;
                    }

                    if (moved.right) {
                        dudes.get(who).x++;
                    } else if (moved.left) {
                        dudes.get(who).x--;
                    }

                    PlayerMoved moved1 = new PlayerMoved();
                    moved1.who = who;
                    moved1.x = dudes.get(who).x;
                    moved1.y = dudes.get(who).y;

                    server.sendToAllTCP(moved1);
                }
            }
        });
    }

    static class Dude {
        String id;
        float x, y;
    }
}
