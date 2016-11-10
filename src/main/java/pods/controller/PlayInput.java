package pods.controller;

import util.Vec;

/**
 * Simple bean to hold the input for a Controller for one turn.
 */
public class PlayInput {
	/**
	 * Location of the pod
	 */
	public Vec pos;
	/**
	 * Direction the pod is facing, in radians
	 */
	public double angle;
	/**
	 * ID of the next check
	 */
	public int nextCheckId;
	/**
	 * Coordinates of the next check
	 */
	public Vec nextCheck;
	/**
	 * The pod's speed
	 */
	public Vec vel;
	
	@Override
	public String toString() {
		return "Angle: " + angle + " " + Vec.UNIT.rotate(angle)
				+ "\nVel: " + vel;
	}
}
