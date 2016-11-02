package pods.controller;

/**
 * A Controller is an object that controls a single Pod.
 */
public interface Controller {
	/**
	 * Given information about the state of a pod, decide what play to make.
	 * @param pi
	 * @return
	 */
	PlayOutput play(PlayInput pi);
}
