package genetic;

/**
 * Factory for generating n instances of a type of Individual.
 */
public interface IndividualFactory<T extends Individual> {
	public Iterable<T> generate(int num);
}
