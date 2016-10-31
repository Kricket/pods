package pods.controller.nn;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import pods.controller.ControllerWrapper;
import world.PodWorld;
import genetic.IndividualFactory;

/**
 * Factory to generate instances of the GeNNController.
 */
public class GeNNControllerFactory implements IndividualFactory<GeNNController> {
	private final Collection<PodWorld> worlds;
	private final int[] layers;

	/**
	 * @param worlds The worlds to use for training.
	 * @param nLayers The sizes of each hidden layer.
	 */
	public GeNNControllerFactory(Collection<PodWorld> worlds, int ...nLayers) {
		this.worlds = worlds;
		this.layers = nLayers;
	}
	
	public Iterable<GeNNController> generate(int num) {
		ControllerWrapper wrapper = new ControllerWrapper();
		for(PodWorld world : worlds)
			world.addPlayer(wrapper);
		
		Set<GeNNController> set = new HashSet<GeNNController>();
		for(int i=0; i<num; i++) {
			set.add(new GeNNController(worlds, wrapper, layers));
		}
		return set;
	}
}
