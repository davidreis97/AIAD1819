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

		// Cars in the road
		ArrayList<Car> cars = new ArrayList<Car>();
		
		// create a new car in the road 1  
		Car c1 = new Car("1");
		cars.add(c1);
		
		//CarControllerAgent
		CarControllerAgent carController = new CarControllerAgent();
		
		AgentController ac2;
		try {
			ac2 = mainContainer.acceptNewAgent("CarControllerAgent", carController);
			ac2.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
		
		AgentController ac1;
		try {
			ac1 = mainContainer.acceptNewAgent("car1", c1);
			ac1.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}

		AgentController ac3;
		try {
			ac3 = mainContainer.acceptNewAgent("myRMA", new jade.tools.rma.rma());
			ac3.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		 
		/* Draw the map */
		Map mapa = new Map(cars);

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
