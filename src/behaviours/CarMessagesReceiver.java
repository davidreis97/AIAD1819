package src.behaviours;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import src.agents.Car;
import src.resources.Messages.MessageType;
import src.graph.Point;
import src.resources.Messages;


public class CarMessagesReceiver extends CyclicBehaviour{

	
	private Car car = null;
	
	public CarMessagesReceiver(Car car) {
		this.car = car;
	}
	
	@Override
	public void action() {
		

		ACLMessage msg = car.receive();

		if (msg != null) {

			MessageType type = Messages.getMessageType(msg.getContent());
			
			if(type == null) {
				return;
			}
			
			switch(type){
				
				case FRONT_CAR:{
					handleFrontCar(msg);
					break;
				}
				
				case BACK_CAR:{
					handleBackCar(msg);
					break;
				}
				case CAR_LOCATION:{
					handleFrontCarLocation(msg);
					break;
				}
				
				case REQUEST_ACCEPTED:{
					handleIntersectionAccepted(msg);
					break;
					
				}
				default:{
					break;
				}
			}

		} else {
			block();
		}
		
	}
	
	
	public void handleFrontCar(ACLMessage msg) {
			
		String parts[] = msg.getContent().split(Messages.SEPARATOR);
		
		if(parts.length==2) {
			
			String frontCar = parts[1];
			AID agentID = car.getAID(frontCar);
			car.frontCar = agentID;
 
		} else {
			car.frontCar = null ;
		}
	}
	
	public void handleBackCar(ACLMessage msg) {

		String parts[] = msg.getContent().split(Messages.SEPARATOR);
		
		if(parts.length==2) {
			
			String backCar = parts[1];
			AID agentID = car.getAID(backCar);
			car.backCar = agentID;
 
		
		} else {
			car.backCar = null ;
		}
	}

	public void handleFrontCarLocation(ACLMessage msg) {
		
		
		String parts[] = msg.getContent().split(Messages.SEPARATOR);
		String location = parts[1];
		
		String coordinates[] = location.split(",");
		double x = Double.parseDouble(coordinates[0]);
		double y = Double.parseDouble(coordinates[1]);
		
		car.frontCar_position = new Point(x, y);
			
	}
	
	
	public void handleIntersectionAccepted(ACLMessage msg) {
		car.getPath().setInIntersection();
				
	}
	
	
}
