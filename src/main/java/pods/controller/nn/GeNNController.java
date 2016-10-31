package pods.controller.nn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nn.GeNN;
import pods.controller.Controller;
import pods.controller.ControllerWrapper;
import pods.controller.PlayInput;
import pods.controller.PlayOutput;
import util.Matrix;
import util.Vec;
import world.PodInfo;
import world.PodWorld;

public class GeNNController extends GeNN<GeNNController> implements Controller {
	public static final long CHECK_BONUS = (long) (PodWorld.WORLD_X + PodWorld.WORLD_Y);
	public static final int INPUT_SIZE = 10, OUTPUT_SIZE = 3;
	public static final int MUT_REPLACE = 0, MUT_SIGN = 1, MUT_MULT = 2, MUT_ADD = 3;
	public static final int CROSS_WEIGHT = 0, CROSS_NEURON = 1, CROSS_LAYER = 2;
	private static final Set<Integer> mutations, crossovers;
	static {
		Set<Integer> s = new HashSet<Integer>();
		s.add(MUT_REPLACE);
		s.add(MUT_SIGN);
		s.add(MUT_MULT);
		s.add(MUT_ADD);
		mutations = Collections.unmodifiableSet(s);
		
		s = new HashSet<Integer>();
		s.add(CROSS_LAYER);
		s.add(CROSS_NEURON);
		s.add(CROSS_WEIGHT);
		crossovers = Collections.unmodifiableSet(s);
	}
	
	/**
	 * The number of steps to go through in the world to calculate fitness.
	 */
	public static int STEPS_FOR_FITNESS = 10;
	
	private Collection<PodWorld> worlds;
	private ControllerWrapper wrapper;
	private PodWorld currentWorld;
	
	private static Map<PodWorld, List<Vec>> startingPositions = new HashMap<PodWorld, List<Vec>>();
	private static final List<Vec> startingVelocities;
	private static final List<Double> startingDirections;
	static {
		startingVelocities = new ArrayList<Vec>();
		for(double angle = 0; angle < 2*Math.PI; angle+= Math.PI / 3.5) {
			for(double mag = 0; mag < 650; mag += 200) {
				startingVelocities.add(Vec.UNIT.rotate(angle).times(mag));
			}
		}
		
		startingDirections = new ArrayList<Double>();
		for(double angle = 0; angle < 2*Math.PI; angle+= Math.PI / 4.5) {
			startingDirections.add(angle);
		}
	}
	
	/**
	 * Append the input and output layers to the given list of layer sizes.
	 */
	private static int[] getRealLayers(int ...layers) {
		int[] realLayers = new int[layers.length + 2];
		realLayers[0] = INPUT_SIZE;
		realLayers[realLayers.length-1] = OUTPUT_SIZE;
		for(int i=0; i<layers.length; i++)
			realLayers[i+1] = layers[i];
		return realLayers;
	}

	/**
	 * @param world
	 * @param wrapper The wrapper that is used to substitute this Controller when calculating fitness
	 * @param layers Sizes of the HIDDEN LAYERS ONLY! Input and output layers will automatically be added.
	 */
	public GeNNController(Collection<PodWorld> worlds, ControllerWrapper wrapper, int ...layers) {
		super(getRealLayers(layers));
		
		this.worlds = worlds;
		this.wrapper = wrapper;
		
		for(PodWorld world : worlds) {
			if(!startingPositions.containsKey(world))
				buildStartingPositions(world);
		}
	}
	
	private static void buildStartingPositions(PodWorld world) {
		List<Vec> positions = new ArrayList<Vec>();
		Vec check = world.getCheckpoints().get(0);
		for(double angle=0; angle < 2*Math.PI; angle+=Math.PI/2.5) {
			positions.add(Vec.UNIT.rotate(angle).times(3000.).plus(check));
		}
		startingPositions.put(world, positions);
	}

	/**
	 * Copy constructor. Create another controller with the same weights and biases.
	 * @param parent
	 */
	private GeNNController(GeNNController parent) {
		super(parent.weights, parent.biases);
		worlds = parent.worlds;
		wrapper = parent.wrapper;
	}
	
	/**
	 * Set this Controller to be the one that actually plays.
	 * @param world The world to play
	 */
	public void setActivePlayer(PodWorld world) {
		wrapper.controller = this;
		currentWorld = world;
	}
	
	@Override
	protected long calculateFitness() {
		wrapper.controller = this;
		long total = 0;
		for(PodWorld world : worlds) {
			currentWorld = world;
			for(Vec pos : startingPositions.get(world)) {
				for(Vec vel : startingVelocities) {
					for(double dir : startingDirections) {
						world.reset();
						PodInfo pod = world.getPod(wrapper);
						pod.pos = pos;
						pod.vel = vel;
						pod.angle = dir;
						total += doCalculateFitness(pod);
					}
				}
			}
		}
		return total;
	}
	
	protected long doCalculateFitness(PodInfo pod) {
		for(int i=0; i<STEPS_FOR_FITNESS; i++) {
			currentWorld.step();
		}
		
		// Every checkpoint passed gives a bonus (equal to a rough estimate of the max distance between checks)
		long score = (pod.laps * currentWorld.getCheckpoints().size() + pod.nextCheck) * CHECK_BONUS;
		
		// After that, extra points for being closer to the following check
		double dist = pod.pos.minus(currentWorld.getCheckpoints().get(pod.nextCheck)).norm();
		if(dist < CHECK_BONUS)
			score += CHECK_BONUS - dist;
		
		return score;
	}
	
	public PlayOutput play(PlayInput pi) {
		Matrix input = buildInput(pi);
		Matrix output = forward(input);
		
		PlayOutput play = new PlayOutput();
		play.setDir(new Vec((output.at(0, 0) - 0.5) * 2. * PodWorld.WORLD_X, (output.at(1, 0) - 0.5) * 2. * PodWorld.WORLD_Y));
		
		play.setThrust((int) (output.at(2, 0) * 101.));
		return play;
	}

	/**
	 * Build a Matrix to feed to the network, equivalent to the given input
	 * @param pi
	 * @return
	 */
	private Matrix buildInput(PlayInput pi) {
		Matrix m = new Matrix(INPUT_SIZE, 1);
		Vec dir = Vec.UNIT.rotate(pi.angle);
		Vec pos = pi.pos.scale(1. / PodWorld.WORLD_X, 1. / PodWorld.WORLD_Y);
		Vec vel = pi.vel.scale(1. / 700., 1. / 700.);
		Vec nextCheck = pi.nextCheck.scale(1. / PodWorld.WORLD_X, 1. / PodWorld.WORLD_Y);
		Vec followingCheck = currentWorld.getCheckpoints().get(pi.nextCheckId).scale(1. / PodWorld.WORLD_X, 1. / PodWorld.WORLD_Y);
		
		m.set(0, 0, pos.x);
		m.set(1, 0, pos.y);
		m.set(2, 0, vel.x);
		m.set(3, 0, vel.y);
		m.set(4, 0, dir.x);
		m.set(5, 0, dir.y);
		m.set(6, 0, nextCheck.x);
		m.set(7, 0, nextCheck.y);
		m.set(8, 0, followingCheck.x);
		m.set(9, 0, followingCheck.y);
		
		return m;
	}

	public void mutate(int op) {
		Matrix m;
		int layer = (int) (Math.random() * weights.length);
		if(Math.random() > 0.5) {
			m = weights[layer];
		} else {
			m = biases[layer];
		}
		int idx = (int) (Math.random() * m.data.length);
		
		switch(op) {
		case MUT_REPLACE:
			m.data[idx] = (Math.random() - 0.5) * getInitialRange();
			break;
		case MUT_SIGN:
			m.data[idx] *= -1;
			break;
		case MUT_MULT:
			m.data[idx] *= (Math.random() + 0.5);
			break;
		case MUT_ADD:
			m.data[idx] += (Math.random() - .5);
			break;
		}
	}

	public GeNNController crossover(GeNNController partner, int op) {
		GeNNController baby = new GeNNController(this);
		Matrix b, p;
		int layer = (int) (Math.random() * weights.length);
		
		switch(op) {
		case CROSS_WEIGHT:
			if(Math.random() > 0.5) {
				b = baby.weights[layer];
				p = partner.weights[layer];
			} else {
				b = baby.biases[layer];
				p = partner.biases[layer];
			}
			int idx = (int) (Math.random() * b.data.length);
			b.data[idx] = p.data[idx];
			break;
		case CROSS_NEURON:
			b = baby.weights[layer];
			p = partner.weights[layer];
			
			int neuron = (int) (Math.random() * b.rows);
			for(int col=0; col<b.cols; col++) {
				b.set(neuron, col, p.at(neuron, col));
			}
			baby.biases[layer].set(neuron, 0, partner.biases[layer].at(neuron, 0));
			break;
		case CROSS_LAYER:
			baby.weights[layer] = partner.weights[layer].copy();
			baby.biases[layer] = partner.biases[layer].copy();
			break;
		}
		
		return baby;
	}

	public Set<Integer> mutationOperations() {
		return mutations;
	}

	public Set<Integer> crossoverOperations() {
		return crossovers;
	}

	@Override
	protected double getInitialRange() {
		return .2;
	}

	@Override
	public GeNNController clone() {
		return new GeNNController(this);
	}
}
