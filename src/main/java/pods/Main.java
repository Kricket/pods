package pods;

import genetic.Population;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import pods.controller.SimpleController;
import pods.controller.nn.GeNNController;
import pods.controller.nn.GeNNControllerFactory;
import util.Vec;
import world.Drawer;
import world.PodWorld;

public class Main {
	public static void main(String[] args) {
		HashSet<PodWorld> worlds = buildWorlds();
		
		Population<GeNNController> population = new Population<GeNNController>(100, new GeNNControllerFactory(worlds, 15));
		GeNNController.STEPS_FOR_FITNESS = 10;
		for(int i=0; i<50; i++)
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
		int idx = 0;
		for(double angle = Math.PI/11; angle < 2*Math.PI; angle += 2.*Math.PI/7) {
			if(idx == CHECKS.size())
				idx = 0;
			
			Vec check = CHECKS.get(idx++);
			Vec nextCheck = Vec.UNIT.rotate(angle).times(6000.).plus(check);
			
			worlds.add(new PodWorld(Arrays.asList(check, nextCheck)));
		}
		
		return worlds;
	}
	
	private static final Vec
		CORN00 = new Vec(PodWorld.BORDER_PADDING, PodWorld.BORDER_PADDING),
		CORN01 = new Vec(PodWorld.BORDER_PADDING, PodWorld.WORLD_Y - PodWorld.BORDER_PADDING),
		CORN10 = new Vec(PodWorld.WORLD_X - PodWorld.BORDER_PADDING, PodWorld.BORDER_PADDING),
		CORN11 = new Vec(PodWorld.WORLD_X - PodWorld.BORDER_PADDING, PodWorld.WORLD_Y - PodWorld.BORDER_PADDING),
		MID = new Vec(PodWorld.WORLD_X/2, PodWorld.WORLD_Y/2);
	private static final List<Vec> CHECKS = Arrays.asList(CORN00, CORN01, CORN10, CORN11, MID);
}
