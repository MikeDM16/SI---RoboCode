/**
 * Copyright (c) 2001-2017 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package d1team;


import robocode.AdvancedRobot;
import robocode.RobocodeFileOutputStream;

import robocode.*;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;


public class Distancia extends AdvancedRobot {
	static boolean incrementedBattles = false;
	static float dist_round = 0;
	int nr_round = 1;
	static boolean peek; // Don't turn if there's a robot there
	double moveAmount; // How much to move
	
	boolean stopWhenSeeRobot = false; // See goCorner()
	private double x_i, y_i, x_f, y_f;
	 
	private float readDistance(){
		float distance = 0;

		try {
			BufferedReader reader = null;
			try {reader = new BufferedReader(new FileReader(getDataFile("dist.dat")));

				distance = Integer.parseInt(reader.readLine());

			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (Exception e) {		distance = 0;		}
		
		return distance;
	}

	private void writeDistance(float d){
		PrintStream w = null;
		try {
			w = new PrintStream(new RobocodeFileOutputStream(getDataFile("dist.dat")));

			w.println(d);

			// PrintStreams don't throw IOExceptions during prints, they simply set a flag.... so check it here.
			if (w.checkError()) {
				out.println("I could not write the count!");
			}
		} catch (IOException e) {
			out.println("IOException trying to write: ");
			e.printStackTrace(out);
		} finally {
			if (w != null) {
				w.close();
			}
		} 
	}

	public void onRobotDeath(RobotDeathEvent event){
	   System.out.println("Descoloquei " + dist_round + "." );
	   
	   float distance = readDistance();
	   distance += dist_round; 
	   writeDistance(distance);
   }

	public void run() {
		x_i = getX(); 
		y_i = getY();
		
		// Set colors
		setBodyColor(Color.black);
		setGunColor(Color.black);
		setRadarColor(Color.orange);
		setBulletColor(Color.cyan);
		setScanColor(Color.cyan);

		// Initialize moveAmount to the maximum possible for this battlefield.
		moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
		// Initialize peek to false
		peek = false;

		// turnLeft to face a wall.
		// getHeading() % 90 means the remainder of
		// getHeading() divided by 90.
		turnLeft(getHeading() % 90);
		ahead(moveAmount);
		// Turn the gun to turn right 90 degrees.
		peek = true;
		turnGunRight(90);
		turnRight(90);

		while (true) {
			// Look before we turn when ahead() completes.
			peek = true;
			// Move up the wall
			ahead(moveAmount);
			// Don't look now
			peek = false;
			// Turn to the next wall
			turnRight(35);
			
			x_f = getX();
			y_f = getY();
			
			dist_round += Math.sqrt( Math.pow(this.x_i-this.x_f, 2) + Math.pow(this.y_i-this.y_f, 2));
		}
	}

}
