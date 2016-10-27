package pods.controller;

import util.Vec;

/**
 * Simple bean to hold the input for a Controller for one turn.
 */
public class PlayInput {
	public Vec pos;
	// Cheating here: this is supposed to be in degrees...
	public double angle;
	public int nextCheckId;
	public Vec nextCheck;
	public Vec vel;
}
