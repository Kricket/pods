package pods.controller;

/**
 * Simple helper to let us dynamically switch controllers.
 */
public class ControllerWrapper implements Controller {
	public Controller controller;
	public PlayOutput play(PlayInput pi) {
		return controller.play(pi);
	}
}
