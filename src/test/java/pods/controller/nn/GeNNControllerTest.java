package pods.controller.nn;

import org.junit.Assert;
import org.junit.Test;

import pods.controller.PlayInput;
import pods.controller.PlayOutput;
import util.Matrix;
import util.Vec;

public class GeNNControllerTest {

	@Test
	public void testGetOutputForPlay() {
		PlayInput pi = new PlayInput();
		pi.pos = new Vec(6969, 802);
		pi.vel = Vec.ORIGIN;
		Matrix output = new Matrix(GeNNController.OUTPUT_SIZE, 1);
		for(double x=0.001; x<1.; x+=0.0123) for(double y=0.001; y<1.; y+=0.0456) {
			output.data[0] = x;
			output.data[1] = y;
			output.data[2] = 0.5;
			PlayOutput po = GeNNController.buildOutput(pi, output);
			Matrix generatedOutput = GeNNController.getOutputForPlay(pi, po);
			
			// What really interests us here is the ratio between the x and y
			double outRatio = (output.data[0] + GeNNController.DIR_ADJ.x)
					/ (output.data[1] + GeNNController.DIR_ADJ.y);
			double genRatio = (generatedOutput.data[0] + GeNNController.DIR_ADJ.x)
					/ (generatedOutput.data[1] + GeNNController.DIR_ADJ.y);
			System.out.println("Ratio difference: " + (genRatio - outRatio));
			Assert.assertEquals("Real: " + output + "\ngenerated: " + generatedOutput,
					outRatio, genRatio, 0.0001);
		}
	}
	
	@Test
	public void testWorldToNN() {
		PlayInput pi = new PlayInput();
		pi.nextCheck = new Vec(1234, 5678);
		pi.pos = new Vec(9012, 3456);
		pi.vel = new Vec(123, 456);
		for(double d=-Math.PI; d<2*Math.PI; d+=Math.PI/11) {
			pi.angle = d;
			Vec pos = GeNNController.worldToNN(pi, pi.pos);
			Assert.assertEquals(0, pos.x, Vec.PRECISION);
			Assert.assertEquals(0, pos.y, Vec.PRECISION);
			
			// vector 1000 units in front of the pod
			Vec realFront = new Vec(1000, 0).rotate(d).plus(pi.pos);
			Vec relFront = GeNNController.worldToNN(pi, realFront);
			Assert.assertEquals(1000. * GeNNController.WORLD_TO_NN_SCALE, relFront.x, Vec.PRECISION);
			Assert.assertEquals(0, relFront.y, Vec.PRECISION);
		}
	}
}
