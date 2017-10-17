//Author: Wilson Harris

public class Synapse {
	private double weight;
	private double originalWeight;
	private double partialDerivative = 0;
	private double lastChange = 0;
	private int partials = 0;
	private Node toNode;//Node the synapse goes to
	
	/**
	 * Constructor for Synapse
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