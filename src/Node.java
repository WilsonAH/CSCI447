
public class Node {
	
	private double weights[];
	
	public Node(){
		
	}
	
	//Needs Work
	public double sumInputsTimesWeights(double inputs[]){
		double sum = 0;
		for(int position = 0; position<inputs.length; position++){
			sum += inputs[position]*weights[position];
		}
		return sum;
	}
}
