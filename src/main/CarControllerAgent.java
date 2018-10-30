package main;

import java.util.HashMap;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;


public class CarControllerAgent extends Agent {
	
	HashMap<AID, Point> lista;
	 
	public void setup() {
		addBehaviour(new ListeningBehaviour());
		lista = new HashMap<AID, Point>();
	}
	
	class ListeningBehaviour extends CyclicBehaviour {
		
		public void action() {
			
			ACLMessage msg = receive();
			
			
			if(msg != null) {
 
				if(msg.getPerformative() == ACLMessage.INFORM) {
						
					try {
							
						lista.put(msg.getSender(), (Point)msg.getContentObject());
									
					} catch (UnreadableException e) {
						
						e.printStackTrace();
					}
					
				}
				else if(msg.getPerformative() == ACLMessage.REQUEST) {
					
					//ver se nenhum carro a frente atraves da lista TODO 
					
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.CONFIRM); 
					send(reply);
					
					try {
						
						lista.put(msg.getSender(), (Point)msg.getContentObject() );
						System.out.println(lista);
									
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
