package main;

import java.io.IOException;
import java.util.HashMap;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;


public class CarControllerAgent extends Agent {
	
	HashMap<AID, Car> lista;
	 
	public void setup() {
		addBehaviour(new ListeningBehaviour());
		lista = new HashMap<AID, Car>();
	}
	
	class ListeningBehaviour extends CyclicBehaviour {
		
		public void action() {
			
			ACLMessage msg = receive();
			
			if(msg != null) {
 
				if(msg.getPerformative() == ACLMessage.REQUEST) {
										
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.INFORM); 
					try {
						reply.setContentObject(lista); //Cada carro usa a sua propria informacao para determinar se avanca - provavelmente fica mais fixe se eles falarem uns com os outros com paginas amarelas sem controlador mas dps ve-se isso
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					send(reply);
					
					try {
						
						lista.put(msg.getSender(), (Car)msg.getContentObject() );
						//System.out.println(lista);
									
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
