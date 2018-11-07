package src.agents;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.Map.Entry;
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
	protected Queue<SimpleEntry<AID,String>> waitingCars;
	protected boolean intersectionOccupied = false;
	protected boolean nextRoadOccupied = false;
	protected AID intersectionCar = null;

 
	public void setup() {
 
		this.waitingCars = new LinkedList<SimpleEntry<AID,String>>();
		
		if(ALGORITHM == SelectionAlgorithm.FIRST_COME_FIRST_SERVED) {
			addBehaviour(new FirstComeFirstServedBehaviour());
		}		
	}

	class FirstComeFirstServedBehaviour extends CyclicBehaviour {

		public void action() {			
						
			ACLMessage msg = receive();
			
			checkNextRoadOccupied();
								
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
					case SPACE_INFO:{
						String status = Messages.getMessageContent(msg.getContent())[0];
						if (status.equals("FREE")) {
							nextRoadOccupied = false;
							checkCarsInQueue();
						}else if(status.equals("FULL")) {
							nextRoadOccupied = true;
						}
						break;
					}
					default:{
						System.out.println(msg.getContent());
						break;
					}
				}
			} else {
				
			}
		}
	}
	
	private void checkCarsInQueue() {
		if(!intersectionOccupied && !nextRoadOccupied && waitingCars.size() > 0) {
			acceptCar(waitingCars.poll().getKey());
		}
	}
	
	private void checkNextRoadOccupied() {
		if(waitingCars.size() > 0) {
			AID roadAgent = getAID(waitingCars.peek().getValue());
			ACLMessage sendMsg = new ACLMessage(ACLMessage.INFORM);
			sendMsg.setContent(Messages.MessageType.POLL_SPACE.toString());
			sendMsg.addReceiver(roadAgent);
			send(sendMsg);
		}
	}
	
	private boolean inWaitingCars(AID car) {
		for(SimpleEntry<AID,String> se : waitingCars) {
			if (se.getKey().equals(car)) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Accept car request to enter an intersection, or add the car to the
	 * waiting list
	 */
	private void handleSubscribe(ACLMessage msg) {
		if (!msg.getSender().equals(intersectionCar) && !inWaitingCars(msg.getSender())) {
			
			String roadAgentName = msg.getContent().split(Messages.SEPARATOR)[1];
			waitingCars.add(new SimpleEntry<AID,String>(msg.getSender(),roadAgentName));
		}		
	}
	
	
	/*
	 * Remove agent from intersection and notify the next waiting car that
	 * he can now enter the intersection
	 */
	private void handleUnsubscribe(ACLMessage msg) {
		
		if (msg.getSender().equals(intersectionCar)) {
			System.out.println("Intersection cleared");
			intersectionOccupied = false;
		}	
	}
	
	/*
	 * Sends the accept message to an agent to enter the intersection
	 */
	public void acceptCar(AID car) {
		
		intersectionOccupied = true;
		nextRoadOccupied = true; //Might not be true but we'll assume it is and check again for the next car

		intersectionCar = car;
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent(MessageType.REQUEST_ACCEPTED.toString());
		msg.addReceiver(car);
		send(msg);
	}

}
