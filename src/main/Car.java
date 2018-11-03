package main;

import java.io.IOException;
import java.util.HashMap;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.lang.acl.MessageTemplate;

/* * * * * * * * * * * * * PARA O DAVID LER  * * * * * * * * * * * * * * 
 * 
 *  A ideia é: 
 *  Um carro entra numa rua, informa o RoadAgent que entrou atraves de 'SUBSCRIBE'
 * 	Recebe informacao se tem algum carro á frente através de 'CONFIRM'
 *  O carro da sua frente entretanto recebe um 'PROPOSE' informando que agora tem um carro atras dele.
 *  Assim, sempre que ele se move, manda-lhe a sua nova posição atraves de 'PROPAGATE'
 *  
 *  Um carro antes de andar ve se vai contra o da frente e manda a sua posiçao para o de tras, para ele
 *  conseguir fazer o mesmo.
 *  
 *  Assim, nao temos de tar sempre a verificar colisoes com todos os carros da rua, quando obviamente
 *  so iamos bater no que esta a nossa frente.
 *  
 *  A interseçao para ja continua a funcionar da mesma forma. Tem uma lista com todos os carros que la estao.
 *  Acho que podemos melhorar com algoritmos de seleçao, e assim e tb o que falaste de se ambos tiverem bloqueados,
 *  comunicar um com o outro para decidir, pq ainda ha acidentes.
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

public class Car extends Agent {

	public Point location; // location
	public Point velocity; // velocity
	public Point size; // car size

	/* Road that the car will travel */
	private Point startPoint;
	private Point interPoint;
	private Point stopPoint;
	private String road;

	private AID frontCar, backCar;
	private Point frontCar_position;

	private boolean waitingIntersection;
	private boolean inIntersection;

	public Car(String road) {

		this.frontCar = null;
		this.backCar = null;
		this.frontCar_position = null;

		this.size = new Point(1, 1);
		this.road = road;

		this.waitingIntersection = false;
		this.inIntersection = false;

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

		addBehaviour(new InitBehaviour());

		// Behavior that represents the car moving

		addBehaviour(new TickerBehaviour(this, 100) {

			@Override
			protected void onTick() {

				// send my position to the back car
				if (backCar != null) {
					sendPosition(backCar, ACLMessage.PROPAGATE);
				}

				boolean canMove = false;
				
				if(inIntersection()) {
					
					if(!inIntersection && !waitingIntersection) {	//first time here
						waitingIntersection = true;
						addBehaviour(new WaitingIntersection());
					}
					else if(inIntersection){
						canMove = true;
					}
					
				}  else {

					// see if I can move (front car)
					if (frontCar != null && frontCar_position != null) {

						canMove = !collisionRisk(frontCar_position);

					} else {
						canMove = true;
					}
				}

				if (canMove) {
					location.add(velocity);
				}

				if (!inIntersection() && inIntersection) {
					removeCar("IntersectionAgent");
					inIntersection = false;
				}

				if (isOutOfBounds()) {
					this.myAgent.doDelete();
					return;
				}

			}

		});

		addBehaviour(new BackCar());
		addBehaviour(new FrontCarPosition());
		addBehaviour(new BackCarEnd());
	}

	
	class WaitingIntersection extends Behaviour {

		public void action() {

			AID agent = getAID("IntersectionAgent");
			sendPosition(agent, ACLMessage.REQUEST); // manda request a perguntar se pode entrar na rua

			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage answer = receive(mt);

			if (answer != null) {
		
				inIntersection = true;

			} else {
				block();
			}
		}

		public boolean done() {
			return inIntersection == true;
		}
	}

	/*
	 * Subscribe the road and receives the car in front. O behaviour acaba logo, é
	 * so para iniciar
	 */
	class InitBehaviour extends Behaviour {

		boolean received = false;

		public void action() {

			ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
			msg.addReceiver(getAID("RoadAgent" + road));
			send(msg);

			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);

			ACLMessage msg2 = receive(mt);
			if (msg2 != null) {

				AID response;
				try {
					response = (AID) msg2.getContentObject();

					if (response != null) { // pode ser null se n tiver carro a frente
						frontCar = response;
					}

				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				received = true;

			} else {
				block();
			}
		}

		public boolean done() {
			return received == true;
		}
	}

	// Informaram q tem um carro atras
	class BackCar extends CyclicBehaviour {

		public void action() {

			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
			ACLMessage msg = receive(mt);

			if (msg != null) {

				AID response;
				try {

					response = (AID) msg.getContentObject();
					backCar = response;

				} catch (UnreadableException e) {
					e.printStackTrace();
				}

			} else {
				block();
			}
		}
	}

	// Informaram q carro da frente ja terminou percurso
	class BackCarEnd extends CyclicBehaviour {

		public void action() {

			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.FAILURE);
			ACLMessage msg = receive(mt);

			if (msg != null) {
				frontCar = null;
			} else {
				block();
			}
		}
	}

	// Recebendo posicao do carro da frente
	class FrontCarPosition extends CyclicBehaviour {

		public void action() {

			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE);
			ACLMessage msg = receive(mt);

			if (msg != null) {

				Point location;
				try {
					location = (Point) msg.getContentObject();
					frontCar_position = location;

				} catch (UnreadableException e) {

					e.printStackTrace();
				}

			} else {
				block();
			}
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
	public void sendPosition(AID agent, int type) {

		ACLMessage msg = new ACLMessage(type);

		try {
			msg.setContentObject(location);
		} catch (IOException e) {
			e.printStackTrace();
		}

		msg.addReceiver(agent);
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
