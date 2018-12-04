package src.resources;

import java.util.ArrayList;

import src.agents.Car;
import src.graph.Intersection;
import src.graph.Map;
import src.graph.Point;
import src.graph.Road;
import src.graph.Road.Direction;

/*
 * Class that represents the path that a car will go through
 */
public class Path {

	public ArrayList<String> path;				//List of road names

	private Direction currentDirection;			//Current car direction
		
	private Road currentRoad;					//Current road
	private String currentRoadName;				//Current road name
	private int currentIndex = 0;				//Index of the current road in the path list

	public Intersection currentIntersection;	//Current intersection
	public boolean waitingIntersection;			//Flag indicating if the car is waiting in at intersection
	public boolean inIntersection;				//Flag indicating if the car is in at intersection
	
	public Point nextSwitchPoint;				//Point where the car have to switch road

	
	/*
	 * Constructor
	 */
	public Path(ArrayList<String> path) {
		this.path = path;

		this.currentRoadName = path.get(currentIndex);
		this.currentRoad = Map.roads.get(currentRoadName);
		this.currentDirection = currentRoad.getDirection();
	}
	
	@Override
	public String toString() {
		return this.path.toString();
	}

	/*
	 * Returns the current road
	 */
	public Road getCurrentRoad() {
		return currentRoad;
	}

	/*
	 * Returns the current road name
	 */
	public String getCurrentRoadName() {
		return currentRoadName;
	}

	/*
	 * Returns the current direction
	 */
	public Direction getDirection() {
		return currentDirection;
	}

	/*
	 * Updates the flags when a car is at an intersection
	 */
	public void setInIntersection() {
		inIntersection = true;
		waitingIntersection = false;
	}

	/*
	 * Updates the flags when a car is entering an intersection
	 */
	public void setWaitingIntersection() {
		
		waitingIntersection = true;
		currentIntersection = currentRoad.getIntersection();
		
		String nextRoad = path.get(currentIndex + 1);
		nextSwitchPoint = currentIntersection.getExitPoint(currentRoad, nextRoad);
	}

	/*
	 * Reset all flags when a car leaves an intersection
	 */
	public void removeIntersection() {

		inIntersection = false;
		currentIntersection = null;
		nextSwitchPoint = null;
	}

	/*
	 * Switch car road to the next
	 */
	public void switchRoad(Car car) {

		currentIndex++;

		//unsubscribe the current road agent
		car.removeCar("RoadAgent" + currentRoadName);
		
		this.currentRoadName = path.get(currentIndex);
		
		//subscribe the next road agent
		car.subscribeRoad(currentRoadName);

		//update road, direction and velocity
		this.currentRoad = Map.roads.get(currentRoadName);
		this.currentDirection = currentRoad.getDirection();
		car.velocity = currentRoad.getVelocity();
		car.velocity.mul(car.carVelocity);
	
		nextSwitchPoint=null;
	}
	
	/*
	 * Returns the next road name
	 */
	public String getNextRoad() {
		if (currentIndex + 1 < path.size()) {
			return path.get(currentIndex+1);
		}
		return "NONE";
	}
	
	
	public double getTotalDistance() {
		
		double totalDis=0;
		
		for(String s:path) {
		
			Road r = Map.roads.get(s);
			totalDis+=r.getDistance();
		}
		
		if(Map.roads.get(path.get(0)).getDirection()==Direction.LEFT ||
				Map.roads.get(path.get(0)).getDirection()==Direction.UP ){
			totalDis++;
		}
		
		for(int i=0; i<path.size(); i++) {
			
			Road r = Map.roads.get(path.get(i));
			Intersection inters =r.getIntersection();
			if(inters==null) {
				 break;
			}
			
			Rectangle area = inters.getAreaOccupied(path.get(i), path.get(i+1));
 
			if(area.area()==1.0) {
				totalDis+=1;
			}
			else if(area.area()==2.0) {
				totalDis+=2;
			}
			else if(area.area()==4.0) {
				totalDis+=3;
			}
					 
		}
		
		//totalDis+=(path.size()-1)*2;
		
		return totalDis;
	}

}
