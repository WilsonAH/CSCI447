package evolution;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author laura sullivan-russett, wilson harris
 * @version November 13, 2017
 *
 */
public class GeneticAlgorithm extends EvolutionAlgorithm {
	private MultilayerPerceptron[] population;
	private MultilayerPerceptron[] offspring;
	// tunable mutation and crossover parameters
	private double mutationRate;
	private double crossoverRate;
	// bounds for random mutation value
	private final double MUTSTART = -0.5;
	private final double MUTEND = 0.5;
	// tunable population size
	private int size;

	public GeneticAlgorithm(int size, double mutationRate, double crossoverRate) {
		this.size = size;
		this.population = new MultilayerPerceptron[size];
		this.offspring = new MultilayerPerceptron[size]; 
		this.crossoverRate = crossoverRate;
		this.mutationRate = mutationRate;
	}
	/**
	 * initPopulation method to initialize the population
	 * 
	 * @param input MLP array
	 */
	public void initPopulation(MultilayerPerceptron[] MLPs){
		for(int individual = 0; individual < size; individual++){
			population[individual] = MLPs[individual];
		}
	}
	
	public void train(double[][] inputs, double[][] expected){
		for(int index = 0; index < 3; index++){
			createNewGeneration(inputs, expected);
			rankAndOrganize(inputs, expected);
		}
	}
	/**
	 * createOneGeneration method to evolve and replace the population
	 * 
	 * @param inputs
	 * @param expected
	 */
	private void createNewGeneration(double[][] inputs, double[][] expected){
		crossover(inputs, expected);
		for(int i = 0; i < offspring.length; i++){
			mutate(offspring[i]);
		}
	}
	/**
	 * crossOver method to cross parent individuals and create children with
	 * high probability using one point crossover
	 * 
	 * @param parents
	 * @return children
	 */
	private void crossover(double[][] inputs, double[][] expected) {
		offspring = population;
		Random rand = new Random();
		// for each pair of individuals in the population, create 2 offspring
		for(int i = 0; i < size; i+=2) {
			double cross = rand.nextDouble();
			if(cross < crossoverRate) {
				// choose 2 parents from the population using tournament selection
				MultilayerPerceptron parent1 = tournamentSelection(inputs, expected);
				MultilayerPerceptron parent2 = tournamentSelection(inputs, expected);
				// get weights of parents for crossover
				double[][][] p1W = parent1.getWeights();
				double[][][] p2W = parent2.getWeights();
				double[][][] c1W = p1W;
				double[][][] c2W = p2W;
				// for each layer in the MLP, cross half to each child
				for(int l = 0; l < 2; l++) {
					for(int n = 0; n < p1W[l].length; n++) {
						for(int w = 0; w < p1W[l][n].length; w++) {
							c1W[l][n][w] = p2W[l][n][w];
							c2W[l][n][w] = p1W[l][n][w];
						}
					}
				}
				for(int l = 2; l < p1W.length; l++) {
					for(int n = 0; n < p1W[l].length; n++) {
						for(int w = 0; w < p1W[l][n].length; w++) {
							c1W[l][n][w] = p1W[l][n][w];
							c2W[l][n][w] = p2W[l][n][w];
						}
					}
				}
				// set the new weights of the offspring
				offspring[i].setWeight(c1W);
				offspring[i+1].setWeight(c2W);
			}
		}
		// replace the population with the offspring
		this.population = offspring;
		
	}
	/**
	 * tournamentSelection method to randomly select 4 parents and return the most fit
	 * as one parent for crossover
	 * 
	 * @param inputs
	 * @param expected
	 * @return best random parent individual
	 */
	private MultilayerPerceptron tournamentSelection(double[][] inputs, double[][] expected) {
		Random rand = new Random();
		MultilayerPerceptron parent;
		// randomly select four choices for parents
		int p1 = rand.nextInt(population.length-1);
		int p2;
		int p3;
		int p4;
		do {
			p2 = rand.nextInt(population.length-1);
		} while (p1 == p2);
		do {
			p3 = rand.nextInt(population.length/2-2);
		} while (p3 == p1 || p3 == p2);
		do {
			p4 = rand.nextInt(population.length/2-3);
		} while (p4 == p1 || p4 == p2 || p4 == p3);
		
		MultilayerPerceptron choice1 = population[p1];
		MultilayerPerceptron choice2 = population[p2];
		MultilayerPerceptron choice3 = population[p3];
		MultilayerPerceptron choice4 = population[p4];
		// calculate error for each choice
		double error1 = sumErrors(choice1, inputs, expected);
		double error2 = sumErrors(choice2, inputs, expected);
		double error3 = sumErrors(choice3, inputs, expected);
		double error4 = sumErrors(choice4, inputs, expected);
		// set parent individual to best of random choices
		if(error1 < error2) {
			parent = choice1;
		}
		else {
			parent = choice2;
		}
		if(sumErrors(parent, inputs, expected) > error3) {
			parent = choice3;
		}
		if(sumErrors(parent, inputs, expected) > error4) {
			parent = choice4;
		}
		// return best MLP as the parent
		return parent;
	}
	/**
	 * sumErrors method to get the sum of errors of an MLP
	 * 
	 * @param mlp
	 * @param inputs
	 * @param expected
	 * @return sum of errors
	 */
	private double sumErrors(MultilayerPerceptron mlp, double[][] inputs, double[][] expected){
		double[] errors = mlp.test(inputs, expected);
		double sumError = 0;
		for(double error: errors){
			sumError+=error;
		}
		return sumError;
	}
	/**
	 * mutate method to mutate individuals in the population
	 * 
	 * @param current population
	 */
	private void mutate(MultilayerPerceptron mlp){
		Random r = new Random();
		double[][][] weights = mlp.getWeights();
		// for each weight in the chromosome
		for(int layer = 0; layer<weights.length;layer++){
			for(int node = 0; node<weights[layer].length;node++){
				for(int weight = 0; weight<weights[layer][node].length;weight++){
					double random = r.nextDouble();
					if(random<=mutationRate){
						double randMut = new Random().nextDouble();
						// create mutator value
						double mutator = (MUTSTART) + (randMut * MUTEND - MUTSTART);
						// calculate new weight based on real mutation value
						double mutWeight = weights[layer][node][weight] + mutator;
						weights[layer][node][weight] = mutWeight;
					}
				}
			}
		}	
		// set the weights after mutation
		mlp.setWeight(weights);
	}
	
	public double test(double[][] inputs, double expected[][]){
		this.rankAndOrganize(inputs, expected);
		double[] errors = population[0].test(inputs, expected);
		double sumError=0;
		for(double error: errors){
			sumError+=Math.abs(error);
		}
		return sumError/errors.length;
	}
	
	private void rankAndOrganize(double[][] inputs, double expected[][]){
		double[] averageErrors = new double[this.population.length];
		for(int individual = 0; individual < this.population.length; individual++){
			double[] errors = population[individual].test(inputs, expected);
			double sumError = 0;
			for(double error: errors){
				sumError += Math.abs(error);
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

	public MultilayerPerceptron[] getPopulation() {
		MultilayerPerceptron[] MLPs = new MultilayerPerceptron[size];
		for(int i = 0; i < size; i++){
			MLPs[i] = this.population[i];
		}
		return MLPs;
	}
}
