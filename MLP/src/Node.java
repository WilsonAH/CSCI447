//Author Wilson Harris

public class Node {
	private boolean useActivationFunciton;
	
	//Track how many nodes in the previous layer have passed their output to this node
	private int inputsTriggered = 0;
	private int inputCount = 0;
	
	//Input and output value.
	private double senesteinput = 0;
	private double senesteoutput = 0;
	
	//Delta value.
	private double delta = 0;
	
	//Variable to keep track of inputs passed in
	private double sum;
	
	//Array of weights
	private Synapse[] synapses;
	
	/**
	 * Constructor for Node. Creates an array of Synapses.
	 * @param forwardLayerNodeCount 	number of nodes in next layer
	 * @param nextLayer				 	next layer of nodes
	 * @param useActivationFunciton		whether the node will use an activation function or not
	 */
	public Node(int forwardLayerNodeCount, Layer nextLayer, boolean useActivationFunciton){
		this.useActivationFunciton = useActivationFunciton;
		
		if(nextLayer!=null){// If not the output layer, create an array of Synapses
			synapses = new Synapse[forwardLayerNodeCount];
			for(int s = 0; s < this.getSynapses().length; s++){
				//Synapse Constructor: new Synapse(randomWeight, nextLayer);
				this.getSynapses()[s] = new Synapse(Math.random()*(Math.random() > 0.5 ? 1 : -1),nextLayer.nodes[s]);
				nextLayer.nodes[s].incrementInputCount();
			}
		}else{
			//Empty array of synapses for output layer
			synapses = new Synapse[0];
		}
	}
	
	public double getSenesteInput(){
		return this.senesteinput;
	}
	
	public double getSenesteOutput(){
		return this.senesteoutput;
	}
	
	public double getDelta(){
		return this.delta;
	}
	
	public void setDelta(double delta){
		this.delta = delta;
	}
	
	/**
	 * Receives an input, adds it to its sum, and then checks if all of the inputs have been received.  If so, go to test.
	 * @param input	the input
	 */
	public void input(double input){
		this.inputsTriggered++;
		this.sum = sum+input;
		if(this.inputsTriggered >= this.inputCount){
			this.senesteinput = sum;//Update input
			test();
		}
	}
	
	/**
	 * Applies an activation function if applicable, then multiplies by the synapse weight and passes its output to the next node
	 */
	private void test(){
		//Passes output forward
		for(int s = 0; s < this.getSynapses().length; s++){
			Synapse synapse = this.getSynapses()[s];
			if(this.useActivationFunciton){
				synapse.getToNode().input(MultilayerPerceptron.activation(this.sum)*synapse.getWeight());
			}
			else{
				synapse.getToNode().input(this.sum*synapse.getWeight());
			}
		}
		
		//Updates output
		if(this.useActivationFunciton){
			this.senesteoutput = MultilayerPerceptron.activation(this.sum);	
		}
		else{
			this.senesteoutput = this.sum;
		}
		
		//Resets
		this.sum = 0.0;
		this.inputsTriggered = 0;
	}
	
	public void incrementInputCount(){
		this.inputCount++;
	}
	
	public Synapse[] getSynapses(){
		return this.synapses;
	}
}
