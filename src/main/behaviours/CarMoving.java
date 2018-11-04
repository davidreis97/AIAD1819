package main.behaviours;

import java.io.IOException;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import main.Car;
import main.Messages;
import main.Point;


public class CarMoving extends TickerBehaviour{

	private Car car;
	
	public CarMoving(Agent a, long period) {
		super(a, period);
		
		this.car = (Car)a;
	}

	@Override
	protected void onTick() {
		
		//send my position to the back car
		if(car.backCar != null) {
			sendPosition(car.backCar);
		}
		
		boolean canMove = false;
		
		if(car.inIntersection()) {
			
			if(!car.inIntersection && !car.waitingIntersection) {
				
				car.waitingIntersection = true;
				
			
				//Sends a request to the intersection agent
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.setContent("REQUEST_INTERSECTION");
				
				AID agent = car.getAID("IntersectionAgent");
				msg.addReceiver(agent);
				car.send(msg);
				
			} else if(car.inIntersection) {
				canMove = true;
			}
		
		} else {
			
			// see if the car can move, according to the front car position
			
			Point frontCarLocation = car.frontCar_position;
			
			if (car.frontCar != null &&  frontCarLocation!= null) {

				canMove = !car.collisionRisk(frontCarLocation);

			} else {
				
				canMove = true;
			}
		}
		
		
		if (canMove) {
			car.location.add(car.velocity);
		}
		
		
		if(car.path.midPoint!=null && car.inInterPoint()) {
			
			car.velocity = car.path.finalVelocity;
			
			
			// check if the car has to change his road agent
			
			if(! car.road.equals(car.path.finalRoad)) {
				
				car.removeCar("RoadAgent" + car.road);
				
				car.road = car.path.finalRoad;
				
				 //TODO adicionar o car ao novo road agent !!!
				
				
			}else {
				car.road = car.path.finalRoad;
			}
			
		}


		if (!car.inIntersection() && car.inIntersection) {
			
			car.removeCar("IntersectionAgent");
			car.inIntersection = false;
		}

		if (car.isOutOfBounds()) {
			this.myAgent.doDelete();
			return;
		}
	
		
	}
	
	/*
	 * Sends the car position to another agent
	 */
	public void sendPosition(AID agent) {

		String message = Messages.buildPositionMessage(car.location);
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(agent);
		msg.setContent(message);
		car.send(msg);

	}

}
 