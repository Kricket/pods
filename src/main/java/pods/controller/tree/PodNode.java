package pods.controller.tree;

import java.util.ArrayList;
import java.util.List;

import pods.controller.PlayInput;
import pods.controller.PlayOutput;
import pods.world.PodInfo;
import pods.world.PodWorld;
import util.Vec;

public class PodNode {
	public static final int ADVANCE_STEPS = 2;
	public static final int DEPTH = 8;

	public static enum Turn {
		NEG, POS, ZERO;

		public double getAngle(double angle) {
			switch(this) {
			case NEG:
				return angle - PodWorld.MAX_TURN;
			case POS:
				return angle + PodWorld.MAX_TURN;
			case ZERO:
			}
			return angle;
		}
	}
	
	/**
	 * The state of the pod at this node.
	 */
	PodInfo pod;
	/**
	 * The play that was performed to get to this state.
	 */
	private List<PodNode> children = null;
	private int thrust;
	private Turn turn;
	
	public PodNode(PodInfo pod, Turn turn, int thrust) {
		this.pod = pod;
		this.thrust = thrust;
		this.turn = turn;
	}

	public List<PodNode> getChildren(PodWorld world) {
		if(children == null) {
			children = new ArrayList<PodNode>();
			for(int thrust = 100; thrust < 101; thrust += 50) {
				for(Turn t : Turn.values()) {
					children.add(generateChild(world, t, thrust));
				}
			}
		}
		
		return children;
	}

	private PodNode generateChild(PodWorld world, Turn t, int thr) {
		PodInfo nextPod = pod;
		for(int i=0; i<ADVANCE_STEPS; i++) {
			PlayOutput play = new PlayOutput();
			play.setThrust(thr);
			play.setDir(Vec.BIGUNIT.rotate(t.getAngle(nextPod.angle)));
			nextPod = world.stepTest(nextPod, play);
		}
		return new PodNode(nextPod, t, thr);
	}

	/**
	 * Return the child with the best score at the given depth.
	 * @param depth The depth to which to search.
	 * @param world
	 * @return
	 */
	public PodNode bestPlay(PodWorld world) {
		PodNode best = null;
		long bestScore = -1;
		for(PodNode child : getChildren(world)) {
			long childScore = child.bestChildScore(world, DEPTH);
			if(best == null || bestScore < childScore) {
				bestScore = childScore;
				best = child;
			}
		}
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

	/**
	 * With the given play input, generate the play output corresponding to what this pod does.
	 * @param pi
	 * @return
	 */
	public PlayOutput getPlay(PlayInput pi) {
		PlayOutput play = new PlayOutput();
		play.setThrust(thrust);
		play.setDir(Vec.BIGUNIT.rotate(turn.getAngle(pi.angle)));
		return play;
	}
}
