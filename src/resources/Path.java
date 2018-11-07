package src.resources;

import java.util.ArrayList;

import src.agents.Car;
import src.graph.Intersection;
import src.graph.Map;
import src.graph.Point;
import src.graph.Road;
import src.graph.Road.Direction;

public class Path {

	private ArrayList<String> path;

	private Direction currentDirection;

	private Road currentRoad;
	private String currentRoadName;

	private int currentIndex = 0;

	// intersection stuff
	public Intersection currentIntersection;
	public boolean waitingIntersection;
	public boolean inIntersection;

	public Point nextSwitchPoint;

	public Path(ArrayList<String> path) {
		this.path = path;

		this.currentRoadName = path.get(currentIndex);
		this.currentRoad = Map.roads.get(currentRoadName);
		this.currentDirection = currentRoad.getDirection();

	}

	public String toString() {
		return this.path.toString();
	}

	public Road getCurrentRoad() {
		return currentRoad;
	}

	public String getCurrentRoadName() {
		return currentRoadName;
	}

	public Direction getDirection() {
		return currentDirection;
	}

	public void setInIntersection() {
		inIntersection = true;
		waitingIntersection = false;

	}

	/*
	 * Sets all the variables when entering an intersection
	 */
	public void setWaitingIntersection() {
		
		System.out.println("Set in intersection " + currentRoad.getIntersection());

		waitingIntersection = true;

		currentIntersection = currentRoad.getIntersection();
		String nextRoad = path.get(currentIndex + 1);
		nextSwitchPoint = currentIntersection.getExitPoint(currentRoad, nextRoad);
	}

	/*
	 * Reset all the variables when leaving an intersection
	 */
	public void removeIntersection() {

		inIntersection = false;
		currentIntersection = null;
		nextSwitchPoint = null;

	}

	/*
	 * Switch car road
	 */
	public void switchRoad(Car car) {

		currentIndex++;

		car.removeCar("RoadAgent" + currentRoadName);
		
		this.currentRoadName = path.get(currentIndex);
		
		car.subscribeRoad(currentRoadName);

		this.currentRoad = Map.roads.get(currentRoadName);
		this.currentDirection = currentRoad.getDirection();
		
		car.velocity = currentRoad.getVelocity();
		
		nextSwitchPoint=null;

	}

	public String getNextRoad() {
		if (currentIndex + 1 < path.size()) {
			return path.get(currentIndex+1);
		}
		return "NONE";
	}

}
