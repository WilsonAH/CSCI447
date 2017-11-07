
public class OutputNode extends Node{
	public double[] lastInputs;
	public boolean useLogisticFunciton;
	
	public OutputNode(int outputCount, boolean useLogisticFunciton, double learningRate,int id) {
		super(outputCount,learningRate,id);
		this.useLogisticFunciton = useLogisticFunciton;
	}

	public double getOutputs(double input) {
		return input;
	}
	
	public double logisticActivationOutput(double input){
		//Sigmoid Funciton: 1/(1+e^(-SumOfInputs*Weights))
		double activation = (1.0/(1.0+Math.exp(-input)));
		return activation;
	}
	
	public double calculateGradient(double expected, double output, double input){
		return (expected-output)*input;
	}
	
	public double getError(double expected, double output){
		double error = (expected-output);
		return error;
	}
}
