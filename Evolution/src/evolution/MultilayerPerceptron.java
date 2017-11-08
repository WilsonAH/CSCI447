//Author: Wilson Harris
//Credit to tutorial by Andreas Thiele for basic structure of algorithm
import java.lang.Math;
import javax.swing.JFrame;


public class MultilayerPerceptron{
	private double[] points; //Array of error over iterations to be graphed using graph()
	private int pointCounter = 0; //Pointer to the position in points[] that is the first empty slot
	private double momentum;
	
	private int[] nodeCounts;
	private int inputVectors;
	private boolean useLinearOutputActivation;
	
	private double learningrate; //Rate at which weights are adjusted
	private Layer[] layers; //Layers
	
	
	/**
	 * Constructor for MLP. Creates layers and places them in an array.
	 * @param nodeCount 	number of nodes in each layer
	 * @param learningrate 	learning rate
	 * @param inputVectors	number of values to be trained on
	 */
	public MultilayerPerceptron(int[] nodeCounts, double learningrate, double momentum, int inputVectors, boolean useLinearOutputActivation){
 		this.learningrate = learningrate;
 		this.momentum = momentum;
 		this.nodeCounts = nodeCounts;
 		this.inputVectors = inputVectors;
 		this.useLinearOutputActivation = useLinearOutputActivation;
 		points = new double[(int) (inputVectors-(inputVectors*0.20))];
 		
 		//Creates an array of Layers. Each layer has an array of nodes
 		layers = new Layer[nodeCounts.length];
 		
 		//Layer constructor: Layer(nodeCount, numberOfInputs, nextLayer, useActivationFunction)
 		layers[layers.length-1] = new Layer(nodeCounts[layers.length-1],1,null,useLinearOutputActivation);
		for(int layer = layers.length-2; layer>=0; layer--){
			if(layer==0){//If it is the first layer, then no activation function will be used
				layers[layer] = new Layer(nodeCounts[layer],nodeCounts[layer+1],layers[layer+1],false);
			}else{
				layers[layer] = new Layer(nodeCounts[layer],nodeCounts[layer+1],layers[layer+1],true);
			}
		}
	}
	
	public MultilayerPerceptron createCopy(){
		MultilayerPerceptron copy = new MultilayerPerceptron(this.nodeCounts, this.learningrate, this.momentum, this.inputVectors, this.useLinearOutputActivation);
		copy.setWeight(this.getWeights());
		return copy;
	}
	
	/**
	 * Algorithm for propagating error back through the network and updating the weights.
	 * @param input 		The input for the network
	 * @param expected 		The expected output based on the input to the network
	 */
	private void backpropagate(double[] input, double[] expected){
		//Gets an output based on the input passed in
		double[] output = classify(input);
		//Sets output error
		for(int n = 0; n < layers[layers.length-1].nodes.length;n++){
			Node node = layers[layers.length-1].nodes[n];
			double error = expected[n]-output[n];
			double derivative = output[n]*(1-output[n]);
			node.setDelta(error*derivative);
		}
		
		//Adds error to array of points to be graphed
		points[pointCounter] = Math.abs(expected[0]-output[0]);
		pointCounter++;
		
		//For each layer except the output layer
		for(int l = layers.length-2; l >=0; l--){
			Layer layer = layers[l];
			Layer forwardLayer = layers[l+1];
			//For every node in the layer
			for(int n = 0; n < layer.nodes.length; n++){
				Node node = layer.nodes[n];
				double sumError = 0;
				//For every node in the layer forward of this one
				for(int forwardN = 0;forwardN < forwardLayer.nodes.length; forwardN++){
					Node forwardNode = forwardLayer.nodes[forwardN];
					//Add the error of that forward node times the weight connecting it to this one
					sumError+=forwardNode.getDelta()*node.getSynapses()[forwardN].getWeight();
				}
				//Set the node delta to the sum of errors * weights in the forward layer times the derivative of this node: o*(1-o)
				node.setDelta(sumError * node.getSenesteOutput()*(1 - node.getSenesteOutput()));
			}
		}
		
		//Sets partial derivatives on each weight
		
		//For each weight in each node in each layer
		for(int l = 0; l <layers.length; l++){
			Layer layer = layers[l];
			for(int n = 0; n <layer.nodes.length;n++){
				Node node = layer.nodes[n];
				for(int s = 0; s < node.getSynapses().length;s++){
					Synapse synapse = node.getSynapses()[s];
					//Add the weights partial derivative to its list of partials with the value node.delta * node.output
					synapse.addPartial(synapse.getToNode().getDelta() * node.getSenesteOutput());
				}
			}
		}
	}
	
	
	/**
	 * Updates the weights based on the partial derivative averages made by backpropogation
	 */
	public void gradientDescent(){
		//For each weight in each node in each layer
		for(int l = 0; l <layers.length; l++){
			Layer layer = layers[l];
			for(int n = 0; n <layer.nodes.length;n++){
				Node node = layer.nodes[n];
				for(int s = 0; s < node.getSynapses().length;s++){
					Synapse synapse = node.getSynapses()[s];
					//Update the weight by adding learningRate * the average partial derivative on the weight + the momentum of weight change
					double weightChange = learningrate*synapse.getAveragePartial()+synapse.getLastChange()*this.momentum;
					synapse.setWeight(synapse.getWeight()+weightChange);
					synapse.setLastChange(weightChange);
				}
			}
		}
	}
	
	
	/**
	 * Resets weights and point array
	 */
	public void reset(){
		//For each weight in each node in each layer
		for(int l = 0; l < layers.length; l++){
			Layer layer = layers[l];
			for(int n = 0; n < layer.nodes.length;n++){
				Node node = layer.nodes[n];
				for(int s = 0; s < node.getSynapses().length;s++){
					Synapse synapse = node.getSynapses()[s];
					//Set the weight to the original random value
					synapse.resetWeight();
				}
			}
		}
		
		//Reset the array graphing the errors
		points = new double[points.length];
		pointCounter = 0;
	}
	
	/**
	 * Set the weights to passed in values
	 * @param weights the values to set the weights to
	 */
	public void setWeight(double[][][] weights){
		//For each weight in each node in each layer
		for(int l = 0; l < layers.length; l++){
			Layer layer = layers[l];
			for(int n = 0; n < layer.nodes.length;n++){
				Node node = layer.nodes[n];
				for(int s = 0; s < node.getSynapses().length;s++){
					Synapse synapse = node.getSynapses()[s];
					//Set the weight to the passed in value
					synapse.setWeight(weights[l][n][s]);
				}
			}
		}
	}
	
	/**
	 * Get the weights for every synapse
	 * @return the weights of every synapse with the index [layer][node][node connected to]
	 */
	public double[][][] getWeights(){
		//Array of weights with index of [layer][node][node connected to]
		double[][][] weights = new double[layers.length][][];
		//For each weight in each node in each layer
		for(int l = 0; l < layers.length; l++){
			Layer layer = layers[l];
			weights[l] = new double[layer.nodes.length][];
			for(int n = 0; n < layer.nodes.length;n++){
				Node node = layer.nodes[n];
				weights[l][n] = new double[node.getSynapses().length];
				for(int s = 0; s < node.getSynapses().length;s++){
					Synapse synapse = node.getSynapses()[s];
					//Set the weight of the array to return
					weights[l][n][s] = synapse.getWeight();
				}
			}
		}
		return weights;
	}
	
	
	
	/**
	 * Changes an input based on an activation function
	 * @param x 	input to be run through sigmoidal activation function
	 * @return 		activated input
	 */
	static double activation(double x){
		//1/1+e^-x
		return 1.0/(1+Math.pow(Math.E, -x));
	}
	
	/**
	 * Passes in input and returns outputs based on those inputs
	 * @param input 	input to be classified
	 * @return 			the outputs of the output layer
	 */
	public double[] classify(double[] input) {
		//Loops through input nodes and passes in input
		for(int n = 0; n < layers[0].nodes.length;n++){
			Node node = layers[0].nodes[n];
			
			//Calling input on a node will activate the whole network.
			node.input(input[n]);
		}
		
		//Loops though output nodes and gets their outputs
		double[] outputs = new double[layers[layers.length-1].nodes.length];
		for(int n = 0; n < layers[layers.length-1].nodes.length;n++){
			Node node = layers[layers.length-1].nodes[n];
			outputs[n] = node.getSenesteOutput();
		}
		return outputs;
	}
	
	/**
	 *Iterates through all of input vectors and updates the weights based on their correctness.
	 *@param input 		Inputs to the network
	 *@param expected	Expected outputs based on the inputs
	 */
	public void train(double[] input, double[] expected){
		//Trains on the data at 10-35% of data. The first 10% is reserved for validation
		for(int n = 0; n < layers[0].nodes.length;n++){
			Node node = layers[0].nodes[n];
			node.input(input[n]); //Calling input on all the input nodes will activate all of the nodes in the network.
		}
		//Update the weights and errors based on the results of the inputs
		backpropagate(input,expected);
	}
	
	/**
	 *Iterates through all of input vectors and gets the average error
	 *@param input 		Inputs to the network
	 *@param expected	Expected outputs based on the inputs
	 */
	public double[] test(double[][] inputs, double expected[][]){
		double correct = 0;
		double sumExpected = 0;
		//For each input vector
		for(int vector = 0; vector<inputs.length;vector++){
			//Get the output based on that input
			double[] output = this.classify(inputs[vector]);
			double[] expect = expected[vector];
			
			//For each output node
			int maxPosition = 0;
			int correctPosition = 0;
			double maxPercent = 0;
			for(int n = 0; n < layers[layers.length-1].nodes.length;n++){
				//Add the error and expected to their sum values
				double perdiction = Math.abs(expect[n]-output[n]);
				if(expect.length>1){
					if(expect[n] == 1){
						correctPosition = n;
					}
					if(perdiction>maxPercent){
						maxPercent=perdiction;
						maxPosition = n;
					}
					sumExpected += Math.abs(expect[n]);
				}else{
					if(expect[n]==1){
						if(output[n]>0.5){
							correct++;
						}
					}else{
						if(output[n]<=0.5){
							correct++;
						}
					}
				}
			}
			if(expect.length>1){
				if(maxPosition==correctPosition){
					correct++;
				}
			}
			
			//Average the error and expected value
			sumExpected/=layers[layers.length-1].nodes.length;
		}
		double[] averages = {(inputs.length-correct)/inputs.length};
		return averages;
	}
	
	/**
	 *Creates a graph based on the errors over iterations of the training sequence
	 */
	public void graph(){
		JFrame f = new JFrame();
	    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    f.add(new graphPlotter(points));//Graphs the points of error over the iterations
	    f.setSize(400,400);
	    f.setLocation(200,200);
	    f.setVisible(true);
	}
}