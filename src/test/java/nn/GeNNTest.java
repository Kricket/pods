package nn;

import java.util.Set;

import nn.GeNN.Gradient;

import org.junit.Assert;
import org.junit.Test;

import util.Matrix;

public class GeNNTest {

	@Test
	public void backprop_1_1() {
		ensureProgress(new TestGeNN(1,1), new Matrix(1.), new Matrix(0.));
	}
	
	@Test
	public void backprop_3_3() {
		ensureProgress(new TestGeNN(3,3), new Matrix(1., 0., 0.5), new Matrix(0., 0.5, 1.));
	}
	
	@Test
	public void backprop_1_5_1() {
		ensureProgress(new TestGeNN(1,5,1), new Matrix(1.), new Matrix(0.));
	}
	
	@Test
	public void backprop_3_1_3() {
		ensureProgress(new TestGeNN(3,1,3), new Matrix(1., 0., 0.5), new Matrix(0., 0.5, 1.));
	}
	
	/**
	 * The idea here is to keep backpropagating for the exact same data (input and output).
	 * The NN should get closer to the target answer every time.
	 * @param nn
	 * @param INPUT
	 * @param TARGET
	 */
	private void ensureProgress(TestGeNN nn, final Matrix INPUT, final Matrix TARGET) {
		Matrix forward = nn.forward(INPUT);
		double fDiff = forward.minus(TARGET).norm();
		for(int i=0; i<100; i++) {
			Gradient grad = nn.backprop(INPUT, TARGET);
			nn.apply(grad, -.1);
			Matrix result = nn.forward(INPUT);
			double rDiff = result.minus(TARGET).norm();
			
			Assert.assertTrue("Improved on iteration " + i, rDiff < fDiff);
			forward = result;
			fDiff = rDiff;
		}
	}
	
	private static class TestGeNN extends GeNN<TestGeNN> {
		public TestGeNN(int ...layers) {
			super(layers);
		}

		@Override
		protected double getInitialRange() {
			return 1;
		}

		@Override
		protected long calculateFitness() {
			return 0;
		}

		@Override
		public void mutate(int op) {
		}

		@Override
		public Set<Integer> mutationOperations() {
			return null;
		}

		@Override
		public TestGeNN crossover(TestGeNN partner, int op) {
			return null;
		}

		@Override
		public Set<Integer> crossoverOperations() {
			return null;
		}

		@Override
		public TestGeNN clone() {
			return null;
		}
		
	}
}
