package com.semdog.spacerace.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.semdog.spacerace.vehicles.SmallBombarder;

public class UniverseLoader {
	public void load(Universe universe) {
		JsonReader jsonReader = new JsonReader();
		JsonValue data = jsonReader.parse(Gdx.files.internal("data/testlevel.json"));
		
		int index = 0;
		
		while(true) {
			JsonValue value = data.get(index);
			
			if(value == null)
				break;

            String name = value.name.split("-")[0];

            switch (name) {
                case "planet":
                    Gdx.app.log("UniverseLoader", "NEW PLANET");

                    float r = value.get(0).getFloat("radius");
				float x = value.get(0).getFloat("x");
				float y = value.get(0).getFloat("y");
				
				universe.createPlanet(x, y, r);
				
				break;
                case "playerstuff":
                    float dx = value.get(0).getFloat("x");
                    float dy = value.get(0).getFloat("y");
				
				Gdx.app.log("UniverseLoader", "PLAYER DEETS");
				
				universe.spawnPlayer(dx, dy);
								
				break;
				
			case "runt":
				float rx = value.get(0).getFloat("x");
				float ry = value.get(0).getFloat("y");
				
				Gdx.app.log("UniverseLoader", "RONTS");
				
				new SmallBombarder(rx, ry);
								
				break;
			}
			
			index++;
		}
	} 
}
