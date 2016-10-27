package nn;

import util.Matrix;
import genetic.Individual;

/**
 * Basic feedforward neural network for use with genetic learning
 * @param <Self> Set to the type of your class
 */
public abstract class GeNN<Self extends Individual<?>> implements Individual<Self> {

	/**
	 * All layers, except the first one
	 */
	protected final Matrix[] layers;
	protected final Matrix[] biases;
	private long lastFitness = -1;
	
	/**
	 * @param nLayers The number of neurons in each layer, from input to output
	 */
	public GeNN(int ...nLayers) {
		layers = new Matrix[nLayers.length - 1];
		biases = new Matrix[nLayers.length - 1];
		for(int i=0; i<layers.length; i++) {
			layers[i] = Matrix.random(nLayers[i+1], nLayers[i], 1);
			biases[i] = Matrix.random(nLayers[i+1], 1, 1);
		}
	}
	
	public GeNN(Matrix[] layersCopy, Matrix[] biasesCopy) {
		layers = new Matrix[layersCopy.length];
		biases = new Matrix[biasesCopy.length];
		for(int i=0; i<layers.length; i++) {
			layers[i] = layersCopy[i].copy();
			biases[i] = biasesCopy[i].copy();
		}
	}

	/**
	 * Get the number of neurons in the given layer.
	 * @param layer 0 is the input layer; the last is the output layer
	 */
	public int numNeurons(int layer) {
		if(layer == layers.length)
			return layers[layers.length - 1].rows;
		return layers[layer].cols;
	}
	
	/**
	 * Feed the given input forward through this matrix.
	 * @param input
	 * @return
	 */
	public Matrix forward(Matrix input) {
		for(int i=0; i<layers.length-1; i++) {
			input = sigma(layers[i].times(input).plusEquals(biases[i]));
		}
		input = layers[layers.length-1].times(input).plusEquals(biases[layers.length-1]);
		return input;
	}
	
	/**
	 * The sigma function, for smoothing.
	 * @param z
	 * @return
	 */
	private double sigma(double z) {
		return 1. / (Math.expm1(-z) + 2.);
	}
	
	/**
	 * Apply the sigma function to every element of the given Matrix. Note that this will CHANGE the matrix!
	 */
	private Matrix sigma(Matrix m) {
		for(int i=0; i<m.data.length; i++)
			m.data[i] = sigma(m.data[i]);
		return m;
	}

	public long fitness() {
		if(lastFitness < 0)
			lastFitness = calculateFitness();
		return lastFitness;
	}

	/**
	 * Actually calculate the fitness value.
	 */
	protected abstract long calculateFitness();

	public void clearFitness() {
		lastFitness = -1;
	}
}