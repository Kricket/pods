package genetic;

import org.junit.Assert;
import org.junit.Test;

public class PopulationTest {

	@Test
	public void testNewGeneration() {
		final int POP_SIZE = 100;
		TestIndividualFactory factory = new TestIndividualFactory();
		Population<TestIndividual> pop = new Population<TestIndividual>(POP_SIZE, factory);
		Assert.assertEquals(POP_SIZE, factory.lastGenerated.size());
		pop.newGeneration();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBadPopSize() {
		TestIndividualFactory factory = new TestIndividualFactory();
		new Population<TestIndividual>(Population.MIN_POP_SIZE - 1, factory);
	}
	
	@Test
	public void testOKPopSizes() {
		TestIndividualFactory factory = new TestIndividualFactory();
		Population<TestIndividual> pop = new Population<TestIndividual>(Population.MIN_POP_SIZE, factory);
		pop.newGeneration();
	}
}
