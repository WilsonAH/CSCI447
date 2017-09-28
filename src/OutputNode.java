
public class OutputNode extends Node{
	private double weights[];
	private final double LEARNING_RATE = 0.01; //Should be small
	
	public OutputNode(int inputCount) {
		super(inputCount);
		initWeights(inputCount);
	}

	//Needs Work
	public double getOutput(double[] inputs) {
		return 0;
	}
	
	//Needs Work
	protected double calculateError(double inputs[], double expected){
		return 0;
	}

}
