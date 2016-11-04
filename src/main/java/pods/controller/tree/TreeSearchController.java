package pods.controller.tree;

import pods.controller.Controller;
import pods.controller.PlayInput;
import pods.controller.PlayOutput;
import pods.world.PodWorld;

public class TreeSearchController implements Controller {
	private static final int DEPTH = 4;
	private PodWorld world;
	private PodNode root;

	public TreeSearchController(PodWorld world) {
		this.world = world;
		world.addPlayer(this);
		root = new PodNode(world.getPod(this), null);
	}

	public PlayOutput play(PlayInput pi) {
		root = root.bestPlay(DEPTH, world);
		return root.getPlay();
	}

}
