package src.graph;
import src.resources.Rectangle;

/*
 * Represents a road in the map
 */
public class Road {

	//Road directions
	public enum Direction {
		UP, DOWN, LEFT, RIGHT
	}

	public String name;						//Road name

	protected Point startPoint;				//Start point
	protected Point endPoint;				//End point

	public Intersection startIntersection;	//Start intersection
	public Intersection endIntersection;	//End intersection

	protected Direction direction;			//Direction

	public int maxCars;						//Max cars
	
	/*
	 * Constructor
	 */
	public Road(String name, Point start, Point end, Intersection i1, Intersection i2, Direction dir) {

		this.name = name;

		this.startPoint = start;
		this.endPoint = end;
		
		this.maxCars = (int) startPoint.distance(endPoint) - 1;

		this.startIntersection = i1;
		this.endIntersection = i2;

		this.direction = dir;
	}

	/*
	 * Returns the velocity  
	 */
	public Point getVelocity() {

		switch (direction) {

			case RIGHT: {
				return new Point(0.1, 0);
			}
			case LEFT: {
				return new Point(-0.1, 0);
			}
			case UP: {
				return new Point(0, -0.1);
			}
			case DOWN: {
				return new Point(0, 0.1);
			}
		}
		return null;
	}

	/*
	 * Returns the initial point  
	 */
	public Point getInitialPoint() {
		if(direction != Direction.UP && direction != Direction.LEFT) {
			return startPoint;
		}
		return endPoint;
	}

	/*
	 * Returns the direction
	 */
	public Direction getDirection() {
		return direction;
	}

	/*
	 * Checks if a rectangle intersects a road intersection
	 */
	public boolean inIntersection(Rectangle r) {

		boolean res = false;
		
		if(endIntersection!= null) {
			res = endIntersection.inIntersection(r);
		}
		if(startIntersection!= null) {
			res |= startIntersection.inIntersection(r);
		}
		
		return res;
	}
	
	/*
	 * Returns the next intersection the car will arrive
	 */
	public Intersection getIntersection() {
		
		if(direction == Direction.RIGHT || direction == Direction.DOWN) {
			return endIntersection;
		
		} else {
			return startIntersection;
		}		 
	}
	
	/*
	 * Returns the end point of the road
	 */
	public Point getEndPoint() {
		return endPoint;
	}

}
