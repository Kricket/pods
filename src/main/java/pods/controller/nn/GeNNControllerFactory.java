package pods.controller.nn;

import java.util.HashSet;
import java.util.Set;

import pods.controller.ControllerWrapper;
import world.PodWorld;
import genetic.IndividualFactory;

public class GeNNControllerFactory implements IndividualFactory<GeNNController> {
	private PodWorld world;
	private int[] layers;

	public GeNNControllerFactory(PodWorld world, int ...nLayers) {
		this.world = world;
		this.layers = nLayers;
	}
	
	public Iterable<GeNNController> generate(int num) {
		ControllerWrapper wrapper = new ControllerWrapper();
		world.addPlayer(wrapper);
		
		Set<GeNNController> set = new HashSet<GeNNController>();
		for(int i=0; i<num; i++) {
			set.add(new GeNNController(world, wrapper, layers));
		}
		return set;
	}
}
