package main;

import java.util.ArrayList;
import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class CarSpawner extends Agent {
	
	private ContainerController container;
	private Map mapa;
	public static int index=0;

	
	public CarSpawner(ContainerController container, Map mapa) {
		this.container = container;
		this.mapa = mapa;
	}
	
	public void setup() {
		

		addBehaviour(new WakerBehaviour(this,0) {
			protected void handleElapsedTimeout() {
				
				Random r = new Random();
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
				
 
				this.reset(3000 + (int)(Math.random() * 1000));
			}
		});
	}
	
	public void takeDown() {
		
	}
}
