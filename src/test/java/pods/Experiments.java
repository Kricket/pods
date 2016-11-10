package pods;

import genetic.Population;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import pods.controller.SimpleController;
import pods.controller.nn.GeNNController;
import pods.controller.nn.GeNNControllerFactory;
import pods.controller.tree.TreeSearchController;
import pods.world.Drawer;
import pods.world.PodInfo;
import pods.world.PodWorld;
import util.Vec;

public class Experiments {
	public static void main(String[] args) {
		new Experiments().testBackprop();
	}
	
	public void simple() {
		PodWorld world = new PodWorld();
		world.addPlayer(new SimpleController());
		new Drawer(world);
	}
	
	public void testTree() {
//		PodWorld world = new PodWorld(Arrays.asList(MID, MID.scale(1.5, 0.5)));
		PodWorld world = new PodWorld(5);
		TreeSearchController c = new TreeSearchController(world);
		PodInfo pod = world.getPod(c);
		pod.pos = MID.plus(new Vec(1000, 0));
		world.addPlayer(new SimpleController());
		new Drawer(world);
	}
	
	public void testClimb() {
		HashSet<PodWorld> worlds = buildWorlds(7);
		GeNNController.prepare(worlds);
		
		GeNNController indiv = new GeNNController(10);
		
		GeNNController.STEPS_FOR_FITNESS = 5;
		System.out.println("Steps: " + GeNNController.STEPS_FOR_FITNESS);
		for(int i=0; i<10000; i++) {
			indiv = indiv.climb();
			if(i%100 == 0)
				System.out.println("Best fitness: " + indiv.fitness());
		}
		
		GeNNController.STEPS_FOR_FITNESS = 15;
		System.out.println("Steps: " + GeNNController.STEPS_FOR_FITNESS);
		for(int i=0; i<5000; i++) {
			indiv = indiv.climb();
			if(i%100 == 0)
				System.out.println("Best fitness: " + indiv.fitness());
		}
		
		GeNNController.STEPS_FOR_FITNESS = 50;
		System.out.println("Steps: " + GeNNController.STEPS_FOR_FITNESS);
		for(int i=0; i<1000; i++) {
			indiv = indiv.climb();
			if(i%100 == 0)
				System.out.println("Best fitness: " + indiv.fitness());
		}
		
		PodWorld world = worlds.iterator().next();
		world.reset();
		indiv.setActivePlayer();
		new Drawer(world);
		System.out.println("Running...");
	}
	
	public void testEvolve() {
		HashSet<PodWorld> worlds = buildWorlds(7);
		GeNNController.prepare(worlds);
		System.out.println("Number of worlds: " + worlds.size());
		
		Population<GeNNController> population = new Population<GeNNController>(500, new GeNNControllerFactory());
		GeNNController.STEPS_FOR_FITNESS = 5;
		for(int i=0; i<100; i++) {
			if(i % 10 == 0) {
				GeNNController.setupNextTest();
			}
			population.newGeneration();
		}
		
		GeNNController.STEPS_FOR_FITNESS = 15;
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
	
	public void testBackprop() {
		HashSet<PodWorld> worlds = buildWorlds(7);
		GeNNController.prepare(worlds);
		
		GeNNController genn = new GeNNController(15, 10);
		SimpleController hero = new SimpleController();
		
		for(int i=0; i<100; i++) {
			GeNNController.setupNextTest();
			genn.imitate(hero);
			System.out.println("For " + i + " fitness = " + genn.fitness());
		}
		
		PodWorld world = worlds.iterator().next();
		world.addPlayer(hero);
		new Drawer(world);
	}

	private static HashSet<PodWorld> buildWorlds(int slices) {
		HashSet<PodWorld> worlds = new HashSet<PodWorld>();
		boolean dist = false;
		for(double angle = 0; angle < 2.*Math.PI; angle += 2.*Math.PI/slices) {
			dist = !dist;
			Vec nextCheck = Vec.UNIT.rotate(angle).times(6000. + (dist ? 2000. : 0.)).plus(MID);
			
			worlds.add(new PodWorld(Arrays.asList(MID, nextCheck)));
		}
		
		return worlds;
	}
	
	private static final Vec MID = new Vec(PodWorld.WORLD_X/2, PodWorld.WORLD_Y/2);

}
