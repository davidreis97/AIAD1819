package src.graph;

/*
 * Represents a point in the map
 */
public class Point implements java.io.Serializable {

	public double x;
	public double y;

	/*
	 * Constructor
	 */
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point(Point p) {
		this.x = p.x;
		this.y = p.y;
	}

	public Point() {
		this.x = 0;
		this.y = 0;
	}

	@Override
	public boolean equals(Object obj) {
		Point p2 = (Point) obj;
		return this.x == p2.x && this.y == p2.y;
	}

	@Override
	public String toString() {
		return x + "," + y;
	}
	
	/*
	 * Returns the sum of two points
	 */
	public void add(Point point) {
		this.x += point.x;
		this.y += point.y;
	}

	/*
	 * Returns the symetric of a point
	 */
	public Point symetric() {
		return new Point(-this.x, -this.y);
	}
	
	/*
	 * Returns the distance between two points 
	 */
	public double distance(Point anotherPoint) {
		return Math.sqrt(Math.pow((x-anotherPoint.x), 2) + Math.pow((y-anotherPoint.y), 2));
	}
	
	public void mul(double val) {
		this.x *= val;
		this.y *= val;
	}

}
