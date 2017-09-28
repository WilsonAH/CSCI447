
public class HiddenNode extends Node{
	private double weights[];
	private boolean useLogisticFunciton;
	
	public HiddenNode(int inputCount, boolean useLogisticFunciton) {
		super(inputCount);
		this.useLogisticFunciton = useLogisticFunciton;
		initWeights(inputCount);
	}
	
	public double getOutput(double inputs[]){
		double sum = sumInputsTimesWeights(inputs);
		double activation = 0;
		if(useLogisticFunciton){
			activation = logisticActivationFunction(sum);
		}else{
			activation = hyperbolicActivationFunction(sum);
		}
		return activation;
	}
	
	//Needs work
	//Could be wrong
	protected double calculateError(double inputs[], double expected){
		double output = this.getOutput(inputs);
		
		if(useLogisticFunciton){
			//Logistic Error = -(expected - output) * output * (1 - output)
			return -(expected - output) *output * (1 - output);
		}else{
			return 0;
		}
	}

	private double logisticActivationFunction(double input){
		return (1.0/(1+Math.exp(input)));
	}
	
	//Needs Work
	private double hyperbolicActivationFunction(double input){
		return 0;
	}
}
