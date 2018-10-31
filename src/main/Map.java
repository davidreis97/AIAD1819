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
	public static final Point r1InterPoint = new Point(3, 5);
	public static final Point r1StopPoint = new Point(9, 5);

	// Road 2
	public static final Point r2StartPoint = new Point(5, 9);
	public static final Point r2InterPoint = new Point(5, 6);
	public static final Point r2StopPoint = new Point(5, 0);

	// Road 3
	public static final Point r3StartPoint = new Point(9, 4);
	public static final Point r3InterPoint = new Point(6, 4);
	public static final Point r3StopPoint = new Point(0, 4);

	// Road 4
	public static final Point r4StartPoint = new Point(4, 0);
	public static final Point r4InterPoint = new Point(4, 3);
	public static final Point r4StopPoint = new Point(4, 9);

	//Intersection 
	public static final Point intersectionP = new Point(4, 4);
 
 
	public ArrayList<Car> cars;

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
