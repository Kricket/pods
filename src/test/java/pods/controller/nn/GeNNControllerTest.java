package pods.controller.nn;

import org.junit.Assert;
import org.junit.Test;

import pods.controller.PlayInput;
import pods.controller.PlayOutput;
import util.Matrix;
import util.Vec;

public class GeNNControllerTest {

	@Test
	public void testGenerateOutput() {
		PlayInput pi = new PlayInput();
		pi.pos = Vec.ORIGIN;
		pi.vel = Vec.ORIGIN;
		Matrix output = new Matrix(GeNNController.OUTPUT_SIZE, 1);
		output.data[0] = 0.25;
		output.data[1] = 0.75;
		output.data[2] = 0.5;
		PlayOutput po = GeNNController.buildOutput(pi, output);
		Matrix generatedOutput = GeNNController.getOutputForPlay(pi, po);
		
		System.out.println("Real output: " + output);
		System.out.println("Generated output: " + generatedOutput);
		for(int i=0; i<output.data.length; i++) {
			Assert.assertEquals("For index " + i, output.data[i], generatedOutput.data[i], 0.00000001);
		}
	}
}
