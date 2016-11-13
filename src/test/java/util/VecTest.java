package util;

import static org.junit.Assert.*;
import org.junit.Test;

public class VecTest {
	
	@Test
	public void angleTestQ1() {
		Vec v = new Vec(0.5, 0.5);
		assertEquals(Math.PI/4., v.getAngle(), Vec.PRECISION);
	}

	@Test
	public void angleTestQ2() {
		Vec v = new Vec(-0.5, 0.5);
		assertEquals(Math.PI*3./4., v.getAngle(), Vec.PRECISION);
	}

	@Test
	public void angleTestQ3() {
		Vec v = new Vec(-0.5, -0.5);
		assertEquals(Math.PI*5./4., v.getAngle(), Vec.PRECISION);
	}

	@Test
	public void angleTestQ4() {
		Vec v = new Vec(0.5, -0.5);
		assertEquals(Math.PI*7./4., v.getAngle(), Vec.PRECISION);
	}
	
	@Test
	public void testNorm2() {
		Vec v = new Vec(3,4);
		assertEquals(25., v.norm2(), Vec.PRECISION);
	}
	
	@Test
	public void testDot() {
		Vec v = new Vec(1,2), w = new Vec(-5, -2);
		assertEquals(w.dot(v), v.dot(w), Vec.PRECISION);
		assertEquals(w.dot(v), -9., Vec.PRECISION);
	}
	
	@Test
	public void testRot90() {
		Vec v = Vec.UNIT;
		v = v.rotate(Math.PI/2);
		assertEquals(v.x, 0, Vec.PRECISION);
		assertEquals(v.y, 1, Vec.PRECISION);

		v = v.rotate(Math.PI/2);
		assertEquals(v.x, -1, Vec.PRECISION);
		assertEquals(v.y, 0, Vec.PRECISION);
		
		v = v.rotate(Math.PI/2);
		assertEquals(v.x, 0, Vec.PRECISION);
		assertEquals(v.y, -1, Vec.PRECISION);
		
		v = v.rotate(Math.PI/2);
		assertEquals(v.x, 1, Vec.PRECISION);
		assertEquals(v.y, 0, Vec.PRECISION);
	}
	
	@Test
	public void testRot45() {
		Vec v = Vec.UNIT.rotate(Math.PI/4);
		assertEquals(v.x, v.y, Vec.PRECISION);
		assertEquals(v.x, 1/Math.sqrt(2), Vec.PRECISION);
		
		v = Vec.UNIT.rotate(Math.PI/(-4));
		assertEquals(v.x, -v.y, Vec.PRECISION);
		assertEquals(v.x, 1/Math.sqrt(2), Vec.PRECISION);
	}
	
	@Test
	public void testEquals() {
		Vec v = new Vec(1.234, -5.678);
		
		Vec p1 = new Vec(Vec.PRECISION/1.1, Vec.PRECISION/1.1);
		assertEquals(v, v.plus(p1));
		assertEquals(v, v.minus(p1));
	}
}
