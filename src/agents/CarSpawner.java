package src.agents;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import src.graph.Intersection;
import src.graph.Map;
import src.graph.Road;
import src.graph.Road.Direction;
import src.resources.CMDArgs;
import src.resources.Messages;
import src.resources.Messages.MessageType;

/*
 * Agent responsible for adding cars to the system
 */
public class CarSpawner extends Agent {
	
	public static final int SPAWN_INTERVAL = CMDArgs.SPAWN_RATE;	//s
	
	private ContainerController container;
	private Map mapa;
	public static int index=0;
	
	private ArrayList<ArrayList<String>> starters;
	private int latestAgentChecked;
	
	private ArrayList<AID> initialPoints;
	
	/*
	 * Constructor
	 */
	public CarSpawner(ContainerController container, Map mapa) {
		this.container = container;
		this.mapa = mapa;
		this.starters = new ArrayList<ArrayList<String>>(); 
	}
	
	
	public void setup() {
		
		this.initialPoints = new ArrayList<>();
		
		//Get the initial points of the map
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("road");
		sd.setName("entranceRoad");
		template.addServices(sd);
		
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			for(int i = 0; i < result.length; i++) {
				initialPoints.add(result[i].getName());
			}
		}catch (FIPAException fe) {
			fe.getStackTrace();
		}

		// Behavior that represents the car spawner receiving the messages
		addBehaviour(new ReceiveMessageBehaviour());
		
		// Behavior that represents the car spawner checking the road space
		addBehaviour(new TickerBehaviour(this,SPAWN_INTERVAL) {

			@Override
			protected void onTick() {
				checkIfInitialRoadHasSpace();
			}
		});
	}

	/*
	 * Receives the space information
	 */
	public class ReceiveMessageBehaviour extends CyclicBehaviour{

		@Override
		public void action() {			
			if(starters.size() < 10) {
				ArrayList<String> path = generateRandomPath();
				
				starters.add(path);
			}
			
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
				block();
			}
		}	
	}
	
	
	/*
	 * Check if the initial road has space
	 */
	public void checkIfInitialRoadHasSpace() {
			
		//choose a random agent waiting 
		latestAgentChecked = (int) (Math.random()*starters.size());
		String roadAgentName = "RoadAgent" + starters.get(latestAgentChecked).get(0);			
		AID roadAgent = getAID(roadAgentName);
			
		ACLMessage sendMsg = new ACLMessage(ACLMessage.INFORM);
		sendMsg.setContent(Messages.MessageType.POLL_SPACE.toString());
		sendMsg.addReceiver(roadAgent);
		send(sendMsg);
	}
	
	
	/*
	 * Generate a random path for the car agents
	 */
	public ArrayList<String> generateRandomPath(){
		
		AID roadAgent = initialPoints.get((int) (Math.random() * initialPoints.size()));
		int roadIndex = Integer.parseInt(roadAgent.getName().split("@")[0].split("RoadAgent")[1]);
		Road currentRoad = Map.roads.get(""+roadIndex);
		Intersection nextIntersection = currentRoad.getIntersection();
		
		ArrayList<String> path = new ArrayList<>();
		path.add(currentRoad.name);
		
		while (nextIntersection != null) {
			currentRoad = nextIntersection.getRandomOutRoad(currentRoad);
			path.add(currentRoad.name);
			nextIntersection = currentRoad.getIntersection();
		}
		
		return path;
	}

}
