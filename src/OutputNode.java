
public class OutputNode extends Node{
	public double[] lastInputs;
	
	public OutputNode(int outputCount,double learningRate,int id) {
		super(outputCount,learningRate,id);
	}

	public double getOutputs(double input) {
		//double sum = sumInputs(inputs);
		return input;
	}
	
	public double calculateGradient(double expected, double output, double input){
		return (expected-output)*input;
	}
	
	//Needs Work
	public double getError(double expected, double output){
		double error = expected-output;
		double errorSquared;
		//if(error>0){
			errorSquared = (error*error);
		//}else{
		//	errorSquared = -(error*error);
		//}
		//errorSquared = error;
		//System.out.println("Error: "+errorSquared);
		return errorSquared;
	}
}
