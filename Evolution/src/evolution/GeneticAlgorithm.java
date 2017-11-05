package evolution;

import java.util.Random;

/**
 * @author laura sullivan-russett
 * @version November 13, 2017
 *
 */
public class GeneticAlgorithm extends EvolutionAlgorithm {
	private MultilayerPerceptron[] population;
	private int size;
	private double mutationRate;
	private double crossoverRate;
	private final double MUTSTART = -0.1;
	private final double MUTEND = 0.1;
	private final double ERROR = 0.10;
	private double NUMGENS = 0;

	public GeneticAlgorithm(int size, double mutationRate, double crossoverRate) {
		this.population = new MultilayerPerceptron[size];
		this.crossoverRate = crossoverRate;
		this.mutationRate = mutationRate;
	}
	/**
	 * initPopulation method to initialize the starting population
	 * 
	 * @param MLPs
	 */
	public void initPopulation(MultilayerPerceptron[] MLPs) {
		for (int individual = 0; individual < MLPs.length; individual++) {
			population[individual] = MLPs[individual];
		}

	}
	/**
	 * evolveOneGeneration method to check the termination condition, then replace the
	 * population with an evolved population if the generational error is above the 
	 * threshold 
	 * 
	 * @param inputs
	 * @param expected
	 */
	public void evolveOneGeneration(double[][] inputs, double[][] expected) {
		
		System.out.println("Evolving population.");
		// Save the current population in a temp variable for post evolution comparison
		MultilayerPerceptron[] temp = population;
		MultilayerPerceptron[] newPop = new MultilayerPerceptron[population.length];
		MultilayerPerceptron[] child = new MultilayerPerceptron[2]; 
		// find fitness of old population and new population
		double[] tempAvg = fitness(inputs, expected);
		System.out.println("Error = " + averageGenerationFitness(tempAvg));
		// call mutate function for population
		mutate();
		// create 2 children each crossover and add to new population
		for(int i = 0; i < newPop.length; i+=2) {
			child = crossOver(child);
			newPop[i] = child[0];
			newPop[i+1] = child[1];
		}
		population = newPop;
		double[] newAvg = fitness(inputs, expected);
		// if the new population is less fit, keep the old population otherwise replace
		if(averageGenerationFitness(tempAvg) <= averageGenerationFitness(newAvg)) {
			population = temp;
		}
		else
			population = newPop;
		
		if(averageGenerationFitness(tempAvg) == averageGenerationFitness(newAvg)) {
			NUMGENS++;
		}
		double popAvg = averageGenerationFitness(fitness(inputs, expected));
		if(NUMGENS == 10 || terminate(popAvg)) {
			result(fitness(inputs, expected));
		}
	}
	/**
	 * fitness method to determine the fitness of each individual MLP by determining
	 * the error in classification
	 * 
	 * @param inputs
	 * @param expected
	 * @return array of fitness values of individuals
	 */
	public double[] fitness(double[][] inputs, double[][] expected) {
		double[] averageErrors = new double[this.population.length];
		// for each individual in the population, test its classification and record the error
		for(int individual = 0; individual < this.population.length; individual++){
			double[] errors = population[individual].test(inputs, expected);
			double sumError = 0;
			for(double error: errors){
				sumError += error;
			}
			// average the error for each individual
			averageErrors[individual]=sumError/errors.length;
		}
		return averageErrors;
	}

	/**
	 * mutate method to mutate individuals in the population
	 * 
	 * @param current population
	 */
	public void mutate() {
		// rand variable for probability of mutation
		Random rand = new Random();
		// randMut variable to create real values for mutation of weights
		double randMut = new Random().nextDouble();
		for (int i = 0; i < population.length; i++) {
			// get current weights for current individual
			double[][][] weights = population[i].getWeights();
			// for each of the weights in the array
			for (int l = 0; l < weights.length; l++) {
				for (int n = 0; n < weights[l].length; n++) {
					for (int s = 0; s < weights[l][n].length; s++) {
						// mutate each weight with mutationRate probability
						double mut = rand.nextDouble();
						// mutate current weight if mut is true
						if (mut < mutationRate) {
							// create range for real mutation values
							double mutator = (MUTSTART) + (randMut * MUTEND - MUTSTART);
							// calculate new weight based on real mutation value
							double mutWeight = weights[l][n][s] + mutator;
							weights[l][n][s] = mutWeight;
						}
					}
				}
			}
			// update weights for individual
			population[i].setWeight(weights);
		}
	}
	/**
	 * getPopulation method to return population
	 * 
	 * @return population of MLPs
	 */
	public  MultilayerPerceptron[] getPopulation() {
		MultilayerPerceptron[] MLPs = new MultilayerPerceptron[size];
		for(int i = 0; i < size; i++){
			MLPs[i] = this.population[i];
		}
		return MLPs;
	}
	public  void train(double inputs[][], double expected[][]) {
		for(int index = 0; index < 10; index++){
			evolveOneGeneration(inputs, expected);
		}
	}
	public  double test(double inputs[][], double expected[][]) {
		double[] errors = population[0].test(inputs, expected);
		double sumError=0;
		for(double error: errors){
			sumError+=error;
		}
		return sumError/errors.length;
	}
	/**
	 * crossOver method to cross parent individuals and create children with
	 * high probability using one point crossover
	 * 
	 * @param parents
	 * @return children
	 */
	public MultilayerPerceptron[] crossOver(MultilayerPerceptron[] children) {
		Random rand = new Random();
		// randomly choose two parents
		int p1 = rand.nextInt(population.length);
		int p2;
		do {
			p2 = rand.nextInt(population.length);
		} while (p1 == p2);
		// get current weights for parents
		double[][][] p1Weights = population[p1].getWeights();
		double[][][] p2Weights = population[p2].getWeights();
		children[0] = population[p1];
		children[1] = population[p2];
		// randCo variable for probability of crossover between current parents
		double randCO = rand.nextDouble();
		// point variable for crossover at a random point
		int range = p1Weights.length + p1Weights[0].length + p1Weights[0][0].length;
		int point = rand.nextInt(range);

		// new arrays for children's weights
		double[][][] c1Weights = p1Weights;
		double[][][] c2Weights = p2Weights;
		// initiate crossover with crossoverRate probability
		if (randCO <= crossoverRate) {
			// replace all weights before point for one parent with other
			// parent's weights
			for (int l = 0; l < p1Weights.length; l++) {
				for (int n = 0; n < p1Weights[l].length; n++) {
					for (int i = 0; i < p1Weights[l][n].length; i++) {
						if(i <= point) {
							c1Weights[l][n][i] = p2Weights[l][n][i];
							c2Weights[l][n][i] = p1Weights[l][n][i];
						}
						else {
							c1Weights[l][n][i] = p1Weights[l][n][i];
							c2Weights[l][n][i] = p2Weights[l][n][i];
						}
					}
				}
			}
			// add own parent's weights after point
			
		}
		// update child weights after crossover occurs or not
		children[0].setWeight(c1Weights);
		children[1].setWeight(c2Weights);
		
		return children;
	}
	/**
	 * averageGenerationFitness method to get the average fitness of the entire
	 * population
	 * 
	 * @param averageErrors
	 * @return average fitness of the population
	 */
	public double averageGenerationFitness(double[] averageErrors) {
		double average = 0;
		// for each individual in the population, sum the error
		for(int i = 0; i < population.length; i++) {
			average += averageErrors[i];
		}
		average = average/population.length;
		return average;
	}
	/**
	 * terminate method to determine termination condition for returning results
	 * 
	 * @return boolean
	 */
	public boolean terminate(double avg) {
		if (avg <= ERROR) {
			return true;
		} else
			return false;
	}
	public MultilayerPerceptron result(double[] errors) {
		MultilayerPerceptron best = population[0];
		double smallest = errors[0];
		// for each individual's error, if the error at that index is smallest,
		// then the individual at that index in the population is the best
		for(int i = 1; i < population.length; i++) {
			if(errors[i] < smallest) {
				smallest = errors[i];
				best = population[i];
			}
		}
		return best;
	}
}