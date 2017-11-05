package evolution;
//Author: Wilson Harris

public class Synapse {
	private double weight;
	private double originalWeight;//Original randomly generated weight
	private double partialDerivative = 0;//Partial derivatives based on the last set of inputs
	private double lastChange = 0;//The last update of weight. Used for momentum
	private int partials = 0;//Number of partial derivatives added without averaging them
	private Node toNode;//Node the synapse goes to
	
	/**
	 * Constructor for Synapse
	 * @param weight	weight of synapse
	 * @param toNode	node the synapse is going to
	 */
	public Synapse(double weight, Node toNode){
		this.weight = weight;
		this.originalWeight = weight;
		this.toNode = toNode;
	}
	
	public Node getToNode(){
		return this.toNode;
	}
	
	public void resetWeight(){
		this.weight = originalWeight;
	}
	
	public double getWeight(){
		return this.weight;
	}
	
	public void setWeight(double weight){
		this.weight = weight;
	}
	
	/**
	 * Averages the partial derivatives of the synapse
	 */
	public double getAveragePartial(){
		double temp = this.partialDerivative;
		temp/=partials;
		this.partialDerivative = 0;
		this.partials = 0;
		return temp;
	}
	
	public void addPartial(double partial){
		this.partialDerivative += partial;
		this.partials++;
	}
	
	public double getLastChange(){
		return this.lastChange;
	}
	
	public void setLastChange(double change){
		this.lastChange = change;
	}
}