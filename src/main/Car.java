package main;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

public class Car extends Agent {
	public void setup() {
		addBehaviour(new CarBehaviour(0, 0, 0, 0)); //TODO - Ver como se passam parametros aos agentes
		System.out.println("Hello this is mr car");
	}
	
	public void takeDown() {
		System.out.println("Car is done");
	}
	
	class CarBehaviour extends CyclicBehaviour {
		private int id;
		
		private double x;
		private double y;
		
		private double velMax;
		private double curVel;
		
		public CarBehaviour(int id, double x, double y, double velMax) {
			this.id = id;
			this.x = x;
			this.y = y;
			this.velMax = velMax;
			this.curVel = 0;
		}

		@Override
		public void action() {
			/*
			 *  1 - Perguntar a intersecao posicao dos outros carros
			 *  2 - Verificar se ha carros na caixa de colisao
			 *  3 - Se nao, incrementar posicao
			 *  4 - Se sim, nao sair do sitio.
			 *  5 - Informar a nova posiçao
			 */
		}
		
		
	}
}
