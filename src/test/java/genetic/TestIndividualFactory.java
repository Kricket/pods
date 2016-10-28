package genetic;

import java.util.ArrayList;
import java.util.List;

public class TestIndividualFactory implements IndividualFactory<TestIndividual> {
	public List<TestIndividual> lastGenerated;
	public Iterable<TestIndividual> generate(int num) {
		lastGenerated = new ArrayList<TestIndividual>();
		for(int i=0; i<num; i++) {
			lastGenerated.add(new TestIndividual());
		}
		
		ArrayList<TestIndividual> result = new ArrayList<TestIndividual>();
		result.addAll(lastGenerated);
		return result;
	}
}
