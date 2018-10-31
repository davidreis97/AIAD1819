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
				
		//CarControllerAgent
		CarControllerAgent carController = new CarControllerAgent();
		
		AgentController ac1;
		try {
			ac1 = mainContainer.acceptNewAgent("CarControllerAgent", carController);
			ac1.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}


		//RMA
		AgentController ac2;
		try {
			ac2 = mainContainer.acceptNewAgent("myRMA", new jade.tools.rma.rma());
			ac2.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}

		 
		/* Draw the map */
		Map mapa = new Map(new ArrayList<Car>());

		//CarSpawner
		CarSpawner carSpawner = new CarSpawner(mainContainer, mapa);
				
		AgentController ac3;
		try {
			ac3 = mainContainer.acceptNewAgent("CarSpawner", carSpawner);
			ac3.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
		
		Runnable myRunnable = new Runnable() {
			public void run() {

				while (true) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
					}
					mapa.repaint();
				}
			}
		};
		myRunnable.run();
		
	}

}
