
import java.util.ArrayList;
import java.util.Random;
/**
 * @author laura sullivan-russett
 * @version November 13, 2017
 *
 */
public class DifferentialEvolution {
	private MultilayerPerceptron[] population;
	private final int SIZE = 20;
	
	// differential weight/scaling factor, tunable parameter between (0, ∞)
	private final double F = 0.5;
	// crossover probability, tunable parameter between (0, 1)
	private final double CR = 0.5;
	// error threshold for termination condition
	private final double ERROR = 0.001;
	
	/**
	 * Constructor to initialize DE algorithm and create its population
	 */
	public DifferentialEvolution() {
		this.population = new MultilayerPerceptron[SIZE];
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
	}
	/**
	 * evolveOneGeneration method to update the population with the individuals
	 * after mutation and crossover.
	 */
	public void evolveOneGeneration(double[][] inputs, double[][] expected) {
		double[] averageErrors = fitness(inputs, expected);
		MultilayerPerceptron[] temp = this.population;
		double avg = averageGenerationFitness(averageErrors);
		if(terminate(avg)) {
			result(averageErrors);
		}
		else {
			// create new array to hold next generation and mutate current population
			MultilayerPerceptron[] generation = crossOver(inputs, expected); 
			// update population with new generation
			this.population = generation;
			double[] newErrors = fitness(inputs, expected);
			if(averageGenerationFitness(newErrors) > avg) {
				this.population = temp;
			}
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
	 * crossOver method to use each individual in the population as a target
	 * individual, select agents to create a trial vector. Use trial and target to 
	 * create child individual and compare to target individual for replacement 
	 * in population 
	 * 
	 * @return array of MLPs
	 */
	public MultilayerPerceptron[] crossOver(double[][] inputs, double[][] expected) {
		System.out.println("Crossover Happening");
		// get the fitness of the current population
		double[] averageErrors = fitness(inputs, expected);
		// save the current population in a temporary variable
		MultilayerPerceptron[] temp = this.population;
		// rand variable for feature crossover
		Random rand = new Random();
		// create array for manipulated population
		MultilayerPerceptron[] replacements = this.population;
		// for each individual (parent) in the population, get agents and calculate trial individual
		for(int i = 0; i < population.length; i++) {
			MultilayerPerceptron parent = this.population[i];
			MultilayerPerceptron[] agents = selectAgents(i);
			// get weight arrays for each selected agent
			double[][][] target = agents[0].getWeights();
			double[][][] diff1 = agents[1].getWeights();
			double[][][] diff2 = agents[2].getWeights();
			// create empty trial weight array
			double[][][] trial = target;
			// for each weight in the parent's array list, calculate trial value
			// using weights at same index for each agent 
			for (int l = 0; l < target.length; l++) {
				for (int n = 0; n < target[l].length; n++) {
					for (int s = 0; s < target[l][n].length; s++) {
						//System.out.println("Trial calculation");
						// calculate trial weight (target + scaling factor(differenceV1 - differenceV2)
						double weight = (target[l][n][s] + F * (diff1[l][n][s] - diff2[l][n][s]));
						trial[l][n][s] = weight;
					}
				}
			}
			ArrayList<Double> trialArray = toArray(trial);
			// get an array of random values (0, 1) for each feature
			double[] featureVals = coIndices();
			// pick a random index, ensuring one parent feature will be retained
			int k = rand.nextInt(featureVals.length);
			double[][][] parentWeights = parent.getWeights();
			ArrayList<Double> parentArray = toArray(parentWeights);
			// for each feature, if the feature value is greater than the crossover probability
			// or the index = k, the parent feature will be retained in the child
			for(int j = 0; j < featureVals.length; j++) {
				if(featureVals[j] > CR || j == k) {
					trialArray.set(j, parentArray.get(j));
				}
			}
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
			replacements[i].setWeight(trial);
		}
		// replace the current population with the new one and determine the new fitness
		this.population = replacements;
		double[] newErrors = fitness(inputs, expected);
		// for each individual, if the error is higher for the new individual, the parent 
		// individual is put back into the population, otherwise the child remains 
		for(int i = 0; i < population.length; i++) {
			if(newErrors[i] > averageErrors[i]){
				replacements[i] = temp[i];
			}
		}
		return replacements;	
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
		int select = r.nextInt(population.length);
		// while individual is not unique, randomly select another
		do {
			select = r.nextInt(population.length);
		}
		while(select == parentIndex); 
		// once unique agent selected, add to agents array
		agents[0] = population[select];
		int agent0 = select;
		// while individual is not unique, randomly select another
		do {
			select = r.nextInt(population.length);
		}
		while(select == parentIndex || select == agent0);
		agents[1] = population[select];
		int agent1 = select;
		// while individual is not unique, randomly select another
		do {
			select = r.nextInt(population.length);
		}
		while(select == parentIndex || select == agent0 || select == agent1);
		agents[2] = population[select];
		return agents;
	}
	
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
	private double[] coIndices() {
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
	private ArrayList<Double> toArray(double[][][] weights) {
		ArrayList<Double> weightArray = new ArrayList<Double>();
		for (int l = 0; l < weights.length; l++) {
			for (int n = 0; n < weights[l].length; n++) {
				for (int s = 0; s < weights[l][n].length; s++) {
					weightArray.add(weights[l][n][s]);
				}
			}
		}
		return weightArray;
	}
}

