package main;

public class Point implements java.io.Serializable{
	
	public double x;
	public double y;
	
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
		Point p2 = (Point)obj;
		return this.x==p2.x && this.y==p2.y; //ALTO LA PODE NAO FUNCIONAR - 0.00000001 != 0
		/*
		 * Alternativa que funciona:
		 * 
		 * double threshold = 0.001;
		 * 
		 * return this.x > p2.x - threshold && this.x < p2.x + threshold && this.y > p2.y - threshold && this.y < p2.y + threshold;
		 * 
		 */
	}
	
	@Override
	public String toString() { 
	    return "(" + x + "," + y + ")";
	}
	
	public void add(Point point) {
		this.x += point.x;
		this.y += point.y;
	}
	
	public Point symetric() {
		return new Point(-this.x,-this.y);
	}
	
	
	
}
