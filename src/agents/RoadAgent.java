package src.agents;

import java.util.ArrayList;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import src.resources.Messages;
import src.resources.Messages.*;

/*
 * Agent that represents a road. 
 */
public class RoadAgent extends Agent {

	ArrayList<AID> carros;

	public void setup() {

		addBehaviour(new ListeningBehaviour());
		carros = new ArrayList<AID>();
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
				default: {
					break;
				}
				}
			} else {
				block();
			}
		}
	}

	public void handleSubscribe(ACLMessage msg) {

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

			int index = carros.indexOf(msg.getSender());
			AID backCar = carros.get(index + 1);

			String message = Messages.buildFrontCarMessage(null);
			sendMessage(message, backCar);
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
