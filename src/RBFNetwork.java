public class RBFNetork {
	private Layer layers[];

	public RBFNetork(int layerCount, int[] nodeCounts, int inputCount) {
		layers = new Layer[layerCount];
	}
	public void UpdateWeights(){
		
	}
	
	private void findSigma(double[] inputs) {

	}
	
	public double getOutput(double inputs[]){
		double[] outputs = inputs;
		for(int loop = 0; loop < layers.length; loop++){
			outputs = layers[loop].getOutputs(outputs);
		}
		return outputs[0];
	}
	private double getDistance(double[] x1, double[] x2) {

	}
}