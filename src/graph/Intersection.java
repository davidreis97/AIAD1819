package src.graph;

import src.resources.Rectangle;
import src.graph.Road.Direction;

import java.util.ArrayList;

public class Intersection {

	public String name;

	private ArrayList<Road> inRoads;
	private ArrayList<Road> outRoads;

	private Rectangle area;

	private Point location;
	
	public Point getLocation() {
		return location;
	}
	
	public String toString() {
		return ""+name+location;
	}

	public Intersection(String name, Point loc) {

		this.inRoads = new ArrayList<Road>();
		this.outRoads = new ArrayList<Road>();

		this.name = name;

		this.location = loc;

		// x, y, width, height
		this.area = new Rectangle(loc.x - 1, loc.y - 1, 2, 2);

	}

	public void addInRoad(Road r) {
		this.inRoads.add(r);
	}

	public void addOutRoad(Road r) {
		this.outRoads.add(r);
	}

	public boolean inIntersection(Rectangle r2) {
		return area.intersects(r2);
	}

	public Road getOutRoad(String road) {
		
		System.out.println(outRoads);
		
		Road nextR = null;

		for (Road r : outRoads) {
			if (r.name.equals(road)) {
				nextR = r;
				break;
			}
		}

		return nextR;
	}

	public Point getExitPoint(Road currentRoad, String nextRoad) {

		Road nextR = getOutRoad(nextRoad);
		Point exitPoint = null;

		switch (currentRoad.direction) {
			case RIGHT: {
	
				if (nextR.direction == Direction.RIGHT) {
					exitPoint = new Point( nextR.startPoint.x, nextR.startPoint.y+0.5);
	
				} else if (nextR.direction == Direction.UP) {
					exitPoint = new Point(this.location.x + 1+.2, this.location.y+0.5);
	
				} else if (nextR.direction == Direction.DOWN) {
					exitPoint = new Point (this.location.x+.2, this.location.y+0.5);	//!!
				}
	
				break;
			}
			case LEFT: {
	
				if (nextR.direction == Direction.LEFT) {
					exitPoint = new Point(nextR.endPoint.x, nextR.endPoint.y+0.5);
				} else if (nextR.direction == Direction.UP) {
					exitPoint = new Point (this.location.x, this.location.y-0.5);
				} else if (nextR.direction == Direction.DOWN) {
					exitPoint = new Point(this.location.x - 1, this.location.y-0.5);
				}
	
				break;
			}
			case UP: {
	
				if (nextR.direction == Direction.UP) {
					exitPoint = new Point( nextR.endPoint.x +.5, nextR.endPoint.y);
	
				} else if (nextR.direction == Direction.RIGHT) {
					exitPoint = new Point(this.location.x + 0.5, this.location.y);
					
				} else if (nextR.direction == Direction.LEFT) {
					exitPoint = new Point(this.location.x+ 0.5, this.location.y - 1);
				}
	
				break;
			}
			case DOWN: {
	
				if (nextR.direction == Direction.DOWN) {
					exitPoint = new Point (nextR.startPoint.x+0.5, nextR.startPoint.y);
	
				} else if (nextR.direction == Direction.LEFT) {
					exitPoint = new Point(this.location.x -0.5, this.location.y+0.2);
					
				} else if (nextR.direction == Direction.RIGHT) {
					exitPoint = new Point(this.location.x-0.5, this.location.y + 1+0.2);
				}
				break;
			}
		}

		return exitPoint;

	}

}
