package main;

import java.util.Random;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;


public class CarSpawner extends Agent {
	
	public static final int SPAWN_TIME = 1;	//s
	public static final int SPAWN_INTERVAL = 1;	//s
	
	private ContainerController container;
	private Map mapa;
	public static int index=0;
	
	public CarSpawner(ContainerController container, Map mapa) {
		this.container = container;
		this.mapa = mapa;
 
	}
	
	public void setup() {
		
		Random r = new Random();
		
		addBehaviour(new WakerBehaviour(this,0) {
			protected void handleElapsedTimeout() {
				
				int result = r.nextInt(4-1) + 1;
 
				Car newCar = new Car(""+result);
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
	
	public void takeDown() {
		
	}
}
