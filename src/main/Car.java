package main;

import java.io.IOException;
import java.util.HashMap;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class Car extends Agent {

	// public static int id = 0;
	// private int carID; //id
	// private double curVel; //velocity
	// private double velMax;

	public Point location; // location
	public Point velocity;
	public Point size;

	private Point startPoint;
	private Point interPoint;
	private Point stopPoint;
	private String road;

	public Car(String road) {
		
		this.size = new Point(1,1); //No futuro podemos ter carros de tamanho diferente
		
		this.road = road;

		switch (road) {

		case "1":
			this.startPoint = Map.r1StartPoint;
			this.interPoint = Map.r1InterPoint;
			this.stopPoint = Map.r1StopPoint;
			this.velocity = new Point(0.1,0); //Depende da faixa em que ele estiver, daí estar dentro do switch, ok claudia?
			break;
		case "2":
			this.startPoint = Map.r2StartPoint;
			this.interPoint = Map.r2InterPoint;
			this.stopPoint = Map.r2StopPoint;
			this.velocity = new Point(0,-0.1);
		case "3":
			this.startPoint = Map.r3StartPoint;
			this.interPoint = Map.r3InterPoint;
			this.stopPoint = Map.r3StopPoint;
			this.velocity = new Point(-0.1,0);
		case "4":
			this.startPoint = Map.r4StartPoint;
			this.interPoint = Map.r4InterPoint;
			this.stopPoint = Map.r4StopPoint;
			this.velocity = new Point(0,0.1);
		}

		System.out.println("Hello this is mr car");

		this.location = startPoint;
		// this.carID = id++;
	}

	public void setup() {

		//sendPosition(); //Porque enviar uma vez como inform? Tem mesmo de se enviar no inicio do carro?

		addBehaviour(new TickerBehaviour(this, 200) {

			@Override
			protected void onTick() {

				sendPosition();
				
				ACLMessage answer = receive();

				if (answer != null) {

					if (answer.getPerformative() == ACLMessage.INFORM) {
						HashMap<AID, Car> lista = null;
						
						try {
							lista = (HashMap<AID, Car>)answer.getContentObject();
						} catch (UnreadableException e) {
							e.printStackTrace();
						}
						
						boolean canMove = true;

						for (HashMap.Entry<AID, Car> entry : lista.entrySet()) {
							if (entry.getKey() == this.getAgent().getAID()){ //Nao verificar perigo de colisao com ele proprio
								continue;
							}
							
							if(collisionRisk(entry.getValue())) {
								canMove = false;
								break;
							}
						}	
						
						if (location.equals(stopPoint)) { // TODO end
							return;
						}
						
						System.out.println(location);

						if(canMove) {
							location.add(velocity);
						}
					}
				} else {
					block();
				}

			}

		});
	}
	
	public boolean collisionRisk(Car otherCar) {
		double threshold = 0.2; //Minimum space to other cars
		
		//Our collision box
		double Ax1 = 0;
		double Ax2 = 0;
		double Ay1 = 0;
		double Ay2 = 0;
		
		//Other car
		double Bx1 = otherCar.location.x;
		double Bx2 = otherCar.location.x + otherCar.size.x;
		double By1 = otherCar.location.y;
		double By2 = otherCar.location.y + otherCar.size.y;
		
		//IDEIA - Se eles ficarem os dois bloqueados (ambos no bloco de colisao um do outro) falam um com um outro para determinar qual dos dois avanca (é pro 20)
		
		//Calculate size of our collision box
		if(velocity.x > 0) {
			Ax1 = location.x + size.x;
			Ax2 = location.x + size.x + threshold;
			Ay1 = location.y;
			Ay2 = location.y + size.y;
		}else if(velocity.x < 0) {
			Ax1 = location.x - threshold;
			Ax2 = location.x;
			Ay1 = location.y;
			Ay2 = location.y + size.y;
		}else if(velocity.y > 0) {
			Ax1 = location.x;
			Ax2 = location.x + size.x;
			Ay1 = location.y + size.y;
			Ay2 = location.y + size.y + threshold;
		}else if(velocity.y < 0) {
			Ax1 = location.x;
			Ax2 = location.x + size.x;
			Ay1 = location.y - threshold;
			Ay2 = location.y;
		}else {
			System.out.println("Car has 0 velocity, bugged or out of fuel? Probably bugged.");
		}
		
		//Formula para calcular colisoes entre retangulos (https://stackoverflow.com/questions/31022269/collision-detection-between-two-rectangles-in-java)
		if (Ax1 < Bx2 && Ax2 > Bx1 && Ay1 < By2 && Ay2 > By1) {
			return true;
		}else {
			return false;
		}
	}

	public void sendPosition() {

		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);

		try {
			msg.setContentObject(this);
		} catch (IOException e) {
			e.printStackTrace();
		}

		AID dest = this.getAID("CarControllerAgent");
		msg.addReceiver(dest);
		send(msg);
	}

	public void takeDown() {

		System.out.println("Car is done");
	}

}
