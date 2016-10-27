package genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Population<T extends Individual<T>> {
	private class FitnessComparator implements Comparator<T> {
		public int compare(T o1, T o2) {
			return (o1.fitness() < o2.fitness() ? 1 : (o1.fitness() == o2.fitness() ? 0 : -1));
		}
	}
	
	/**
	 * The members of this population.
	 */
	private Set<T> population;
	private List<T> results;
	
	public Population(int num, IndividualFactory<T> factory) {
		population = new HashSet<T>();
		for(T indiv : factory.generate(num))
			population.add(indiv);
	}
	
	public void newGeneration() {
		results = new ArrayList<T>();
		for(T indiv : population) {
			indiv.clearFitness();
			results.add(indiv);
		}
		
		Collections.sort(results, new FitnessComparator());
		System.out.println("Best is " + results.get(0).fitness() + " worst is " + results.get(results.size()-1).fitness());
		
		// Now, rebuild the population.
		population = new HashSet<T>();
		Set<Integer> crossoverOperations = results.get(0).crossoverOperations();
		Set<Integer> mutationOperations = results.get(0).mutationOperations();
		// Kill the losers
		results.subList(results.size()/2, results.size()).clear();
		
		for(Integer op : mutationOperations) {
			for(T indiv : results) {
				indiv.mutate(op);
			}
		}
		
		for(Integer op : crossoverOperations) {
			for(int i=0; i<2*results.size()/crossoverOperations.size(); i++) {
				// For the remaining ones, make babies!
				int dad = (int) (Math.random() * results.size());
				int mom;
				while((mom = (int) (Math.random() * results.size())) == dad);
				T child = results.get(mom).crossover(results.get(dad), op);
				population.add(child);
			}
		}
		
		/*
		int survivors = population.size() / 2;
		
		for(int i=0; i<survivors; i++) {
			T mutant = results.get(i);
			mutant.mutate();
			population.add(mutant);
		}
		
		for(int i=survivors; i<results.size(); i++) {
			// For the remaining ones, make babies!
			int dad = (int) (Math.random() * survivors);
			int mom;
			while((mom = (int) (Math.random() * survivors)) == dad);
			T child = results.get(mom).crossover(results.get(dad));
			population.add(child);
		}
		*/
		
	}
}
