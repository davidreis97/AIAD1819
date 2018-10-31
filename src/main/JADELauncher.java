package main;

import java.util.ArrayList;
import javax.swing.SwingUtilities;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class JADELauncher {

	public static void main(String[] args) {

		Runtime rt = Runtime.instance();

		Profile p1 = new ProfileImpl();
		ContainerController mainContainer = rt.createMainContainer(p1);
				
		/* Intersection agent */
		IntersectionAgent carController = new IntersectionAgent();
		
		AgentController ac1;
		try {
			ac1 = mainContainer.acceptNewAgent("IntersectionAgent", carController);
			ac1.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}

		/* RMA */
		AgentController ac2;
		try {
			ac2 = mainContainer.acceptNewAgent("myRMA", new jade.tools.rma.rma());
			ac2.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
		
		/* Map */ 
		Map mapa = new Map();
		
		/* CarSpawner */
		CarSpawner carSpawner = new CarSpawner(mainContainer, mapa);
		
		AgentController ac3;
		try {
			ac3 = mainContainer.acceptNewAgent("CarSpawner", carSpawner);
			ac3.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
		
		/* Road agents */
		AgentController ac4;
		
		for(int i=1; i<=4; i++) {
			
			try {
				ac4 = mainContainer.acceptNewAgent("RoadAgent"+i, new RoadAgent());
				ac4.start();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
			
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
