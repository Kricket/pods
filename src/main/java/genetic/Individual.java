package genetic;

import java.util.Set;

/**
 * An individual for genetic fun! Comparisons should be based on fitness.
 * @param <Self> Set to its own type
 */
public abstract class Individual<Self extends Individual<?>> implements Comparable<Self> {
	private long lastFitness = -1;
	/**
	 * Calculate the fitness value (and save it for later...).
	 * @return
	 */
	public long fitness() {
		if(lastFitness < 0)
			lastFitness = calculateFitness();
		return lastFitness;
	}
	
	/**
	 * Really calculate the fitness of this Individual.
	 */
	protected abstract long calculateFitness();
	
	/**
	 * Clear the saved fitness value. The next call to fitness() will have to re-calculate it.
	 */
	public void clearFitness() {
		lastFitness = -1;
	}
	
	/**
	 * Randomly mutate a little of this Individual.
	 * @param op The type of mutation to perform (implementation-dependent).
	 */
	abstract public void mutate(int op);
	
	abstract public Set<Integer> mutationOperations();
	
	/**
	 * Make a baby between this and the other.
	 * @param op The crossover algorithm to use (implementation-dependent).
	 */
	abstract public Self crossover(Self partner, int op);
	
	abstract public Set<Integer> crossoverOperations();
	
	public int compareTo(Self o) {
		int cmp = new Long(fitness()).compareTo(o.fitness());
		return (cmp == 0 ? 1 : -cmp);
	}
	
	abstract public Self clone();
}
