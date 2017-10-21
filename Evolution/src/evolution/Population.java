package evolution;

import java.util.ArrayList;
/**
 * @author laurabsullivan-russett
 * @version November 13, 2017
 *
 */
public class Population {
	
	private final int POP = 100;
	private Individual[] individuals;
	
	public Population() {
		this.individuals = createPop();
	}
	
	/**
	 * createPop method to create a new population of randomized individuals
	 * 
	 * @return new population of individuals
	 */
	public Individual[] createPop() {
		Individual[] init = new Individual[POP];
		// add a new Individual object for each place in the population
		for(int i = 0; i < init.length; i++) {
			init[i] = new Individual();
		}
		return init;
	}
	
	/**
	 * update method to update the weights of an individual in the population
	 * @param Individual
	 * @param newWeights
	 */
	public void update(Individual ind, ArrayList<Double> newWeights) {
			ind.updateWeights(ind, newWeights);
	}
	
	/**
	 * getPop method to return the individuals in the current population
	 * 
	 * @return array of individuals in the population
	 */
	public Individual[] getPop() {
		return individuals;
	}
}
