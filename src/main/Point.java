package main;

public class Point {
	
	double x;
	double y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point() {
		this.x = 0;
		this.y = 0;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	} 
	
	 
	@Override
	public boolean equals(Object obj) {
		Point p2 = (Point)obj;
		return this.x>=p2.x && this.y>=p2.y;
	}
	
	@Override
	public String toString() { 
	    return "(" + x + "," + y + ")";
	}

	
}
