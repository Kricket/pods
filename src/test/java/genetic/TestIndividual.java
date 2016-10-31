package genetic;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;

public class TestIndividual extends Individual<TestIndividual> {
	public static final Set<Integer> CROSS_OPS, MUT_OPS;
	static {
		HashSet<Integer> set = new HashSet<Integer>();
		set.add(1);
		set.add(3);
		CROSS_OPS = Collections.unmodifiableSet(set);
		
		set = new HashSet<Integer>();
		set.add(33);
		set.add(199);
		set.add(69);
		MUT_OPS = Collections.unmodifiableSet(set);
	}
	
	@Override
	protected long calculateFitness() {
		return (long) (Math.random() * 100);
	}

	public void mutate(int op) {
		Assert.assertTrue("Operation is valid", MUT_OPS.contains(op));
	}

	public Set<Integer> mutationOperations() {
		return MUT_OPS;
	}

	public TestIndividual crossover(TestIndividual partner, int op) {
		Assert.assertTrue("Operation is valid", CROSS_OPS.contains(op));
		return this;
	}

	public Set<Integer> crossoverOperations() {
		return CROSS_OPS;
	}

	@Override
	public TestIndividual clone() {
		return new TestIndividual();
	}

}
