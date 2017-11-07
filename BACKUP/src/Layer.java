
public class Layer {
	public int id;
	public boolean isHiddenLayer;
	public int outputCount;
	public int nodeCount;
	private double BIAS;
	
	private Node[] nodes;
	public double[] inputs;
	public double[] outputs;
	public double[] errors;
	
	
	public Layer(int nodeCount, int outputCount, boolean isHiddenLayer, boolean useLogisticFunction, double momentum, double bias, double learningRate, int id){
		nodes = new Node[nodeCount];
		errors = new double[nodeCount];
		outputs = new double[nodeCount];
		
		this.nodeCount = nodeCount;
		this.outputCount = outputCount;
		this.isHiddenLayer = isHiddenLayer;
		this.BIAS = bias;
		this.id = id;
		for(int loop = 0; loop < nodes.length; loop++){
			if(isHiddenLayer){
				nodes[loop] = new HiddenNode(outputCount,momentum,learningRate,loop);
			}else{
				nodes[loop] = new OutputNode(outputCount,useLogisticFunction,learningRate,loop);
			}
		}
	}
	
	
	public void updateWeights(double[] errorsDownstream){
		//System.out.println("Layer: "+this.id);
		for(int node = 0;node < nodes.length; node++){
			HiddenNode hn = (HiddenNode)nodes[node];
			hn.updateWeights(errorsDownstream, inputs[node]);
			/*if(this.id==0){
				for(int weight = 0;weight<hn.weights.length;weight++){
					hn.weights[weight] = 1;
				}
			}*/
		}
	}
	
	//Hidden Layer
	public double[] updateErrors(double[] errorsOfLayerToRight){
		for(int node = 0;node < nodes.length; node++){
			HiddenNode hn = (HiddenNode)nodes[node];
			double sum = 0;
			for(int nextLayerNode = 0; nextLayerNode < errorsOfLayerToRight.length;nextLayerNode++){
				sum+=errorsOfLayerToRight[nextLayerNode]*hn.weights[nextLayerNode];
			}
			//System.out.println(errors.length+" "+outputs.length);
			errors[node] = sum*outputs[node]*(1-outputs[node]);
			for(int weight = 0; weight < hn.weights.length;weight++){
				//System.out.println("Layer "+this.id+" node "+node+" error: "+errors[node]+"   \tinput: "+inputs[node]+"   \tweight "+weight+": "+hn.weights[weight]);
			}
		}
		return errors;
	}
	
	//Output Layer
	public double[] updateErrors(double expected, double[] output){
		for(int node = 0;node < nodes.length; node++){
			OutputNode on = (OutputNode) nodes[node];
			errors[node] = on.getError(expected, output[node]);
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
				if(on.useLogisticFunciton){
					outputs[node] = on.logisticActivationOutput(inputs[node])+(BIAS/100);
					this.outputs[node] = on.logisticActivationOutput(inputs[node]);
				}else{
					outputs[node] = on.getOutputs(inputs[node])+BIAS;
					this.outputs[node] = on.getOutputs(inputs[node]);
				}
				
			}
		}
		return outputs;
	}
	
	private double sumWeightsTimesActivations(int receivingNode){
		double sum = 0;
		for(int node = 0;node < nodes.length; node++){
			HiddenNode hn = (HiddenNode)nodes[node];//Will only be called for hidden layers
			if(this.id==0){
				outputs[node] = inputs[node];
			}else{
				outputs[node] = hn.logisticActivationFunction(inputs[node]);
			}
			//System.out.println(this.id+"   "+hn.weights[receivingNode]+"   "+outputs[node]+"   "+inputs[node]);
			
			sum +=  hn.weights[receivingNode]*outputs[node];
			this.outputs[node] = sum;
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
