package main;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

public class Intersection extends Agent {
	public void setup() {
		addBehaviour(new IntersectionBehaviour(0)); //TODO - Ver como se passam parametros aos agentes
		System.out.println("Hello this is mr car");
	}
	
	public void takeDown() {
		System.out.println("Car is done");
	}
	
	class IntersectionBehaviour extends CyclicBehaviour {
		
		//TODO - Estrutura para guardar posicao de carros (para conseguir apagar carros, tem de associar o id do carro a cada posicao)
		
		public IntersectionBehaviour(int timeBetweenSpawns) {
			this.timeBetweenSpawns = timeBetweenSpawns;
		}

		@Override
		public void action() {
			/*
			 *  1 - Decrementar tempo atual
			 *  2 - Se tempo atual < 0, criar carro e fazer cooldown
			 */
		}
		
		
	}
}
