import java.util.Random;

public class MLP {
	public static newLayer[] layers;
	
	
	public static int nodeCounts[] = {2, 10, 1};
	public static int layerCount = nodeCounts.length;
	public static int inputVectors = 4;
	public static int dimensions = 1;
	public static int maxDomain = 3;
	public static int minDomain = -3;
	public static double bias = 0.1;
	public static double learningRate = 0.1;
	
	public static void main(String[] args){
		layers = new newLayer[layerCount];
		for(int layer = 0; layer < layers.length; layer++){
			if(layer == 0){
				layers[layer] = new newLayer(nodeCounts[layer],0);
			}else{
				layers[layer] = new newLayer(nodeCounts[layer],layers[layer-1].nodes.length);
			}
		}
		
		double inputs[][] = {{0, 0},{1,0},{0,1},{1,1}};//generateInputs();
		for(int loop = 0; loop < 8; loop++){
			for(int i = 0; i < inputVectors; i++){
				//backPropagate(inputs[i], getOutput(inputs[i]));
				//printStatus();
				System.out.println(backPropagate(inputs[i], getOutput(inputs[i])));
			}
		}
	}
	
	public static void printStatus(){
		for(int layer = 0; layer < layers.length; layer++){
			System.out.println("Layer "+layer+":");
			for(int node = 0; node < layers[layer].nodes.length;node++){
				System.out.println("    Node:"+node+"");
				for(int weight = 0; weight < layers[layer].nodes[node].weights.length;weight++){
					System.out.println("        Weight:"+weight+": "+layers[layer].nodes[node].weights[weight]);
				}
			}
		}
	}
	
	public static double backPropagate(double[] inputs, double output[]){
		double expected = calculateExpected(inputs);
		for(int node = 0; node < layers[layers.length-1].nodes.length;node++){
			double error = expected-output[node];
			layers[layers.length-1].nodes[node].delta = error*evaluateDerivative(layers[layers.length-1].nodes[node].value);
		}
		
		for(int layer = layers.length-2;layer>=0;layer--){
			for(int node = 0; node < layers[layer].nodes.length;node++){
				double error = 0;
				for(int forwardNode = 0; forwardNode < layers[layer+1].nodes.length;forwardNode++){
					error += layers[layer+1].nodes[forwardNode].delta * layers[layer+1].nodes[forwardNode].weights[node];
				}
				//System.out.println(error+ " * "+evaluateDerivative(layers[layer].nodes[node].value));
				layers[layer].nodes[node].delta = error * evaluateDerivative(layers[layer].nodes[node].value);				
			}
			
			for(int forwardNode = 0; forwardNode < layers[layer+1].nodes.length;forwardNode++){
				for(int node = 0; node < layers[layer].nodes.length; node++){
					layers[layer+1].nodes[forwardNode].weights[node] += learningRate*layers[layer+1].nodes[forwardNode].delta
							* layers[layer].nodes[node].value;
				}
			}
		}
		double error = 0;
		
		for(int node = 0; node < output.length; node++)
		{
			//System.out.println(expected + " " + output[node]);
			error += Math.abs(expected - output[node]);
		}

		error = error / output.length;
		return error;
	}
	
	public static double[] getOutput(double[] inputs){
		double[] outputs = new double[layers[layers.length-1].nodes.length];
		
		for(int node = 0; node < layers[0].nodes.length;node++){
			layers[0].nodes[node].value = inputs[node];
		}
		
		for(int layer = 1; layer < layers.length; layer++){
			for(int receivingNode = 0; receivingNode < layers[layer].nodes.length;receivingNode++){
				double inputValueForReceivingNode = 0;
				for(int node = 0;node < layers[layer-1].nodes.length;node++){
					inputValueForReceivingNode += layers[layer].nodes[receivingNode].weights[node]*layers[layer-1].nodes[node].value;
					//System.out.println(layers[layer].nodes[receivingNode].weights[node]+" * "+layers[layer-1].nodes[node].value);
				}
				inputValueForReceivingNode += bias;
				layers[layer].nodes[receivingNode].value = transferFunction(inputValueForReceivingNode);
				/*if(layer==layers.length-2){
					layers[layer].nodes[receivingNode].value = inputValueForReceivingNode;
				}else{
					layers[layer].nodes[receivingNode].value = transferFunction(inputValueForReceivingNode);
				}*/
			}
		}
		
		for(int outputNode = 0;outputNode<layers[layers.length-1].nodes.length;outputNode++){
			outputs[outputNode] = layers[layers.length-1].nodes[outputNode].value;
		}
		
		return outputs;
	}
	
	public static double[][] generateInputs(){
		double[][] inputs = new double[inputVectors][dimensions];
		Random numberGenerator = new Random();
		for(int i = 0; i < inputVectors; i++){
			for(int i2 = 0; i2 < dimensions; i2++){
				inputs[i][i2] = ((double)numberGenerator.nextInt(((maxDomain*100)-(minDomain*100))+1)+(minDomain*100))/100.00;
			}
		}
		return inputs;
	}
	
	public static double transferFunction(double input){
		return (1.0/(1.0+Math.exp(-input)));
	}
	
	public static double evaluateDerivative(double value){
		return value*(1 - value);
	}
	
	public static double calculateExpected(double[] input){
		if((input[0] == 0 && input[1] == 0) || (input[0] == 1 && input[1] == 1)){
			return 0;
		}else{
			return 1;
		}
	}
}
