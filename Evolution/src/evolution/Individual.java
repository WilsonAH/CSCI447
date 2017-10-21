package evolution;

import java.util.ArrayList;
import java.util.Random;
/**
 * @author laurabsullivan-russett
 * @version November 13, 2017
 *
 */
public class Individual {
	private ArrayList<Double> weights;
	
	/**
	 * Constructor to create a new Individual and intialize its weights randomly
	 */
	public Individual() {
		this.weights = initialize();
	}
	
	/**
	 * initialize method to randomly set each weight for an individual 
	 * 
	 * @return random initial weights for an individual
	 */
	private ArrayList<Double> initialize() {
		ArrayList<Double> initWeights = new ArrayList<Double>();
		Random random = new Random();
		for(int i = 0; i < 10; i++) {
			// initialize weights randomly to a double between 0 and 1
			double weight = random.nextDouble();
			initWeights.add(weight);
		}
		return initWeights;
	}
	
	/**
	 * updateWeights method to update each weight in an individual's chromosome
	 * 
	 * @param Individual
	 * @param newWeights
	 */
	public void updateWeights(Individual ind, ArrayList<Double> newWeights) {
		for(int i = 0; i < newWeights.size(); i++) {
			// replace old weights with new weights
			ind.weights.add(newWeights.set(i, newWeights.get(i)));
		}
	}
	
	/**
	 * toString method to return String representation of the individual's weights
	 * 
	 * @param Individual
	 * @return String representation of the individual's weights
	 */
	public String toString(Individual ind) {
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		for(int i = 0; i < weights.size()-1; i++) {
			sb.append(weights.get(i) + ", ");
		}
		sb.append(weights.get(weights.size()-1) + ">");
		return sb.toString();
	}
	
	/**
	 * getWeights method to return an individual's weights
	 * 
	 * @param Individual
	 * @return ArrayList of individual's weights
	 */
	public ArrayList<Double> getWeights(Individual ind) {
		return ind.weights;
	}
}
