package evolution;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author laurabsullivan-russett
 * @version November 13, 2017
 *
 */
public class GeneticAlgorithm {
	private int genCounter;
	
	private final int PROB = 100;
	private final double MUTSTART = -0.1;
	private final double MUTEND = 0.1;
	
	
	public GeneticAlgorithm() {
		this.genCounter = 0;
	}
	public void intialize() {
		Population pop = new Population();
		printPop(pop);
		
	}
	public void fitness() {
		
	}
	
	public void selectParents() {
		
	}
	
	/**
	 * mutate method to mutate individuals in the population
	 * 
	 * @param current population
	 */
	public void mutate(Population pop) {
		// rand variable for probability of mutation
		Random rand = new Random();
		// randMut variable to create real values for mutation of weights
		double randMut = new Random().nextDouble();
		Individual[] inds = pop.getPop();
		for(int i = 0; i < inds.length; i++) {
			// get current weights for current individual
			ArrayList<Double> weights = inds[i].getWeights(inds[i]);
			for(int j = 0; j < weights.size(); j++) {
				// mutate each weight with 0.01 probability
				boolean mut = rand.nextInt(PROB) == 0;
				// mutate current weight if mut is true
				if(mut) {
					// create range for real mutation values
					double mutator = (MUTSTART) + (randMut * MUTEND - MUTSTART);
					// calculate new weight based on real mutation value
					double mutWeight = weights.get(j) + mutator;
					weights.set(j, mutWeight);
				}
			}
			// update weights for individual
			pop.update(inds[i], weights);
		}
	}
	
	public void crossOver(Individual[] parents) {
		Random rand = new Random();
		// randCo variable for probability of crossover between current parents
		int randCO = rand.nextInt(PROB);
		
		if(randCO <= 90) {
			
		}
		
	}
	
	
	public void offspringFitness() {
		
	}
	
	public void replacePopulation() {
		
	}
	
	public boolean terminate() {
		if(genCounter == 10) {
			return true;
		}
		else
			return false;
	}
	public void result() {
		
	}
	
	/**
	 * printPop method to print each individual in the population
	 * 
	 * @param current population
	 */
	public void printPop(Population pop) {
		Individual[] inds = pop.getPop();
		
		for(int i = 0; i < inds.length; i++) {
			System.out.println(inds[i].toString(inds[i]));
		}
	}
}
