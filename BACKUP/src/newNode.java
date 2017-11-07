import java.util.Random;

public class newNode {
	public double value;
	public double[] weights;
	public double delta;
	
	private final int MAX_RAND_WEIGHT = 50;
	private final int MIN_RAND_WEIGHT = -50;
	
	public newNode(int inputCount){
		initWeights(inputCount);
	}
	
	private void initWeights(int inputCount){
		weights = new double[inputCount];
		//originalWeights = new double[outputCount];
		Random numberGenerator = new Random();
		for(int loop = 0; loop < weights.length;loop++){
			weights[loop] = ((double)numberGenerator.nextInt((MAX_RAND_WEIGHT-MIN_RAND_WEIGHT)+1)+MIN_RAND_WEIGHT)/1000.0;
			//originalWeights[loop] = weights[loop];
		}
	}
}
