package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.lang.acl.MessageTemplate;

public class Car extends Agent {

	public Point location; // location
	public Point velocity; // velocity
	public Point size; // car size

	/* Road that the car will travel */
	private Point startPoint;
	private Point interPoint;
	private Point stopPoint;
	private String road;
	private boolean moving;
	private ArrayList<AID> waitingCars;

	public Car(String road) {

		this.size = new Point(1, 1);
		this.road = road;
		this.moving = true;
		this.waitingCars = new ArrayList<AID>();

		switch (road) {

		case "1":
			this.startPoint = new Point(Map.r1StartPoint);
			this.interPoint = new Point(Map.r1InterPoint);
			this.stopPoint = new Point(Map.r1StopPoint);
			this.velocity = new Point(0.1, 0);
			break;
		case "2":
			this.startPoint = new Point(Map.r2StartPoint);
			this.interPoint = new Point(Map.r2InterPoint);
			this.stopPoint = new Point(Map.r2StopPoint);
			this.velocity = new Point(0, -0.1);
			break;
		case "3":
			this.startPoint = new Point(Map.r3StartPoint);
			this.interPoint = new Point(Map.r3InterPoint);
			this.stopPoint = new Point(Map.r3StopPoint);
			this.velocity = new Point(-0.1, 0);
			break;
		case "4":
			this.startPoint = new Point(Map.r4StartPoint);
			this.interPoint = new Point(Map.r4InterPoint);
			this.stopPoint = new Point(Map.r4StopPoint);
			this.velocity = new Point(0, 0.1);
			break;
		}

		this.location = startPoint;

		System.out.println("Hello this is mr car, in road: " + road);
	}

	public void setup() {

		addBehaviour(new TickerBehaviour(this, 100) {

			@Override
			protected void onTick() {

				if (!moving) {
					return ;
				}

				boolean inIntersection = false;

				if (inIntersection()) {

					inIntersection = true;
					sendPosition("IntersectionAgent", ACLMessage.REQUEST);
					sendPosition("RoadAgent" + road, ACLMessage.INFORM);

				} else {

					sendPosition("RoadAgent" + road, ACLMessage.REQUEST);
				}

				ACLMessage answer = receive();

				if (answer != null) {

					if (answer.getPerformative() == ACLMessage.INFORM) {

						HashMap<AID, Point> lista = null;

						try {
							lista = (HashMap<AID, Point>) answer.getContentObject();

						} catch (UnreadableException e) {
							e.printStackTrace();
						}

						boolean canMove = true;

						// check collisions
						for (HashMap.Entry<AID, Point> entry : lista.entrySet()) {

							if (entry.getKey().equals(this.getAgent().getAID())) {
								continue;
							}

							if (collisionRisk(entry.getValue())) {
								canMove = false;

								/*
								 * IDEIA: Se nao se encontra numa intersecao avisa o carro da frente q esta a
								 * espera q ele ande assim n precisa de estar sempre a perguntar ao roadAgent
								 */

								if (!inIntersection) {

									System.out.println("waiting!!");
									ACLMessage msg = new ACLMessage(ACLMessage.REQUEST_WHEN);
									AID dest = entry.getKey();
									msg.addReceiver(dest);
									send(msg);
									moving = false;
									addBehaviour(new Cenas2());
								}

								break;
							}
						}

						if (canMove) {
							location.add(velocity);

							// Send a message to each car, saying that i moved
							if (!waitingCars.isEmpty()) {
								for (AID car : waitingCars) {

									System.out.println("an agent is no longer waiting" + car);

									ACLMessage msg = new ACLMessage(ACLMessage.PROPAGATE);
									msg.addReceiver(car);
									send(msg);
								}
							}
							waitingCars.clear();
						}

						if (isOutOfBounds()) {
							this.myAgent.doDelete();
							return;
						}

						if (inIntersection && !inIntersection()) {
							removeCar("IntersectionAgent");
						}

					}  

				} else {
					block();
				}

			}

		});

		addBehaviour(new Cenas());

	}

	class Cenas extends CyclicBehaviour {

		public void action() {

			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST_WHEN);
			ACLMessage msg = receive(mt);

			if (msg != null) {
				
				// Add to cars waiting for me to move
				waitingCars.add(msg.getSender());
				System.out.println("an agent is waiting" + msg.getSender());
				 
			}
			else {
				block();
			}
		}
	}
	
	class Cenas2 extends Behaviour {
		
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE);
			ACLMessage msg = receive(mt);
			if (msg != null) {
				if (msg.getPerformative() == ACLMessage.PROPAGATE) {
					System.out.println("moving!!");
					moving = true;
				}
			} else {
				block();
			}
		}

		public boolean done() {
			return moving == true;
		}
		
	}

	/*
	 * Checks if the car is out of bounds
	 */
	public boolean isOutOfBounds() {
		if (location.x > 10 || location.x < 0 || location.y > 10 || location.y < 0) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Checks if the car is in the intersection
	 */
	public boolean inIntersection() {

		double threshold = 0.4;

		double Ax1 = 0;
		double Ax2 = 0;
		double Ay1 = 0;
		double Ay2 = 0;

		double Bx1 = Map.intersectionP.x;
		double Bx2 = Map.intersectionP.x + 2;
		double By1 = Map.intersectionP.y;
		double By2 = Map.intersectionP.y + 2;

		if (velocity.x > 0) {
			Ax1 = location.x;
			Ax2 = location.x + size.x + threshold;
			Ay1 = location.y;
			Ay2 = location.y + size.y;
		} else if (velocity.x < 0) {
			Ax1 = location.x - threshold;
			Ax2 = location.x + size.x;
			Ay1 = location.y;
			Ay2 = location.y + size.y;
		} else if (velocity.y > 0) {
			Ax1 = location.x;
			Ax2 = location.x + size.x;
			Ay1 = location.y;
			Ay2 = location.y + size.y + threshold;
		} else if (velocity.y < 0) {
			Ax1 = location.x;
			Ax2 = location.x + size.x;
			Ay1 = location.y - threshold;
			Ay2 = location.y + size.y;
		} else {
			System.out.println("Car has 0 velocity, bugged or out of fuel? Probably bugged.");
		}

		if (Ax1 < Bx2 && Ax2 > Bx1 && Ay1 < By2 && Ay2 > By1) {
			return true;
		}
		return false;

	}

	/*
	 * Checks the collision of a car to another
	 */
	public boolean collisionRisk(Point otherCarLocation) {

		double threshold = 0.2; // Minimum space to other cars

		// Our collision box
		double Ax1 = 0;
		double Ax2 = 0;
		double Ay1 = 0;
		double Ay2 = 0;

		/*
		 * if (otherCarLocation.x > 10 || otherCarLocation.x < 0 || otherCarLocation.y >
		 * 10 || otherCarLocation.y < 0) { return false; }
		 */

		// Other car
		double Bx1 = otherCarLocation.x;
		double Bx2 = otherCarLocation.x + 1;
		double By1 = otherCarLocation.y;
		double By2 = otherCarLocation.y + 1;

		// IDEIA - Se eles ficarem os dois bloqueados (ambos no bloco de colisao um do
		// outro)
		// falam um com um outro para determinar qual dos dois avanca (é pro 20)

		// Calculate size of our collision box
		if (velocity.x > 0) {
			Ax1 = location.x + size.x;
			Ax2 = location.x + size.x + threshold;
			Ay1 = location.y;
			Ay2 = location.y + size.y;
		} else if (velocity.x < 0) {
			Ax1 = location.x - threshold;
			Ax2 = location.x;
			Ay1 = location.y;
			Ay2 = location.y + size.y;
		} else if (velocity.y > 0) {
			Ax1 = location.x;
			Ax2 = location.x + size.x;
			Ay1 = location.y + size.y;
			Ay2 = location.y + size.y + threshold;
		} else if (velocity.y < 0) {
			Ax1 = location.x;
			Ax2 = location.x + size.x;
			Ay1 = location.y - threshold;
			Ay2 = location.y;
		} else {
			System.out.println("Car has 0 velocity, bugged or out of fuel? Probably bugged.");
		}

		// Formula para calcular colisoes entre retangulos
		// (https://stackoverflow.com/questions/31022269/collision-detection-between-two-rectangles-in-java)
		if (Ax1 < Bx2 && Ax2 > Bx1 && Ay1 < By2 && Ay2 > By1) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Sends the car position to another agent
	 */
	public void sendPosition(String agent, int type) {

		ACLMessage msg = new ACLMessage(type);

		try {
			msg.setContentObject(location);
		} catch (IOException e) {
			e.printStackTrace();
		}

		AID dest = this.getAID(agent);
		msg.addReceiver(dest);
		send(msg);
	}

	/*
	 * Sends the message to remove the agent from a list
	 */
	public void removeCar(String agent) {

		ACLMessage msg = new ACLMessage(ACLMessage.FAILURE);

		AID dest = this.getAID(agent);
		msg.addReceiver(dest);
		send(msg);
	}

	/*
	 * Deletes an agent
	 */
	public void takeDown() {

		removeCar("RoadAgent" + this.road);

		System.out.println("Car is done");
	}

}
