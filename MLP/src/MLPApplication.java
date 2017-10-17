//Author: Wilson Harris
import java.util.Random;

import javax.swing.JFrame;

public class MLPApplication {
	private static int dimensions = 2; //How many inputs are passed in
	private static int inputVectors = 10000; //How many arrays of inputs are tested
	
	private static int maxDomain = 1; //Max and min domain of Rosenbrock number generation
	private static int minDomain = -1;
	
	private static int gradientBatchSize = 10;
	
	public static void main(String[] args){
		//Constructs a mlp
		int[] nodeCounts = {2,100,1};
		MultilayerPerceptron mlp = new MultilayerPerceptron(nodeCounts, 0.01, 0.5, inputVectors, false);
		double[] input = {1,-1};
		System.out.println(rosenbrockFunction(input));
		double sumError = 0;
		for(int validation = 0; validation < 10; validation++){
			sumError+=fiveByTwoCrossValidation(generateInputs(),mlp,(validation==4));
		}
		double totalAverageError = sumError/10;
		System.out.println("Error: "+totalAverageError);
		
		
		/*double[][] inputs = generateInputs();
		Random r = new Random();
		for(int i = 0; i < inputVectors; i++){
			double[] input = inputs[i%20];
			double expected = rosenbrockFunction(input);
			mlp.train(input, expected);
			if(i%30==0){
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
	
	private static double fiveByTwoCrossValidation(double[][] inputs, MultilayerPerceptron mlp, boolean lastRun){
		//double[][] inputs = generateInputs();
		double sumErrors = 0;
		double sumExpect = 0;
		Random r = new Random();
		for(int loop = 0; loop < 5; loop++){
			//Reset weights and point array
			mlp.reset();
			
			//Randomly Organize inputs
			for(int vector = 0; vector<inputs.length;vector++){
				for(int input = 0; input<inputs[vector].length;input++){
					int random = r.nextInt(inputs[input].length);
					double temp = inputs[vector][input];
					inputs[vector][input] = inputs[vector][random];
					inputs[vector][random] = temp;
				}
			}
			
			//Split data in to train and test sets.
			double[][] firstHalfInputs = new double[inputVectors/2][dimensions];
			double[] firstExpected = new double[inputVectors/2];
			double[][] secondHalfInputs = new double[inputVectors-(inputVectors/2)][dimensions];
			double[] secondExpected = new double[inputVectors-(inputVectors/2)];
			
			
			//Use 10% of data as a validation set. (5% of total data)
			int validationCutoff = (int) (firstHalfInputs.length*0.1);
			//Init validation data
			double[][] firstValidationData = new double[validationCutoff][firstHalfInputs[0].length];
			double[] firstValidationExpected = new double[validationCutoff];
			double[][] secondValidationData = new double[validationCutoff][firstHalfInputs[0].length];
			double[] secondValidationExpected = new double[validationCutoff];
			
			for(int input = 0; input<inputVectors;input++){
				if(input<inputVectors/2){
					firstHalfInputs[input] = inputs[input%inputs.length];
					firstExpected[input] = rosenbrockFunction(firstHalfInputs[input]);
					if(input<firstValidationData.length){
						firstValidationData[input] = firstHalfInputs[input];
						firstValidationExpected[input] = firstExpected[input];
					}
				}else{
					secondHalfInputs[input-(inputVectors/2)] = inputs[input%inputs.length];
					secondExpected[input-(inputVectors/2)] = rosenbrockFunction(secondHalfInputs[input-(inputVectors/2)]);
					if(input<secondValidationData.length){
						secondValidationData[input] = secondHalfInputs[input];
						secondValidationExpected[input] = secondExpected[input];
					}
				}
			}
			
			//Train first test second half
			mlp.reset();

			//Trains on data in 10-30% position
			int vector;
			for(vector = validationCutoff; vector<firstHalfInputs.length*0.30;vector++){
				mlp.train(firstHalfInputs[vector],firstExpected[vector]);
				if(vector%gradientBatchSize==0){
					mlp.gradientDescent();
				}
			}
			
			//Starts to test against validation
			int batchSize = (int) (firstHalfInputs.length*0.05); //Size to train before testing validation set
			boolean errorDecreasing = true;
			double[] lastErrors = new double[4];
			while(vector<firstHalfInputs.length&&errorDecreasing){
				int lastVectorStop = vector;
				for(vector = lastVectorStop; vector<(lastVectorStop+batchSize) && vector < firstHalfInputs.length;vector++){
					mlp.train(firstHalfInputs[vector],firstExpected[vector]);
					if(vector%gradientBatchSize==0){
						mlp.gradientDescent();
					}
				}
				lastVectorStop+=batchSize;
				lastErrors[3] = lastErrors[2];
				lastErrors[2] = lastErrors[1];
				lastErrors[1] = lastErrors[0];
				lastErrors[0] = mlp.test(firstValidationData,firstValidationExpected)[0];
				//If error has increased over the last two iterations.
				if(lastErrors[0]>=lastErrors[1] && lastErrors[1]!=0){
					errorDecreasing = false;
				}
			}
			if(loop == 0 && lastRun){
				mlp.graph();
			}
			double []averages = mlp.test(secondHalfInputs, secondExpected);
			double error = averages[0];
			double expect = averages[1];
			
			
			//Train second half test first half
			mlp.reset();

			//Trains on data in 10-30% position
			for(vector = validationCutoff; vector<firstHalfInputs.length*0.30;vector++){
				mlp.train(secondHalfInputs[vector],secondExpected[vector]);
				if(vector%gradientBatchSize==0){
					mlp.gradientDescent();
				}
			}
			
			//Starts to test against validation
			batchSize = (int) (firstHalfInputs.length*0.05); //Size to train before testing validation set
			errorDecreasing = true;
			lastErrors = new double[4];
			vector = 0;
			while(vector<secondHalfInputs.length&&errorDecreasing){
				int lastVectorStop = vector;
				for(vector = lastVectorStop; vector<(lastVectorStop+batchSize) && vector < secondHalfInputs.length;vector++){
					mlp.train(secondHalfInputs[vector],secondExpected[vector]);
					if(vector%gradientBatchSize==0){
						mlp.gradientDescent();
					}
				}
				lastVectorStop+=batchSize;
				lastErrors[3] = lastErrors[2];
				lastErrors[2] = lastErrors[1];
				lastErrors[1] = lastErrors[0];
				lastErrors[0] = mlp.test(secondValidationData,secondValidationExpected)[0];
				//If error has increased over the last two iterations.
				if(lastErrors[0]>=lastErrors[1] && lastErrors[1]!=0){
					errorDecreasing = false;
				}
			}
			
			averages = mlp.test(firstHalfInputs,firstExpected);
			error += averages[0];
			expect += averages[1];
			sumErrors+=error/2;
			sumExpect+=expect/2;
		}
		double totalError = sumErrors/5;
		double totalExpect = sumExpect/5;
		return totalError;
	}
	
	/**
	 *Gets a value from the rosenbrock function based on an array of inputs
	 *@param inputs the inputs
	 *@return		the value of the rosenbrock function at the inputs
	 */
	private static double rosenbrockFunction(double[] inputs){
		double sum = 0;
		for(int i = 0; i < dimensions-1;i++){
			sum += Math.pow((1-inputs[i]),2)+100*Math.pow((inputs[i+1]-Math.pow(inputs[i], 2)),2);
		}
		return sum;
	}
	
	/**
	 *Generates a random input between this.maxDomain and this.minDomain/100
	 *@return	the randomly generated inputs
	 */
	private static double[][] generateInputs(){
		double[][] inputs = new double[inputVectors][dimensions];
		Random numberGenerator = new Random();
		for(int i = 0; i < inputVectors; i++){
			for(int i2 = 0; i2 < dimensions; i2++){
				inputs[i][i2] = ((double)numberGenerator.nextInt(((maxDomain*100)-(minDomain*100))+1)+(minDomain*100))/100.00;
			}
		}
		return inputs;
	}
}
