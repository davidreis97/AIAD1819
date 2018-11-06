package src.agents;

import java.util.ArrayList;
import java.util.Random;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import src.graph.Map;

/*
 * Agent responsible for adding cars to the system
 */
public class CarSpawner extends Agent {
	
	public static final int SPAWN_TIME = 100;	//s
	public static final int SPAWN_INTERVAL = 1;	//s
	
	private ContainerController container;
	private Map mapa;
	public static int index=0;
	
	public CarSpawner(ContainerController container, Map mapa) {
		this.container = container;
		this.mapa = mapa;
	}
	
	public void setup() {
		
		Random rnd = new Random();
		
		addBehaviour(new WakerBehaviour(this,0) {
			protected void handleElapsedTimeout() {
				
				int randomNum = rnd.nextInt(Map.paths.size()-1) + 1;

				ArrayList<String> path = Map.paths.get(""+randomNum);
			 
				Car newCar = new Car(path);
				mapa.addCar(newCar);
				
				AgentController ac4;
				try {
					ac4 = container.acceptNewAgent("car"+index++, newCar);
					ac4.start();
				} catch (StaleProxyException e) {
					e.printStackTrace();
				}
				
				this.reset(SPAWN_TIME*1000 + (int)(Math.random() * SPAWN_INTERVAL * 1000));
			}
		});
	}

}