package src.agents;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import src.resources.Messages.MessageType;
import src.resources.Rectangle;
import src.graph.Map;
import src.graph.Road;
import src.resources.Messages;

/*
 * Agent that represents an intersection. 
 */
public class IntersectionAgent extends Agent {

	private int index;

	// Selection Algorithms
	public enum SelectionAlgorithm {

		FIRST_COME_FIRST_SERVED, COLLISION_DETECTION, RANDOM_NEXT
	}

	// Chosen algorithm
	protected static final SelectionAlgorithm ALGORITHM = SelectionAlgorithm.RANDOM_NEXT;

	// Cars waiting in the intersection
	protected LinkedList<SimpleEntry<AID, String>> waitingCars;

	// RANDOM_NEXT and FIRST_COME_FIRST_SERVED algorithms variables
	protected boolean intersectionOccupied = false;
	protected AID intersectionCar = null;
	protected LinkedList<SimpleEntry<AID, String>> confirmationReceived;

	// COLLISION_DETECTION algorithm variables
	protected HashMap<AID, Rectangle> waitingCarsArea;
	protected HashMap<AID, Rectangle> intersectionRectangles;
	protected boolean nextRoadOccupied = false;

	public IntersectionAgent(int index) {
		super();
		this.index = index;
	}

	public void setup() {

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("intersection");
		sd.setName("intersection" + index);
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		this.waitingCars = new LinkedList<SimpleEntry<AID, String>>();

		if (ALGORITHM == SelectionAlgorithm.FIRST_COME_FIRST_SERVED) {
			addBehaviour(new FirstComeFirstServedBehaviour(this, 100));

		} else if (ALGORITHM == SelectionAlgorithm.RANDOM_NEXT) {

			this.confirmationReceived = new LinkedList<SimpleEntry<AID, String>>();
			addBehaviour(new RandomNextBehaviour(this, 10));

		} else if (ALGORITHM == SelectionAlgorithm.COLLISION_DETECTION) {

			this.waitingCarsArea = new HashMap<AID, Rectangle>();
			this.intersectionRectangles = new HashMap<AID, Rectangle>();

			addBehaviour(new CollisionDetectionBehaviour(this, 10));
		}
	}

	// First Come First Served
	class FirstComeFirstServedBehaviour extends TickerBehaviour {

		public FirstComeFirstServedBehaviour(Agent a, long period) {
			super(a, period);
		}

		public void onTick() {

			ACLMessage msg = receive();

			if (msg != null) {

				MessageType type = Messages.getMessageType(msg.getContent());
				switch (type) {

				case REQUEST_INTERSECTION: {

					handleSubscribe(msg);
					chooseCarToGo();
					break;
				}
				case UNSUBSCRIBE: {
					handleUnsubscribe(msg);
					break;
				}
				case SPACE_INFO: {

					SimpleEntry<AID, String> car = waitingCars.pollFirst();
					acceptCar(car.getKey());

					break;
				}

				default: {
					System.out.println(msg.getContent());
					break;
				}
				}
			}
		}
	}

	// Random next
	class RandomNextBehaviour extends TickerBehaviour {

		public RandomNextBehaviour(Agent a, long period) {
			super(a, period);
		}

		@Override
		public void onTick() {

			ACLMessage msg = receive();

			if (msg != null) {

				MessageType type = Messages.getMessageType(msg.getContent());
				switch (type) {

				case REQUEST_INTERSECTION: {
					handleSubscribe(msg);
					break;
				}
				case UNSUBSCRIBE: {
					handleUnsubscribe(msg);
					break;
				}

				case SPACE_INFO: {
					handleSpaceInfo(msg);
					break;
				}

				default: {
					System.out.println(msg.getContent());
					break;
				}
				}
			}
		}
	}

	// Collision detection
	class CollisionDetectionBehaviour extends TickerBehaviour {

		public CollisionDetectionBehaviour(Agent a, long period) {
			super(a, period);
		}

		public void onTick() {

			ACLMessage msg = receive();

			checkNextRoadOccupied();

			if (msg != null) {

				MessageType type = Messages.getMessageType(msg.getContent());
				switch (type) {

				case REQUEST_INTERSECTION: {
					handleSubscribe2(msg);
					break;
				}
				case UNSUBSCRIBE: {

					// remove car area
					intersectionRectangles.remove(msg.getSender());

					break;
				}
				case SPACE_INFO: {

					String status = Messages.getMessageContent(msg.getContent())[0];
					if (status.equals("FREE")) {
						nextRoadOccupied = false;
						checkCarsInQueue2(msg.getSender().getName().split("@")[0]);
					} else if (status.equals("FULL")) {
						nextRoadOccupied = true;
					}
					break;
				}
				default: {
					break;
				}
				}
			}
		}
	}

	/*
	 * [First Come First Served] Chooses the next car to enter the intersection 
	 */
	public void chooseCarToGo() {

		if (intersectionOccupied)
			return;

		if (waitingCars.size() == 0)
			return;

		// Sends the space request for this car
		SimpleEntry<AID, String> nextCar = waitingCars.getFirst();
		String roadAgentName = nextCar.getValue();

		AID roadAgent = getAID(roadAgentName);
		ACLMessage sendMsg = new ACLMessage(ACLMessage.INFORM);
		sendMsg.setContent(Messages.MessageType.POLL_SPACE.toString());
		sendMsg.addReceiver(roadAgent);
		send(sendMsg);

		intersectionOccupied = true;
	}

	
	/*
	 * [Collision detection] Asks the road agent if he has space to another car
	 */
	private void checkNextRoadOccupied() {

		if (waitingCars.size() > 0) {
			AID roadAgent = getAID(waitingCars.peek().getValue());
			ACLMessage sendMsg = new ACLMessage(ACLMessage.INFORM);
			sendMsg.setContent(Messages.MessageType.POLL_SPACE.toString());
			sendMsg.addReceiver(roadAgent);
			send(sendMsg);
		}
	}

	/*
	 * [Collision detection] When have space in the road, get a car from the waiting list
	 * and check the collision to the other cars in the intersection
	 */
	private void checkCarsInQueue2(String roadAgent) {

		if (!nextRoadOccupied && waitingCars.size() > 0) {
			for (SimpleEntry<AID, String> entry : waitingCars) {
				if (entry.getValue().equals(roadAgent)) {

					boolean accept = true;

					// check collision boxes
					for (AID aiad : intersectionRectangles.keySet()) {

						Rectangle rect = intersectionRectangles.get(aiad);

						if (waitingCarsArea.get(entry.getKey()).intersects(rect)) {
							accept = false;
						}
					}

					if (accept == true) {

						acceptCar(entry.getKey());

						intersectionRectangles.put(entry.getKey(), waitingCarsArea.get(entry.getKey()));

						waitingCarsArea.remove(entry.getKey());

					}

					return; 
				}
			}
		}
	}

	
	/*
	 * [Collision detection] Subscribe adds the cars and the area they will occupy in the 
	 * intersection to the waiting list
	 */
	private void handleSubscribe2(ACLMessage msg) {

		if (!inWaitingCars(msg.getSender())) {

			String roadAgentName = msg.getContent().split(Messages.SEPARATOR)[1];

			// add car to waiting list
			waitingCars.add(new SimpleEntry<AID, String>(msg.getSender(), roadAgentName));

			String currentRoad = msg.getContent().split(Messages.SEPARATOR)[2];
			String nextRoad = msg.getContent().split(Messages.SEPARATOR)[3];

			Road r1 = Map.roads.get(currentRoad);

			Rectangle rec = r1.getIntersection().getAreaOccupied(currentRoad, nextRoad);

			// add car area to waiting list;
			waitingCarsArea.put(msg.getSender(), rec);

		}
	}

	/*
	 * [Random Next] Return the cars in the waiting list that want to go to a certain road
	 */
	public ArrayList<AID> findCarInStreet(String roadAgent) {

		ArrayList<AID> carros = new ArrayList<AID>();

		for (SimpleEntry<AID, String> entry : waitingCars) {
			if (entry.getValue().equals(roadAgent)) {
				carros.add(entry.getKey());
			}
		}

		if (carros.size() == 0) {
			System.out.println("WARNING RECEIVED GO AHEAD FOR UNREQUESTED ROAD");
		}

		return carros;
	}

	/*
	 * [Random Next] When receiving a space information, if the intersection is not occupied,
	 * choose a random car that was waiting for that road to have space. If is
	 * occupied, save that information for later.
	 */
	private void handleSpaceInfo(ACLMessage msg) {

		ArrayList<AID> carros = findCarInStreet(msg.getSender().getName().split("@")[0]);

		if (!intersectionOccupied) {

			AID car = carros.get(new Random().nextInt(carros.size()));
			acceptCar(car);

		} else {

			for (AID car : carros) {
				SimpleEntry<AID, String> entry = new SimpleEntry<AID, String>(car,
						msg.getSender().getName().split("@")[0]);

				boolean alreadySaved = false;

				for (int i = 0; i < confirmationReceived.size(); i++) {
					if (confirmationReceived.get(i).getKey().equals(car)) {
						alreadySaved = true;
						break;
					}
				}

				if (!alreadySaved) {
					confirmationReceived.add(entry);
				}
			}
		}
	}
	
	/*
	 * Checks if a car is in the waiting list 
	 */
	private boolean inWaitingCars(AID car) {
		for (SimpleEntry<AID, String> se : waitingCars) {
			if (se.getKey().equals(car)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Accept car request to enter an intersection, or add the car to the waiting
	 * list
	 */
	private void handleSubscribe(ACLMessage msg) {

		// Add the car to the waiting list
		if (!msg.getSender().equals(intersectionCar) && !inWaitingCars(msg.getSender())) {

			String roadAgentName = msg.getContent().split(Messages.SEPARATOR)[1];
			SimpleEntry<AID, String> entry = new SimpleEntry<AID, String>(msg.getSender(), roadAgentName);
			waitingCars.add(entry);

			// Sends the request for space
			if (ALGORITHM == SelectionAlgorithm.RANDOM_NEXT) {

				AID roadAgent = getAID(roadAgentName);
				ACLMessage sendMsg = new ACLMessage(ACLMessage.INFORM);
				sendMsg.setContent(Messages.MessageType.POLL_SPACE.toString());
				sendMsg.addReceiver(roadAgent);
				send(sendMsg);
			}
		}
	}

	/*
	 * Remove agent from intersection and notify the next waiting car that he can
	 * now enter the intersection
	 */
	private void handleUnsubscribe(ACLMessage msg) {

		if (msg.getSender().equals(intersectionCar)) {
			System.out.println("Car " + msg.getSender().getName().split("@")[0] + " left intersection");

			intersectionOccupied = false;

			if (ALGORITHM == SelectionAlgorithm.FIRST_COME_FIRST_SERVED) {

				// choose the next car to enter the intersection
				chooseCarToGo();
			}

			else if (ALGORITHM == SelectionAlgorithm.RANDOM_NEXT) {

				// check if other cars got space confirmation meanwhile.
				// if so, choose a random one
				// the others have to send the space request again (outdated information)

				if (confirmationReceived.size() > 0) {

					AID nextCar = null;

					int result = new Random().nextInt(confirmationReceived.size());

					SimpleEntry<AID, String> entry = confirmationReceived.get(result);
					nextCar = entry.getKey();

					acceptCar(nextCar);

					for (SimpleEntry<AID, String> aux : confirmationReceived) {

						if (aux.getKey() != nextCar) {

							AID roadAgent = getAID(aux.getValue());
							ACLMessage sendMsg = new ACLMessage(ACLMessage.INFORM);
							sendMsg.setContent(Messages.MessageType.POLL_SPACE.toString());
							sendMsg.addReceiver(roadAgent);
							send(sendMsg);
						}
					}

					// clear the list
					confirmationReceived.clear();

				}
			}

		} else {
			System.out.println("Received unsubscribe from unknown car");
		}
	}

	/*
	 * Sends the accept message to an agent to enter the intersection
	 */
	public void acceptCar(AID car) {

		String road = null;

		for (int i = 0; i < waitingCars.size(); i++) {
			if (waitingCars.get(i).getKey().equals(car)) {
				road = waitingCars.get(i).getValue();
				waitingCars.remove(i);
				break;
			}
		}

		if (ALGORITHM == SelectionAlgorithm.FIRST_COME_FIRST_SERVED) {

			intersectionCar = car;

		} else if (ALGORITHM == SelectionAlgorithm.RANDOM_NEXT) {

			intersectionCar = car;
			intersectionOccupied = true;

			// inform road agent, will have another car
			AID roadAgent = getAID(road);
			ACLMessage sendMsg = new ACLMessage(ACLMessage.INFORM);
			sendMsg.setContent("NEW_CAR");
			sendMsg.addReceiver(roadAgent);
			send(sendMsg);

		}

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent(MessageType.REQUEST_ACCEPTED.toString());
		msg.addReceiver(car);
		send(msg);
	}

}
