package nn;

import util.Matrix;
import genetic.Individual;

/**
 * Basic feedforward neural network for use with genetic learning
 * @param <Self> Set to the type of your class
 */
public abstract class GeNN<Self extends Individual<?>> extends Individual<Self> {
	/**
	 * All layers, except the first one
	 */
	protected final Matrix[] weights;
	protected final Matrix[] biases;
	
	/**
	 * @param nLayers The number of neurons in each layer, from input to output
	 */
	public GeNN(int ...nLayers) {
		weights = new Matrix[nLayers.length - 1];
		biases = new Matrix[nLayers.length - 1];
		for(int i=0; i<weights.length; i++) {
			weights[i] = Matrix.random(nLayers[i+1], nLayers[i], getInitialRange());
			biases[i] = Matrix.random(nLayers[i+1], 1, getInitialRange());
		}
	}
	
	public GeNN(Matrix[] layersCopy, Matrix[] biasesCopy) {
		weights = new Matrix[layersCopy.length];
		biases = new Matrix[biasesCopy.length];
		for(int i=0; i<weights.length; i++) {
			weights[i] = layersCopy[i].copy();
			biases[i] = biasesCopy[i].copy();
		}
	}
	
	/**
	 * Get the initial range of random values available to initialize the matrices.
	 */
	protected abstract double getInitialRange();

	/**
	 * Get the number of neurons in the given layer.
	 * @param layer 0 is the input layer; the last is the output layer
	 */
	public int numNeurons(int layer) {
		if(layer == weights.length)
			return weights[weights.length - 1].rows;
		return weights[layer].cols;
	}
	
	/**
	 * Feed the given input forward through this matrix.
	 * @param input
	 * @return
	 */
	public Matrix forward(Matrix input) {
		for(int i=0; i<weights.length-1; i++) {
			input = sigma(weights[i].times(input).plusEquals(biases[i]));
		}
		input = weights[weights.length-1].times(input).plusEquals(biases[weights.length-1]);
		return input;
	}
	
	/**
	 * The sigma function, for smoothing.
	 * @param z
	 * @return
	 */
	public static double sigma(double z) {
		return 1. / (Math.expm1(-z) + 2.);
	}
	
	/**
	 * Apply the sigma function to every element of the given Matrix. Note that this will CHANGE the matrix!
	 */
	public static Matrix sigma(Matrix m) {
		for(int i=0; i<m.data.length; i++)
			m.data[i] = sigma(m.data[i]);
		return m;
	}
}
