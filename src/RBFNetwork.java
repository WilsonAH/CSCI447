public class RBFNetork {
	private Layer layers[];

	public RBFNetork(int layerCount, int[] nodeCounts, int inputCount) {
		layers = new Layer[layerCount];
	}
	public void UpdateWeights(){
		
	}
	
	public double getOutput(double inputs[]){
		double[] outputs = inputs;
		for(int loop = 0; loop < layers.length; loop++){
			outputs = layers[loop].getOutputs(outputs);
		}
		return outputs[0];
	}
}