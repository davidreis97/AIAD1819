package main;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import main.behaviours.CarMessagesReceiver;
import main.behaviours.CarMoving;

public class Car extends Agent {

	public Point location; // location
	public Point velocity; // velocity
	public String road;   // road
	
	public Point size; 	   // car size

	public AID frontCar, backCar;
	public Point frontCar_position;

	public boolean waitingIntersection;
	public boolean inIntersection;
	
	public Path path;


	public Car(Path p) {

		this.frontCar = null;
		this.backCar = null;
		this.frontCar_position = null;

		this.size = new Point(1, 1);	 

		this.waitingIntersection = false;
		this.inIntersection = false;
		
		this.path = p;
		
		this.velocity = p.initialVelocity;
		this.location = p.startPoint; 
		this.road = p.initialRoad;  
		
		System.out.println("Hello this is mr car, in road: " + p.initialRoad + ";" + p.finalRoad);
	}

	
	public void setup() {

		/*
		 * Sends the request to enter the initial road
		 */
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent("SUBSCRIBE");
		msg.addReceiver(getAID("RoadAgent" + road));
		send(msg);
		
		// Behavior that represents the car receiving the messages
		addBehaviour(new CarMessagesReceiver(this));
		
		// Behavior that represents the car moving
		addBehaviour(new CarMoving(this, 100));

	}
	

	/*
	 * Checks if the car is out of bounds
	 */
	public boolean isOutOfBounds() {
		if (location.x > 10 || location.x < 0 || location.y > 10 || location.y < 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean inInterPoint() {
	    
		double Ax1 = 0;
		double Ax2 = 0;
		double Ay1 = 0;
		double Ay2 = 0;

		if (velocity.x > 0) {
			Ax1 = location.x;
			Ax2 = location.x + size.x;
			Ay1 = location.y;
			Ay2 = location.y + size.y;
		} else if (velocity.x < 0) {
			Ax1 = location.x;
			Ax2 = location.x + size.x;
			Ay1 = location.y;
			Ay2 = location.y + size.y;
		} else if (velocity.y > 0) {
			Ax1 = location.x;
			Ax2 = location.x + size.x;
			Ay1 = location.y;
			Ay2 = location.y + size.y;
		} else if (velocity.y < 0) {
			Ax1 = location.x;
			Ax2 = location.x + size.x;
			Ay1 = location.y;
			Ay2 = location.y + size.y;
		} else {
			System.out.println("Car has 0 velocity, bugged or out of fuel? Probably bugged.");
		}
		
		double x = path.midPoint.x;
		double y = path.midPoint.y;
		
		return x >= Ax1 && y >= Ay1 && x <= Ax2 && y <= Ay2;             

	}

	/*
	 * Checks if the car is in the intersection
	 */
	public boolean inIntersection() {

		double threshold = 0.4;

		double Ax1 = 0;
		double Ax2 = 0;
		double Ay1 = 0;
		double Ay2 = 0;

		double Bx1 = Map.intersectionP.x;
		double Bx2 = Map.intersectionP.x + 2;
		double By1 = Map.intersectionP.y;
		double By2 = Map.intersectionP.y + 2;

		if (velocity.x > 0) {
			Ax1 = location.x;
			Ax2 = location.x + size.x + threshold;
			Ay1 = location.y;
			Ay2 = location.y + size.y;
		} else if (velocity.x < 0) {
			Ax1 = location.x - threshold;
			Ax2 = location.x + size.x;
			Ay1 = location.y;
			Ay2 = location.y + size.y;
		} else if (velocity.y > 0) {
			Ax1 = location.x;
			Ax2 = location.x + size.x;
			Ay1 = location.y;
			Ay2 = location.y + size.y + threshold;
		} else if (velocity.y < 0) {
			Ax1 = location.x;
			Ax2 = location.x + size.x;
			Ay1 = location.y - threshold;
			Ay2 = location.y + size.y;
		} else {
			System.out.println("Car has 0 velocity, bugged or out of fuel? Probably bugged.");
		}

		if (Ax1 < Bx2 && Ax2 > Bx1 && Ay1 < By2 && Ay2 > By1) {
			return true;
		}
		return false;

	}

	/*
	 * Checks the collision of a car to another
	 */
	public boolean collisionRisk(Point otherCarLocation) {

		double threshold = 0.2; // Minimum space to other cars

		// Our collision box
		double Ax1 = 0;
		double Ax2 = 0;
		double Ay1 = 0;
		double Ay2 = 0;

		/*
		 * if (otherCarLocation.x > 10 || otherCarLocation.x < 0 || otherCarLocation.y >
		 * 10 || otherCarLocation.y < 0) { return false; }
		 */

		// Other car
		double Bx1 = otherCarLocation.x;
		double Bx2 = otherCarLocation.x + 1;
		double By1 = otherCarLocation.y;
		double By2 = otherCarLocation.y + 1;

		// IDEIA - Se eles ficarem os dois bloqueados (ambos no bloco de colisao um do
		// outro)
		// falam um com um outro para determinar qual dos dois avanca (é pro 20)

		// Calculate size of our collision box
		if (velocity.x > 0) {
			Ax1 = location.x + size.x;
			Ax2 = location.x + size.x + threshold;
			Ay1 = location.y;
			Ay2 = location.y + size.y;
		} else if (velocity.x < 0) {
			Ax1 = location.x - threshold;
			Ax2 = location.x;
			Ay1 = location.y;
			Ay2 = location.y + size.y;
		} else if (velocity.y > 0) {
			Ax1 = location.x;
			Ax2 = location.x + size.x;
			Ay1 = location.y + size.y;
			Ay2 = location.y + size.y + threshold;
		} else if (velocity.y < 0) {
			Ax1 = location.x;
			Ax2 = location.x + size.x;
			Ay1 = location.y - threshold;
			Ay2 = location.y;
		} else {
			System.out.println("Car has 0 velocity, bugged or out of fuel? Probably bugged.");
		}

		// Formula para calcular colisoes entre retangulos
		// (https://stackoverflow.com/questions/31022269/collision-detection-between-two-rectangles-in-java)
		if (Ax1 < Bx2 && Ax2 > Bx1 && Ay1 < By2 && Ay2 > By1) {
			return true;
		} else {
			return false;
		}
	}

	

	/*
	 * Sends the message to remove the agent from a list
	 */
	public void removeCar(String agent) {

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent("UNSUBSCRIBE");
		AID dest = this.getAID(agent);
		msg.addReceiver(dest);
		send(msg);
		
	}

	/*
	 * Deletes an agent
	 */
	public void takeDown() {

		removeCar("RoadAgent" + this.road);

		System.out.println("Car is done");
	}

}
