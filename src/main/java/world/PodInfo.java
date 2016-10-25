package world;

import java.util.List;

import pods.controller.PlayInput;
import util.Vec;

/**
 * Simple bean to hold basic information about a Pod. This is used internally by the PodWorld.
 */
public class PodInfo {
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
		p.vel = vel;
		return p;
	}
}
