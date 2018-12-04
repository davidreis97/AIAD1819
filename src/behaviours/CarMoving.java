package src.behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import src.agents.Car;
import src.resources.Messages;
import src.resources.Path;
import src.graph.Point;
import src.main.JADELauncher;
import src.resources.Rectangle;
import src.resources.WriteExcel;

/*
 * Behaviour that represents the car moving
 */
public class CarMoving extends TickerBehaviour {

	private Car car;
	
	private long ticksWaiting;
	private long totalTicks;

	/*
	 * Constructor
	 */
	public CarMoving(Agent a, long period) {
		super(a, period);

		ticksWaiting = 0;
		this.car = (Car) a;
	}

	@Override
	protected void onTick() {
		
		totalTicks++;
		
		// send the car position to the back car
		if (car.backCar != null) {
			sendPosition(car.backCar);
		}

		boolean canMove = false;

		Path p = car.getPath();

		if (car.inIntersection()) {

			if (!p.inIntersection && !p.waitingIntersection) { // send the request
				 
				p.setWaitingIntersection();

				requestIntersection();

			} else if (p.inIntersection) { // request is accepted

				canMove = true;

			}

		} else {

			// see if the car can move, according to the front car position

			Point frontCarLocation = car.frontCar_position;

			if ( frontCarLocation != null) {

				canMove = !car.collisionRisk(frontCarLocation);

			} else {

				canMove = true;
			}
		}

		if (canMove) {
		 
			car.location.add(car.velocity);
	
		}else {
			ticksWaiting++;
		}

		
		//switch road
		if (p.inIntersection && p.nextSwitchPoint!=null && car.getRectangle().contains(p.nextSwitchPoint)) {

			car.backCar = null;
			p.switchRoad(car);
		}

		//leaves intersection
		if (!car.inIntersection() && p.inIntersection) {
			 
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("intersection");
			sd.setName("intersection"+p.currentIntersection.name);
			template.addServices(sd);
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template);
				AID intersection = result[0].getName();
				car.removeCar(intersection);
				p.removeIntersection();
			}catch(FIPAException fe) {
				fe.printStackTrace();
			}
		}

		//car done 
		if (car.isOutOfBounds()) {
			//System.out.println("CAR WAITED " + (double) (ticksWaiting * 50) / 1000.0 + " SECONDS");
			
			WriteExcel.addCar(ticksWaiting, car.getLocalName(), car.initialTime, totalTicks, car.getPath(), car.carVelocity);
	 
			this.myAgent.doDelete(); 
			return;
		}

	}

	/*
	 * Sends the car request to enter an intersection
	 */
	public void requestIntersection() {
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		
		msg.setContent("REQUEST_INTERSECTION;RoadAgent"+car.getPath().getNextRoad()+";"+
				car.getPath().getCurrentRoadName()+";"+car.getPath().getNextRoad());
		
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("intersection");
		sd.setName("intersection" + car.getPath().currentIntersection.name);
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(myAgent, template);
			AID intersection = result[0].getName();
			msg.addReceiver(intersection);
			car.send(msg);
		}catch(FIPAException fe) {
			fe.printStackTrace();
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
