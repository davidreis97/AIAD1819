package main;

import java.io.IOException;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class Car extends Agent {

	// public static int id = 0;
	// private int carID; //id
	// private double curVel; //velocity
	// private double velMax;

	public Point location; // location

	private Point startPoint;
	private Point interPoint;
	private Point stopPoint;
	private String road;

	public Car(String road) {

		this.road = road;

		switch (road) {

		case "1":
			this.startPoint = Map.r1StartPoint;
			this.interPoint = Map.r1InterPoint;
			this.stopPoint = Map.r1StopPoint;
			break;
		}

		System.out.println("Hello this is mr car");

		this.location = startPoint;
		// this.carID = id++;
	}

	public void setup() {

		sendPosition();

		addBehaviour(new TickerBehaviour(this, 200) {

			@Override
			protected void onTick() {

				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);

				try {
					msg.setContentObject(location);
				} catch (IOException e) {
					e.printStackTrace();
				}

				AID dest = getAID("CarControllerAgent");
				msg.addReceiver(dest);
				send(msg);
				
				ACLMessage answer = receive();

				if (answer != null) {

					if (answer.getPerformative() == ACLMessage.CONFIRM) {	 

						if (location.equals(stopPoint)) { // TODO end
							return;
						}
						System.out.println(location);

						switch (road) {

						case "1":
							location.setX(location.x + 0.1);
							break;
						}

					}
				} else {
					block();
				}

			}

		});
	}

	public void sendPosition() {

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

		try {
			msg.setContentObject(location);
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
