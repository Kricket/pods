package util;

import static org.junit.Assert.*;
import org.junit.Test;

public class VecTest {
	
	public static double DELTA = 0.0000001;

	@Test
	public void angleTestQ1() {
		Vec v = new Vec(0.5, 0.5);
		assertEquals(Math.PI/4., v.getAngle(), DELTA);
	}

	@Test
	public void angleTestQ2() {
		Vec v = new Vec(-0.5, 0.5);
		assertEquals(Math.PI*3./4., v.getAngle(), DELTA);
	}

	@Test
	public void angleTestQ3() {
		Vec v = new Vec(-0.5, -0.5);
		assertEquals(Math.PI*5./4., v.getAngle(), DELTA);
	}

	@Test
	public void angleTestQ4() {
		Vec v = new Vec(0.5, -0.5);
		assertEquals(Math.PI*7./4., v.getAngle(), DELTA);
	}
	
	@Test
	public void testNorm2() {
		Vec v = new Vec(3,4);
		assertEquals(25., v.norm2(), DELTA);
	}
	
	@Test
	public void testDot() {
		Vec v = new Vec(1,2), w = new Vec(-5, -2);
		assertEquals(w.dot(v), v.dot(w), DELTA);
		assertEquals(w.dot(v), -9., DELTA);
	}
	
	@Test
	public void testRot90() {
		Vec v = Vec.UNIT;
		v = v.rotate(Math.PI/2);
		assertEquals(v.x, 0, DELTA);
		assertEquals(v.y, 1, DELTA);

		v = v.rotate(Math.PI/2);
		assertEquals(v.x, -1, DELTA);
		assertEquals(v.y, 0, DELTA);
		
		v = v.rotate(Math.PI/2);
		assertEquals(v.x, 0, DELTA);
		assertEquals(v.y, -1, DELTA);
		
		v = v.rotate(Math.PI/2);
		assertEquals(v.x, 1, DELTA);
		assertEquals(v.y, 0, DELTA);
	}
	
	@Test
	public void testRot45() {
		Vec v = Vec.UNIT.rotate(Math.PI/4);
		assertEquals(v.x, v.y, DELTA);
		assertEquals(v.x, 1/Math.sqrt(2), DELTA);
		
		v = Vec.UNIT.rotate(Math.PI/(-4));
		assertEquals(v.x, -v.y, DELTA);
		assertEquals(v.x, 1/Math.sqrt(2), DELTA);
	}
}
