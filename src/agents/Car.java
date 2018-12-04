package src.agents;

import java.util.ArrayList;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import src.behaviours.*;
import src.graph.*;
import src.graph.Road.Direction;
import src.main.JADELauncher;
import src.resources.Rectangle;
import src.resources.Path;
import src.resources.Messages.MessageType;

/*
 * Agent that represents a car in the system
 */
public class Car extends Agent {

	public AID backCar;					//Back car
	public Point frontCar_position;		//Front car position
	
	public Point size; 		// size
	public Point location;  // location
	public Point velocity;  // velocity
	
	public double carVelocity;
	
	private Path path;
	
	public long initialTime;
	
	/*
	 * Constructor
	 */
	public Car(ArrayList<String> p, double randomValue) {

		this.backCar = null;
		this.frontCar_position = null;
		
		this.size = new Point(1, 1); 
		this.path = new Path(p);
		
		Road r = this.path.getCurrentRoad();
		
		this.carVelocity = randomValue;
		this.velocity = new Point(r.getVelocity()); 	 // velocity
		this.velocity.mul(carVelocity);
		
		this.location = new Point(r.getInitialPoint());  // location

		this.initialTime = System.currentTimeMillis() - JADELauncher.startTime;
		
		System.out.print("Hello this is mr car;  path: " + path.toString());
	}

	public void setup() {
		System.out.println( ";id: " +this.getLocalName() );

		// Subscribe initial road
		subscribeRoad(path.getCurrentRoadName());		

		// Behavior that represents the car receiving the messages
		addBehaviour(new CarMessagesReceiver(this));

		// Behavior that represents the car moving
		addBehaviour(new CarMoving(this, 50));
		
	}
	
	/*
	 * Send the request to subscribe the road
	 */
	public void subscribeRoad(String roadName) {
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent(MessageType.SUBSCRIBE.toString());

		msg.addReceiver(getAID("RoadAgent" + roadName));
		send(msg);
	}

	/*
	 * Checks if the car is out of bounds
	 */
	public boolean isOutOfBounds() {
 
		if (location.x > Map.width || location.x < -1 || location.y > Map.width || location.y < -1) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Returns the path of the car
	 */
	public Path getPath() {
		return this.path;
	}

	
	/*
	 * Get the rectangle dimensions of the car
	 */
	public Rectangle getRectangle() {

		double threshold = 0.2;

		double Ax1 = 0;
		double Ay1 = 0;
		
		double dimX = 1;
		double dimY = 1;

		switch (this.path.getDirection()) {

		case RIGHT: {

			Ax1 = location.x;
			Ay1 = location.y;
			
			dimX = 1 + threshold;

			break;
		}
		case LEFT: {
			Ax1 = location.x - threshold;
			Ay1 = location.y;

			break;
		}
		case UP: {
			Ax1 = location.x;
			Ay1 = location.y - threshold;

			break;
		}
		case DOWN: {
			Ax1 = location.x;
			Ay1 = location.y;
			
			dimY = 1 + threshold;
			break;
		}

		}
		return new Rectangle(Ax1, Ay1, dimX, dimY);
	}


	/*
	 * Checks if the car is in an intersection
	 */
	public boolean inIntersection() {

		return this.path.getCurrentRoad().inIntersection(getRectangle());

	}

	/*
	 * Checks the collision of a car to another
	 */
	public boolean collisionRisk(Point carLoc) {

		 return getRectangle().intersects(new Rectangle(carLoc.x, carLoc.y, 1, 1));
	}

	/*
	 * Sends the message to remove the agent from a list
	 */
	public void removeCar(AID intersection) {

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent(MessageType.UNSUBSCRIBE.toString());
		msg.addReceiver(intersection);
		send(msg);
	}
	
	/*
	 * Sends the message to remove the agent from a list
	 */
	public void removeCar(String agent) {

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent(MessageType.UNSUBSCRIBE.toString());
		AID dest = this.getAID(agent);
		msg.addReceiver(dest);
		send(msg);
	}

	/*
	 * Deletes an agent
	 */
	public void takeDown() {

		removeCar("RoadAgent" + this.path.getCurrentRoadName());
		System.out.println("Car "+this.getLocalName() +" is done");
	}


}
