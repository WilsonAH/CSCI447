import java.util.Random;

public class MultilayerNetwork {
	
	private Layer layers[];
	private int dimensions;
	private int maxDomain;
	private int minDomain;
	private int inputVectors;
	
	
	public MultilayerNetwork(int layerCount, int[] nodeCounts, double momentum, int dimensions, int maxDomain, int minDomain, double bias, double learningRate, int inputVectors, boolean useLogisticFunction){
		layers = new Layer[layerCount];
		this.dimensions = dimensions;
		this.maxDomain = maxDomain;
		this.minDomain = minDomain;
		this.inputVectors = inputVectors;
		for(int loop = layers.length-1; loop >= 0; loop--){
			if(loop == layers.length-1){
				layers[loop] = new Layer(nodeCounts[loop], nodeCounts[loop], false, useLogisticFunction, momentum, bias, learningRate, loop);
			}else{
				layers[loop] = new Layer(nodeCounts[loop], layers[loop+1].nodeCount, true, useLogisticFunction, momentum, bias, learningRate, loop);
			}
		}

		/*double errorSum = 0;
		for(int loop = 0; loop<10; loop++){
			errorSum+=fiveByTwoCrossValidation();
		}
		double finalErrorPercent = (errorSum/10);
		System.out.printf("Error: %.2f", finalErrorPercent);
		System.out.println("%");*/
		singleTest();
	}
	
	private void singleTest(){
		double[][] inputs = generateInputs();
		for(int i = 0; i < 10000; i++){
			System.out.println("Iteration: "+i);
			train(inputs);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private double fiveByTwoCrossValidation(){
		double[][] inputs = generateInputs();
		double sumErrors = 0;
		for(int loop = 0; loop < 5; loop++){
			shuffleArray(inputs);
			double[][] firstHalfInputs = new double[inputVectors/2][dimensions];
			double[][] secondHalfInputs = new double[inputVectors-(inputVectors/2)][dimensions];
			for(int input = 0; input<inputVectors;input++){
				if(input<inputVectors/2){
					firstHalfInputs[input] = inputs[input];
				}else{
					secondHalfInputs[input-(inputVectors/2)] = inputs[input];
				}
			}
			train(firstHalfInputs);
			double error = test(secondHalfInputs);
			train(secondHalfInputs);
			error += test(firstHalfInputs);
			error = error/2;
			sumErrors+=error;
		}
		double totalError = sumErrors/5;
		return totalError;
	}
	
	
	private void train(double[][] inputs){
		for(int i = 0; i<inputs.length; i++){
			double[] input = inputs[i];
			double[] outputs = this.getOutputs(input);
			double expected = rosenbrockFunction(input);
			setErrors(expected, outputs);
			this.updateWeights();
			
			System.out.println("Output: "+outputs[0]);
			System.out.println("Expected: "+expected+"\n");
		}
	} 
	
	private double test(double[][] inputs){
		double totalErrorSum = 0;
		for(int i = 0; i<inputs.length; i++){
			double[] input = inputs[i];
			double[] outputs = this.getOutputs(input);
			double expected = rosenbrockFunction(input);
			double percentErrorSum = 0;
			for(int output = 0;output<outputs.length;output++){
				percentErrorSum+=(Math.abs((expected-outputs[output])/expected))*100;
			}
			double averageError = percentErrorSum/outputs.length;
			totalErrorSum +=averageError;
		}
		double totalAverageError = totalErrorSum/inputs.length;
		return totalAverageError;
	}
	
	
	//Function shuffleArray and swap taken from Vogella.com to randomly arrange an array
	public static void shuffleArray(double[][] a) {
        int n = a.length;
        Random random = new Random();
        random.nextInt();
        for (int i = 0; i < n; i++) {
            int change = i + random.nextInt(n - i);
            swap(a, i, change);
        }
	}
    private static void swap(double[][] a, int i, int change) {
        double[] helper = a[i];
        a[i] = a[change];
        a[change] = helper;
    }
	
	private double rosenbrockFunction(double[] inputs){
		double expected = Math.pow((1-inputs[0]),2)+100*Math.pow((inputs[1]-Math.pow(inputs[0], 2)),2);
		return expected;
	}
	
	private double[][] generateInputs(){
		double[][] inputs = new double[inputVectors][dimensions];
		Random numberGenerator = new Random();
		for(int i = 0; i < inputVectors; i++){
			for(int i2 = 0; i2 < dimensions; i2++){
				inputs[i][i2] = ((double)numberGenerator.nextInt(((maxDomain*100)-(minDomain*100))+1)+(minDomain*100))/100.00;
			}
		}
		return inputs;
	}
	
	private double[] getOutputs(double[] inputs){
		double[] outputs = this.layers[0].getOutputs(inputs);
		for(int layer = 1; layer<this.layers.length;layer++){
			outputs = this.layers[layer].getOutputs(outputs);
		}
		return outputs;
	}
	
	private void setErrors(double expected, double[] outputs){
		double[] errors = this.layers[this.layers.length-1].updateErrors(expected, outputs);
		for(int layer = layers.length-2;layer>=0;layer--){
			errors = this.layers[layer].updateErrors(errors);
		}
	}
	
	private void updateWeights(){
		for(int layer = 0;layer<layers.length-1;layer++){
			this.layers[layer].updateWeights(layers[layer+1].errors,layers[layer+1].gradients);
		}
	}
}
