package main;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;

public class Car extends Agent {
	
	//public static int id = 0; 
	//private int carID;	//id
	//private double curVel;	//velocity
	//private double velMax;
	
	public Point location;	//location 
	
	private Point startPoint;
	private Point interPoint;
	private Point stopPoint;
	private String road;
	
	
	public Car(String road) {
			
		this.road= road;
		
		switch(road) {
			
		case "1": 
			this.startPoint = Map.r1StartPoint;
			this.interPoint = Map.r1InterPoint;
			this.stopPoint = Map.r1StopPoint;
			break;
		}
	 
		System.out.println("Hello this is mr car");
		
		this.location = startPoint;
		//this.carID = id++;
	}

	public void setup() {
		addBehaviour(new TickerBehaviour(this, 200) {

			@Override
			protected void onTick() {
				
				if(location.equals(stopPoint)) {	//TODO end 
					return;
				}
				System.out.println(location);
					
				switch(road) {
				
				case "1": 
					location.setX(location.x+0.1);
					break;
				}
				
			}
 
		}); 	
	}
	
	public void takeDown() {
		System.out.println("Car is done");
	}
	
	 
}
