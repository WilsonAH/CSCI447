//Author: Wilson Harris
//Credit to tutorial by Andreas Thiele for basic structure of algorithm
import java.lang.Math;
import java.util.Random;
import javax.swing.JFrame;


public class MultilayerPerceptron{
	private double[] points; //Array of error over iterations to be graphed using graph()
	private int pointCounter = 0; //Pointer to the position in points[] that is the first empty slot
	private double lastError = 0;
	private double momentum;
	
	private double learningrate; //Rate at which weights are adjusted
	private Layer[] layers;
	
	
	/**
	 * Constructor for MLP. Creates layers and places them in an array.
	 * @param nodeCount 	number of nodes in each layer
	 * @param learningrate 	learning rate
	 * @param inputVectors	number of values to be trained on
	 */
	public MultilayerPerceptron(int[] nodeCounts, double learningrate, double momentum, int inputVectors, boolean useLinearOutputActivation){
 		this.learningrate = learningrate;
 		this.momentum = momentum;
 		points = new double[inputVectors];
 		
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
	
	/**
	 * Algorithm for propagating error back through the network and updating the weights.
	 * @param input 		The input for the network
	 * @param expected 		The expected output based on the input to the network
	 */
	private void backpropagate(double[] input, double expected){
		//Gets an output based on the input passed in
		double[] output = classify(input);
		//Sets output error
		for(int n = 0; n < layers[layers.length-1].nodes.length;n++){
			Node node = layers[layers.length-1].nodes[n];
			node.setDelta(expected-output[n]);
		}
		
		points[pointCounter] = Math.abs(expected-output[0]);
		pointCounter++;
		
		//Adds the error to the points array for graphing
		for(int l = layers.length-2; l >=0; l--){
			Layer layer = layers[l];
			Layer forwardLayer = layers[l+1];
			for(int n = 0; n < layer.nodes.length; n++){
				Node node = layer.nodes[n];
				double sumError = 0;
				for(int forwardN = 0;forwardN < forwardLayer.nodes.length; forwardN++){
					Node forwardNode = forwardLayer.nodes[forwardN];
					sumError+=forwardNode.getDelta()*node.getSynapses()[forwardN].getWeight();
				}
				node.setDelta(sumError * node.getSenesteOutput()*(1 - node.getSenesteOutput()));
			}
		}
		
		//Sets partial derivatives on each weight
		for(int l = 0; l <layers.length; l++){
			Layer layer = layers[l];
			for(int n = 0; n <layer.nodes.length;n++){
				Node node = layer.nodes[n];
				for(int s = 0; s < node.getSynapses().length;s++){
					Synapse synapse = node.getSynapses()[s];
					synapse.addPartial(synapse.getToNode().getDelta() * node.getSenesteOutput());
					/*double weightChange = learningrate*synapse.getPartial()+synapse.getLastChange()*this.momentum;
					synapse.setWeight(synapse.getWeight()+weightChange);
					synapse.setLastChange(weightChange);*/
				}
			}
		}
		
		//Gradient Descent
		
		/*
		}*/
	}
	
	public void gradientDescent(){
		for(int l = 0; l <layers.length; l++){
			Layer layer = layers[l];
			for(int n = 0; n <layer.nodes.length;n++){
				Node node = layer.nodes[n];
				for(int s = 0; s < node.getSynapses().length;s++){
					Synapse synapse = node.getSynapses()[s];
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
		for(int l = 0; l < layers.length; l++){
			Layer layer = layers[l];
			for(int n = 0; n < layer.nodes.length;n++){
				Node node = layer.nodes[n];
				for(int s = 0; s < node.getSynapses().length;s++){
					Synapse synapse = node.getSynapses()[s];
					synapse.resetWeight();
				}
			}
		}
		points = new double[points.length];
		pointCounter = 0;
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
	 *@param expected	Expected value based on input
	 */
	public void train(double[] input, double expected){
		//Trains on the data at 10-35% of data. The first 10% is reserved for validation
		for(int n = 0; n < layers[0].nodes.length;n++){
			Node node = layers[0].nodes[n];
			node.input(input[n]); //Calling input on all the input nodes will activate all of the nodes in the network.
		}
		//Update the weights and errors based on the results of the inputs
		backpropagate(input,expected);
	}
	
	public double[] test(double[][] inputs, double expected[]){
		double sumError = 0;
		double sumExpected = 0;
		for(int vector = 0; vector<inputs.length;vector++){
			double[] output = this.classify(inputs[vector]);
			for(int n = 0; n < layers[layers.length-1].nodes.length;n++){
				sumError += Math.abs(expected[vector]-output[n]);
				sumExpected += Math.abs(expected[n]);
			}
			sumError/=layers[layers.length-1].nodes.length;
			sumExpected/=layers[layers.length-1].nodes.length;
		}
		double[] averages = {sumError/inputs.length,sumExpected/inputs.length};
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