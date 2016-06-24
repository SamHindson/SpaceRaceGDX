package com.semdog.spacerace.universe;

import java.lang.reflect.InvocationTargetException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.semdog.spacerace.collectables.Health;
import com.semdog.spacerace.vehicles.RubbishLander;
import com.semdog.spacerace.vehicles.SmallBombarder;

public class UniverseLoader {
	public void load(Universe universe) {
		JsonReader jsonReader = new JsonReader();
		JsonValue data = jsonReader.parse(Gdx.files.internal("data/testlevel.json"));

		int index = 0;

		while (true) {
			JsonValue value = data.get(index);

			if (value == null)
				break;

			String name = value.name.split("-")[0];

			switch (name) {
			case "planet":
				Gdx.app.log("UniverseLoader", "NEW PLANET");

				float r = value.get(0).getFloat("radius");
				float x = value.get(0).getFloat("x");
				float y = value.get(0).getFloat("y");
				String id = value.get(0).getString("id");

				universe.createPlanet(id, x, y, r);

				break;
			case "playerstuff":
				float dx = value.get(0).getFloat("x");
				float dy = value.get(0).getFloat("y");

				universe.spawnPlayer(dx, dy);

				break;

			case "runt":
				float rx = value.get(0).getFloat("x");
				float ry = value.get(0).getFloat("y");
				String rid = value.get(0).getString("id");

				SmallBombarder dude = new SmallBombarder(rx, ry, rid);
				
				try {
					String ssname = value.get(1).get(0).name;
					float ssvalue = value.get(1).getFloat(0);
					System.out.println(ssname + " ---> " + ssvalue);
					SmallBombarder.class.getDeclaredMethod(ssname, float.class).invoke(dude, ssvalue);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			case "rubbish":
				float cx = value.get(0).getFloat("x");
				float cy = value.get(0).getFloat("y");
				String cid = value.get(0).getString("id");

				new RubbishLander(cx, cy, cid);

				break;
			case "playercriteria":
				JsonValue details = value.get(0);

				for (JsonValue criteria : details) {
					String criterion = criteria.name;
					String criterionType = criterion.split("-")[0];
					String criterionName = criterion.split("-")[1];

					System.out.println("Found a value.");
					System.out.println("Name: " + criterionName);
					System.out.println("Full name: " + criterion);
					System.out.println("Value: " + details.get(criterion).asString());

					switch (criterionType) {
					case "boolean":
						universe.getGoalChecker().addBoolean(criterionName, details.getBoolean(criterion));
						break;
					case "float":
						universe.getGoalChecker().addFloat(criterionName, details.getFloat(criterion));
						break;
					case "string":
						universe.getGoalChecker().addString(criterionName, details.getString(criterion));
						break;
					}
				}

				break;
			case "mission":
				JsonValue missionDetails = value.get(0);

				universe.setTimeLimit(missionDetails.getFloat("timelimit"));
				universe.setSuddenDeath(missionDetails.getBoolean("suddendeath"));

				Gdx.app.log("UniverseLoader", "Mission details good!");

				break;
			case "healthpack":

				float hx = value.get(0).getFloat("x");
				float hy = value.get(0).getFloat("y");
				universe.addCollectable(new Health(hx, hy));

				break;
			}

			index++;
		}
	}
}
