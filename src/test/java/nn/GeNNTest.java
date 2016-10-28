package nn;

import org.junit.Assert;
import org.junit.Test;

public class GeNNTest {

	@Test
	public void testSigma() {
		final double RANGE = 10., STEP = RANGE / 20;
		double min = GeNN.sigma(-RANGE);
		Assert.assertEquals(0., min, 0.001);
		double max = GeNN.sigma(RANGE);
		Assert.assertEquals(1., max, 0.001);
		
		double last = -1;
		for(double d = -RANGE; d<RANGE; d+=STEP) {
			double sigma = GeNN.sigma(d);
			Assert.assertTrue("Sigma for " + d + " should be greater than for " + (d-STEP) + "\nsigma=" + sigma + "\nlastSigma=" + last, sigma > last);
			last = sigma;
		}
	}
}
