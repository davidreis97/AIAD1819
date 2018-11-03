package main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.*;

public class Map extends JPanel {
	
	// Road 1
	public static final Point r1StartPoint = new Point(0, 5);
	public static final Point r1InterPoint1 = new Point(5, 5);	//muda para 4
	public static final Point r1InterPoint2 = new Point(6, 5);	//muda para 2
	public static final Point r1StopPoint = new Point(9, 5);
	public static final Point r1Vel = new Point(0.1,0);
 
	// Road 2
	public static final Point r2StartPoint = new Point(5, 9);
	public static final Point r2InterPoint1 = new Point(5, 5);	//muda para a 1
	public static final Point r2InterPoint2 = new Point(5, 4);	//muda para a 3
	public static final Point r2StopPoint = new Point(5, 0);
	public static final Point r2Vel = new Point(0, -0.1);

	// Road 3
	public static final Point r3StartPoint = new Point(9, 4);
	public static final Point r3InterPoint1 = new Point(5, 4);	//muda para a 2
	public static final Point r3InterPoint2 = new Point(4, 4);	//muda para a 4
	public static final Point r3StopPoint = new Point(0, 4);
	public static final Point r3Vel = new Point(-0.1, 0);
	
	// Road 4
	public static final Point r4StartPoint = new Point(4, 0);
	public static final Point r4InterPoint1 = new Point(5, 5);	//muda para a 3
	public static final Point r4InterPoint2 = new Point(5, 6);	//muda para a 1
	public static final Point r4StopPoint = new Point(4, 9);
	public static final Point r4Vel = new Point(0, 0.1);
	
	//Intersection 
	public static final Point intersectionP = new Point(4, 4);
  
	public ArrayList<Car> cars;
	
	public static ArrayList<Path> caminhos;
	
	static {
		caminhos = new ArrayList<Path>();
		
	 
		Path p = new Path("1", "1", r1StartPoint, null, r1StopPoint, r1Vel, r1Vel);
		caminhos.add(p);
		p = new Path("1", "4", r1StartPoint, r1InterPoint1, r4StopPoint, r1Vel, r4Vel);
		caminhos.add(p);
		p = new Path("1", "2", r1StartPoint, r1InterPoint2, r2StopPoint, r1Vel, r2Vel);
		caminhos.add(p);
		p = new Path("2", "2", r2StartPoint, null, r2StopPoint, r2Vel, r2Vel);
		caminhos.add(p);
		p = new Path("2", "1", r2StartPoint, r2InterPoint1, r1StopPoint, r2Vel, r1Vel );
		caminhos.add(p);
		p = new Path("2", "3", r2StartPoint, r2InterPoint2, r3StopPoint, r2Vel, r3Vel);
		caminhos.add(p);
		p = new Path("3", "3", r3StartPoint, null, r3StopPoint, r3Vel, r3Vel);
		caminhos.add(p);
		p = new Path("3", "4", r3StartPoint, r3InterPoint2, r4StopPoint, r3Vel, r4Vel);
		caminhos.add(p);
		p = new Path("3", "2", r3StartPoint, r3InterPoint1, r2StopPoint, r3Vel, r2Vel);
		caminhos.add(p);
		p = new Path("4", "4", r4StartPoint, null, r4StopPoint, r4Vel, r4Vel);
		caminhos.add(p);
		p = new Path("4", "1", r4StartPoint, r4InterPoint2, r1StopPoint, r4Vel, r1Vel);
		caminhos.add(p);
		p = new Path("4", "3", r4StartPoint, r4InterPoint1, r3StopPoint, r4Vel, r3Vel);
		caminhos.add(p);
		
		
		
	}
	
	public Map() {
		
		JFrame frame = new JFrame("Map");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(500, 500);

	    frame.add(this);
	    frame.setVisible(true);
	    
		this.cars = new ArrayList<Car>();
	}
	
	public void addCar(Car car) {
		this.cars.add(car);
	}

	public void paintComponent(Graphics g) {

		//super.paint(g);
		Graphics2D g2 = (Graphics2D) g;

		// set color
		g2.setColor(Color.GRAY);

		// set the thickness
		g2.setStroke(new BasicStroke(2f));

		// ROAD 1 / 3
		int unit = 50;
		g2.drawLine(0 * unit, 4 * unit, 4 * unit, 4 * unit);
		g2.drawLine(6 * unit, 4 * unit, 10 * unit, 4 * unit);
		g2.drawLine(0 * unit, 6 * unit, 4 * unit, 6 * unit);
		g2.drawLine(6 * unit, 6 * unit, 10 * unit, 6 * unit);

		// ROAD 2 / 4
		g2.drawLine(4 * unit, 0 * unit, 4 * unit, 4 * unit);
		g2.drawLine(4 * unit, 6 * unit, 4 * unit, 10 * unit);
		g2.drawLine(6 * unit, 0 * unit, 6 * unit, 4 * unit);
		g2.drawLine(6 * unit, 6 * unit, 6 * unit, 10 * unit);

		g2.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, 
				BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
		g2.drawLine(0 * unit, 5 * unit, 10 * unit, 5 * unit);
		g2.drawLine(5 * unit, 0 * unit, 5 * unit, 10 * unit);
		
		// draw cars

		for (int i = 0; i < cars.size(); ) {
			Car c = cars.get(i);

			Rectangle2D rect = new Rectangle2D.Double(c.location.x * unit, c.location.y * unit, c.size.x * unit, c.size.y * unit);
			g2.setPaint(Color.PINK);
			g2.fill(rect);
			
			if (!c.isAlive()) {
				cars.remove(c);
			}else {
				i++;
			}
		}
	}

}
