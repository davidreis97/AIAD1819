package main;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;


public class IntersectionAgent extends Agent {

	public enum SelectionAlgorithm {
		FIRST_COME_FIRST_SERVED, COLLISION_DETECTION
	}
	public static final SelectionAlgorithm ALGORITHM = SelectionAlgorithm.FIRST_COME_FIRST_SERVED; 
	
	
	Queue<AID> waitingCars;
	boolean intersectionOccupied = false;
	AID intersectionCar = null;
 
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

				if (msg.getPerformative() == ACLMessage.REQUEST) {

					if (!intersectionOccupied) {

						intersectionOccupied = true;
						intersectionCar = msg.getSender();

						ACLMessage reply = msg.createReply();
						reply.setPerformative(ACLMessage.INFORM);
						send(reply);

					} else {

						if (!msg.getSender().equals(intersectionCar) && !waitingCars.contains(msg.getSender())) {
							
							waitingCars.add(msg.getSender());
						}
					}

				} else if (msg.getPerformative() == ACLMessage.FAILURE) {

					if (msg.getSender().equals(intersectionCar)) {
						
						intersectionOccupied = false;

						if (!waitingCars.isEmpty()) {
							intersectionCar = waitingCars.poll();

							intersectionOccupied = true;

							ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
							msg2.addReceiver(intersectionCar);
							send(msg2);

						}
					}
				}
			} else {
				block();
			}
		}
	}

}
