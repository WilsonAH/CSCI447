
public class Synaps {
	private Node til;
	private double vægt;
	public Synaps(Node til, double vægt){
		this.til = til;
		this.vægt = vægt;
	}
	
	public double getVægt(){
		return this.vægt;
	}
	
	public void setVægt(double v){
		this.vægt = v;
	}
	
	public Node getTil(){
		return this.til;
	}
	
	public String toString(){
		return vægt+"";
	}
}
