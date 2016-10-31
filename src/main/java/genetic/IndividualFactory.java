package genetic;

/**
 * Factory for generating n instances of a type of Individual.
 */
public interface IndividualFactory<T extends Individual<T>> {
	/**
	 * Generate the given number of individuals
	 */
	public Iterable<T> generate(int num);
}
