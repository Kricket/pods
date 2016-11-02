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
		GeNNController.prepare(worlds);
		System.out.println("Number of worlds: " + worlds.size());
		
		Population<GeNNController> population = new Population<GeNNController>(500, new GeNNControllerFactory(10));
		GeNNController.STEPS_FOR_FITNESS = 30;
		for(int i=0; i<100; i++) {
			if(i % 10 == 0) {
				GeNNController.setupNextTest();
			}
			population.newGeneration();
		}
		
		GeNNController.STEPS_FOR_FITNESS = 100;
		for(int i=0; i<100; i++) {
			if(i % 10 == 0) {
				GeNNController.setupNextTest();
			}
			population.newGeneration();
		}

		ArrayList<GeNNController> pop = new ArrayList<GeNNController>(population.getPopulation());
		Collections.sort(pop);
		
		pop.get(0).setActivePlayer();
		
		PodWorld world = worlds.iterator().next();
		world.reset();
		world.addPlayer(new SimpleController());
		new Drawer(world);
	}

	private static HashSet<PodWorld> buildWorlds() {
		HashSet<PodWorld> worlds = new HashSet<PodWorld>();
		boolean dist = false;
		for(double angle = Math.PI/11; angle < 2*Math.PI; angle += 2.*Math.PI/7) {
			dist = !dist;
			Vec nextCheck = Vec.UNIT.rotate(angle).times(6000. + (dist ? 2000. : 0.)).plus(MID);
			
			worlds.add(new PodWorld(Arrays.asList(MID, nextCheck)));
		}
		
		return worlds;
	}
	
	private static final Vec MID = new Vec(PodWorld.WORLD_X/2, PodWorld.WORLD_Y/2);
}
