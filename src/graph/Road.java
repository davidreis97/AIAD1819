package src.graph;
import src.resources.Rectangle;

public class Road {

	public enum Direction {
		UP, DOWN, LEFT, RIGHT
	}

	public String name;

	protected Point startPoint;
	protected Point endPoint;

	public Intersection startIntersection;
	public Intersection endIntersection;

	protected Direction direction;

	public int maxCars;
	
	public Road(String name, Point start, Point end, Intersection i1, Intersection i2, Direction dir) {

		this.name = name;

		this.startPoint = start;
		this.endPoint = end;
		
		this.maxCars = (int) startPoint.distance(endPoint) -1;

		this.startIntersection = i1;
		this.endIntersection = i2;

		this.direction = dir;
	}

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

	public Point getInitialPoint() {
		if(direction != Direction.UP && direction != Direction.LEFT) {
			return startPoint;
		}
		return endPoint;
	}

	public Direction getDirection() {
		return direction;
	}

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
	
	
	public Intersection getIntersection() {
		
		if(direction == Direction.RIGHT || direction == Direction.DOWN) {
			return endIntersection;
		
		} else {
			return startIntersection;
		}
			 
	}

	public Point getEndPoint() {
		return endPoint;
	}

}
