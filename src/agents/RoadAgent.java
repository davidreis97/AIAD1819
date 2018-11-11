package src.agents;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import src.resources.Messages;
import src.resources.Messages.*;

/*
 * Agent that represents a road. 
 */
public class RoadAgent extends Agent {

	ArrayList<AID> carros;			//Cars in the road
	Queue<AID> intersecoes;			//Intersections waiting for space
		
	private int maxCars;			//Max cars in the road
 	private int waitingForCars;		//Cars that are allocated to enter the road

	public RoadAgent(int maxCars) {
		super();
		this.maxCars = maxCars;
		this.waitingForCars = 0;
	}

	public void setup() {		
	
		//Behaviour that represents the road agent receiving the messages
		addBehaviour(new ListeningBehaviour());
		
		carros = new ArrayList<AID>();
		intersecoes = new LinkedList<AID>();
	}
	
	
	/*
	 * Behaviour that receives the messages
	 */
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
						
						if(! intersecoes.contains(msg.getSender())) {
							intersecoes.add(msg.getSender());
						}
						break;
					}
					case NEW_CAR:{
						waitingForCars++;
						break;
					}
					default: {
						break;
					}
				}
				
				//check if the road has space to ohter cars
				checkEmptySpace();
				
			} else {
				block();
				
			}

		}
	}
 
	/*
	 * If the road has space, send that information to the intersections waiting
	 */
	public void checkEmptySpace() {
		
		if(carros.size() + waitingForCars < maxCars) {
			AID inter = intersecoes.poll();
			
			String content = Messages.buildHasSpaceMessage("FREE");
			sendMessage(content, inter);
 
		}
	}

	/*
	 * Adds a new car to the cars list. Informs the car if he has another car in front of him, and the other car
	 * that he has a car behind him.
	 */
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

	/*
	 * Removes the car from the cars list. Also informs the car in from of him that he no longer has a back car.
	 */
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

	/*
	 * Sends an ACL message
	 */
	public void sendMessage(String message, AID car) {
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(car);
		msg.setContent(message);
		send(msg);
	}

}
