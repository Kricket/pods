package util;

import org.junit.Assert;
import org.junit.Test;

public class ActivationTest {
	final double RANGE = 10., STEP = RANGE / 20;
	
	@Test
	public void testSigma() {
		double min = Activation.sigma(-RANGE);
		Assert.assertEquals(0., min, 0.001);
		double max = Activation.sigma(RANGE);
		Assert.assertEquals(1., max, 0.001);
		
		double last = -1;
		for(double d = -RANGE; d<RANGE; d+=STEP) {
			double sigma = Activation.sigma(d);
			Assert.assertTrue("Sigma for " + d + " should be greater than for " + (d-STEP) + "\nsigma=" + sigma + "\nlastSigma=" + last, sigma > last);
			last = sigma;
		}
	}
	
	@Test
	public void testDSigma() {
		for(double d = -RANGE; d < RANGE; d += STEP) {
			double s1 = Activation.sigma(d), s2 = Activation.sigma(d + STEP);
			double slope = (s2 - s1) / STEP;
			Assert.assertEquals("For d = " + d,
					slope, Activation.dSigma(d + STEP/2), 0.01);
		}
	}
}
