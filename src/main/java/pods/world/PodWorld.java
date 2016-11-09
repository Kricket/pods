package pods.world;

import java.util.ArrayList;
import java.util.List;

import pods.controller.Controller;
import pods.controller.PlayInput;
import pods.controller.PlayOutput;
import util.Vec;

public class PodWorld {
	/** Dimensions of the world */
	public static final double WORLD_X = 16000, WORLD_Y = 9000;
	/** Padding for checkpoints around the border */
	public static final int BORDER_PADDING = 1000;
	/** Minimum space between two checkpoints */
	public static final double CHECK_SPACING = 3000;
	/**
	 * Default range of number of checkpoints
	 */
	public static final int MIN_CHECKS = 3, MAX_CHECKS = 6;
	
	public static final double CHECK_RADIUS = 600;
	public static final double POD_RADIUS = 400;
	public static final double MAX_TURN = Math.toRadians(18.);
	public static final double FRICTION = 0.85;
	
	/**
	 * The checkpoints. The last checkpoint is "home base", and new pods start there.
	 */
	private final List<Vec> checkpoints;
	private List<PodInfo> pods = new ArrayList<PodInfo>();
	private List<Controller> players = new ArrayList<Controller>();
	
	/**
	 * Create a world with a number of checkpoints between MAX and MIN.
	 */
	public PodWorld() {
		this((int) (Math.random() * (MAX_CHECKS - MIN_CHECKS) + MIN_CHECKS));
	}
	
	public PodWorld(int numChecks) {
		checkpoints = new ArrayList<Vec>(numChecks);
		for(int i=0; i<numChecks; i++) {
			checkpoints.add(generateCheckpoint(checkpoints));
		}
	}
	
	public PodWorld(List<Vec> checks) {
		checkpoints = new ArrayList<Vec>(checks);
	}
	
	public void addPlayer(Controller c) {
		players.add(c);
		PodInfo p = new PodInfo();
		p.pos = checkpoints.get(checkpoints.size()-1);
		pods.add(p);
	}

	/**
	 * Generate a checkpoint with the following conditions:
	 * - not too close to the edge
	 * - not too close to any other checkpoints
	 * @param checks Current list of checkpoints
	 * @return
	 */
	public static Vec generateCheckpoint(List<Vec> checks) {
		Vec check;
		do {
			check = new Vec(Math.random() * WORLD_X, Math.random() * WORLD_Y).truncate();
		} while(!checkIsOK(check, checks));
		return check;
	}

	public static boolean checkIsOK(Vec check, List<Vec> checks) {
		if(check.x < BORDER_PADDING || WORLD_X - check.x < BORDER_PADDING)
			return false;
		if(check.y < BORDER_PADDING || WORLD_Y - check.y < BORDER_PADDING)
			return false;
		
		for(Vec other : checks) {
			if(check.minus(other).norm2() < CHECK_SPACING*CHECK_SPACING)
				return false;
		}
		
		return true;
	}
	
	/**
	 * Move the simulation forward one step.
	 */
	public void step() {
		for(int i=0; i<pods.size(); i++) {
			PodInfo pod = pods.get(i);
			PlayInput pi = pod.buildPlayInfo(checkpoints);
			PlayOutput play = players.get(i).play(pi);
			update(pod, pod, play);
		}
	}
	
	/**
	 * Test what would happen if the simulation were to move forward one step for the given pod and play.
	 * @param pod
	 * @param play
	 * @return
	 */
	public PodInfo stepTest(PodInfo pod, PlayOutput play) {
		PodInfo output = new PodInfo();
		update(pod, output, play);
		return output;
	}

	/**
	 * Fill the given output state with the result of running the given play for one step, from the given input state.
	 * @param inputState Starting state of the pod to play
	 * @param outputState Will be filled with the ending state after the play
	 * @param play The play to make
	 */
	private void update(PodInfo inputState, PodInfo outputState, PlayOutput play) {
		/*
On each turn the pods movements are computed this way:

    Rotation: the pod rotates to face the target point, with a maximum of 18 degrees (except for the 1rst round).
    Acceleration: the pod's facing vector is multiplied by the given thrust value. The result is added to the current speed vector.
    Movement: The speed vector is added to the position of the pod. If a collision would occur at this point, the pods rebound off each other.
    Friction: the current speed vector of each pod is multiplied by 0.85
    The speed's values are truncated and the position's values are rounded to the nearest integer.

Collisions are elastic. The minimum impulse of a collision is 120.
A boost is in fact an acceleration of 650.
A shield multiplies the Pod mass by 10.
The provided angle is absolute. 0° means facing EAST while 90° means facing SOUTH.
		 */
		// 1. Rotation
		double requestedAngle = play.getDir().minus(inputState.pos).getAngle();
		double angle = getRealAngle(requestedAngle, inputState.angle);
		outputState.angle = angle;
		
		// 2. Acceleration
		Vec dir = Vec.UNIT.rotate(angle);
		outputState.vel = inputState.vel.plus(dir.times(play.getThrust()));
		
		// 3. Movement
		outputState.pos = inputState.pos.plus(outputState.vel);
		
		// 4. Friction
		outputState.vel = outputState.vel.times(FRICTION);
		
		// Update next check
		outputState.nextCheck = inputState.nextCheck;
		outputState.laps = inputState.laps;
		Vec nextCheck = checkpoints.get(inputState.nextCheck);
		
		if(nextCheck.minus(outputState.pos).norm2() < CHECK_RADIUS*CHECK_RADIUS) {
			if(++outputState.nextCheck >= checkpoints.size()) {
				outputState.nextCheck = 0;
				outputState.laps++;
			}
		}
	}

	public static double getRealAngle(double requestedAngle, double angle) {
		double dAngle = inRange(requestedAngle - angle);
		if(dAngle > MAX_TURN)
			dAngle = MAX_TURN;
		else if(dAngle < -MAX_TURN)
			dAngle = -MAX_TURN;
		
		return inRange(angle + dAngle);
	}
	
	/**
	 * Bring the given angle in the range (-PI, PI).
	 * @param angle An angle between (-5PI/2, 5PI/2)
	 * @return
	 */
	public static double inRange(double angle) {
		if(angle < -Math.PI)
			return angle + 2*Math.PI;
		else if(angle > Math.PI)
			return angle - 2*Math.PI;
		else
			return angle;
	}

	public List<Vec> getCheckpoints() {
		return checkpoints;
	}
	
	public List<PodInfo> getPods() {
		return pods;
	}

	/**
	 * Set all pods back to their initial positions.
	 */
	public void reset() {
		pods = new ArrayList<PodInfo>();
		for(int i=0; i<players.size(); i++) {
			PodInfo p = new PodInfo();
			p.pos = checkpoints.get(checkpoints.size()-1);
			pods.add(p);
		}
	}

	/**
	 * Get the state of the Pod for the given player.
	 * @param player
	 * @return
	 */
	public PodInfo getPod(Controller player) {
		int index = players.indexOf(player);
		return pods.get(index);
	}

	@Override
	public int hashCode() {
		return checkpoints.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof PodWorld) {
			PodWorld p = (PodWorld) obj;
			return checkpoints.equals(p.checkpoints);
		}
		return false;
	}
}
