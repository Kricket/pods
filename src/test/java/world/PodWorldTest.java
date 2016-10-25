package world;

import static org.junit.Assert.*;

import org.junit.Test;

import util.Vec;

public class PodWorldTest {
	@Test
	public void dangleTest() {
		for(double reqDeg=0; reqDeg<360; reqDeg++) {
			for(double realDeg=0; realDeg<360; realDeg++) {
				double realRad = Math.toRadians(realDeg);
				double reqFixedRad = PodWorld.getRealAngle(Math.toRadians(reqDeg), realRad);
				double changeRad = Math.abs(reqFixedRad - realRad);
				assertTrue("At " + realDeg + " requested " + reqDeg + " gives " + Math.toDegrees(changeRad),
						changeRad < PodWorld.MAX_TURN + 0.000001);
			}
		}
	}
	
	@Test
	public void checkpointCreationTest() {
		for(int i=0; i<10; i++) {
			PodWorld p = new PodWorld();
			
			assertTrue(p.getCheckpoints().size() >= PodWorld.MIN_CHECKS);
			assertTrue(p.getCheckpoints().size() <= PodWorld.MAX_CHECKS);
			
			for(Vec check : p.getCheckpoints()) {
				assertTrue(check.x < PodWorld.WORLD_X - PodWorld.BORDER_PADDING);
				assertTrue(check.x > PodWorld.BORDER_PADDING);
				assertTrue(check.y < PodWorld.WORLD_Y - PodWorld.BORDER_PADDING);
				assertTrue(check.y > PodWorld.BORDER_PADDING);
				
				for(Vec other : p.getCheckpoints()) {
					if(other == check)
						continue;
					assertTrue(check.minus(other).norm2() > PodWorld.CHECK_SPACING*PodWorld.CHECK_SPACING);
				}
			}
		}
	}
}
