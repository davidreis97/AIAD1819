package src.agents;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import src.resources.Messages;
import src.resources.Messages.*;

/*
 * Agent that represents a road. 
 */
public class RoadAgent extends Agent {

	ArrayList<AID> carros;
	Queue<AID> intersecoes;
	
	private int maxCars;
	private int waitingForCars;

	public RoadAgent(int maxCars) {
		super();
		this.maxCars = maxCars;
		this.waitingForCars = 0;
	}

	public void setup() {		
		addBehaviour(new ListeningBehaviour());
		carros = new ArrayList<AID>();
		intersecoes = new LinkedList<AID>();
	}

	class ListeningBehaviour extends CyclicBehaviour {

		public void action() {

			ACLMessage msg = receive();

			if (msg != null) {

				MessageType type = Messages.getMessageType(msg.getContent());
				switch (type) {
					case SUBSCRIBE: {
						handleSubscribe(msg);
						break;
					}
					case UNSUBSCRIBE: {
						handleUnsubscribe(msg);
						break;
					}
					case POLL_SPACE:{
						intersecoes.add(msg.getSender());
						break;
					}
					default: {
						break;
					}
				}
				
				checkEmptySpace();
			} else {
				block();
			}
		}
	}
	
	public void checkEmptySpace() {
		if(carros.size() + waitingForCars < maxCars) {
			AID inter = intersecoes.poll();
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(inter);
			msg.setContent(Messages.buildHasSpaceMessage("FREE"));
			send(msg);
			waitingForCars++;
		}
	}

	public void handleSubscribe(ACLMessage msg) {

		if (waitingForCars > 0) waitingForCars--;
		
		// add car to the list
		if (!carros.contains(msg.getSender()))
			carros.add(msg.getSender());

		// check if the car has a car in front of him

		int index = carros.indexOf(msg.getSender());
		if (index != 0) {
			AID front_car = carros.get(index - 1);

			// inform this car that he has a car in front of him

			String message = Messages.buildFrontCarMessage(front_car);

			ACLMessage reply = msg.createReply();
			reply.setPerformative(ACLMessage.INFORM);
			reply.setContent(message);
			send(reply);

			// and the other that he has a car at back
			message = Messages.buildBackCarMessage(msg.getSender());
			sendMessage(message, front_car);

		}
	}

	public void handleUnsubscribe(ACLMessage msg) {

		// inform the back car that he no longer has a front car

		if (carros.size() >= 2) {
			try {

			int index = carros.indexOf(msg.getSender());
			AID backCar = carros.get(index + 1);

			String message = Messages.buildFrontCarMessage(null);
			sendMessage(message, backCar);
			}catch(Exception e){}
		}

		// remove car
		carros.remove(msg.getSender());

	}

	public void sendMessage(String message, AID car) {
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(car);
		msg.setContent(message);
		send(msg);
	}

}
