package src.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import src.graph.Road.Direction;

public class Reader {

	private Document doc;

	public Reader(String filename) throws ParserConfigurationException, IOException, SAXException {

		File inputFile = new File(filename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		doc = dBuilder.parse(inputFile);
		doc.getDocumentElement().normalize();
	}

	public HashMap<String, Intersection> getIntersections() {

		HashMap<String, Intersection> intersections = new HashMap<String, Intersection>();

		try {

			Node intersectionsNode = doc.getElementsByTagName("intersections").item(0);

			NodeList intersectionsNodeList = intersectionsNode.getChildNodes();

			for (int i = 0; i < intersectionsNodeList.getLength(); i++) {

				if (intersectionsNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) intersectionsNodeList.item(i);

					String name = eElement.getAttribute("name");
					float x = Float.parseFloat(eElement.getElementsByTagName("x").item(0).getTextContent());
					float y = Float.parseFloat(eElement.getElementsByTagName("y").item(0).getTextContent());

					Intersection inters = new Intersection(name, new Point(x, y));

					intersections.put("I" + name, inters);

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return intersections;
	}

	public HashMap<String, Road> getRoads(HashMap<String, Intersection> intersections) {

		HashMap<String, Road> roads = new HashMap<String, Road>();

		try {

			Node roadsNode = doc.getElementsByTagName("roads").item(0);

			NodeList roadsNodesList = roadsNode.getChildNodes();

			for (int i = 0; i < roadsNodesList.getLength(); i++) {

				if (roadsNodesList.item(i).getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) roadsNodesList.item(i);

					String name = eElement.getAttribute("name");
					float x = Float.parseFloat(eElement.getElementsByTagName("x1").item(0).getTextContent());
					float y = Float.parseFloat(eElement.getElementsByTagName("y1").item(0).getTextContent());

					Point p1 = new Point(x, y);

					x = Float.parseFloat(eElement.getElementsByTagName("x2").item(0).getTextContent());
					y = Float.parseFloat(eElement.getElementsByTagName("y2").item(0).getTextContent());

					Point p2 = new Point(x, y);

					String direction = eElement.getElementsByTagName("direction").item(0).getTextContent();

					String intersection1 = eElement.getElementsByTagName("intersection1").item(0).getTextContent();
					String intersection2 = eElement.getElementsByTagName("intersection2").item(0).getTextContent();

					Intersection i1 = null;
					Intersection i2 = null;

					if (!intersection1.equals("null")) {
						i1 = intersections.get(intersection1);

					}
					if (!intersection2.equals("null")) {
						i2 = intersections.get(intersection2);
					}

					Direction d = null;
					if (direction.equals("right")) {
						d = Direction.RIGHT;
					} else if (direction.equals("left")) {
						d = Direction.LEFT;
					} else if (direction.equals("up")) {
						d = Direction.UP;
					} else if (direction.equals("down")) {
						d = Direction.DOWN;
					}

					Road r1 = new Road(name, p1, p2, i1, i2, d);
					roads.put(name, r1);

					if (i2 != null) {
						if (direction.equals("right")) {
							i2.addInRoad(r1);
						} else if (direction.equals("left")) {
							i2.addOutRoad(r1);
						} else if (direction.equals("up")) {
							i2.addOutRoad(r1);
						} else if (direction.equals("down")) {
							i2.addInRoad(r1);
						}
					}

					if (i1 != null) {
						if (direction.equals("right")) {
							i1.addOutRoad(r1);
						} else if (direction.equals("left")) {
							i1.addInRoad(r1);
						} else if (direction.equals("up")) {
							i1.addInRoad(r1);
						} else if (direction.equals("down")) {
							i1.addOutRoad(r1);
						}
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return roads;
	}

	public HashMap<String, ArrayList<String>> getPaths() {

		HashMap<String, ArrayList<String>> paths = new HashMap<String, ArrayList<String>>();

		try {

			Node pathsNode = doc.getElementsByTagName("paths").item(0);

			NodeList pathsNodeList = pathsNode.getChildNodes();

			for (int i = 0; i < pathsNodeList.getLength(); i++) {

				if (pathsNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) pathsNodeList.item(i);

					String name = eElement.getAttribute("name");

					NodeList namees = eElement.getElementsByTagName("name");

					ArrayList<String> aux = new ArrayList<String>();

					for (int j = 0; j < namees.getLength(); j++) {

						if (namees.item(j).getNodeType() == Node.ELEMENT_NODE) {
							Element newEle = (Element) namees.item(j);
							aux.add(newEle.getTextContent());
						}
					}

					paths.put(name, aux);

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return paths;
	}

	
	public Integer getMapSize() {

		Integer res = null;

		try {
			Node map_size = doc.getElementsByTagName("size").item(0);
			res = Integer.parseInt(map_size.getTextContent());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}
}
