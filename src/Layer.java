
public class Layer {
	
	private Node nodes[];
	
	public Layer(int nodeCount, int inputCount, boolean hiddenLayer, boolean useLogisticFunction){
		nodes = new Node[nodeCount];
		for(int loop = 0; loop < nodes.length; loop++){
			if(hiddenLayer){
				nodes[loop] = new HiddenNode(inputCount,useLogisticFunction);
			}else{
				nodes[loop] = new OutputNode(inputCount);
			}
		}
	}
	
	public int getNodeCount(){
		return nodes.length;
	}
	
	public double[] getOutputs(double[] inputs){
		double[] outputs = new double[nodes.length];
		for(int loop = 0;loop < nodes.length; loop++){
			outputs[loop] = nodes[loop].getOutput(inputs);
		}
		return outputs;
	}
	
	public String toString(){
		String s = "Layer\nNodes: ";
		for(int loop = 0;loop < nodes.length; loop++){
			s += nodes[loop].toString();
		}
		return s;
	}
}
