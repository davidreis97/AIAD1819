package src.main;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

 
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
 
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import src.agents.CarSpawner;
import src.agents.IntersectionAgent;
import src.agents.RoadAgent;
import src.graph.Intersection;
import src.graph.Map;
import src.graph.Reader;
import src.graph.Road;
import src.resources.CMDArgs;

public class JADELauncher {
	
	public static void main(String[] args) {
		
		CMDArgs.ALGORITHM = args[0];
		CMDArgs.SPAWN_RATE = Integer.parseInt(args[1]);
		CMDArgs.MAP_FILE = args[2];

		Runtime rt = Runtime.instance();
	 
		Profile p1 = new ProfileImpl();
		ContainerController mainContainer = rt.createMainContainer(p1);
		
		
		Reader reader = null;
        try {
            reader = new Reader(CMDArgs.MAP_FILE);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

    	
    	HashMap<String, Intersection> intersections = reader.getIntersections();
    	HashMap<String, Road> roads = reader.getRoads(intersections);
    	int size = reader.getMapSize();
    	
		/* Map */ 
		Map mapa = new Map(intersections, roads, size);
		
		
		/* Intersection agents */
		
		AgentController ac1;
		
		for(int i=1; i<=Map.intersections.size(); i++) {
			try {
				ac1 = mainContainer.acceptNewAgent("IntersectionAgent"+i, new IntersectionAgent(i));
				ac1.start();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
		//intersections

		/* RMA */
		AgentController ac2;
		try {
			ac2 = mainContainer.acceptNewAgent("myRMA", new jade.tools.rma.rma());
			ac2.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		

		/* Road agents */
		AgentController ac4;
		
		for(int i=1; i<=Map.roads.size(); i++) {
			
			try {
				ac4 = mainContainer.acceptNewAgent("RoadAgent"+i, new RoadAgent(roads.get(""+i).maxCars));

				ac4.start();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
			
		}

		/* CarSpawner */
		CarSpawner carSpawner = new CarSpawner(mainContainer, mapa);
		
		AgentController ac3;
		try {
			ac3 = mainContainer.acceptNewAgent("CarSpawner", carSpawner);
			ac3.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
		
		
		/* Draw the map */
		Runnable myRunnable = new Runnable() {
			public void run() {

				while (true) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
					mapa.repaint();
				}
			}
		};
		myRunnable.run();
		
	}

}
