
public class Layer {
	public int id;
	public boolean isHiddenLayer;
	public int outputCount;
	public int nodeCount;
	private double BIAS;
	
	private Node[] nodes;
	public double[] inputs;
	public double[] outputs;
	public double[] gradients;
	public double[] errors;
	
	
	public Layer(int nodeCount, int outputCount, boolean isHiddenLayer, boolean useLogisticFunction, double momentum, double bias, double learningRate, int id){
		nodes = new Node[nodeCount];
		errors = new double[nodeCount];
		outputs = new double[nodeCount];
		gradients = new double[nodeCount];
		this.nodeCount = nodeCount;
		this.outputCount = outputCount;
		this.isHiddenLayer = isHiddenLayer;
		this.BIAS = bias;
		this.id = id;
		for(int loop = 0; loop < nodes.length; loop++){
			if(isHiddenLayer){
				nodes[loop] = new HiddenNode(outputCount,useLogisticFunction,momentum,learningRate,loop);
			}else{
				nodes[loop] = new OutputNode(outputCount,learningRate,loop);
			}
		}
	}
	
	
	public void updateWeights(double[] errorsOfLayerToRight, double[] gradientsOfLayerToRight){
		for(int node = 0;node < nodes.length; node++){
			HiddenNode hn = (HiddenNode)nodes[node];//Will only be called for hidden layers
			hn.updateWeights(errorsOfLayerToRight,gradientsOfLayerToRight,inputs[node]);
		}
	}
	
	//Hidden Layer
	public double[] updateErrors(double[] errorsOfLayerToRight){
		for(int node = 0;node < nodes.length; node++){
			HiddenNode hn = (HiddenNode)nodes[node];
			double sum = 0;
			for(int nextLayerNode = 0; nextLayerNode < errorsOfLayerToRight.length;nextLayerNode++){
				sum+=errorsOfLayerToRight[nextLayerNode]*this.nodes[node].weights[nextLayerNode];
			}
			errors[node] = sum*gradients[node];
			//System.out.println("Layer "+this.id+" node "+node+" error: "+errors[node]+"   \tinput: "+inputs[node]);
		}
		return errors;
	}
	
	//Output Layer
	public double[] updateErrors(double expected, double[] output){
		for(int node = 0;node < nodes.length; node++){
			OutputNode on = (OutputNode) nodes[node];
			errors[node] = on.getError(expected, output[node]);
			gradients[node] = on.calculateGradient(expected, output[node],inputs[node]);
		}
		return errors;
	}
	
	public double[] getOutputs(double[] inputs){
		double[] outputs = new double[this.outputCount];
		this.inputs = inputs;
		if(this.isHiddenLayer){
			for(int receivingNode = 0;receivingNode < outputCount; receivingNode++){
				if(this.isHiddenLayer){
					outputs[receivingNode] = this.sumWeightsTimesActivations(receivingNode)+BIAS;
				}
			}
		}else{
			for(int node = 0; node < nodes.length;node++){
				OutputNode on = (OutputNode)nodes[node];
				outputs[node] = on.getOutputs(inputs[node])+BIAS;
			}
		}
		
		this.outputs = outputs;
		return outputs;
	}
	
	private double sumWeightsTimesActivations(int receivingNode){
		double sum = 0;
		for(int node = 0;node < nodes.length; node++){
			HiddenNode hn = (HiddenNode)nodes[node];//Will only be called for hidden layers
			gradients[node] = hn.calculateGradient(hn.logisticActivationFunction(inputs[node]));
			sum +=  hn.weights[receivingNode]*hn.logisticActivationFunction(inputs[node]);
		}
		return sum;
	}
	
	public String toString(){
		String s = "Layer\nNodes: ";
		for(int loop = 0;loop < nodes.length; loop++){
			s += nodes[loop].toString();
		}
		return s;
	}
}
