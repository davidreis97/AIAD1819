package src.resources;

import src.graph.Point;

public class Rectangle {

	private final double x;
	private final double y;

	private final double width;
	private final double height;

	public Rectangle(double x0, double y0, double w, double h) {
		x = x0;
		y = y0;
		width = w;
		height = h;
	}
	
	public String toString() {
		return "x:"+x+" y:"+y+" width:"+width+" height:"+height;
	}

	public double area() {
		return width * height;
	}

	public double perimeter() {
		return 2 * width + 2 * height;
	}

	public boolean intersects(Rectangle r) {
		return x < r.x + r.width && x + width > r.x && y < r.y + r.height && y + height > r.y;
	}
	
	public boolean contains(Point p) {
		return x<=p.x && p.x<=x+width && y<=p.y && p.y<=y+height;
	}

}