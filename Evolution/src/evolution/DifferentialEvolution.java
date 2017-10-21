package evolution;

public class DifferentialEvolution {
	private int genCounter;
	
	public DifferentialEvolution() {
		this.genCounter = 0;
	}
	
	public void intialize() {
		Population pop = new Population();
		printPop(pop);
		
	}
	public double fitness() {
		double fitness = 0;
		
		return fitness;
	}
	
	public Individual[] selectAgents(Individual ind) {
		Individual[] agents = new Individual[3];
		return agents;
	}
	/**
	 * printPop method to print each individual in the population
	 * 
	 * @param current population
	 */
	
	public Individual perturb(Individual ind, Individual[] agents) {
		Individual perturbed = new Individual();
		
		return perturbed;
	}
	public void printPop(Population pop) {
		Individual[] inds = pop.getPop();
		
		for(int i = 0; i < inds.length; i++) {
			System.out.println(inds[i].toString(inds[i]));
		}
	}
}

