package main;

public class Path {
	
	public String initialRoad;
	public String finalRoad;
	
	public Point initialVelocity;
	public Point finalVelocity;
	
	public Point startPoint;
	public Point endPoint;
	public Point midPoint;
	
	
	public Path(String ini, String fin, Point startPoint, Point midPoint, Point endPoint, Point velin, Point velf) {
		this.startPoint = new Point(startPoint);
		
		if(midPoint!= null) {
			this.midPoint = new Point(midPoint);
		}else {
			this.midPoint = null;
		}
		
		this.endPoint = new Point(endPoint);
		
		this.initialRoad = ini;
		this.finalRoad = fin;
		
		this.initialVelocity = new Point(velin);
		this.finalVelocity = new Point(velf);

	}
}
