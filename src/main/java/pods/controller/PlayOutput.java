package pods.controller;

import util.Vec;

/**
 * Represents the output of a Controller for one turn.
 */
public class PlayOutput {
	public static final int MAX_THRUST = 100;
	
	private Vec dir;
	private int thrust;
	
	/**
	 * Setting the dir (where we want to turn) truncates it to integer values!
	 * @param d
	 */
	public void setDir(Vec d) {
		dir = new Vec((int)d.x, (int)d.y);
	}
	public Vec getDir() {
		return dir;
	}
	
	/**
	 * Setting the thrust (acceleration factor) forces the value to be between 0 and 100.
	 * @param t
	 */
	public void setThrust(int t) {
		if(t > MAX_THRUST)
			t = MAX_THRUST;
		if(t < 0)
			t = 0;
		thrust = t;
	}
	public int getThrust() {
		return thrust;
	}
	
	@Override
	public String toString() {
		return "Dir " + dir + " " + Math.atan2(dir.y, dir.x) * 180. / Math.PI
				+ "\nThrust " + thrust;
	}
}
