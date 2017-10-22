package evolution;
/**
 * @author laurabsullivan-russett
 * @version November 13, 2017
 *
 */
public class Evolution {
	
	public static void main(String[] args) {
		GeneticAlgorithm ga = new GeneticAlgorithm();
		System.out.println("Printing GA population...");
		ga.printPop();
		DifferentialEvolution de = new DifferentialEvolution();
		System.out.println("\nPrinting DE population...");
		de.printPop();
	}
}
