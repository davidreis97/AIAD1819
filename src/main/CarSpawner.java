package main;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class CarSpawner extends Agent {
	private ContainerController container;
	
	private int carID;
	
	public CarSpawner(ContainerController container) {
		this.container = container;
		this.carID = 0;
	}
	
	public void setup() {
		//TODO - Usar paginas amarelas para obter carros atuais, para nao exceder um certo valor
		
		addBehaviour(new WakerBehaviour(this,0) {
			protected void handleElapsedTimeout() {
				AgentController ac1;
				try {
					Car car = new Car(/*ID;Velocidade;Forma de passar estrutura da rua*/);
					ac1 = container.acceptNewAgent("car-"+carID++,car);
					ac1.start();
				}catch(StaleProxyException e) {
					e.printStackTrace();
				}
				
				this.reset(1000 + (int)(Math.random() * 1000));
			}
		});
	}
	
	public void takeDown() {
		
	}
}
