package main;

import java.io.IOException;
import java.util.HashMap;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

/*
 * Este agente é responsavel por 'guardar' os agentes que se encontram numa rua
 * 
 * -->'INFORM'
 * 		- apenas atualiza a posicao do carro
 * -->'REQUEST'
 *      - responde com 'INFORM' uma lista com os carros e atualiza a posiçao recebida;
 * -->'FAILURE' 
 *      - um determinado agente já nao se encontra na rua, e é apagado da lista;
 *     
 */

public class RoadAgent extends Agent {

	HashMap<AID, Point> lista;

	public void setup() {

		addBehaviour(new ListeningBehaviour());
		lista = new HashMap<AID, Point>();
	}

	class ListeningBehaviour extends CyclicBehaviour {

		public void action() {

			ACLMessage msg = receive();

			if (msg != null) {

				if (msg.getPerformative() == ACLMessage.REQUEST) {

					try {

						// car location
						Point location = (Point) msg.getContentObject();

						// save
						lista.put(msg.getSender(), location);

						// send list
						ACLMessage reply = msg.createReply();
						reply.setPerformative(ACLMessage.INFORM);
						try {
							reply.setContentObject(lista);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						send(reply);

					} catch (UnreadableException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}

				} else if (msg.getPerformative() == ACLMessage.FAILURE) {

					// remove car
					lista.remove(msg.getSender());
				
				} else if (msg.getPerformative() == ACLMessage.INFORM) {

					// update car
					try {
						Point location = (Point) msg.getContentObject();
						lista.put(msg.getSender(), location);
						
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					
				}

			} else {
				block();
			}
		}

	}

}
