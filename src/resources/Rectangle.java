package src.resources;

import src.graph.Point;

/*
 * Represents a rectangle
 */
public class Rectangle {

	private final double x;
	private final double y;

	private final double width;
	private final double height;

	/*
	 * Constructor
	 */
	public Rectangle(double x0, double y0, double w, double h) {
		x = x0;
		y = y0;
		width = w;
		height = h;
	}
	
	public String toString() {
		return "x:"+x+" y:"+y+" width:"+width+" height:"+height;
	}

	/*
	 * Returns the area
	 */
	public double area() {
		return width * height;
	}

	/*
	 * Returns the perimeter
	 */
	public double perimeter() {
		return 2 * width + 2 * height;
	}

	/*
	 * Checks if the rectangle intersects with other
	 */
	public boolean intersects(Rectangle r) {
		return x < r.x + r.width && x + width > r.x && y < r.y + r.height && y + height > r.y;
	}
	
	/*
	 * Checks if the rectangle contains a given point
	 */
	public boolean contains(Point p) {
		return x<=p.x && p.x<=x+width && y<=p.y && p.y<=y+height;
	}

}