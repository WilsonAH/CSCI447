package evolution;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author laura sullivan-russett
 * @version November 13, 2017
 *
 */
public class GeneticAlgorithm extends EvolutionAlgorithm {
	private MultilayerPerceptron[] population;
	private MultilayerPerceptron[] offspring;
	private int size;
	private double mutationRate;
	private double crossoverRate;
	private final double MUTSTART = -0.1;
	private final double MUTEND = 0.1;

	public GeneticAlgorithm(int size, double mutationRate, double crossoverRate) {
		this.population = new MultilayerPerceptron[size];
		this.offspring = new MultilayerPerceptron[size]; 
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
		offspring = population;
	}
	/**
	 * evolveOneGeneration method to evolve and replace the population
	 * 
	 * @param inputs
	 * @param expected
	 */
	public void evolveOneGeneration(double[][] inputs, double[][] expected) {
		rankAndOrganize(inputs, expected);
		// find fitness of current population
		double[] tempAvg = generationFitness(inputs, expected);
		// print fitness of best individual
		System.out.println("Error = " + tempAvg[0]);
		// call mutate function for population
		mutate(inputs, expected);
		// call crossover function
		crossOver();
		// replace population with offspring
		population = offspring;
	}
	
	/**
	 * mutate method to mutate individuals in the population
	 * 
	 * @param current population
	 */
	public void mutate(double[][] inputs, double[][] expected) {
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
		offspring = population;
	}
	/**
	 * crossOver method to cross parent individuals and create children with
	 * high probability using one point crossover
	 * 
	 * @param parents
	 * @return children
	 */
	public void crossOver() {
		MultilayerPerceptron[] children = offspring; 
		Random rand = new Random();
		for(int i = 0; i < offspring.length; i += 2) {
			// randomly choose two parents
			int p1 = rand.nextInt(population.length-1);
			int p2;
			do {
				p2 = rand.nextInt(population.length-1);
			} while (p1 == p2);
			// get current weights for parents
			double[][][] p1Weights = population[p1].getWeights();
			double[][][] p2Weights = population[p2].getWeights();
			children[i] = population[p1];
			children[i+1] = population[p2];
			// randCo variable for probability of crossover between current parents
			double randCO = rand.nextDouble();
			// new arrays for children's weights
			double[][][] c1Weights = p1Weights;
			double[][][] c2Weights = p2Weights;
			// initiate crossover with crossoverRate probability
			if (randCO <= crossoverRate) {
				ArrayList<Double> parent1Array = toArray(p1Weights);
				ArrayList<Double> parent2Array = toArray(p2Weights);
				ArrayList<Double> child1Array = parent1Array;
				ArrayList<Double> child2Array = parent2Array;
				// randomly select point for crossover
				int point = rand.nextInt(parent1Array.size()-1);
				// replace all weights before point for one child with one
				// parent's weights
				for(int p = 0; p < point; p++) {
					child1Array.set(i, parent2Array.get(i));
					child2Array.set(i, parent1Array.get(i));
				}
				// replace weights after point with second parent's weights
				for(int p = point; p < parent1Array.size(); p++) {
					child1Array.set(i, parent1Array.get(i));
					child2Array.set(i, parent2Array.get(i));
				}
				// put resulting weights back into the 3D weight array
				int a = 0;
				for (int l = 0; l < c1Weights.length; l++) {
					for (int n = 0; n < c1Weights[l].length; n++) {
						for (int s = 0; s < c1Weights[l][n].length; s++) {
							c1Weights[l][n][s] = child1Array.get(a);
							c2Weights[l][n][s] = child2Array.get(a);
							a++;
						}
					}
				}
				// add new children to the children population
				children[i].setWeight(c1Weights);
				children[i+1].setWeight(c2Weights);
			}
		}
		// set the offspring population to the children created
		offspring = children;
	}
	/**
	 * generationFitness method to determine the fitness of each individual MLP by determining
	 * the error in classification
	 * 
	 * @param inputs
	 * @param expected
	 * @return array of fitness values of individuals
	 */
	public double[] generationFitness(double[][] inputs, double[][] expected) {
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
	 * sumGenerationFitness method to add up the error for each individual
	 * and get the average of the population
	 * 
	 * @param averageErrors
	 * @return average fitness of generation
	 */
	public double sumGenerationFitness(double[] averageErrors) {
		double average = 0;
		// for each individual in the population, sum the error
		for(int i = 0; i < population.length; i++) {
			average += averageErrors[i];
		}
		average = average/population.length;
		return average;
	}
	public  void train(double inputs[][], double expected[][]) {
		for(int index = 0; index < 10; index++){
			rankAndOrganize(inputs, expected);
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
	private void rankAndOrganize(double[][] inputs, double expected[][]){
		double[] averageErrors = new double[this.population.length];
		for(int individual = 0; individual < this.population.length; individual++){
			double[] errors = population[individual].test(inputs, expected);
			double sumError = 0;
			for(double error: errors){
				sumError += error;
			}
			averageErrors[individual]=sumError/errors.length;
		}
		
		quickSort(population, averageErrors, 0, population.length);
	}
	private void quickSort(MultilayerPerceptron[] unsortedPopulation, double[] errors, int low, int high){
		if ((high-low)<2){
			return;
		}
		int fenceIndex = partition(unsortedPopulation, errors, low, high);
		quickSort(unsortedPopulation, errors, low, fenceIndex);
		quickSort(unsortedPopulation, errors, fenceIndex+1, high);
	}
	private int partition(MultilayerPerceptron[] unsortedPopulation, double[] errors, int low, int high){
		double fence = errors[low];
		MultilayerPerceptron fenceMLP = unsortedPopulation[low];
		int highIndex = high-1;
		int lowIndex = low;
		while(highIndex-lowIndex > 0){
			while(highIndex != lowIndex && errors[highIndex]>fence){
				highIndex = highIndex-1;
			}
			if(highIndex!=lowIndex){
				errors[lowIndex] = errors[highIndex];
				unsortedPopulation[lowIndex] = unsortedPopulation[highIndex];
				lowIndex++;
			}
			while(highIndex!=lowIndex && errors[lowIndex]<fence){
				lowIndex++;
			}
			if(highIndex!=lowIndex){
				errors[highIndex] = errors[lowIndex];
				unsortedPopulation[highIndex] = unsortedPopulation[lowIndex];
				highIndex--;
			}
		}
		errors[lowIndex] = fence;
		unsortedPopulation[lowIndex] = fenceMLP;
		return lowIndex;
	}
	/**
	 * toArray helper method to convert 3D weight array into an ArrayList
	 * 
	 * @param weights
	 * @return ArrayList of weights
	 */
	private ArrayList<Double> toArray(double[][][] weights) {
		ArrayList<Double> weightArray = new ArrayList<Double>();
		// for each weight in the 3D array, add to ArrayList
		for (int l = 0; l < weights.length; l++) {
			for (int n = 0; n < weights[l].length; n++) {
				for (int s = 0; s < weights[l][n].length; s++) {
					weightArray.add(weights[l][n][s]);
				}
			}
		}
		return weightArray;
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
}