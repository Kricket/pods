package pods;

import pods.controller.HumanController;
import pods.controller.SimpleController;
import world.Drawer;
import world.PodWorld;

public class Main {
	public static void main(String[] args) {
		PodWorld world = new PodWorld();
		world.addPlayer(new HumanController());
		world.addPlayer(new SimpleController());
		new Drawer(world);
	}
}
