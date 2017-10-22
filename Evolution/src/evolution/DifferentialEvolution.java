package evolution;

import java.util.ArrayList;
import java.util.Random;
/**
 * @author laurabsullivan-russett
 * @version November 13, 2017
 *
 */
public class DifferentialEvolution {
	private int genCounter;
	private Population pop;
	
	private final double F = 1.0;
	private final double CR = 0;
	
	
	/**
	 * Constructor to initialize DE algorithm and create its population
	 */
	public DifferentialEvolution() {
		this.genCounter = 0;
		this.pop = new Population();
	}
	
	/**
	 * fitness method to calculate the fitness of an individual solution
	 * 
	 * @param individual
	 * @return individual's fitness value
	 */
	public double fitness(Individual ind) {
		double fitness = 0;
		
		return fitness;
	}
	
	/**
	 * selectAgents method to select and return three other unique individuals 
	 * to calculate mutation
	 * 
	 * @param target individual
	 * @return three unique individuals 
	 */
	public Individual[] selectAgents(Individual target) {
		// get current population and create array for agents
		Individual[] inds = pop.getPop();
		Individual[] agents  = new Individual[3];
		// select individual randomly to compare to target
		Random r = new Random();
		int select = r.nextInt(inds.length);
		// while individual is not unique, randomly select another
		while(inds[select] == target) {
			select = r.nextInt(inds.length);
		}
		// once unique agent selected, add to agents array
		agents[0] = inds[select];
		// while individual is not unique, randomly select another
		while(inds[select] == target || inds[select] == agents[0]) {
			select = r.nextInt(inds.length);
		}
		// once unique agent selected, add to agents array
		agents[1] = inds[select];
		// while individual is not unique, randomly select another
		while(inds[select] == target || inds[select] == agents[0] || inds[select] == agents[0]) {
			select = r.nextInt(inds.length);
		}
		// once unique agent selected, add to agents array
		agents[2] = inds[select];
		return agents;
	}
		
	/**
	 * mutate method to use each individual in the population as a target
	 * individual, select agents to mutate with and compare target individual
	 * to mutated individual for replacement in population 
	 * 
	 * @return array of individuals
	 */
	public Individual[] mutate() {
		// get current population and create array for mutated population
		Individual[] inds = pop.getPop();
		Individual[] replacements = new Individual[inds.length];
		Individual mutated, replacement;
		// for each individual in the population, get agents and calculate mutated
		// individual
		for(int i = 0; i < inds.length; i++) {
			Individual target = inds[i];
			mutated = new Individual();
			Individual[] agents = selectAgents(target);
			// for each weight in the target's array list, calculate mutated value
			// using weights at same index for each agent 
			for(int j = 0; j < target.getWeights().size(); j++) {
				ArrayList<Double> a = agents[0].getWeights();
				ArrayList<Double> b = agents[1].getWeights();
				ArrayList<Double> c = agents[2].getWeights();
				ArrayList<Double> mutWeights = new ArrayList<Double>();
				mutWeights.add(a.get(j) + F * (b.get(j) - c.get(j)));
				// update mutated individual with calculated weights
				mutated.updateWeights(mutWeights);
			}
			// compare target to mutated individual, set replacement to 
			// individual with highest fitness
			if(fitness(target) < fitness(mutated)) {
				replacement = mutated;
			}
			else
				replacement = target;
			
			// add replacement individual to replacements array
			replacements[i] = replacement;
		}
	return replacements;	
	}
	
	/**
	 * crossover method to cross target and mutated individuals at random indices
	 * 
	 * @param target individual
	 * @param mutated individual
	 * 
	 * @return individual resulting from crossover
	 */
	public Individual crossover(Individual ind, Individual mut) {
		// get weight array lists of individuals
		ArrayList<Double> indW = ind.getWeights();
		ArrayList<Double> mutW = mut.getWeights();
		Random r = new Random();
		for(int i = 0; i < indW.size(); i++) {
			// random value to compare to crossover constant
			double crComp = r.nextDouble();
			// random index to compare to current index
			int index = r.nextInt(indW.size()); 
			// use weight from mutated individual in this index
			if(crComp <= CR || index == i) {
				mutW.set(i, mutW.get(i));
			}
			// or use weight from target individual in this index
			else
				mutW.set(i, indW.get(i));
		}
		return mut;
	}
	
	/**
	 * mostFit method to determine which individual in a population 
	 * has the highest fitness value 
	 * 
	 * @param inds array
	 * @return individual with highest fitness
	 */
	public Individual mostFit(Individual[] inds) {
		// set most fit individual to first in population
		Individual most = inds[0];
		double best = fitness(inds[0]);
		// compare every subsequent individual's fitness to the first
		for(int i = 1; i < inds.length; i++) {
			double temp = fitness(inds[i]);
			// set most fit individual as highest fitness
			if(temp > best) 
				most = inds[i];
		}
		return most;
	}
	/**
	 * terminate method to determine termination condition for 
	 * returning results
	 * 
	 * @return boolean
	 */
	public boolean terminate() {
		if(genCounter == 10) {
			return true;
		}
		else
			return false;
	}
	
	/**
	 * newGeneration method to update the population with the individuals
	 * after mutation and crossover.
	 */
	public void newGeneration() {
		// create new array to hold next generation and mutate current population
		Individual[] generation = mutate(); 
		// update population with new generation
		pop.updatePop(generation);
		// increment genCounter for termination condition
		genCounter++;
		
	}
	/**
	 * printPop method to print each individual in the population
	 * 
	 * @param current population
	 */
	public void printPop() {
		Individual[] inds = pop.getPop();
		
		for(int i = 0; i < inds.length; i++) {
			System.out.println(inds[i].toString(inds[i]));
		}
	}
}

