package pods.controller.nn;

import genetic.IndividualFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Factory to generate instances of the GeNNController.
 */
public class GeNNControllerFactory implements IndividualFactory<GeNNController> {
	private final int[] layers;

	/**
	 * @param worlds The worlds to use for training.
	 * @param nLayers The sizes of each hidden layer.
	 */
	public GeNNControllerFactory(int ...nLayers) {
		this.layers = nLayers;
	}
	
	public Iterable<GeNNController> generate(int num) {
		Set<GeNNController> set = new HashSet<GeNNController>();
		for(int i=0; i<num; i++) {
			set.add(new GeNNController(layers));
		}
		return set;
	}
}
