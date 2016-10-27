package pods;

import genetic.Population;
import pods.controller.nn.GeNNController;
import pods.controller.nn.GeNNControllerFactory;
import world.PodWorld;

public class Main {
	public static void main(String[] args) {
		PodWorld world = new PodWorld();
		Population<GeNNController> population = new Population<GeNNController>(10000, new GeNNControllerFactory(world));
		for(int i=0; i<10000; i++)
			population.newGeneration();
//		world.addPlayer(new HumanController());
//		world.addPlayer(new SimpleController());
//		new Drawer(world);
	}
}
