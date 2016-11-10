package pods.world;

import java.util.List;

import pods.controller.PlayInput;
import util.Vec;

/**
 * Simple bean to hold basic information about a Pod. This is used internally by the PodWorld.
 */
public class PodInfo {
	public static final long CHECK_BONUS = (long) (PodWorld.WORLD_X + PodWorld.WORLD_Y);
	
	/**
	 * Absolute coordinates of this pod
	 */
	public Vec pos;
	/**
	 * Velocity of this pod
	 */
	public Vec vel = Vec.ORIGIN;
	/**
	 * NOTE: in the real game, this is in degrees
	 */
	public double angle = 0;
	/**
	 * ID (index) of the next checkpoint
	 */
	public int nextCheck = 0;
	/**
	 * Number of laps completed
	 */
	public int laps = 0;
	
	/**
	 * Transform this pod (the real state of the pod) into the bare information that the
	 * Controller actually gets to use.
	 * @param checkpoints The checkpoints of the world.
	 */
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
		Vec check = world.getCheckpoints().get(nextCheck);
		double dist = pos.minus(check).norm();
		if(dist < CHECK_BONUS)
			score += CHECK_BONUS - dist;
		
		// Finally, we get a bonus if the velocity is pointing toward the target
		score += dirBonus(world);
		
		return score;
	}
	
	/**
	 * Calculate a bonus to add to the score, based on how closely the pod is pointing toward the next checkpoint
	 * @param world
	 * @return
	 */
	public double dirBonus(PodWorld world) {
		if(vel.equals(Vec.ORIGIN))
			return 0;
		Vec check = world.getCheckpoints().get(nextCheck);
		Vec toCheck = check.minus(pos);
		Vec dir = Vec.UNIT.rotate(angle);
		
		double dot = dir.dot(toCheck) / toCheck.norm();
		return dot * 200.;
	}
}
