package evolution;


public abstract class EvolutionAlgorithm {
	public abstract void initPopulation(MultilayerPerceptron[] MLPs);
	public abstract MultilayerPerceptron[] getPopulation();
	public abstract void train(double inputs[][], double expected[][]);
	public abstract double test(double inputs[][], double expected[][]);
}
