import java.util.Random;

public class EvolutionaryStrategy {
	private int mu; //Population Size
	private int lambda; //Number of offspring each generation
	private MultilayerPerceptron[] population;
	
	public EvolutionaryStrategy(int mu, int lambda){
		this.mu = mu;
		this.lambda = lambda;
		population = new MultilayerPerceptron[mu+lambda];
		
	}
	
	public void initPopulation(MultilayerPerceptron[] MLPs){
		for(int individual = 0; individual < mu; individual++){
			population[individual] = MLPs[individual];
		}
	}
	
	public void evolveOneGeneration(double[][] inputs, double[][] expected){
		createNewGeneration();
		rankAndOrganize(inputs, expected);
		
	}
	
	private void createNewGeneration(){
		Random r = new Random();
		for(int offspring = 0; offspring < lambda; offspring++){
			int parentPosition = r.nextInt(population.length-1);
			MultilayerPerceptron newOffSpring = population[parentPosition].createCopy();
			mutate(newOffSpring);
			population[mu+offspring] = newOffSpring;
		}
	}
	
	//TODO add mutate function;
	private void mutate(MultilayerPerceptron mlp){
		
	}
	
	
	
	public double test(double[][] inputs, double expected[][]){
		this.rankAndOrganize(inputs, expected);
		double[] errors = population[0].test(inputs, expected);
		double sumError=0;
		for(double error: errors){
			sumError+=error;
		}
		return sumError/errors.length;
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
}
