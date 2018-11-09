package src.agents;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import src.resources.Messages.MessageType;
import src.resources.Messages;

/*
 * Agent that represents an intersection. 
 */
public class IntersectionAgent extends Agent {

	public enum SelectionAlgorithm {
		FIRST_COME_FIRST_SERVED, COLLISION_DETECTION, RANDOM_NEXT
	}
	
	
	protected static final SelectionAlgorithm ALGORITHM = SelectionAlgorithm.RANDOM_NEXT; 
	protected LinkedList<SimpleEntry<AID,String>> waitingCars;
	protected boolean intersectionOccupied = false;
	protected boolean nextRoadOccupied = false;
	protected AID intersectionCar = null;
	
	private int index;

 
	public IntersectionAgent(int index) {
		super();
		
		this.index = index;
	}

	public void setup() {
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("intersection");
		sd.setName("intersection"+index);
		dfd.addServices(sd);
		
		try {
			DFService.register(this, dfd);
		}catch(FIPAException fe) {
			fe.printStackTrace();
		}
		
		this.waitingCars = new LinkedList<SimpleEntry<AID,String>>();
		
		if(ALGORITHM == SelectionAlgorithm.FIRST_COME_FIRST_SERVED) {
			addBehaviour(new FirstComeFirstServedBehaviour(this,100));
		}else if(ALGORITHM == SelectionAlgorithm.RANDOM_NEXT) {
			addBehaviour(new RandomNextBehaviour(this,10));
		}
	}

	class FirstComeFirstServedBehaviour extends TickerBehaviour {

		public FirstComeFirstServedBehaviour(Agent a, long period) {
			super(a, period);
		}

		public void onTick() {			
						
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
							checkCarsInQueue(msg.getSender().getName().split("@")[0]);
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
			}
		}
	}
	
	class RandomNextBehaviour extends TickerBehaviour {

		public RandomNextBehaviour(Agent a, long period) {
			super(a, period);
		}
		
		@Override
		public void onTick() {			
						
			ACLMessage msg = receive();
			
			checkNextRandomRoadOccupied();
																	
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
							checkCarsInQueue(msg.getSender().getName().split("@")[0]);
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
			}
		}
	}
	
	private void checkCarsInQueue(String roadAgent) {
		if(!intersectionOccupied && !nextRoadOccupied && waitingCars.size() > 0) {
			for(SimpleEntry<AID,String> entry : waitingCars) {
				if(entry.getValue().equals(roadAgent)) {
					acceptCar(entry.getKey());
					return;
				}
			}
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
	
	private void checkNextRandomRoadOccupied() {
		if(waitingCars.size() > 0) {
			int nextIndex = (int) (Math.random() * waitingCars.size());
			AID roadAgent = getAID(waitingCars.get(nextIndex).getValue());
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
			System.out.println("Car " + msg.getSender().getName().split("@")[0] + " left intersection");
			intersectionOccupied = false;
		}else {
			System.out.println("Received unsubscribe from unknown car");
		}
	}
	
	/*
	 * Sends the accept message to an agent to enter the intersection
	 */
	public void acceptCar(AID car) {
		
		for(int i = 0; i < waitingCars.size(); i++) {
			if(waitingCars.get(i).getKey().equals(car)) {
				waitingCars.remove(i);
				break;
			}
		}
				
		intersectionOccupied = true;
		nextRoadOccupied = true; //Might not be true but we'll assume it is and check again for the next car

		intersectionCar = car;
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent(MessageType.REQUEST_ACCEPTED.toString());
		msg.addReceiver(car);
		send(msg);
	}

}
