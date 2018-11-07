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

public class Map extends JPanel {

	public static HashMap<String, Intersection> intersections;
	public static HashMap<String, Road> roads;
	public static ArrayList<Car> cars;
	
	public static HashMap<String, ArrayList<String>> paths;
	

	static {

		intersections = new HashMap<String, Intersection>();
		roads = new HashMap<String, Road>();
		paths = new HashMap<String, ArrayList<String>>();
		cars = new ArrayList<Car>();

		// ---- Mapa 1 ----

		Intersection intersection1 = new Intersection("1", new Point(3, 3));
		Intersection intersection2 = new Intersection("2", new Point(7, 3));

		Point p1, p2;
		Road r1;

		// create roads

		p1 = new Point(0, 2);
		p2 = new Point(2, 2);
		r1 = new Road("1", p1, p2, null, intersection1, Direction.LEFT);
		roads.put("1", r1);

		intersection1.addOutRoad(r1);

		p1 = new Point(0, 3);
		p2 = new Point(2, 3);
		r1 = new Road("2", p1, p2, null, intersection1, Direction.RIGHT);
		roads.put("2", r1);

		intersection1.addInRoad(r1);

		p1 = new Point(4, 2);
		p2 = new Point(6, 2);
		r1 = new Road("3", p1, p2, intersection1, intersection2, Direction.LEFT);
		roads.put("3", r1);

		intersection1.addInRoad(r1);
		intersection2.addOutRoad(r1);

		p1 = new Point(4, 3);
		p2 = new Point(6, 3);
		r1 = new Road("4", p1, p2, intersection1, intersection2, Direction.RIGHT);
		roads.put("4", r1);

		intersection1.addOutRoad(r1);
		intersection2.addInRoad(r1);

		p1 = new Point(9, 2);
		p2 = new Point(10, 2);
		r1 = new Road("5", p1, p2, intersection2, null, Direction.LEFT);
		roads.put("5", r1);

		intersection2.addInRoad(r1);

		p1 = new Point(8, 3);
		p2 = new Point(10, 3);
		r1 = new Road("6", p1, p2, intersection2, null, Direction.RIGHT);
		roads.put("6", r1);

		intersection2.addOutRoad(r1);

		// ----
		p1 = new Point(2, 0);
		p2 = new Point(2, 2);
		r1 = new Road("7", p1, p2, null, intersection1, Direction.DOWN);
		roads.put("7", r1);

		intersection1.addInRoad(r1);

		p1 = new Point(3, 0);
		p2 = new Point(3, 2);
		r1 = new Road("8", p1, p2, null, intersection1, Direction.UP);
		roads.put("8", r1);

		intersection1.addOutRoad(r1);

		p1 = new Point(2, 4);
		p2 = new Point(2, 10);
		r1 = new Road("9", p1, p2, intersection1, null, Direction.DOWN);
		roads.put("9", r1);

		intersection1.addOutRoad(r1);

		p1 = new Point(3, 4);
		p2 = new Point(3, 10);
		r1 = new Road("10", p1, p2, intersection1, null, Direction.UP);
		roads.put("10", r1);

		intersection1.addInRoad(r1);

		p1 = new Point(6, 0);
		p2 = new Point(6, 2);
		r1 = new Road("11", p1, p2, null, intersection2, Direction.DOWN);
		roads.put("11", r1);

		intersection2.addInRoad(r1);

		p1 = new Point(7, 0);
		p2 = new Point(7, 2);
		r1 = new Road("12", p1, p2, null, intersection2, Direction.UP);
		roads.put("12", r1);

		intersection2.addOutRoad(r1);

		p1 = new Point(6, 4);
		p2 = new Point(6, 10);
		r1 = new Road("13", p1, p2, intersection2, null, Direction.DOWN);
		roads.put("13", r1);

		intersection2.addOutRoad(r1);

		p1 = new Point(7, 4);
		p2 = new Point(7, 10);
		r1 = new Road("14", p1, p2, intersection2, null, Direction.UP);
		roads.put("14", r1);

		intersection2.addInRoad(r1);

		intersections.put("I1", intersection1);
		intersections.put("I2", intersection2);
		
		
		// -- paths example --
		
		ArrayList<String> path = new ArrayList<String>();
		path.add("5");
		path.add("3");
		path.add("1");
		
		paths.put("1", path);
		
		path = new ArrayList<String>();
		path.add("2");
		path.add("4");
		path.add("12");

		paths.put("2", path);

		path = new ArrayList<String>();
		path.add("7");
		path.add("4");
		path.add("13");

		paths.put("3", path);
		
		path = new ArrayList<String>();
		path.add("2");
		path.add("4");
		path.add("6");
		
		paths.put("4", path);
		
		path = new ArrayList<String>();
		path.add("11");
		path.add("3");
		path.add("9");
		
		paths.put("5", path);
	}

	public Map() {

		JFrame frame = new JFrame("Map");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);

		frame.add(this);
		frame.setVisible(true);

	}
	
	public void addCar(Car car) {
		Map.cars.add(car);
	}


	public void paintComponent(Graphics g) {

		// super.paint(g);
		Graphics2D g2 = (Graphics2D) g;

		// set color
		g2.setColor(Color.GRAY);

		// set the thickness
		g2.setStroke(new BasicStroke(2f));

		int unit = 50;

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

		// draw cars

		for (int i = 0; i < cars.size();) {
			Car c = cars.get(i);

			Rectangle2D rect = new Rectangle2D.Double(c.location.x * unit, c.location.y * unit, c.size.x * unit,
					c.size.y * unit);
			
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
