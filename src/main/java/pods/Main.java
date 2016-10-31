package pods;

import genetic.Population;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import pods.controller.SimpleController;
import pods.controller.nn.GeNNController;
import pods.controller.nn.GeNNControllerFactory;
import util.Vec;
import world.Drawer;
import world.PodWorld;

public class Main {
	public static void main(String[] args) {
		HashSet<PodWorld> worlds = buildWorlds();
		
		Population<GeNNController> population = new Population<GeNNController>(100, new GeNNControllerFactory(worlds));
		for(int i=0; i<1000; i++)
			population.newGeneration();
		ArrayList<GeNNController> pop = new ArrayList<GeNNController>(population.getPopulation());
		Collections.sort(pop);
		PodWorld world = worlds.iterator().next();
		pop.get(0).setActivePlayer(world);
		world.reset();
		world.addPlayer(new SimpleController());
		new Drawer(world);
	}

	private static HashSet<PodWorld> buildWorlds() {
		HashSet<PodWorld> worlds = new HashSet<PodWorld>();
		worlds.add(new PodWorld(Arrays.asList(CORN11, CORN01, CORN00, CORN10)));
		worlds.add(new PodWorld(Arrays.asList(CORN00, CORN01, CORN11, CORN10)));
		worlds.add(new PodWorld(Arrays.asList(CORN00, CORN11, CORN01, CORN10)));
		worlds.add(new PodWorld(Arrays.asList(CORN00, MID, CORN11)));
		worlds.add(new PodWorld(Arrays.asList(CORN00, MID, CORN10)));
		return worlds;
	}
	
	private static final Vec
		CORN00 = new Vec(PodWorld.BORDER_PADDING, PodWorld.BORDER_PADDING),
		CORN01 = new Vec(PodWorld.BORDER_PADDING, PodWorld.WORLD_Y - PodWorld.BORDER_PADDING),
		CORN10 = new Vec(PodWorld.WORLD_X - PodWorld.BORDER_PADDING, PodWorld.BORDER_PADDING),
		CORN11 = new Vec(PodWorld.WORLD_X - PodWorld.BORDER_PADDING, PodWorld.WORLD_Y - PodWorld.BORDER_PADDING),
		MID = new Vec(PodWorld.WORLD_X/2, PodWorld.WORLD_Y/2);
}
