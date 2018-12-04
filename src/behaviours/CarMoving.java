package src.behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import src.agents.Car;
import src.resources.Messages;
import src.resources.Path;
import src.graph.Point;
import src.resources.Rectangle;

public class CarMoving extends TickerBehaviour {

	private Car car;

	public CarMoving(Agent a, long period) {
		super(a, period);

		this.car = (Car) a;
	}

	@Override
	protected void onTick() {

		// send my position to the back car
		if (car.backCar != null) {
			sendPosition(car.backCar);
		}

		boolean canMove = false;

		Path p = car.getPath();

		if (car.inIntersection()) {

			if (!p.inIntersection && !p.waitingIntersection) { // send the request
				System.out.println("new intersection");
				p.setWaitingIntersection();

				requestIntersection();

			} else if (p.inIntersection) { // request is accepted

				canMove = true;

			}

		} else {

			// see if the car can move, according to the front car position

			Point frontCarLocation = car.frontCar_position;

			if (car.frontCar != null && frontCarLocation != null) {

				canMove = !car.collisionRisk(frontCarLocation);

			} else {

				canMove = true;
			}
		}

		if (canMove) {

			car.location.add(car.velocity);
		}

		
		if (p.inIntersection && p.nextSwitchPoint!=null && car.getRectangle().contains(p.nextSwitchPoint)) {

			System.out.println("switching road");

			p.switchRoad(car);

		}

		if (!car.inIntersection() && p.inIntersection) {

			car.removeCar("IntersectionAgent" + p.currentIntersection.name);
			//System.out.println("leaving intersection");
			p.removeIntersection();

		}

		if (car.isOutOfBounds()) {
			this.myAgent.doDelete();
			return;
		}

	}

	/*
	 * Sends the car request to enter an intersection
	 */
	public void requestIntersection() {

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent("REQUEST_INTERSECTION");

		AID agent = car.getAID("IntersectionAgent" + car.getPath().currentIntersection.name);
		msg.addReceiver(agent);
		car.send(msg);
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
