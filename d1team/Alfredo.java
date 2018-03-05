package d1team;

//Robocode imports
import robocode.*;
import static robocode.util.Utils.normalAbsoluteAngle;
import static robocode.util.Utils.normalRelativeAngle;
import java.awt.geom.Point2D;

//Java imports
//import java.awt.*;
import java.io.*;
import java.util.*;
//import java.math.*;
//import java.lang.*;
import java.text.*;

/**
 * Alfredo - the Ferrero Rocher robot
 */
public class Alfredo extends AdvancedRobot
{
	private DistanceThread counter; 
	HashMap<String,Meco> obstaculos = new HashMap<String,Meco>();
	double contorno = 0;
	double lastx;
	double lasty;
	double perimetro_percorrido; 

	public void run() {
		// Fase inicial para esperar pela deslocação dos obstáculos
	 	for(int i=0;i<5;i++){
	 		turnRight(360);
	 	}
		
		gotoXY(30,30);
		scan();
		init_counter();
		this.perimetro_percorrido = 0;
		ordena_obstaculos();
		// Fazer função robot genérica 
		robot1();
		robot2();
		robot3();
		gotoXY(30,30);
		this.counter.kill();
		resultados();
	}
	
	private void ordena_obstaculos(){
		ArrayList<Meco> visitar = new ArrayList<Meco>();
		
		for(Meco o: this.obstaculos.values()){
			if(o.jaVisitado == 0) { visitar.add(o); }
		}
		
   		 boolean swapped = true;
		 int j = 0;
		 Meco tmp;
		 while (swapped) {
		 	swapped = false;
		    j++;
		    for (int i = 0; i < visitar.size() - j; i++) {
		    	if (visitar.get(i).y > visitar.get(i+1).y ) {
		        	tmp = visitar.get(i);
		            visitar.add(i, visitar.get(i+1) );
		            visitar.add(i+1, tmp);
		            swapped = true;
		         }
		     }
		}
	}


	private void init_counter(){
		this.counter = new DistanceThread(this);
		this.counter.start();
	}
	
	private void resultados(){
		ArrayList<Point2D> aux = new ArrayList<Point2D>();
		for(Meco m : this.obstaculos.values()){
			aux.add(new Point2D.Double(m.x, m.y));
		} 
		
		double perimetro_obstaculos = 0;
		if(aux.size() == 3){
			perimetro_obstaculos += (new Point2D.Double(30, 30)).distance( aux.get(0) );
			perimetro_obstaculos += aux.get(0).distance( aux.get(1) );
			perimetro_obstaculos += aux.get(1).distance( aux.get(2) );
			perimetro_obstaculos += aux.get(2).distance( new Point2D.Double(30, 30) );
		}
				
		DecimalFormat df = new DecimalFormat("#.##");
		double dist = this.counter.getDistance();
		StringBuilder sb = new StringBuilder();
		sb.append("Perimetro obstaculos: " + df.format(perimetro_obstaculos) + "\n"); 
		sb.append("Perimetro percorrido: " + dist + "\n");
		sb.append("Racio: " + df.format( dist / perimetro_obstaculos) + "\n" );
		System.out.println(sb.toString() ); 
		//regista_percurso( sb.toString() );
	}
	
	/*private void regista_percurso(String s){
		try{
		    String filename= "Percurso.txt";
		    BufferedWriter bw = new BufferedWriter(new FileWriter(filename,true)); //the true will append the new data
		    PrintWriter out = new PrintWriter(bw);
		    out.append(s);
			out.close();
		}catch(IOException ioe){
		    System.err.println("IOException: " + ioe.getMessage());
		}
	}*/
	
	public void scan(){
		obstaculos = new HashMap<String, Meco>();
		turnRadarRight(360);
	}

		/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		if(!(obstaculos.containsKey(e.getName()))){
			double angulo = Math.toRadians((getHeading() + e.getBearing() % 360));
			double pX = (getX() + Math.sin(angulo) * e.getDistance());
        	double pY = (getY() + Math.cos(angulo) * e.getDistance());
			Meco o = new Meco(e.getName(), pX, pY, angulo, e.getDistance());
			obstaculos.put(o.nome, o);
		}
	}

	public void robot1(){
		double min_x=getBattleFieldWidth();
		Meco robot1 = null;

		for(Meco o: obstaculos.values()){
			if(o.x <= min_x) {robot1 = o;
				min_x = o.x;
			}
		}
		double angulo = normalAbsoluteAngle(Math.atan2(robot1.x - getX() - 33, robot1.y - getY() + 40));
		turnRight(Math.toDegrees(normalRelativeAngle(angulo - getGunHeadingRadians())));
		double distX = robot1.x - 30;
		double distY =  robot1.y - 30;
		double dist = Math.sqrt((distX * distX) + (distY * distY));
		
		ahead(dist + 40);
		obstaculos.get(robot1.nome).jaVisitado=1;

		contorno += dist;

		this.lastx = robot1.x;
		this.lasty = robot1.y;

	}

	public void robot2(){
		double max_y=0;
		Meco robot2 = null;
		for(Meco o: obstaculos.values()){
			if(o.y >= max_y && o.jaVisitado == 0) {robot2 = o;
				max_y = o.y;
			}
		}
		double angulo = normalAbsoluteAngle(Math.atan2(robot2.x - getX() + 50, robot2.y - getY() + 45));
		turnRight(Math.toDegrees(normalRelativeAngle(angulo - getGunHeadingRadians())));
		double distX = robot2.x - lastx;
		double distY =  robot2.y - lasty;
		double dist = Math.sqrt((distX * distX) + (distY * distY));

		ahead(dist + 70);
		obstaculos.get(robot2.nome).jaVisitado=1;
		
		contorno += dist;

		lastx = robot2.x;
		lasty = robot2.y;

	}

	public void robot3(){
		Meco robot3 = null;
		for(Meco o: obstaculos.values()){
			if(o.jaVisitado==0) robot3 = o;
		}
		double angulo = normalAbsoluteAngle(Math.atan2(robot3.x - getX() + 50, robot3.y - getY() + 55));
		turnRight(Math.toDegrees(normalRelativeAngle(angulo - getGunHeadingRadians())));
		double distX = robot3.x - lastx;
		double distY =  robot3.y - lasty;
		double dist = Math.sqrt((distX * distX) + (distY * distY));

		ahead(dist + 90);
		
		obstaculos.get(robot3.nome).jaVisitado=1;

		contorno += dist;

		distX = robot3.x - 30;
		distY =  robot3.y - 30;
		dist = Math.sqrt((distX * distX) + (distY * distY));
		contorno += dist;

	}

	public void paraTerminar(){
		double x_i = getX(); 
		double y_i = getY();
		
		double angulo = normalAbsoluteAngle(Math.atan2(20 - getX(), 20 - getY()));
		turnRight(Math.toDegrees(normalRelativeAngle(angulo - getGunHeadingRadians())));
		double distX = getX() - 30;
		double distY = getY() - 30;
		double dist = Math.sqrt((distX * distX) + (distY * distY));
		ahead(dist);
		
		double x_f = getX();
		double y_f = getY();
			
		contorno += Math.sqrt( Math.pow(x_i - x_f, 2) + Math.pow(y_i - y_f, 2));
		
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		double bearing = e.getBearing(); //get the bearing of the wall
    	turnRight(-bearing); //This isn't accurate but release your robot.
    	ahead(100); //The robot goes away from the wall.
	}	

	/*	public void onRoundEnded(RoundEndedEvent e) {
		double distancia_percorrida=0;
		PrintStream w = null;
	
		try {
			BufferedReader reader = null;
			try {
				// Read file "count.dat" which contains 2 lines, a round count, and a battle count
				reader = new BufferedReader(new FileReader(getDataFile("distancia.txt")));
				w = new PrintStream(new RobocodeFileOutputStream(getDataFile("distancia.txt")));
				// Try to get the counts
				distancia_percorrida = Double.parseDouble(reader.readLine());
				distancia_percorrida+=contorno;
				w.println(distancia_percorrida);

			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (IOException g) {
			// Something went wrong reading the file, reset to 0.

		} catch (NumberFormatException t) {
			// Something went wrong converting to ints, reset to 0
		}

		out.println("Round:" + getNumRounds() + "-> Distance:" + distancia_percorrida);
	}*/

	public void onBattleEnded(BattleEndedEvent e) {
		double distancia_total=0;
	
		try {
			BufferedReader reader = null;
			try {
				// Read file "count.dat" which contains 2 lines, a round count, and a battle count
				reader = new BufferedReader(new FileReader(getDataFile("distancia.txt")));

				// Try to get the counts
				distancia_total = Double.parseDouble(reader.readLine());

				} finally {
					if (reader != null) {
					reader.close();
					}
				}
			} catch (IOException g) {
			// Something went wrong reading the file, reset to 0.
			
			
		} catch (NumberFormatException t) {
			// Something went wrong converting to ints, reset to 0
		}
		
		out.println("Total Distance : " + distancia_total);
	}

	// Go to GPS position (x,y)
	private void gotoXY(double x, double y) {
		double dx = x - getX();
		double dy = y - getY();
		double turnDegrees;

		// Determine how much to turn
		turnDegrees = (Math.toDegrees(Math.atan2(dx, dy)) - getHeading()) % 360;
		turnRight(turnDegrees);
		ahead(Math.sqrt(dx*dx+dy*dy));
	} // end gotoXY()
}
