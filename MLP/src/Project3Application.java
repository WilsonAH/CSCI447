//Author: Wilson Harris
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Project3Application {
	private static int dimensions = 9; //How many inputs are passed in
	private static int inputVectors = 958; //How many arrays of inputs are tested
	private static int NUMGENS = 0;
	private static int possibleClassifications = 2; //If the problem is multiClass, the number of classifcations
	
	private static String filename = "tictactoe.txt";
	
	private static int gradientBatchSize = 5;//How many inputs are trained on before updating weights
	
	public static void main(String[] args){
		//Constructs an mlp
		int[] nodeCounts = {9,50,1};
		
		//Creates a MLP with the inputs MultilayerPerceptron(nodeCounts, learningrate, momentum, numberOfInputVectors, useLogisticOutputActivation)
		MultilayerPerceptron mlp = new MultilayerPerceptron(nodeCounts, 0.5, 0.1, inputVectors, true);
		// Workaround to run the GA with randomized MLPs
		MultilayerPerceptron[] MLPs = new MultilayerPerceptron[50];
		for(int i = 0; i < MLPs.length; i++) {
			mlp.reset();
			MLPs[i] = mlp;
		}
		GeneticAlgorithm ga = new GeneticAlgorithm();
		System.out.println("Initializing GA");
		ga.initPopulation(MLPs);
		// Creating the inputs and expected to use for fitness of populations - stolen from cross validation
		double[][] inputsAndExpected = loadInputs();
		double inputs[][] = new double[inputsAndExpected.length][dimensions];
		double[][] expected = null;
		if(possibleClassifications>2){
			expected = new double[inputsAndExpected.length][possibleClassifications];
		}else
			expected = new double[inputsAndExpected.length][1];
		//Separate inputs and outputs
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
		// Find the error for the initial population
		double errors = ga.averageGenerationFitness(ga.fitness(inputs, expected));
		int p = 0;
		do {
			System.out.println("Error = " + errors);
			p++;
			System.out.println("Evolving Generation" + p);
			ga.evolveOneGeneration(inputs, expected);
			double newError = ga.averageGenerationFitness(ga.fitness(inputs, expected));
			// If error stays the same, increment the number of generations
			if(newError == errors) {
				NUMGENS++;
			}
			errors = newError;
		}
		// continue to evolve until the error falls beneath the threshold or 
		// error is the same for 10 generations
		while(!ga.terminate(errors) && NUMGENS != 10); 
		mlp = ga.result(ga.fitness(inputs, expected));
		// run MLP with best version returned by GA
		System.out.println("Best MLP returned.");
		//Runs 10 5x2 cross validation tests and sums the error
		double sumCorrectPercent = 0;
		for(int validation = 0; validation < 10; validation++){
			sumCorrectPercent+=fiveByTwoCrossValidation(loadInputs(),mlp,(validation==4));
		}
		
		//Averages the errors from the ten tests
		double totalAverageError = sumCorrectPercent/10;
		System.out.println("Error: "+(totalAverageError*100));
		
		
		//Commented out code is used to run smaller tests for debugging
		/*double[][] inputs = generateInputs();
		Random r = new Random();
		for(int i = 0; i < inputVectors; i++){
			double[] input = inputs[i%15];
			double expected = rosenbrockFunction(input);
			mlp.train(input, expected);
			if(i%gradientBatchSize==0){
				mlp.gradientDescent();
			}
		}
		
		for(int i = 0; i < 4; i++){
			double in[] = inputs[r.nextInt(inputVectors-1)];
			//System.out.println("Classifying "+xor[i][0]+","+xor[i][1]+". Output: "+mlp.classify(xor[i])[0]);
			System.out.println("Classifying "+in[0]+","+in[1]+". Output: "+mlp.classify(in)[0]+". Expected: "+rosenbrockFunction(in));
		}
		mlp.graph();*/
	}
	public double getError(double sumCorrectPercent) {
		double totalAverageError = sumCorrectPercent/10;
		totalAverageError = totalAverageError*100;
		System.out.println("Error: "+(totalAverageError*100));
		return totalAverageError;
	}
	
	/**
	 * Runs 5x2 cross validation of a MLP on an input set.  The input is split into two halves.  The MLP trains 
	 * on the first half, and then it trains on the second.  After getting the error, it resets and starts again,
	 * but it trains on the second and tests on the first.  This process is repeated 5 times.
	 * @param inputs				The data to train and test on
	 * @param MultilayerPerceptron	The mlp to be trained and tested
	 * @param lastRun				A boolean used to check if the data should be graphed. (Only want to graph once and not five times)
	 */	
	private static double fiveByTwoCrossValidation(double[][] inputsAndExpected, MultilayerPerceptron mlp, boolean isLastRun){
		//Initializes variables to be used to average error and expected numbers
		double sumCorrectPercent = 0;
		double sumExpect = 0;
		
		double inputs[][] = new double[inputsAndExpected.length][dimensions];
		double[][] expected = null;
		if(possibleClassifications>2){
			expected = new double[inputsAndExpected.length][possibleClassifications];
		}else
			expected = new double[inputsAndExpected.length][1];
		//Separate inputs and outputs
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
			//Reset weights and point array
			mlp.reset();
			
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
			
			//Split data in to train and test sets with two arrays.  One for input and one to compare the output with.
			double[][] firstHalfInputs = new double[inputVectors/2][dimensions];
			double[][] firstExpected = new double[inputVectors/2][inputs[0].length-dimensions];
			double[][] secondHalfInputs = new double[inputVectors-(inputVectors/2)][dimensions];
			double[][] secondExpected = new double[inputVectors-(inputVectors/2)][inputs[0].length-dimensions];
			
			
			//Use 10% of data as a validation set. (5% of total data)
			int validationCutoff = (int) (firstHalfInputs.length*0.1);
			//Init validation data.  Both input and the array to compare the output with
			double[][] firstValidationData = new double[validationCutoff][firstHalfInputs[0].length];
			double[][] firstValidationExpected = new double[validationCutoff][firstExpected[0].length];
			double[][] secondValidationData = new double[validationCutoff][firstHalfInputs[0].length];
			double[][] secondValidationExpected = new double[validationCutoff][firstExpected[0].length];
			
			//For every input vector
			for(int input = 0; input<inputVectors;input++){
				//Place the first half in the first arrays.
				if(input<inputVectors/2){
					//Inputs
					firstHalfInputs[input] = inputs[input%inputs.length];
					//Calculated expected outputs
					firstExpected[input] = expected[input];
					
					//If it is within the first 10% of the first half of inputs
					if(input<firstValidationData.length){
						//Copy the data into the validation data set
						firstValidationData[input] = firstHalfInputs[input];
						firstValidationExpected[input] = firstExpected[input];
					}
				}else{//Place the second half in the second arryas
					//Inputs
					secondHalfInputs[input-(inputVectors/2)] = inputs[input%inputs.length];
					//Calculated expected outputs
					secondExpected[input-(inputVectors/2)] = expected[input-(inputVectors/2)];
					
					//If it is within the first 10% of the second half of inputs
					if((input-(inputVectors/2))<secondValidationData.length){
						//Copy the data into the validation data set
						secondValidationData[input-(inputVectors/2)] = secondHalfInputs[input-(inputVectors/2)];
						secondValidationExpected[input-(inputVectors/2)] = secondExpected[input-(inputVectors/2)];
					}
				}
			}
			
			//********Train on the first half of data and test on the second half
			//Set weights to a random setting.
			mlp.reset();

			//Trains on data in 5-30% position
			int vector;
			for(vector = validationCutoff; vector<firstHalfInputs.length*0.30;vector++){
				mlp.train(firstHalfInputs[vector],firstExpected[vector]);
				//Once the gradient batch size has been trained on, use gradient descent to update the weights
				if(vector%gradientBatchSize==0){
					mlp.gradientDescent();
				}
			}
			
			//Starts to test against validation after the 30% mark
			
			//Size to train before testing against validation set
			int batchSize = (int) (firstHalfInputs.length*0.05); //Every 5% of data, the system will check if the error against validation is going up or down
			boolean isErrorDecreasing = true;//Boolean to see if error against validation set is decreasing
			double[] lastErrors = new double[2];//Array used to check average error of last comparison against validation set
			//While the input we are checking is less than the total data we have to train on and the error is decreasing against the validation set
			while(vector<firstHalfInputs.length&&isErrorDecreasing){
				//For 5% of the input vectors
				int lastVectorStop = vector;
				for(vector = lastVectorStop; vector<(lastVectorStop+batchSize) && vector < firstHalfInputs.length;vector++){
					//Train the mlp
					mlp.train(firstHalfInputs[vector],firstExpected[vector]);
					//Use gradient descent to update weights after the batch size is trained on
					if(vector%gradientBatchSize==0){
						mlp.gradientDescent();
					}
				}
				
				//Updates last error array by adding the newest error to the front and pushing everything else back one position
				lastVectorStop+=batchSize;
				lastErrors[1] = lastErrors[0];
				lastErrors[0] = mlp.test(firstValidationData,firstValidationExpected)[0];
				//If error has increased over the last two iterations.
				if(lastErrors[0]>=lastErrors[1] && lastErrors[1]!=0){
					isErrorDecreasing = false;
				}
			}
			
			//Graphs the error if this is the last run (We only want to graph once)
			if(loop == 0 && isLastRun){
				mlp.graph();
			}
			
			//Adds the error and expected output to the array used to store the averages.
			double []averages = mlp.test(secondHalfInputs, secondExpected);
			double correctPercent = averages[0];
			double expect = averages[1];
			
			
			//********Train on the second half of data and test on the first half
			//Set weights to a random setting.
			mlp.reset();

			//Trains on data in 5-30% position
			for(vector = validationCutoff; vector<firstHalfInputs.length*0.30;vector++){
				mlp.train(secondHalfInputs[vector],secondExpected[vector]);
				//Once the gradient batch size has been trained on, use gradient descent to update the weights
				if(vector%gradientBatchSize==0){
					mlp.gradientDescent();
				}
			}
			
			//Starts to test against validation
			
			//resets variables
			isErrorDecreasing = true;
			lastErrors = new double[4];
			vector = 0;
			//While the input we are checking is less than the total data we have to train on and the error is decreasing against the validation set
			while(vector<secondHalfInputs.length&&isErrorDecreasing){
				int lastVectorStop = vector;
				//For 5% of the input data
				for(vector = lastVectorStop; vector<(lastVectorStop+batchSize) && vector < secondHalfInputs.length;vector++){
					//Train the MLP
					mlp.train(secondHalfInputs[vector],secondExpected[vector]);
					//Use gradient descent to update weights after the batch size is trained on
					if(vector%gradientBatchSize==0){
						mlp.gradientDescent();
					}
				}
				
				//Updates last error array by adding the newest error to the front and pushing everything else back one position
				lastVectorStop+=batchSize;
				lastErrors[1] = lastErrors[0];
				lastErrors[0] = mlp.test(secondValidationData,secondValidationExpected)[0];
				//If error has increased over the last two iterations.
				if(lastErrors[0]>=lastErrors[1] && lastErrors[1]!=0){
					isErrorDecreasing = false;
				}
			}
			
			//Adds the error and expected output to the array used to store the averages.
			averages = mlp.test(firstHalfInputs,firstExpected);
			correctPercent += averages[0];
			expect += averages[1];
			sumCorrectPercent+=correctPercent/2;
			sumExpect+=expect/2;
		}
		//Averages the error and expected over the 5 tests
		double totalCorrectPercent = sumCorrectPercent/5;
		double totalExpect = sumExpect/5;
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
			br = new BufferedReader(new FileReader("data/"+filename));
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
