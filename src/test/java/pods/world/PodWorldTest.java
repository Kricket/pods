package pods.world;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import pods.controller.SimpleController;
import pods.world.PodWorld;
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
	
	@Test
	public void testReset() {
		PodWorld p = new PodWorld();
		SimpleController c = new SimpleController();
		p.addPlayer(c);
		
		Vec startPos = p.getPod(c).pos;
		p.step();
		Assert.assertNotEquals(startPos, p.getPod(c).pos);
		
		p.reset();
		Assert.assertEquals(startPos, p.getPod(c).pos);
	}
	
	@Test
	public void stepTestTest() {
		PodWorld p = new PodWorld();
		SimpleController c = new SimpleController();
		p.addPlayer(c);
		
		assertEquals(1, p.getPods().size());
		PodInfo pod = p.getPod(c);
		assertEquals(pod, p.getPods().get(0));
		
		PodInfo stepped = p.stepTest(pod, c.play(pod.buildPlayInfo(p.getCheckpoints())));
		assertNotEquals(stepped, pod);
		assertEquals(1, p.getPods().size());
		assertEquals(pod, p.getPods().get(0));
		
		p.step();
		assertEquals(1, p.getPods().size());
		assertEquals(pod, p.getPods().get(0));
		assertEquals(pod.angle, stepped.angle, 0.000001);
		assertEquals(pod.laps, stepped.laps);
		assertEquals(pod.nextCheck, stepped.nextCheck);
		assertEquals(pod.pos, stepped.pos);
		assertEquals(pod.vel, stepped.vel);
	}
}
