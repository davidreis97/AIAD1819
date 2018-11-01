package main;

import java.io.IOException;
import java.util.HashMap;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;


/*
 * Este agente é responsavel por 'guardar' os agentes que se encontram numa interseçao
 * 
 * -->'REQUEST'
 *      - responde com 'INFORM' uma lista com os carros e atualiza a posiçao recebida;
 * -->'FAILURE' 
 *      - um determinado agente já nao se encontra no cruzamento, e é apagado da lista;
 *     
 */

public class IntersectionAgent extends Agent {
	
	HashMap<AID, Point> lista;
	 
	public void setup() {
		
		addBehaviour(new ListeningBehaviour());
		lista = new HashMap<AID, Point>();
	}
	
	class ListeningBehaviour extends CyclicBehaviour {
		
		public void action() {
			
			ACLMessage msg = receive();
			
			if(msg != null) {
 
				if(msg.getPerformative() == ACLMessage.REQUEST) {
			 
					try {
					
						// get car location
						Point location = (Point)msg.getContentObject();
						
						//save
						lista.put(msg.getSender(), location);
						
						//send list
						ACLMessage reply = msg.createReply();
						reply.setPerformative(ACLMessage.INFORM); 
						try {
							reply.setContentObject(lista); 
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						send(reply);
						

					} catch (UnreadableException e2) {
						e2.printStackTrace();
					}
					
				}else if(msg.getPerformative() == ACLMessage.FAILURE) {
					
					//remove car from list
					lista.remove(msg.getSender());
				} 
			} else {
				block();
			}
		}

	}	
	 
}
