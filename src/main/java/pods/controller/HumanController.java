package pods.controller;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import util.Vec;
import world.PodWorld;

public class HumanController implements KeyEventDispatcher, Controller {

	public HumanController() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
	}
	
	private Set<Integer> keys = new HashSet<Integer>();
	
	public boolean dispatchKeyEvent(KeyEvent e) {
		switch(e.getID()) {
		case KeyEvent.KEY_PRESSED:
			keys.add(e.getKeyCode());
			break;
		case KeyEvent.KEY_RELEASED:
			keys.remove(e.getKeyCode());
			break;
		}
		return false;
	}

	public PlayOutput play(PlayInput pi) {
System.out.println("Angle: " + pi.angle);
		PlayOutput response = new PlayOutput();
		Vec dir;
		if(keys.contains(KeyEvent.VK_RIGHT)) {
			dir = Vec.UNIT.rotate(pi.angle + PodWorld.MAX_TURN);
		} else if(keys.contains(KeyEvent.VK_LEFT)) {
			dir = Vec.UNIT.rotate(pi.angle - PodWorld.MAX_TURN);
		} else {
			dir = Vec.UNIT.rotate(pi.angle);
		}
		
		response.setDir(pi.pos.plus(dir.times(1000.)));
		
		if(keys.contains(KeyEvent.VK_UP))
			response.setThrust(100);
		else
			response.setThrust(0);
		
		return response;
	}
}
