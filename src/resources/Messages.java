package src.resources;

import jade.core.AID;
import src.graph.Point;

public class Messages {

	public static String SEPARATOR = ";";
	
	public static enum MessageType{
		
		SUBSCRIBE,
		UNSUBSCRIBE,
		FRONT_CAR,
		BACK_CAR,
		CAR_LOCATION,
		REQUEST_INTERSECTION, 
		REQUEST_ACCEPTED, 
		
	}
	
	
	public static MessageType getMessageType(String content) {
	
		MessageType[] types = MessageType.values();
		
		for(int i = 0; i < types.length; i++){
			if(content.startsWith(types[i].toString()))
				return types[i];
		}
		return null;
	}
	
	
	public static String buildFrontCarMessage(AID frontCar) {
		return MessageType.FRONT_CAR.toString()+ SEPARATOR + ((frontCar!=null)?frontCar.getLocalName():"");
	}
	
	public static String buildBackCarMessage(AID backCar) {
		return MessageType.BACK_CAR.toString()+ SEPARATOR + backCar.getLocalName();
	}
	
	public static String buildPositionMessage(Point location) {
		return MessageType.CAR_LOCATION.toString() + SEPARATOR + location.toString();	
	}
	
}
