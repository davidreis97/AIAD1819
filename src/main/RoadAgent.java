package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

/*
 * Este agente é responsavel por 'guardar' os agentes que se encontram numa rua
 * 
 * -->'SUBSCRIBE'
 *      - adiciona carro a lista, responde com carro da frente, e manda ao carro da frente o carro de tras
 * -->'FAILURE' 
 *      - um determinado agente já nao se encontra na rua, e é apagado da lista;
 *     
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

				if (msg.getPerformative() == ACLMessage.SUBSCRIBE) {

					if (!carros.contains(msg.getSender()))
						carros.add(msg.getSender());

					
					// check if the car has a car in front of him
					AID front_car = null;

					int index = carros.indexOf(msg.getSender());
					if (index != 0) {
						front_car = carros.get(index - 1);
					}

					/*
					 * Informa este carro que tem um a frente e o da frente q tem um atras
					 */

					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.CONFIRM);
					try {
						reply.setContentObject(front_car);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					send(reply);

					if (index != 0) {	//inform the front car

						ACLMessage new_msg = new ACLMessage(ACLMessage.PROPOSE);

						try {
							new_msg.setContentObject(msg.getSender());
						} catch (IOException e) {
							e.printStackTrace();
						}

						new_msg.addReceiver(front_car);
						send(new_msg);

					}

				} else if (msg.getPerformative() == ACLMessage.FAILURE) {

					// Informa o carro de tras que ja nao tem carro a frente

					if (carros.size() >= 2) {

						int index = carros.indexOf(msg.getSender());
						AID backCar = carros.get(index + 1);

						ACLMessage new_msg = new ACLMessage(ACLMessage.FAILURE);
						new_msg.addReceiver(backCar);
						send(new_msg);
					}

					// remove car
					carros.remove(msg.getSender());

				}

			} else {
				block();
			}
		}

	}

}
