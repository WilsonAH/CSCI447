package evolution;


import java.util.ArrayList;
import java.util.Random;
/**
 * @author laura sullivan-russett, wilson harris
 * @version November 13, 2017
 *
 */
public class DifferentialEvolution extends EvolutionAlgorithm {
	private MultilayerPerceptron[] population;
	private MultilayerPerceptron[] offspring;
	MultilayerPerceptron[] generation;
	// differential weight/scaling factor, tunable parameter between (0, ∞)
	private double F;
	// crossover probability, tunable parameter between (0, 1)
	private double CR;
	private int g = 0;
	/**
	 * Constructor to initialize DE algorithm
	 */
	public DifferentialEvolution(int size, double F, double CR) {
		this.population = new MultilayerPerceptron[size];
		this.offspring = new MultilayerPerceptron[size];
		this.generation = new MultilayerPerceptron[size];
		this.F = F;
		this.CR = CR;
	}
	/**
	 * initPopulation method to initialize the population of MLPs
	 * 
	 * @param MLPs
	 */
	public void initPopulation(MultilayerPerceptron[] MLPs){
		for(int individual = 0; individual < population.length; individual++){
			this.population[individual] = MLPs[individual];
		}
		offspring = population;
	}
	/**
	 * evolveOneGeneration method to update the population with the individuals
	 * after mutation and crossover.
	 */
	public void evolveOneGeneration(double[][] inputs, double[][] expected) {
		
		System.out.println("Evolving Generation  " + g);
		// create new array to hold next generation and crossover current population
		generation = crossOver(inputs, expected); 
		// update population with new generation
		this.population = generation;
		rankAndOrganize(inputs, expected);
		// get error of current population
		//double[] averageErrors = generationFitness(inputs, expected);
		g++;
	}
	public void train(double[][] inputs, double[][] expected) {
		for(int index = 0; index < 10; index++){
			evolveOneGeneration(inputs, expected);
			rankAndOrganize(inputs, expected);
		}
		
	}
	public double test(double[][] inputs, double[][] expected) {
		this.rankAndOrganize(inputs, expected);
		double[] errors = population[0].test(inputs, expected);
		double sumError=0;
		for(double error: errors){
			sumError+=error;
		}
		return sumError/errors.length;
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
	 * crossOver method to use each individual in the population as a target
	 * individual, select agents to create a trial vector. Use trial and target to 
	 * create child individual and compare to target individual for replacement 
	 * in population 
	 * 
	 * @return array of MLPs
	 */
	public MultilayerPerceptron[] crossOver(double[][] inputs, double[][] expected) {
		// rand variable for feature crossover
		Random rand = new Random();
		// for each individual (parent) in the population, get agents and calculate trial individual
		for(int i = 0; i < population.length; i++) {
			MultilayerPerceptron parent = this.population[i];
			MultilayerPerceptron[] agents = selectAgents(i);
			// get weight arrays for each selected agent
			double[][][] target = agents[0].getWeights();
			double[][][] diff1 = agents[1].getWeights();
			double[][][] diff2 = agents[2].getWeights();
			// create trial weight array
			double[][][] trial = target;
			// for each weight in the parent's array list, calculate trial value
			// using weights at same index for each agent 
			for (int l = 0; l < target.length; l++) {
				for (int n = 0; n < target[l].length; n++) {
					for (int s = 0; s < target[l][n].length; s++) {
						// calculate trial weight (target + scaling factor(differenceV1 - differenceV2)
						double weight = (target[l][n][s] + F * (diff1[l][n][s] - diff2[l][n][s]));
						trial[l][n][s] = weight;
					}
				}
			}
			// move trial weights into an ArrayList for manipulation
			ArrayList<Double> trialArray = toArray(trial);
			// get an array of random values (0, 1) for each feature for comparison with CR
			double[] featureVals = crossoverIndices();
			// pick a random index, ensuring one parent feature will be retained
			int k = rand.nextInt(featureVals.length-1);
			// move parent weights into an ArrayList for manipulation 
			ArrayList<Double> parentArray = toArray(parent.getWeights());
			// for each feature, if the feature value is greater than the crossover probability
			// or the index = k, the parent feature will be retained in the child
			for(int j = 0; j < featureVals.length; j++) {
				if(featureVals[j] > CR || j == k) {
					trialArray.set(j, parentArray.get(j));
				}
			}
			// put resulting weights back into the 3D weight array
			int a = 0;
			for (int l = 0; l < trial.length; l++) {
				for (int n = 0; n < trial[l].length; n++) {
					for (int s = 0; s < trial[l][n].length; s++) {
						trial[l][n][s] = trialArray.get(a);
						a++;
					}
				}
			}
			// set the new individuals weights after crossover
			offspring[i].setWeight(trial);
			// calculate fitness of parent and child
			double parentError = individualFitness(population[i], inputs, expected);
			double childError = individualFitness(offspring[i], inputs, expected);
			// if parent is more fit than child, replace parent in offspring population
			if(parentError < childError) {
				offspring[i] = population[i];
			}
		}
		return offspring;	
	}
	/**
	 * selectAgents method to select and return three other unique individuals 
	 * to calculate mutation
	 * 
	 * @param target individual
	 * @return three unique individuals 
	 */
	public MultilayerPerceptron[] selectAgents(int parentIndex) {
		// create array for agents
		MultilayerPerceptron[] agents  = new MultilayerPerceptron[3];
		// select individual randomly to compare to target
		Random r = new Random();
		int select = r.nextInt(population.length-1);
		// while individual is not unique, randomly select another
		do {
			select = r.nextInt(population.length-1);
		}
		while(select == parentIndex); 
		// once unique agent selected, add to agents array
		agents[0] = population[select];
		int agent0 = select;
		// while individual is not unique, randomly select another
		do {
			select = r.nextInt(population.length-1);
		}
		while(select == parentIndex || select == agent0);
		agents[1] = population[select];
		int agent1 = select;
		// while individual is not unique, randomly select another
		do {
			select = r.nextInt(population.length-1);
		}
		while(select == parentIndex || select == agent0 || select == agent1);
		agents[2] = population[select];
		return agents;
	}
	private double individualFitness(MultilayerPerceptron mlp, double[][] inputs, double[][] expected){
		double[] errors = mlp.test(inputs, expected);
		double individualFitness = 0;
		for(double error: errors){
			individualFitness+=error;
		}
		return individualFitness;
	}
	/**
	 * averageGenerationFitness method to add up the error for each individual
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
	 * coIndices method to create an array of indices for crossover
	 * 
	 * @return array of index values
	 */
	private double[] crossoverIndices() {
		Random rand = new Random();
		double[][][] weights = population[0].getWeights();
		int r = 0;
		for(int l = 0; l < weights.length; l++) {
			for(int n = 0; n < weights[l].length; n++) {
				for(int s = 0; s < weights[l][n].length; s++) {
					r++;
				}
			}
		}
		double[] indices = new double[r];
		for(int i = 0; i < r; i++) {
			indices[i] = rand.nextDouble();
		}
		return indices;
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
	public MultilayerPerceptron[] getPopulation() {
		return this.population;
	}
}
