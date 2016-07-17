package com.semdog.spacerace.misc;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.semdog.spacerace.universe.Universe;

/***
 * This here is the OrbitalHelper class I created last year for ULTRANAUT.
 * 
 * @author Sam
 */

public class OrbitalHelper {
	
	/**
	 * This method is the reason the OrbitalHelper class exists - to help
	 * determine the various quantities involved in orbits without each class
	 * needing the code.
	 * 
	 * It takes in some orbital states and returns a whole bunch of information
	 * packaged into a float array.
	 *
	 * Blatantly stolen from last year's ULTRANAUT because redoing all of this
	 * math is not an effective way of going about things.
	 * 
	 * For better understanding the math used here refer to the following
	 * resources:
	 * 
	 * http://wiki.kerbalspaceprogram.com/wiki/Tutorial:_Basic_Orbiting_%28Math%29
	 * http://hyperphysics.phy-astr.gsu.edu/hbase/math/ellipse.html
	 * http://sydney.edu.au/engineering/aeromech/AERO4701/Course_Documents/AERO4701_week2.pdf
	 */
	public static float[] computeOrbit(Vector2 orbiteePosition, Vector2 orbiterPosition, Vector2 orbiterVelocity,
			float orbiteeMass) {
		Vector2 orP = new Vector2(orbiterPosition);
		Vector2 offset = orP.sub(orbiteePosition);
		Vector2 orV = new Vector2(orbiterVelocity);

		float mu = Universe.GRAVITY * orbiteeMass;

		float angularMomentum = offset.crs(orV);
		float eccX = (orV.y * angularMomentum) / mu - (offset.x / offset.len());
		float eccY = -(orV.x * angularMomentum) / mu - (offset.y / offset.len());
		Vector2 eccentricityVector = new Vector2(eccX, eccY);
		float eccentricity = eccentricityVector.len();

		float energy = orV.len() * orV.len() / 2 - mu / offset.len();

		float semiMajorAxis = -mu / (2 * energy);
		float semiMinorAxis = (float) (semiMajorAxis * Math.sqrt(1 - (eccentricity * eccentricity)));
		float apoapsis = semiMajorAxis * (1 + eccentricity);
		float periapsis = semiMajorAxis * (1 - eccentricity);

		float trueAnomaly;

		if (angularMomentum > 0) {
			if (offset.dot(orV) < 0) {
				trueAnomaly = (float) Math.acos(eccentricityVector.dot(offset) / (eccentricity * offset.len()));
			} else {
				trueAnomaly = MathUtils.PI2
						- (float) Math.acos(eccentricityVector.dot(offset) / (eccentricity * offset.len()));
			}
		} else {
			if (offset.dot(orV) < 0) {
				trueAnomaly = MathUtils.PI2
						- (float) Math.acos(eccentricityVector.dot(offset) / (eccentricity * offset.len()));
			} else {
				trueAnomaly = (float) Math.acos(eccentricityVector.dot(offset) / (eccentricity * offset.len()));
			}
		}

		float sinE = (float) (MathUtils.sin(trueAnomaly) * Math.sqrt(1 - (eccentricity * eccentricity))
				/ (1 + eccentricity * MathUtils.cos(trueAnomaly)));
		float cosE = (eccentricity + MathUtils.cos(trueAnomaly)) / (1 + eccentricity * MathUtils.cos(trueAnomaly));
		float eccentricAnomaly = MathUtils.atan2(sinE, cosE);

		float orbitalPeriod = MathUtils.PI2 * (float) Math.sqrt(Math.pow(semiMajorAxis, 3) / mu);

		float[] results = new float[10];
		results[0] = angularMomentum;
		results[1] = eccentricity;
		results[2] = energy;
		results[3] = semiMajorAxis;
		results[4] = semiMinorAxis;
		results[5] = apoapsis;
		results[6] = periapsis;
		results[7] = trueAnomaly;
		results[8] = eccentricAnomaly;
		results[9] = orbitalPeriod;
		return results;
	}
}