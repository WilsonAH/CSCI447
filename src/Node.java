import java.util.Random;

public abstract class Node {
	
	public double weights[];
	public double originalWeights[];
	public int id;
	
	private static final int MIN_RAND_WEIGHT = -50;  //Will divide by 1000 so it is -0.05
	private static final int MAX_RAND_WEIGHT = 50; // Will divide by 1000 so it is 0.05
	protected double LEARNING_RATE;
	
	public Node(int inputCount, double learningRate, int id){
		initWeights(inputCount);
		this.LEARNING_RATE=learningRate;
		this.id=id;
	}
	
	private void initWeights(int outputCount){
		weights = new double[outputCount];
		originalWeights = new double[outputCount];
		Random numberGenerator = new Random();
		for(int loop = 0; loop < weights.length;loop++){
			weights[loop] = ((double)numberGenerator.nextInt((MAX_RAND_WEIGHT-MIN_RAND_WEIGHT)+1)+MIN_RAND_WEIGHT)/1000.0;
			originalWeights[loop] = weights[loop];
		}
	}
	
	protected double sumInputs(double inputs[]){
		double sum = 0;
		for(int position = 0; position<inputs.length; position++){
			sum += inputs[position];
		}
		return sum;
	}
	
	public String toString(){
		String s = "Node\nWeights: ";
		for(int loop = 0; loop < weights.length; loop++){
			s += weights[loop]+" ";
		}
		return s;
	}
}
