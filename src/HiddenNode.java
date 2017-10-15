
public class HiddenNode extends Node{
	public double pastWeightUpdates[];
	private double momentum;
	
	public HiddenNode(int outputCount, double momentum, double learningRate, int id) {
		super(outputCount,learningRate,id);
		pastWeightUpdates=new double[outputCount];
		this.momentum = momentum;
	}
	
	public void updateWeights(double[] errorsDownstream, double input){
		for(int weight = 0; weight<this.weights.length;weight++){
			//System.out.println("Node "+this.id+" weight "+weight+": "+weights[weight]+"\tchange: "+LEARNING_RATE*errorsDownstream[weight]*this.logisticActivationFunction(input));
			//System.out.println("Error Downstream: "+errorsDownstream[weight]+"\tOutput: "+this.logisticActivationFunction(input));
			double weightChange = (LEARNING_RATE*errorsDownstream[weight]*this.logisticActivationFunction(input)) + pastWeightUpdates[weight]*momentum;
			weights[weight] += weightChange;
			pastWeightUpdates[weight] = weightChange;
		}
	}

	public double logisticActivationFunction(double input){
		//Sigmoid Funciton: 1/(1+e^(-SumOfInputs*Weights))
		double activation = (1.0/(1.0+Math.exp(-input)));
		return activation;
	}
}
