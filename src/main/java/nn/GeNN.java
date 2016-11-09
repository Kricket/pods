package nn;

import util.Activation;
import util.Matrix;
import genetic.Individual;

/**
 * Basic feedforward neural network for use with genetic learning
 * @param <Self> Set to the type of your class
 */
public abstract class GeNN<Self extends Individual<Self>> extends Individual<Self> {
	/**
	 * All layers, except the first one
	 */
	protected final Matrix[] weights;
	protected final Matrix[] biases;
	/**
	 * Number of layers, INCLUDING the input and output.
	 * <br>(thus, num hidden = N-2)
	 */
	protected final int NUM_LAYERS;
	
	/**
	 * @param nLayers The number of neurons in each layer, from input to output
	 */
	public GeNN(int ...nLayers) {
		NUM_LAYERS = nLayers.length;
		weights = new Matrix[nLayers.length - 1];
		biases = new Matrix[nLayers.length - 1];
		for(int i=0; i<weights.length; i++) {
			weights[i] = Matrix.random(nLayers[i+1], nLayers[i], getInitialRange());
			biases[i] = Matrix.random(nLayers[i+1], 1, getInitialRange());
		}
	}
	
	public GeNN(Matrix[] layersCopy, Matrix[] biasesCopy) {
		NUM_LAYERS = layersCopy.length;
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
		for(int i=0; i<weights.length; i++) {
			input = Activation.sigma(weights[i].times(input).plusEquals(biases[i]));
		}
		return input;
	}
	
	/**
	 * Use the backpropagation algorithm to calculate the gradient (partial derivatives) of this network
	 * with respect to the given inputs and outputs.
	 * @param input An input value
	 * @param output The "correct" answer that the network should provide for the given input
	 * @return The gradient of the network
	 */
	public Gradient backprop(Matrix input, Matrix output) {
		Matrix[] grad_Cb = new Matrix[NUM_LAYERS - 1];
		Matrix[] grad_Cw = new Matrix[NUM_LAYERS - 1];
		// The "z" vectors are the non-activated outputs of the SNs of each layer. The first one is null
		// for convenience. Note that they will get modified in-place when we call dSigma! 
		Matrix[] zs = new Matrix[NUM_LAYERS];
		// The activations are the z vectors, with sigma applied. The first one is just the input.
		Matrix[] activations = new Matrix[NUM_LAYERS];
		activations[0] = new Matrix(input.data);
		
		// Step forward through the network, saving the z and sigma(z) on each layer.
		for(int i=1; i<NUM_LAYERS; i++) {
			zs[i] = weights[i-1].times(activations[i-1]).plusEquals(biases[i-1]);
			activations[i] = Activation.sigma(zs[i].copy());
		}

		// Now, start working backwards. We have to start manually with the last layer...
		Matrix delta = activations[NUM_LAYERS-1]
				.minus(output)
				// WITH the following line, we use quadratic cost.
				// WITHOUT, we're using cross-entropy (which should learn faster).
				//.dotTimesEquals(dSigma(zs[NUM_LAYERS-1]))
				;
		grad_Cb[NUM_LAYERS-2] = delta;
		grad_Cw[NUM_LAYERS-2] = delta.timesTranspose(activations[NUM_LAYERS-2]);
		
		// ...and now walk backwards through the remaining layers.
		for(int layer = NUM_LAYERS-2; layer > 0; layer--) {
			// Transform delta from (layer+1) to (layer)
			delta = weights[layer]
					.transposeTimes(delta)
					.dotTimesEquals(Activation.dSigma(zs[layer]));
			
			grad_Cb[layer-1] = delta;
			grad_Cw[layer-1] = delta.timesTranspose(activations[layer-1]);
		}
		
		return new Gradient(grad_Cb, grad_Cw);
	}
	
	/**
	 * Add (grad*factor) to this network. Note that this will CHANGE the network!
	 * @param grad Gradient (calculated using backprop)
	 * @param factor A factor (should be negative to reduce error!)
	 */
	public void apply(Gradient grad, double factor) {
		for(int i=0; i<NUM_LAYERS-1; i++) {
			weights[i].plusEquals(grad.w[i].timesEquals(factor));
			biases[i].plusEquals(grad.b[i].timesEquals(factor));
		}
	}
	
	/**
	 * Gradient of a neural network. Represents the partial derivatives of all the
	 * weights and biases.
	 */
	public static class Gradient {
		public Matrix[] b, w;
		public Gradient(Matrix[] _b, Matrix [] _w) {
			b = _b;
			w = _w;
		}
		public void plusEquals(Gradient other) {
			for(int i=0; i<w.length; i++) {
				w[i].plusEquals(other.w[i]);
				b[i].plusEquals(other.b[i]);
			}
		}
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<b.length; i++) {
				sb.append("WEIGHT ");
				sb.append(i);
				sb.append("\n");
				sb.append(w[i]);
				sb.append("\nBIAS ");
				sb.append(i);
				sb.append("\n");
				sb.append(b[i]);
			}
			return sb.toString();
		}
	}
}
