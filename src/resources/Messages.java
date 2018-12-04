package src.resources;

import java.util.Arrays;

import jade.core.AID;
import src.graph.Point;

/*
 * Class that builds ang get messages content
 */
public class Messages {

	public static String SEPARATOR = ";";
		
	//Messages types
	public static enum MessageType{
		
		SUBSCRIBE,
		UNSUBSCRIBE,
		FRONT_CAR,
		BACK_CAR,
		CAR_LOCATION,
		REQUEST_INTERSECTION, 
		REQUEST_ACCEPTED, 
		POLL_SPACE,
		SPACE_INFO,
		NEW_CAR
	}
	
	/*
	 * Returns the content of a message
	 */
	public static String[] getMessageContent(String message) {
		String result[] = message.split(SEPARATOR);
		return Arrays.copyOfRange(result, 1, result.length);
	}
	
	/*
	 * Returns the type of a message
	 */
	public static MessageType getMessageType(String content) {
	
		MessageType[] types = MessageType.values();
		
		for(int i = 0; i < types.length; i++){
			if(content.startsWith(types[i].toString()))
				return types[i];
		}
		System.out.println("Unknown message type in " + content + " msg");
		return null;
	}
	
	/*
	 * Build the SPACE_INFO message
	 */
	public static String buildHasSpaceMessage(String status) {
		return MessageType.SPACE_INFO.toString()+ SEPARATOR + status;
	}
	
	/*
	 * Build the FRONT_CAR message
	 */
	public static String buildFrontCarMessage(AID frontCar) {
		return MessageType.FRONT_CAR.toString()+ SEPARATOR + ((frontCar!=null)?frontCar.getLocalName():"");
	}
	
	/*
	 * Build the BACK_CAR message
	 */
	public static String buildBackCarMessage(AID backCar) {
		return MessageType.BACK_CAR.toString()+ SEPARATOR + backCar.getLocalName();
	}
	
	/*
	 * Build the CAR_LOCATION message
	 */
	public static String buildPositionMessage(Point location) {
		return MessageType.CAR_LOCATION.toString() + SEPARATOR + location.toString();	
	}
	
}
