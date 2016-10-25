package pods.controller;


public class SimpleController implements Controller {

	public PlayOutput play(PlayInput pi) {
		PlayOutput play = new PlayOutput();
		play.setDir(pi.nextCheck.minus(pi.vel.times(2)));
		play.setThrust(100);
		return play;
	}
}
