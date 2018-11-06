package src.agents;

import java.util.LinkedList;
import java.util.Queue;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import src.resources.Messages.MessageType;
import src.resources.Messages;

/*
 * Agent that represents an intersection. 
 */
public class IntersectionAgent extends Agent {

	public enum SelectionAlgorithm {
		FIRST_COME_FIRST_SERVED, COLLISION_DETECTION
	}
	
	protected static final SelectionAlgorithm ALGORITHM = SelectionAlgorithm.FIRST_COME_FIRST_SERVED; 
	protected Queue<AID> waitingCars;
	protected boolean intersectionOccupied = false;
	protected AID intersectionCar = null;

 
	public void setup() {
 
		this.waitingCars = new LinkedList<AID>();
		
		if(ALGORITHM == SelectionAlgorithm.FIRST_COME_FIRST_SERVED) {
			addBehaviour(new FirstComeFirstServedBehaviour());
		}		
	}

	class FirstComeFirstServedBehaviour extends CyclicBehaviour {

		public void action() {

			ACLMessage msg = receive();

			if (msg != null) {

				MessageType type = Messages.getMessageType(msg.getContent());
				
				switch(type){
				
					case REQUEST_INTERSECTION:{
						handleSubscribe(msg);
						break;
					}
					case UNSUBSCRIBE:{
						handleUnsubscribe(msg);
						break;
					}
					default:{
						break;
					}
				}
				
			} else {
				block();
			}
		}
	}
	
	
	/*
	 * Remove agent from intersection and notify the next waiting car that
	 * he can now enter the intersection
	 */
	private void handleUnsubscribe(ACLMessage msg) {
		
		if (msg.getSender().equals(intersectionCar)) {
			
			intersectionOccupied = false;

			if (!waitingCars.isEmpty()) {
				
				intersectionCar = waitingCars.poll();
				acceptCar(intersectionCar);
				
			}
		}	
	}
	
	/*
	 * Sends the accept message to an agent to enter the intersection
	 */
	public void acceptCar(AID car) {
		
		intersectionOccupied = true;

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent(MessageType.REQUEST_ACCEPTED.toString());
		msg.addReceiver(car);
		send(msg);
	}

	
	/*
	 * Accept car request to enter an intersection, or add the car to the
	 * waiting list
	 */
	private void handleSubscribe(ACLMessage msg) {
		
		if (!intersectionOccupied) {

			intersectionCar = msg.getSender();
			acceptCar(intersectionCar);


		} else {

			if (!msg.getSender().equals(intersectionCar) && !waitingCars.contains(msg.getSender())) {
				
				waitingCars.add(msg.getSender());
				
			}
		}
		
	}

}
