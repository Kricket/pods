package pods.controller.tree;

import java.util.ArrayList;
import java.util.List;

import pods.controller.PlayOutput;
import pods.world.PodInfo;
import pods.world.PodWorld;
import util.Vec;

public class PodNode {
	/**
	 * The state of the pod at this node.
	 */
	private PodInfo pod;
	/**
	 * The play that was performed to get to this state.
	 */
	private PlayOutput play;
	private List<PodNode> children = null;
	
	public PodNode(PodInfo pod, PlayOutput play) {
		this.pod = pod;
		this.play = play;
	}

	public List<PodNode> getChildren(PodWorld world) {
		if(children == null) {
			children = new ArrayList<PodNode>();
			for(double angle = pod.angle - PodWorld.MAX_TURN; angle < pod.angle + PodWorld.MAX_TURN + 0.01; angle += PodWorld.MAX_TURN) {
				for(int thrust = 2; thrust < 101; thrust += 49) {
					PlayOutput play = new PlayOutput();
					play.setDir(Vec.BIGUNIT.rotate(angle));
					play.setThrust(thrust);
					children.add(new PodNode(world.stepTest(pod, play), play));
				}
			}
			
			// Kill children that are obviously too stupid :)
		}
		
		return children;
	}

	/**
	 * Return the child with the best score at the given depth.
	 * @param depth The depth to which to search.
	 * @param world
	 * @return
	 */
	public PodNode bestPlay(int depth, PodWorld world) {
		PodNode best = null;
		long bestScore = -1;
		for(PodNode child : getChildren(world)) {
			long childScore = child.bestChildScore(world, depth);
			if(best == null || bestScore < childScore) {
				bestScore = childScore;
				best = child;
			}
		}
		System.out.println("Best score is: " + bestScore);
		return best;
	}

	private long bestChildScore(PodWorld world, int depth) {
		if(depth == 0)
			return pod.score(world);
		
		long bestScore = -1;
		for(PodNode child : getChildren(world)) {
			long childScore = child.bestChildScore(world, depth-1);
			if(childScore > bestScore)
				bestScore = childScore;
		}
		
		return bestScore;
	}

	public PlayOutput getPlay() {
		return play;
	}
}
