package main;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

public class CarSpawner extends Agent {
	public void setup() {
		addBehaviour(new CarSpawnerBehaviour(0)); //TODO - Ver como se passam parametros aos agentes
		System.out.println("Hello this is mr car");
	}
	
	public void takeDown() {
		System.out.println("Car is done");
	}
	
	class CarSpawnerBehaviour extends CyclicBehaviour {
		
		private int timeBetweenSpawns;
		private int currentTime;
		
		private int currentCarID;
		
		public CarSpawnerBehaviour(int timeBetweenSpawns) {
			this.timeBetweenSpawns = timeBetweenSpawns;
		}

		@Override
		public void action() {
			/*
			 *  1 - Decrementar tempo atual
			 *  2 - Se tempo atual < 0, criar carro e fazer cooldown (com id currentCarID)
			 */
		}
		
		
	}
}
