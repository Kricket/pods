package util;

/**
 * Activation functions for neural networks.
 */
public class Activation {
	/**
	 * The sigma function, for smoothing.
	 * @param z
	 * @return
	 */
	public static double sigma(double z) {
		return 1. / (Math.expm1(-z) + 2.);
	}
	
	/**
	 * Apply the sigma function to every element of the given Matrix. Note that this will CHANGE the matrix!
	 */
	public static Matrix sigma(Matrix m) {
		for(int i=0; i<m.data.length; i++)
			m.data[i] = sigma(m.data[i]);
		return m;
	}
	
	/**
	 * Derivative of the sigma function.
	 * @param z
	 * @return
	 */
	public static double dSigma(double z) {
		double sigma = sigma(z);
		return sigma * (1 - sigma);
	}
	
	/**
	 * Apply the derivative of the sigma function to every element of the given Matrix. Note that this will CHANGE the matrix!
	 */
	public static Matrix dSigma(Matrix m) {
		for(int i=0; i<m.data.length; i++)
			m.data[i] = dSigma(m.data[i]);
		return m;
	}

}
