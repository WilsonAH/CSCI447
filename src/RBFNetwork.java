import java.util.Scanner;

import com.sun.xml.internal.ws.Closeable;



public class RBFNetwork {
	
	
	/* Implement and test several neural network training algorithms
	 * to perform function approximation 
	 * {for purposes of the RBF section, read as: use different activation functions within the RBF}
	 *  
	 * and compare the results of applying these algorithms, 
	 * to approximate five versions of the Rosenbrock function.
	 * f(x)=f(x1,x2,...,xn) = [n-1/summation/i=1] [(1−xi)2 +100(xi+1 −x2i)2].
	 *  
	 * 
	 * Implement a radial basis function neural network 
	 * with an arbitrary number of inputs, 
	 * an arbitrary number of Gaussian basis functions, 
	 * and an arbitrary number of outputs. 
	 * Your program should accept the number of inputs, Gaussians, and outputs.
	 *  It is your choice which output activation function is used.
	 */
	
// some instance variables...	
// K is the number of vectors to generate and K's dimensions	
static int K, dimensions;	
// number of Gaussian (hidden) nodes, etc.
static int gaus, outputs, theReply, sizeOfDataSet;
static int [] dataPoints;
static int someArbitraryRadius;


//just to run things along
public static void main(String[] args) {
	//get some input about the data
	fetchK();
	//now create more data per problem instructions. 
	//then use it to create the RBF
	generate(K,dimensions);
	//1st take our data. every single point of it and wrap it in a node.
		

}


private static void generate(int k2, int dimensions2) {
	// 
	
}


	//nifty method for getting info
public static int fetchK() {	
	print("Lets make a RBF. But first we need some info.");
	print("Please enter all values as integers.");
	print("How many vectors do you want to generate?");
K = scanIt();
    print("How many dimensions per vector:(2 thru 6)");
dimensions = scanIt();
    print("Now how many hidden nodes should the RBF have?");
gaus = scanIt();
    print("how many output nodes?");
outputs = scanIt();
	print("what about the radius for the Gaussian functions area of influence?");
someArbitraryRadius = scanIt();

	print("Thats "+ K + ", " + dimensions + " dimensional vectors");
	print( "And " + gaus + " hidden and " + outputs + " output nodes,");
	print("....and the Gaussian radius; " + someArbitraryRadius);
	print("generating BRF.");
	

    return K; 
    
	}


//helper methods for fetch;
	//to shorten up javas god-aweful "print"
private static String print ( String x ) {
	System.out.println( x );
	return x;	
	}
	//scan helper	
private static int scanIt() {
	Scanner reply = new Scanner(System.in);
	theReply = reply.nextInt();
	return theReply;
}


public RBFNetwork() {
	
	
		//
	}


	private Layer layers[];

	public RBFNetwork(int layerCount, int[] nodeCounts, int inputCount) {
		layers = new Layer[layerCount];
	}
	public void UpdateWeights(){
		
	}
	
	
	
	private void findSigma(double[] inputs) {

	}
	
	public double getOutput(double inputs[]){
		double[] outputs = inputs;
		for(int loop = 0; loop < layers.length; loop++){
			outputs = layers[loop].getOutputs(outputs);
		}
		return outputs[0];
	}
	
	
	//a good old fashion Gaussian function.
	private static double gausFunction(double queryPoint) {
		
		double avgOfAllData = 0;
		for (int i = 0; i < sizeOfDataSet; i++) {
			double dataBucket = 0;
			dataBucket += dataPoints[i];
			avgOfAllData = dataBucket / sizeOfDataSet;
				
		}
		double somNum = Math.pow((queryPoint - avgOfAllData), 2)/ Math.pow((2.0 * someArbitraryRadius),2 );
		return Math.pow(Math.E,somNum);
		
	}
	
	
//	private double getDistance(double[] x1, double[] x2) {
//
//	}
}
