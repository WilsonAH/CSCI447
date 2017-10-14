
public class HiddenNode extends Node{
	public double pastWeightUpdates[];
	public boolean useLogisticFunciton;
	private double momentum;
	
	public HiddenNode(int outputCount, boolean useLogisticFunciton, double momentum, double learningRate, int id) {
		super(outputCount,learningRate,id);
		this.useLogisticFunciton = useLogisticFunciton;
		pastWeightUpdates=new double[outputCount];
		this.momentum = momentum;
	}
	
	public void updateWeights(double[] errors, double[] gradients, double input){
		for(int weight = 0; weight<this.weights.length;weight++){
			//System.out.println("Node "+this.id+" weight "+weight+": "+weights[weight]+"\tchange: "+errors[weight]*LEARNING_RATE*this.logisticActivationFunction(input));
			double weightChange = (errors[weight]*LEARNING_RATE*this.logisticActivationFunction(input)*gradients[weight]) + pastWeightUpdates[weight]*momentum;
			System.out.println(weightChange+" "+errors[weight]);
			weights[weight] += weightChange;
			pastWeightUpdates[weight] = weightChange;
		}
	}
	
	//Needs work on nonLogistic
	public double calculateGradient(double sum){
		//Sigmoid Gradient = (e^-sumInputsTimesWeights)/(1+e^-sumInputsTimesWeights)^2
		return Math.exp(-sum)/Math.pow(1+Math.exp(-sum),2);
	}

	public double logisticActivationFunction(double input){
		//Sigmoid Funciton: 1/(1+e^(-SumOfInputs*Weights))
		double activation = (1.0/(1.0+Math.exp(-input)));
		return activation;
	}
	
	//Needs Work
	public double linearActivationFunction(double input){
		return 0;
	}
}
