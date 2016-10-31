package genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Population<T extends Individual<T>> {
	public static final int MIN_POP_SIZE = 2;
	
	/**
	 * The members of this population.
	 */
	private Set<T> population;
	private List<T> results;
	private final int POP_SIZE;
	
	public Population(int num, IndividualFactory<T> factory) {
		if(num < MIN_POP_SIZE)
			throw new IllegalArgumentException("A population must have at least "+MIN_POP_SIZE+" individuals");
		POP_SIZE = num;
		population = new HashSet<T>();
		for(T indiv : factory.generate(POP_SIZE))
			population.add(indiv);
	}
	
	public void newGeneration() {
		results = new ArrayList<T>();
		for(T indiv : population) {
			indiv.clearFitness();
			results.add(indiv);
		}
		Collections.sort(results);
		System.out.println("best " + results.get(0).fitness() + " worst " + results.get(results.size()-1).fitness());

		// Now, rebuild the population.
		population = new HashSet<T>();
		
		// Elitism: keep the best
		for(int i=0; i<POP_SIZE / 30; i++)
			population.add(results.get(i));
		
		Set<Integer> crossoverOperations = results.get(0).crossoverOperations();
		Set<Integer> mutationOperations = results.get(0).mutationOperations();
		
		double p = 0.6;
		int k = 2;
		while(population.size() < POP_SIZE) {
			T mom = tournamentSelect(results, p, k);
			T dad;
			while((dad = tournamentSelect(results, p, k)) == mom);
			
			for(int cOp : crossoverOperations) {
				T bastard = mom.crossover(dad, cOp);
				for(int mOp : mutationOperations) {
					T mutant = bastard.clone();
					mutant.mutate(mOp);
					population.add(mutant);
				}
			}
		}
	}

	/**
	 * Use tournament selection to select an individual from the given population.
	 * @param pop The population
	 * @param p Probability of the fittest winning the tournament
	 * @param k Size of the tournament
	 * @return
	 */
	public static <T> T tournamentSelect(List<T> pop, double p, int k) {
		SortedSet<T> set = new TreeSet<T>();
		while(set.size() < k) {
			set.add(pop.get((int) (Math.random() * pop.size())));
		}
		
		double guess = Math.random();
		Iterator<T> iterator = set.iterator();
		T chosen = iterator.next();
		double pp = p;
		while(guess > pp && iterator.hasNext()) {
			chosen = iterator.next();
			pp += (p * (1-pp));
		}
		return chosen;
	}

	public Set<T> getPopulation() {
		return population;
	}
}
