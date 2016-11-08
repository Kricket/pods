package pods.controller.tree;

import pods.controller.Controller;
import pods.controller.PlayInput;
import pods.controller.PlayOutput;
import pods.controller.tree.PodNode.Turn;
import pods.world.PodWorld;

public class TreeSearchController implements Controller {
	private PodWorld world;
	private PodNode root;
	private int timesToPlay = 0;

	public TreeSearchController(PodWorld world) {
		this.world = world;
		world.addPlayer(this);
		root = new PodNode(world.getPod(this), Turn.ZERO, 0);
	}

	public PlayOutput play(PlayInput pi) {
		if(timesToPlay == 0) {
			root = root.bestPlay(world);
			timesToPlay = PodNode.ADVANCE_STEPS;
		}
		
		timesToPlay--;
		return root.getPlay(pi);
	}

}
