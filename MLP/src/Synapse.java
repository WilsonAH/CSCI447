//Author: Wilson Harris

public class Synapse {
	private double weight;
	private double originalWeight;
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
}