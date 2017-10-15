import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.util.Random;

import javax.swing.JFrame;

public class MultilayerNetwork {
	
	private Layer layers[];
	private int dimensions;
	private int maxDomain;
	private int minDomain;
	private int inputVectors;
	
	public double[] points;
	
	
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

		double errorSum = 0;
		boolean lastRun = false;
		for(int loop = 0; loop<10; loop++){
			if(loop==9){
				lastRun=true;
			}
			errorSum+=fiveByTwoCrossValidation(lastRun);
		}
		double finalErrorPercent = (errorSum/10);
		System.out.printf("Error: %.2f", finalErrorPercent);
		System.out.println("%");
		//singleTest();
	}
	
	private void singleTest(){
		double[][] inputs = generateInputs();
		points = new double[1000];
		for(int i = 0; i < 1000; i++){
			//System.out.println("Iteration: "+i);
			double[] outputs = train(inputs);
			double totalErrorSum = 0;
			for(int output = 0;output < inputs.length;output++){
				totalErrorSum+=outputs[output];
			}
			totalErrorSum /= inputs.length;
			points[i] = totalErrorSum;
			//System.out.println(points[i]);
			//try {
			//	Thread.sleep(3000);
			//} catch (InterruptedException e) {
			//	e.printStackTrace();
			//}
		}
		
		JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new graphPlotter(points));
        f.setSize(400,400);
        f.setLocation(200,200);
        f.setVisible(true);
	}
	
	private double fiveByTwoCrossValidation(boolean lastRun){
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
			points = train(firstHalfInputs);
			if(loop == 4 && lastRun){
				JFrame f = new JFrame();
		        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		        f.add(new graphPlotter(points));
		        f.setSize(400,400);
		        f.setLocation(200,200);
		        f.setVisible(true);
			}
			double error = test(secondHalfInputs);
			train(secondHalfInputs);
			error += test(firstHalfInputs);
			error /= 2;
			sumErrors+=error;
		}
		double totalError = sumErrors/5;
		return totalError;
	}
	
	
	private double[] train(double[][] inputs){
		//double totalErrorSum = 0;
		double[] graphPoints = new double[inputs.length];
		for(int i = 0; i<inputs.length; i++){
			double[] input = inputs[i];
			double[] outputs = this.getOutputs(input);
			double expected = rosenbrockFunction(input);
			setErrors(expected, outputs);
			this.updateWeights();
			double errorSum = 0;
			for(int output = 0; output<outputs.length;output++){
				errorSum+= Math.abs(expected-outputs[output]);
			}
			graphPoints[i] = errorSum/outputs.length;
			System.out.println("Output: "+outputs[0]);
			System.out.println("Expected: "+expected+"");
			System.out.println("Error: "+graphPoints[i]+"\n");
		}
		//totalErrorSum /= inputs.length;
		return graphPoints;
	} 
	
	private double test(double[][] inputs){
		double totalErrorSum = 0;
		for(int i = 0; i<inputs.length; i++){
			double[] input = inputs[i];
			double[] outputs = this.getOutputs(input);
			double expected = rosenbrockFunction(input);
			double percentErrorSum = 0;
			for(int output = 0;output<outputs.length;output++){
				percentErrorSum+=Math.abs(expected-outputs[output]);//percentErrorSum+=(Math.abs((expected-outputs[output])/expected))*100;
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
		double sum = 0;
		for(int i = 0; i < dimensions-1;i++){
			sum += Math.pow((1-inputs[i]),2)+100*Math.pow((inputs[i+1]-Math.pow(inputs[i], 2)),2);
		}
		return sum;
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
			this.layers[layer].updateWeights(layers[layer+1].errors);
		}
	}
}
