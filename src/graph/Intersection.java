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

	public Intersection(String name, Point loc) {

		this.inRoads = new ArrayList<Road>();
		this.outRoads = new ArrayList<Road>();

		this.name = name;

		this.location = loc;

		// x, y, width, height
		this.area = new Rectangle(loc.x - 1, loc.y - 1, 2, 2);

	}

	public Point getLocation() {
		return location;
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

		Road nextR = null;

		for (Road r : outRoads) {
			if (r.name.equals(road)) {
				nextR = r;
				break;
			}
		}

		return nextR;
	}

	public Road getRandomOutRoad(Road currentRoad) {
		Road outRoad = null;
		Point exitPoint = null;
		while (exitPoint == null) {
			outRoad = outRoads.get((int) (Math.random() * outRoads.size()));
			exitPoint = getExitPoint(currentRoad, outRoad);
		}
		return outRoad;
	}

	public Point getExitPoint(Road currentRoad, Road nextRoad) {
		Point exitPoint = null;

		switch (currentRoad.direction) {
		case RIGHT: {

			if (nextRoad.direction == Direction.RIGHT) {
				exitPoint = new Point(nextRoad.startPoint.x, nextRoad.startPoint.y + 0.5);

			} else if (nextRoad.direction == Direction.UP) {
				exitPoint = new Point(this.location.x + 1 + .2, this.location.y + 0.5);

			} else if (nextRoad.direction == Direction.DOWN) {
				exitPoint = new Point(this.location.x + .2, this.location.y + 0.5); // !!
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

	public Point getExitPoint(Road currentRoad, String nextRoad) {
		Road nextR = getOutRoad(nextRoad);
		return getExitPoint(currentRoad, nextR);
	}
	

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
	
				if (out.direction == Direction.RIGHT) {	//meio
					 
					res = new Rectangle(this.location.x-1, this.location.y, 2, 1);
	
				} else if (out.direction == Direction.UP) {	//all
					res = new Rectangle(this.location.x-1, this.location.y-1, 2, 2);
	
				} else if (out.direction == Direction.DOWN) {	//1
					res = new Rectangle(this.location.x-1, this.location.y, 1, 1);
				}
	
				break;
			}
			case LEFT: {
	
				if (out.direction == Direction.LEFT) {//meio
					
					res = new Rectangle(this.location.x-1, this.location.y-1, 2, 1);
					 
				} else if (out.direction == Direction.UP) {	//1
					res = new Rectangle(this.location.x, this.location.y-1, 1, 1);
					 
				} else if (out.direction == Direction.DOWN) {	//all
					res = new Rectangle(this.location.x-1, this.location.y-1, 2, 2);
				 
				}
	
				break;
			}
			case UP: {
				
				if (out.direction == Direction.UP) {	//meio
					
					res = new Rectangle(this.location.x, this.location.y-1, 1, 2);
	
				} else if (out.direction == Direction.RIGHT) {	//1
					
					res = new Rectangle(this.location.x, this.location.y, 1, 1);
	
				} else if (out.direction == Direction.LEFT) {	//all
					res = new Rectangle(this.location.x-1, this.location.y-1, 2, 2);
				}
	
				break;
			}
			case DOWN: {
	
				if (out.direction == Direction.DOWN) {	//meio
				 
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
