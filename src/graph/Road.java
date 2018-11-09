package src.graph;
import src.resources.Rectangle;

public class Road {

	public enum Direction {
		UP, DOWN, LEFT, RIGHT
	}

	protected String name;

	protected Point startPoint;
	protected Point endPoint;

	private Intersection startIntersection;
	private Intersection endIntersection;

	protected Direction direction;

	public String toString() {
		return name + ";"+startPoint+";"+endPoint+";"+startIntersection+";"+endIntersection+";"+direction;
	}
	
	public Road(String name, Point start, Point end, Intersection i1, Intersection i2, Direction dir) {

		this.name = name;

		this.startPoint = start;
		this.endPoint = end;

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
		return startPoint;
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
		
		// check if the intersection exists
		/*switch (direction) {

			case RIGHT: {
	
				if (endIntersection == null)
					return false;
				break;
			}
			case LEFT: {
				if (startIntersection == null)
					return false;
				break;
			}
			case UP: {
				if (startIntersection == null)
					return false;
				break;
			}
			case DOWN: {
				if (endIntersection == null)
					return false;
				break;
			}
		}

		switch (direction) {

			case RIGHT:
			case DOWN:
				
				return endIntersection.inIntersection(r);
	
			case LEFT:
			case UP:
	
				return startIntersection.inIntersection(r);
		}
		
		return false;*/
	}
	
	
	public Intersection getIntersection() {
		
		if(direction == Direction.RIGHT || direction == Direction.DOWN) {
			return endIntersection;
		
		} else {
			return startIntersection;
		}
			
	}

}
