package src.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JPanel;

import src.agents.Car;
import src.graph.Road.Direction;

/*
 * Represents the map
 */
public class Map extends JPanel {

	public static HashMap<String, Intersection> intersections;	//Map intersections
	public static HashMap<String, Road> roads;					//Map roads
	public static ArrayList<Car> cars;							//Cars currently in the map
		
	public static int width;									//Map width
	public static final int panel_size = 700;					//Window size
	public static int unit;
	
	/*
	 * Constructor
	 */
	public Map(HashMap<String, Intersection> intersections2, HashMap<String, Road> roads2, int width2) {
		
		width = width2;
		unit = panel_size/width;
		
		cars = new ArrayList<Car>();
		
		intersections = intersections2;
		roads = roads2;

		
		JFrame frame = new JFrame("Map");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(panel_size, panel_size);

		frame.add(this);
		frame.setVisible(true);
		
	}
	
	/*
	 * Adds a car to the map
	 */
	public void addCar(Car car) {
		Map.cars.add(car);
	}

	/*
	 * Draw the map, with the cars
	 */
	public void paintComponent(Graphics g) {

		// super.paint(g);
		Graphics2D g2 = (Graphics2D) g;

		// set color
		g2.setColor(new Color(224, 235, 235));

		// set the thickness
		g2.setStroke(new BasicStroke(2f));

		for(HashMap.Entry<String, Intersection> entry : intersections.entrySet()) {
			
			Intersection inter = entry.getValue();
			g2.fillRect((int)((inter.getLocation().x-1) * unit), (int)( (inter.getLocation().y-1) * unit), 2* unit, 2* unit);
		}
		
		g2.setColor(Color.GRAY);
		
		for (HashMap.Entry<String, Road> entry : roads.entrySet()) {
			String key = entry.getKey();
			Road value = entry.getValue();

			if (value.direction == Direction.LEFT || value.direction == Direction.RIGHT) {

				g2.drawLine((int) value.startPoint.x * unit, (int) value.startPoint.y * unit,
						(int) value.endPoint.x * unit, (int) value.endPoint.y * unit);

				g2.drawLine((int) value.startPoint.x * unit, ((int) value.startPoint.y + 1) * unit,
						(int) value.endPoint.x * unit, ((int) value.endPoint.y + 1) * unit);

			} else {

				g2.drawLine((int) value.startPoint.x * unit, (int) value.startPoint.y * unit,
						(int) value.endPoint.x * unit, (int) value.endPoint.y * unit);

				g2.drawLine(((int) value.startPoint.x + 1) * unit, (int) value.startPoint.y * unit,
						((int) value.endPoint.x + 1) * unit, (int) value.endPoint.y * unit);
			}

		}
		
		// draw the cars
		
		for (int i = 0; i < cars.size();) {
			Car c = cars.get(i);

			Rectangle2D rect = new Rectangle2D.Double(c.location.x * unit, c.location.y * unit, c.size.x * unit * 0.8,
					c.size.y * unit * 0.8);
	 
			g2.setPaint(Color.PINK);
			g2.fill(rect);
			
			g2.setPaint(Color.BLACK);
			g.drawString(c.getName().split("@")[0],(int) (c.location.x + 0.5) * unit,(int) (c.location.y + 1) * unit);

			if (!c.isAlive()) {
				cars.remove(c);
			} else {
				i++;
			}
		}

	}

}
