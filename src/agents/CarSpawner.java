package src.agents;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import src.graph.Map;
import src.resources.Messages;
import src.resources.Messages.MessageType;

/*
 * Agent responsible for adding cars to the system
 */
public class CarSpawner extends Agent {
	
	public static final int SPAWN_TIME = 2;	//s
	public static final int SPAWN_INTERVAL = 1;	//s
	
	private ContainerController container;
	private Map mapa;
	public static int index=0;
	
	private ArrayList<ArrayList<String>> starters;
	private int latestAgentChecked;
	
	public CarSpawner(ContainerController container, Map mapa) {
		this.container = container;
		this.mapa = mapa;
		this.starters = new ArrayList<ArrayList<String>>(); 
	}
	
	public void checkIfInitialRoadHasSpace() {
		if(starters.size() > 0) {
			//Obtem um road agent ao calhas de entre aqueles que estao na lista para entrar
			latestAgentChecked = (int) (Math.random()*starters.size());
			String roadAgentName = "RoadAgent" + starters.get(latestAgentChecked).get(0);			
			AID roadAgent = getAID(roadAgentName);
			ACLMessage sendMsg = new ACLMessage(ACLMessage.INFORM);
			sendMsg.setContent(Messages.MessageType.POLL_SPACE.toString());
			sendMsg.addReceiver(roadAgent);
			send(sendMsg);
		}
	}
	
	public class ReceiveMessageBehaviour extends CyclicBehaviour{

		@Override
		public void action() {						
			ACLMessage msg = receive();
			
			if(msg != null) {
				MessageType msgType = Messages.getMessageType(msg.getContent());
				
				switch(msgType) {
					case SPACE_INFO:{
						String status = Messages.getMessageContent(msg.getContent())[0];
						if(status.equals("FREE")){
							if(starters.size() > 0) {
								ArrayList<String> path = starters.get(latestAgentChecked);
								
								Car newCar = new Car(path);
								mapa.addCar(newCar);
								
								AgentController ac4;
								try {
									ac4 = container.acceptNewAgent("car"+index++, newCar);
									ac4.start();
								} catch (StaleProxyException e) {
									e.printStackTrace();
								}
							}
						}
						break;
					}
				}
			}else {
				//block();
			}
		}
			
	}

	
	public void setup() {
		Random rnd = new Random();
		
		addBehaviour(new ReceiveMessageBehaviour());
		
		addBehaviour(new TickerBehaviour(this,400) {

			@Override
			protected void onTick() {
				checkIfInitialRoadHasSpace();
			}
			
		});
		
		addBehaviour(new WakerBehaviour(this,0) {
			protected void handleElapsedTimeout() {
				if(starters.size() > 10) {
					return;
				}
				
				int randomNum = rnd.nextInt(Map.paths.size()-1) + 1;

				ArrayList<String> path = Map.paths.get(""+randomNum);
				
				starters.add(path);
				
				System.out.println("Added car to queue");
				
				this.reset(SPAWN_TIME*1000 + (int)(Math.random() * SPAWN_INTERVAL * 1000));
			}
		});
	}

}
