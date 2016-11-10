package pods.controller.nn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nn.GeNN;
import pods.controller.Controller;
import pods.controller.ControllerWrapper;
import pods.controller.PlayInput;
import pods.controller.PlayOutput;
import pods.world.PodInfo;
import pods.world.PodWorld;
import util.Matrix;
import util.Vec;

public class GeNNController extends GeNN<GeNNController> implements Controller {
	public static final int INPUT_SIZE = 6, OUTPUT_SIZE = 3;
	public static final int MUT_REPLACE = 0, MUT_SIGN = 1, MUT_MULT = 2, MUT_ADD = 3;
	public static final int CROSS_WEIGHT = 0, CROSS_NEURON = 1, CROSS_LAYER = 2;
	
	private static final double DIR_STRETCH = 1000000., THR_STRETCH = 101.;
	private static final Vec DIR_ADJ = new Vec(-0.5, -0.5);
	
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
	
	/**
	 * Get all GeNNControllers ready to test using the given worlds.
	 * @param _worlds
	 */
	public static void prepare(Collection<PodWorld> worlds) {
		GeNNController.worlds = worlds;
		wrapper = new ControllerWrapper();
		for(PodWorld world : worlds) {
			world.addPlayer(wrapper);
		}
	}

	private static Iterator<PodWorld> worldIt;
	private static Vec currentStartingPos;
	/**
	 * Step to the next world/position configuration
	 */
	public static void setupNextTest() {
		if(worldIt == null || !worldIt.hasNext()) {
			worldIt = worlds.iterator();
		}
		
		currentWorld = worldIt.next();
		currentStartingPos = currentWorld.getCheckpoints().get(0).minus(new Vec(2500, 2500));
	}
	
	private static ControllerWrapper wrapper;
	private static PodWorld currentWorld;
	
	private static Collection<PodWorld> worlds;
	private static final List<Vec> startingVelocities;
	private static final List<Double> startingDirections;
	static {
		startingVelocities = new ArrayList<Vec>();
		for(double angle = 0; angle < 2*Math.PI; angle += Math.PI / 2) {
			for(double mag = 0; mag < 650; mag += 200) {
				startingVelocities.add(Vec.UNIT.rotate(angle).times(mag));
			}
		}
		
		startingDirections = new ArrayList<Double>();
		for(double angle = 0; angle < 2*Math.PI; angle += Math.PI / 2) {
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
	public GeNNController(int ...layers) {
		super(getRealLayers(layers));
	}
	
	/**
	 * Copy constructor. Create another controller with the same weights and biases.
	 * @param parent
	 */
	private GeNNController(GeNNController parent) {
		super(parent.weights, parent.biases);
	}
	
	/**
	 * Set this Controller to be the one that actually plays.
	 */
	public void setActivePlayer() {
		wrapper.controller = this;
	}
	
	@Override
	protected long calculateFitness() {
		wrapper.controller = this;
		long total = 0;
		for(Vec vel : startingVelocities) {
			for(double dir : startingDirections) {
				PodInfo pod = currentWorld.getPod(wrapper);
				pod.pos = currentStartingPos;
				pod.vel = vel;
				pod.angle = dir;
				pod.laps = 0;
				pod.nextCheck = 0;
				total += doCalculateFitness(pod);
			}
		}
		return total;
	}
	
	protected long doCalculateFitness(PodInfo pod) {
		for(int i=0; i<STEPS_FOR_FITNESS; i++) {
			currentWorld.step();
		}
		
		return pod.score(currentWorld);
	}
	
	public PlayOutput play(PlayInput pi) {
		Matrix input = buildInput(pi);
		Matrix output = forward(input);
		return buildOutput(pi, output);
	}
	
	/**
	 * Build the actual Play based on the input and output from the NN.
	 * @param pi
	 * @param output The output of the NN
	 * @return
	 */
	public static PlayOutput buildOutput(PlayInput pi, Matrix output) {
		PlayOutput play = new PlayOutput();
		play.setDir(new Vec(
				// Start with the output direction...
				output.at(0, 0),
				output.at(1, 0))
			// Adjust it so that we can have negative values
			.plus(DIR_ADJ)
			// Stretch it to increase precision (since it will be rounded)
			.times(DIR_STRETCH)
			// Rotate the result, since we standardized the rotation in the input
			.rotate(pi.angle)
			// Move it, since the input is relative to the position
			.plus(pi.pos));
		
		play.setThrust((int) (output.at(2, 0) * THR_STRETCH));
		return play;
	}

	/**
	 * Build a Matrix to feed to the network, equivalent to the given input
	 * @param pi
	 * @return
	 */
	public static Matrix buildInput(PlayInput pi) {
		Matrix m = new Matrix(INPUT_SIZE, 1);
		/*
		 * To reduce the dimensionality of the input/simplify the task of the network:
		 * 1. All checkpoints become relative to the pod. A consequence of this is that all
		 *    scaling is absolute - we don't care what the dimensions of the map are.
		 * 2. All positions are rotated so that the pod is facing straight down the +x axis.
		 *    This should reduce complexity since the network doesn't have to take the pod's
		 *    facing vector into account.
		 */
		Vec vel = pi.vel.rotate(-pi.angle).scale(1. / 700., 1. / 700.);
		Vec nextCheck = pi.nextCheck
				.minus(pi.pos)
				.rotate(-pi.angle)
				.times(0.0001);
		Vec followingCheck = currentWorld.getCheckpoints().get(pi.nextCheckId)
				.minus(pi.pos)
				.rotate(-pi.angle)
				.times(0.0001);
		
		m.set(0, 0, vel.x);
		m.set(1, 0, vel.y);
		m.set(2, 0, nextCheck.x);
		m.set(3, 0, nextCheck.y);
		m.set(4, 0, followingCheck.x);
		m.set(5, 0, followingCheck.y);
		
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
	
	/**
	 * Try all the mutations possible (including none) and return the best result.
	 * @return
	 */
	public GeNNController climb() {
		setupNextTest();
		GeNNController best = this;
		long bestFitness = fitness();
		for(int op : mutations) {
			GeNNController child = clone();
			child.mutate(op);
			long childFitness = child.fitness();
			if(childFitness > bestFitness) {
				bestFitness = childFitness;
				best = child;
			}
		}
		
		return best;
	}

	/**
	 * Use backpropagation to train this network to approximate the same results as the given controller.
	 */
	public void imitate(Controller hero) {
		Gradient grad = null;
		int batchSize = 0;
		for(Vec vel : startingVelocities) {
			for(double dir : startingDirections) {
				PodInfo pod = currentWorld.getPod(wrapper);
				pod.pos = currentStartingPos;
				pod.vel = vel;
				pod.angle = dir;
				pod.laps = 0;
				pod.nextCheck = 0;
				
				PlayInput pi = pod.buildPlayInfo(currentWorld.getCheckpoints());
				
				// What would jesus do?
				PlayOutput heroicPlay = hero.play(pi);
				// Next: what SHOULD my Matrix output be, in order to produce the same result?
				Gradient g = backprop(buildInput(pi), getOutputForPlay(pi, heroicPlay));
				if(grad == null)
					grad = g;
				else
					grad.plusEquals(g);
				batchSize++;
			}
		}
		
		apply(grad, 1. / batchSize);
	}
	
	/**
	 * Generate a Matrix that, were it the output from the NN, would generate the given play.
	 * @param pi The input to the NN.
	 * @param play The play we wish to reproduce
	 * @return 
	 */
	public static Matrix getOutputForPlay(PlayInput pi, PlayOutput play) {
		Matrix output = new Matrix(OUTPUT_SIZE, 1);
		output.data[2] = play.getThrust() / THR_STRETCH;
		Vec playDir = play.getDir().minus(pi.pos).rotate(-pi.angle);
		playDir = playDir.times(1. / playDir.norm()).minus(DIR_ADJ);
		output.data[0] = playDir.x;
		output.data[1] = playDir.y;
		
		return output;
	}
}
