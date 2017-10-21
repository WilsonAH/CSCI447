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
	
	public void updatePop(Individual[] newInds) {
		individuals = newInds;
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
