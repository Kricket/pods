package pods.world;

import java.util.List;

import pods.controller.PlayInput;
import util.Vec;

/**
 * Simple bean to hold basic information about a Pod. This is used internally by the PodWorld.
 */
public class PodInfo {
	public static final long CHECK_BONUS = (long) (PodWorld.WORLD_X + PodWorld.WORLD_Y);
	
	public Vec pos;
	public Vec vel = Vec.ORIGIN;
	/**
	 * NOTE: in the real game, this is in degrees
	 */
	public double angle = 0;
	public int nextCheck = 0;
	public int laps = 0;
	
	public PlayInput buildPlayInfo(List<Vec> checkpoints) {
		PlayInput p = new PlayInput();
		p.angle = angle;
		p.pos = pos.truncate();
		p.nextCheck = checkpoints.get(nextCheck);
		p.nextCheckId = nextCheck;
		p.vel = vel;
		return p;
	}
	
	/**
	 * Get a score for how far along this pod is. Higher values are better.
	 * @param world
	 * @return
	 */
	public long score(PodWorld world) {
		// Every checkpoint passed gives a bonus (equal to a rough estimate of the max distance between checks)
		long score = (laps * world.getCheckpoints().size() + nextCheck) * CHECK_BONUS;
		
		// After that, extra points for being closer to the following check
		double dist = pos.minus(world.getCheckpoints().get(nextCheck)).norm();
		if(dist < CHECK_BONUS)
			score += CHECK_BONUS - dist;
		
		return score;
	}
}
