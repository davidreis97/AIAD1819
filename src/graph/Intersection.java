package src.graph;

import src.resources.Rectangle;
import src.graph.Road.Direction;

import java.util.ArrayList;

/*
 * Represents an intersection in the map
 */
public class Intersection {

	public String name;						//Intersection name

	private ArrayList<Road> inRoads;	 	//In roads
	private ArrayList<Road> outRoads;	 	//Out roads

	private Rectangle area;					//Area of the intersection
	private Point location;					//Intersection location

	/*
	 * Constructor
	 */
	public Intersection(String name, Point loc) {

		this.inRoads = new ArrayList<Road>();
		this.outRoads = new ArrayList<Road>();

		this.name = name;

		this.location = loc;

		this.area = new Rectangle(loc.x - 1, loc.y - 1, 2, 2);
	}

	/*
	 * Get location
	 */
	public Point getLocation() {
		return location;
	}

	/*
	 * Add in road
	 */
	public void addInRoad(Road r) {
		this.inRoads.add(r);
	}

	/*
	 * Add out road
	 */
	public void addOutRoad(Road r) {
		this.outRoads.add(r);
	}

	/*
	 * Checks if other rectangle intersects the intersection area
	 */
	public boolean inIntersection(Rectangle r2) {
		return area.intersects(r2);
	}

	/*
	 * Get out road according to the name
	 */
	public Road getOutRoad(String road) {

		Road nextR = null;

		for (Road r : outRoads) {
			if (r.name.equals(road)) {
				nextR = r;
				break;
			}
		}
		return nextR;
	}

	/*
	 * Get a random out road, from a given road
	 */
	public Road getRandomOutRoad(Road currentRoad) {
		Road outRoad = null;
		Point exitPoint = null;
		while (exitPoint == null) {
			outRoad = outRoads.get((int) (Math.random() * outRoads.size()));
			exitPoint = getExitPoint(currentRoad, outRoad);
		}
		return outRoad;
	}

	/*
	 * Get the exit point, given the in and out roads
	 */
	public Point getExitPoint(Road currentRoad, Road nextRoad) {
		Point exitPoint = null;

		switch (currentRoad.direction) {
		case RIGHT: {

			if (nextRoad.direction == Direction.RIGHT) {
				exitPoint = new Point(nextRoad.startPoint.x, nextRoad.startPoint.y + 0.5);

			} else if (nextRoad.direction == Direction.UP) {
				exitPoint = new Point(this.location.x + 1 + .2, this.location.y + 0.5);

			} else if (nextRoad.direction == Direction.DOWN) {
				exitPoint = new Point(this.location.x + .2, this.location.y + 0.5);  
			}

			break;
		}
		case LEFT: {

			if (nextRoad.direction == Direction.LEFT) {
				exitPoint = new Point(nextRoad.endPoint.x, nextRoad.endPoint.y + 0.5);
			} else if (nextRoad.direction == Direction.UP) {
				exitPoint = new Point(this.location.x, this.location.y - 0.5);
			} else if (nextRoad.direction == Direction.DOWN) {
				exitPoint = new Point(this.location.x - 1, this.location.y - 0.5);
			}

			break;
		}
		case UP: {

			if (nextRoad.direction == Direction.UP) {
				exitPoint = new Point(nextRoad.endPoint.x + 0.5, nextRoad.endPoint.y);

			} else if (nextRoad.direction == Direction.RIGHT) {
				exitPoint = new Point(this.location.x + 0.5, this.location.y);

			} else if (nextRoad.direction == Direction.LEFT) {
				exitPoint = new Point(this.location.x + 0.5, this.location.y - 1);
			}

			break;
		}
		case DOWN: {

			if (nextRoad.direction == Direction.DOWN) {
				exitPoint = new Point(nextRoad.startPoint.x + 0.5, nextRoad.startPoint.y);

			} else if (nextRoad.direction == Direction.LEFT) {
				exitPoint = new Point(this.location.x - 0.5, this.location.y + 0.2);
			} else if (nextRoad.direction == Direction.RIGHT) {
				exitPoint = new Point(this.location.x - 0.5, this.location.y + 1 + 0.2);
			}
			break;
		}
		}

		return exitPoint;
	}
	
	/*
	 * Get the exit point, given the in road, and out road name
	 */
	public Point getExitPoint(Road currentRoad, String nextRoad) {
		Road nextR = getOutRoad(nextRoad);
		return getExitPoint(currentRoad, nextR);
	}
	
	/*
	 * Returns the area that a car will occupy in the intersection, given the in and out roads
	 */
	public Rectangle getAreaOccupied(String inRoad, String outRoad) {

		Road in=null;
		Road out=null;

		for (Road r : outRoads) {
			if (r.name.equals(outRoad)) {
				out = r;
				break;
			}
		}

		for (Road r : inRoads) {
			if (r.name.equals(inRoad)) {
				in = r;
				break;
			}
		}

		Rectangle res = null;

		switch (in.direction) {
			case RIGHT: {
	
				if (out.direction == Direction.RIGHT) {	//half
					 
					res = new Rectangle(this.location.x-1, this.location.y, 2, 1);
	
				} else if (out.direction == Direction.UP) {	//all
					res = new Rectangle(this.location.x-1, this.location.y-1, 2, 2);
	
				} else if (out.direction == Direction.DOWN) {	//1
					res = new Rectangle(this.location.x-1, this.location.y, 1, 1);
				}
	
				break;
			}
			case LEFT: {
	
				if (out.direction == Direction.LEFT) {	// half
					
					res = new Rectangle(this.location.x-1, this.location.y-1, 2, 1);
					 
				} else if (out.direction == Direction.UP) {	//1
					res = new Rectangle(this.location.x, this.location.y-1, 1, 1);
					 
				} else if (out.direction == Direction.DOWN) {	//all
					res = new Rectangle(this.location.x-1, this.location.y-1, 2, 2);
				 
				}
	
				break;
			}
			case UP: {
				
				if (out.direction == Direction.UP) {	//half
					
					res = new Rectangle(this.location.x, this.location.y-1, 1, 2);
	
				} else if (out.direction == Direction.RIGHT) {	//1
					
					res = new Rectangle(this.location.x, this.location.y, 1, 1);
	
				} else if (out.direction == Direction.LEFT) {	//all
					res = new Rectangle(this.location.x-1, this.location.y-1, 2, 2);
				}
	
				break;
			}
			case DOWN: {
	
				if (out.direction == Direction.DOWN) {	//half
				 
					res = new Rectangle(this.location.x-1, this.location.y-1, 1, 2);
	
				} else if (out.direction == Direction.LEFT) {	//1
						
					res = new Rectangle(this.location.x-1, this.location.y-1, 1, 1);
					
				} else if (out.direction == Direction.RIGHT) {	//all 
					 
					res = new Rectangle(this.location.x-1, this.location.y-1, 2, 2);
					
				}
				break;
			}
		}
		return res;

	}

}
