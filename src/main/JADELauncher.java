package src.main;

import java.util.ArrayList;
import javax.swing.SwingUtilities;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import src.agents.CarSpawner;
import src.agents.IntersectionAgent;
import src.agents.RoadAgent;
import src.graph.Map;

public class JADELauncher {

	public static void main(String[] args) {

		Runtime rt = Runtime.instance();

		Profile p1 = new ProfileImpl();
		ContainerController mainContainer = rt.createMainContainer(p1);
		
		
		/* Map */ 
		Map mapa = new Map();
		
		
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
				ac4 = mainContainer.acceptNewAgent("RoadAgent"+i, new RoadAgent());

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
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
					mapa.repaint();
				}
			}
		};
		myRunnable.run();
		
	}

}
