package genetic;

import java.util.Set;

/**
 * An individual for genetic fun!
 * @param <Self> Set to its own type
 */
public interface Individual<Self extends Individual<?>> {
	/**
	 * Calculate the fitness value (and save it for later...).
	 * @return
	 */
	long fitness();
	/**
	 * Clear the saved fitness value. The next call to fitness() will have to re-calculate it.
	 */
	void clearFitness();
	
	/**
	 * Randomly mutate a little of this Individual.
	 * @param op The type of mutation to perform (implementation-dependent).
	 */
	void mutate(int op);
	
	Set<Integer> mutationOperations();
	
	/**
	 * Make a baby between this and the other.
	 * @param op The crossover algorithm to use (implementation-dependent).
	 */
	Self crossover(Self partner, int op);
	
	Set<Integer> crossoverOperations();
}
