import java.util.Random;

public abstract class Node {
	
	private double weights[];
	
	private static final int MIN_RAND_WEIGHT = 1;  //Will divide by 100 so it is 0.01
	private static final int MAX_RAND_WEIGHT = 50; // Will divide by 100 so it is 0.5
	private static final double LEARNING_RATE = 0.25;
	private static final double BIAS = 0.01;
	
	public Node(int inputCount){
		initWeights(inputCount);
	}
	
	protected void initWeights(int inputCount){
		weights = new double[inputCount+1]; //InputCount + 1 to include bias weight
		Random numberGenerator = new Random();
		for(int loop = 0; loop < weights.length;loop++){
			weights[loop] = ((double)numberGenerator.nextInt((MAX_RAND_WEIGHT-MIN_RAND_WEIGHT)+1)+MIN_RAND_WEIGHT)/100.0;
			System.out.println(weights[loop]);
		}
	}
	
	protected double sumInputsTimesWeights(double inputs[]){
		double sum = 0;
		for(int position = 0; position<inputs.length; position++){
			sum += inputs[position]*weights[position];
		}
		return sum+BIAS;
	}
	
	public abstract double getOutput(double inputs[]);
	
	protected abstract double calculateError(double inputs[], double expected);
	
	
	public void updateWeights(double inputs[], double error){
		for(int loop = 0; loop < weights.length; loop++){
			weights[loop] = weights[loop]*inputs[loop]*error*LEARNING_RATE;
		}
	}
	
	public String toString(){
		String s = "Node\nWeights: ";
		for(int loop = 0; loop < weights.length; loop++){
			s += weights[loop]+" ";
		}
		return s;
	}
}
