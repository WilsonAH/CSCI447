
public class MultilayerNetwork {
	
	private Layer layers[];
	
	public MultilayerNetwork(int layerCount, int[] nodeCounts){
		layers = new Layer[layerCount];
		for(int loop = 0; loop < layers.length; loop++){
			layers[loop] = new Layer(nodeCounts[loop]);
		}
	}
	
	//Needs Work
	public void UpdateWeights(){
		
	}
	
	//Needs Work
	public double getOutput(double inputs[]){
		return 0;
	}
}
