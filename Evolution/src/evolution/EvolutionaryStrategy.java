import java.util.Random;

public class EvolutionaryStrategy extends EvolutionAlgorithm{
	private int mu; //Population Size
	private int lambda; //Number of offspring each generation
	private MultilayerPerceptron[] population;
	private double standardDeviation;
	private double mutationRate;
	private double alpha;
	private int offspring;
	private int improvedOffspring;
	private int generationsPerTrain;
	
	
	public EvolutionaryStrategy(int mu, int lambda, double mutationRate, double standardDeviation, double alpha, int generationsPerTrain){
		this.mu = mu;
		this.lambda = lambda;
		this.mutationRate = mutationRate;
		this.standardDeviation = standardDeviation;
		this.alpha = alpha;
		this.offspring = 0;
		this.improvedOffspring = 0;
		this.generationsPerTrain = generationsPerTrain;
		population = new MultilayerPerceptron[mu+lambda];
		
	}
	
	public void initPopulation(MultilayerPerceptron[] MLPs){
		for(int individual = 0; individual < mu; individual++){
			population[individual] = MLPs[individual];
		}
	}
	
	public void train(double[][] inputs, double[][] expected){
		for(int index = 0; index < generationsPerTrain; index++){
			createNewGeneration(inputs, expected);
			rankAndOrganize(inputs, expected);
		}
		updateStandardDeviation();
	}
	
	private void updateStandardDeviation(){
		//1/5th rule
		if((improvedOffspring/offspring)>0.2){
			standardDeviation*=alpha;
		}else{
			standardDeviation/=alpha;
		}
		
		improvedOffspring=0;
		offspring=0;
	}
	
	private void createNewGeneration(double[][] inputs, double[][] expected){
		Random r = new Random();
		for(int offspring = 0; offspring < lambda; offspring++){
			int parentPosition = r.nextInt(population.length-lambda);
			MultilayerPerceptron newOffSpring = population[parentPosition].createCopy();
			mutate(newOffSpring);
			population[mu+offspring] = newOffSpring;
			
			this.offspring++;
			if(sumErrors(newOffSpring,inputs,expected)<sumErrors(population[parentPosition],inputs, expected)){
				improvedOffspring++;
			}
		}
	}
	
	private double sumErrors(MultilayerPerceptron mlp, double[][] inputs, double[][] expected){
		double[] errors = mlp.test(inputs, expected);
		double sumError = 0;
		for(double error: errors){
			sumError+=error;
		}
		return sumError;
	}
	
	private void mutate(MultilayerPerceptron mlp){
		Random r = new Random();
		double[][][] weights = mlp.getWeights();
		for(int layer = 0; layer<weights.length;layer++){
			for(int node = 0; node<weights[layer].length;node++){
				for(int weight = 0; weight<weights[layer][node].length;weight++){
					double random = r.nextDouble();
					if(random<=mutationRate){
						weights[layer][node][weight] += standardDeviation*generateRandomGaussian();
					}
						//System.out.println(weights[layer][node][weight]);
				}
			}
		}	
		mlp.setWeight(weights);
	}
	
	private double generateRandomGaussian(){
		Random r = new Random();
		double random = r.nextDouble()*2-1;
		double gaussian = Math.tanh(random);
		return gaussian;
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
		MultilayerPerceptron[] MLPs = new MultilayerPerceptron[mu];
		for(int i = 0; i < mu; i++){
			MLPs[i] = this.population[i];
		}
		return MLPs;
	}
}
