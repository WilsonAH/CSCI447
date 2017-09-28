
public class MultilayerNetwork {
	
	private Layer layers[];
	
	public MultilayerNetwork(int layerCount, int[] nodeCounts, int inputCount, boolean useLogisticFunction){
		layers = new Layer[layerCount];
		for(int loop = 0; loop < layers.length; loop++){
			if(loop == layers.length-1){
				layers[loop] = new Layer(nodeCounts[loop], inputCount, true, useLogisticFunction);
			}else{
				layers[loop] = new Layer(nodeCounts[loop], inputCount, false, useLogisticFunction);
			}
		}
	}
	
	//Needs Work
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
