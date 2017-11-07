//Author: Wilson Harris
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Project3Application {
	/*Connect 4: 67557 input vectors, 42 dimensions, 42 input nodes, 3 output nodes, 3 possible classifications, true last input of MLP constructor, “connect4.txt”
	Best backprop results with 2 hidden layers: 1 node each layer, momentum 0.5, learning rate 0.1
	
	Seismic: 2584 input vectors, 18 dimensions, 18 input nodes, 1 output node, 2 possible classifications, true last input of MLP constructor, “seismic.txt”
	Best backprop results with 2 hidden layers: 1 node each layer, momentum 0.25, learning rate 0.1

	TicTacToe: 958 input vectors, 9 dimensions, 9 input nodes, 1 output node, 2 possible classifications, true last input of MLP constructor, “tictactoe.txt”
	Best backprop results with 1 hidden layer: 50 nodes, momentum 0.5, learning rate 0.1

	Chess: 28056 input vectors, 6 dimensions, 6 input nodes, 17 output nodes, 17 possible classifications, true last input of MLP constructor, “chess.txt”
	Best backprop results with 1 hidden layer: 5 nodes or 2 hidden layers: 1 node each layer, momentum 0.5, learning rate 0.1 for either

	Poker: 1000000 input vectors, 10 dimensions, 10 input nodes, 10 output nodes, 10 possible classifications, true last input of MLP constructor, “poker.txt”
	Best backprop results with 2 hidden layers: 1 node each layer, momentum 0.5, learning rate 0.01 */
	
	
	
	
	private static int dimensions = 42; //How many inputs are passed in
	private static int inputVectors = 67557; //How many arrays of inputs are tested
	private static int possibleClassifications = 3; //If the problem is multiClass, the number of classifcations
	private static String fileName = "connect4.txt";
	private static int gradientBatchSize = 5;//How many inputs are trained on before updating weight
	private static double learningRate = 0.5;
	private static double momentum = 0.1;
	private static int[] nodeCounts = {42,30,3};
	
	private static int mu = 10;
	private static int lambda = 4;
	private static double mutationRate = 0.5;
	private static double standardDeviation = 0.5;
	private static double alpha = 2;
	private static int generationsPerTrain = 3;
	
	public static void main(String[] args){
		//Creates MLPs with the inputs MultilayerPerceptron(nodeCounts, learningrate, momentum, numberOfInputVectors, useLogisticOutputActivation)
		MultilayerPerceptron[] MLPs = new MultilayerPerceptron[mu];
		for(int i = 0; i < mu; i++){
			MLPs[i] = new MultilayerPerceptron(nodeCounts, learningRate, momentum, inputVectors, true);
		}
		
		EvolutionAlgorithm es = new EvolutionaryStrategy(mu,lambda,mutationRate,standardDeviation,alpha,generationsPerTrain);
		es.initPopulation(MLPs);
		
		//Runs 10 5x2 cross validation tests and sums the error
		double sumCorrectPercent = 0;
		for(int validation = 0; validation < 10; validation++){
			sumCorrectPercent+=fiveByTwoCrossValidation(loadInputs(),es,(validation==4));
		}
		
		//Averages the errors from the ten tests
		double totalAverageError = sumCorrectPercent/10;
		System.out.println("Error: "+(totalAverageError*100));
	}
	
	
	/**
	 * Runs 5x2 cross validation of a MLP on an input set.  The input is split into two halves.  The MLP trains 
	 * on the first half, and then it trains on the second.  After getting the error, it resets and starts again,
	 * but it trains on the second and tests on the first.  This process is repeated 5 times.
	 * @param inputs				The data to train and test on
	 * @param MultilayerPerceptron	The mlp to be trained and tested
	 * @param lastRun				A boolean used to check if the data should be graphed. (Only want to graph once and not five times)
	 */	
	private static double fiveByTwoCrossValidation(double[][] inputsAndExpected, EvolutionAlgorithm es, boolean isLastRun){
		//Initializes varibles to be used to average error and expected numbers
		double sumCorrectPercent = 0;
		
		double inputs[][] = new double[inputsAndExpected.length][dimensions];
		double[][] expected = null;
		if(possibleClassifications>2){
			expected = new double[inputsAndExpected.length][possibleClassifications];
		}else
			expected = new double[inputsAndExpected.length][1];
		//Seperate inputs and outputs
		for(int index = 0; index < inputsAndExpected.length; index++){
			for(int input = 0; input < inputsAndExpected[index].length; input++){
				if(input < dimensions){
					inputs[index][input] = inputsAndExpected[index][input];
				}else{
					if(possibleClassifications>2){
						expected[index][(int)inputsAndExpected[index][input]] = 1;
					}else{
						expected[index][0] = inputsAndExpected[index][input];
					}
				}
			}
		}
		
		//Random generator used to shuffle the input data
		Random r = new Random();
		for(int loop = 0; loop < 5; loop++){
			System.out.println("Testing round "+(loop+1));
			
			//Randomly Organize inputs
			
			//For every input vector
			for(int vector = 0; vector<inputs.length;vector++){
				//Swap the position of the vector with another randomly placed vector in the array
				int random = r.nextInt(inputs.length);
				double[] temp = inputs[random];
				double[] expectTemp = expected[random];
				inputs[vector]= inputs[random];
				expected[vector] = expected[random];
				inputs[random] = temp;
				expected[random] = expectTemp;
			}
			
			//Use 10% of data as a validation set. (5% of total data)
			//Split data in to train and test sets with two arrays.  One for input and one to compare the output with.
			int validationCutoff = (int) (inputVectors/20);
			double[][] firstHalfInputs = new double[(inputVectors/2)-validationCutoff][dimensions];
			double[][] firstExpected = new double[inputVectors/2][expected[0].length];
			double[][] secondHalfInputs = new double[inputVectors-firstHalfInputs.length-validationCutoff][dimensions];
			double[][] secondExpected = new double[inputVectors-firstHalfInputs.length-validationCutoff][expected[0].length];
			
			
			
			//Init validation data.  Both input and the array to compare the output with
			double[][] firstValidationData = new double[validationCutoff][firstHalfInputs[0].length];
			double[][] firstValidationExpected = new double[validationCutoff][firstExpected[0].length];
			double[][] secondValidationData = new double[validationCutoff][firstHalfInputs[0].length];
			double[][] secondValidationExpected = new double[validationCutoff][firstExpected[0].length];
			
			//For every input vector
			for(int input = 0; input<inputVectors;input++){
				//Place the first half in the first arrays.
				if(input<inputVectors/2){
					
					
					//If it is within the first 10% of the first half of inputs
					if(input<firstValidationData.length){
						//Copy the data into the validation data set
						firstValidationData[input] = inputs[input];
						firstValidationExpected[input] = expected[input];
					}else{
						//Inputs
						firstHalfInputs[input-firstValidationData.length] = inputs[input-firstValidationData.length];
						//Calculated expected outputs
						firstExpected[input-firstValidationData.length] = expected[input-firstValidationData.length];
					}
				}else{//Place the second half in the second arryas
					
					
					//If it is within the first 10% of the second half of inputs
					if((input-(inputVectors/2))<secondValidationData.length){
						//Copy the data into the validation data set
						secondValidationData[input-(inputVectors/2)] = inputs[input-(inputVectors/2)];
						secondValidationExpected[input-(inputVectors/2)] = expected[input-(inputVectors/2)];
					}else{
						//Inputs
						secondHalfInputs[input-(inputVectors/2)-secondValidationData.length] = inputs[input-(inputVectors/2)-secondValidationData.length];
						//Calculated expected outputs
						secondExpected[input-(inputVectors/2)-secondValidationData.length] = expected[input-(inputVectors/2)-secondValidationData.length];
					}
				}
			}
			
			
			//********Train on the first half of data and test on the second half
			//Reset weights and point array
			es.train(firstValidationData, firstValidationExpected);
			for(MultilayerPerceptron mlp: es.getPopulation()){
				mlp.reset();
			}
			
			
			boolean isErrorDecreasing = true;//Boolean to see if error against validation set is decreasing
			double[] lastErrors = new double[2];//Array used to check average error of last comparison against validation set
			//While the input we are checking is less than the total data we have to train on and the error is decreasing against the validation set
			System.out.println("First Half");
			while(isErrorDecreasing){
				
				System.out.println("Error of best individual against test data: " + (es.test(secondHalfInputs,secondExpected)*100)+"%");
				es.train(firstHalfInputs, firstExpected);
				
				lastErrors[1] = lastErrors[0];
				lastErrors[0] = es.test(firstValidationData,firstValidationExpected);
				//If error has increased over the last two iterations.
				if(lastErrors[0]>=lastErrors[1] && lastErrors[1]!=0){
					isErrorDecreasing = false;
				}
			}
			System.out.println("Error of best individual against test data: " + (es.test(secondHalfInputs,secondExpected)*100)+"%");
			
			
			//Graphs the error if this is the last run (We only want to graph once)
			if(loop == 0 && isLastRun){
				//es.getPopulation()[0].graph();
			}
			
			//Adds the error and expected output to the array used to store the averages.
			double correctPercent = es.test(secondHalfInputs, secondExpected);
			
			
			//********Train on the second half of data and test on the first half
			//Reset weights and point array
			for(MultilayerPerceptron mlp: es.getPopulation()){
				mlp.reset();
			}
			
			//resets variables
			isErrorDecreasing = true;
			lastErrors = new double[4];
			//While the input we are checking is less than the total data we have to train on and the error is decreasing against the validation set
			//While the input we are checking is less than the total data we have to train on and the error is decreasing against the validation set
			System.out.println("Second Half");
			while(isErrorDecreasing){
				System.out.println("Error of best individual against training data: " + es.test(firstHalfInputs, firstExpected));
				es.train(secondHalfInputs, secondExpected);
				
				lastErrors[1] = lastErrors[0];
				lastErrors[0] = es.test(secondValidationData,secondValidationExpected);
				//If error has increased over the last two iterations.
				if(lastErrors[0]>=lastErrors[1] && lastErrors[1]!=0){
					isErrorDecreasing = false;
				}
			}
			System.out.println("Error of best individual against training data: " + es.test(firstHalfInputs, firstExpected));
			
			
			//Adds the error and expected output to the array used to store the averages.
			correctPercent += es.test(firstHalfInputs,firstExpected);
			sumCorrectPercent+=correctPercent/2;
		}
		//Averages the error and expected over the 5 tests
		double totalCorrectPercent = sumCorrectPercent/5;
		return totalCorrectPercent;
	}
	
	/**
	 *Generates a random input between this.maxDomain and this.minDomain/100
	 *@return	the randomly generated inputs
	 */
	private static double[][] loadInputs(){
		ArrayList<double[]> inputsList = new ArrayList<double[]>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("data/"+fileName));
		} catch (FileNotFoundException e) {
			System.out.println("Arrrg, there was an error loading the file, matey.");
			System.exit(0);
		}
		Scanner scanner = new Scanner(br);
		while(scanner.hasNext()){
			double[] fileInput = new double[dimensions+1];
			for(int index = 0; index < fileInput.length;index++){
				fileInput[index] = scanner.nextDouble();
			}
			inputsList.add(fileInput);
		}
		scanner.close();
		double[][] inputs = new double[inputsList.size()][dimensions];
		int index = 0;
		for(double[] input: inputsList){
			inputs[index] = input;
			index++;
		}
		return inputs;
	}
}
