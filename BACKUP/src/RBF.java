
public class RBF {
	// Radial basis by means of a euclidean distance function

	// instance variables: are arrays of of the coordinant points of p and q
	int[] pCoordinance;
	int[] qCoordinance;

	public RBF(int[] pCoordinance, int[] qCoordinance) {
		this.pCoordinance = pCoordinance;
		this.qCoordinance = qCoordinance;
	}

	// euclid takes in the coordinance of points p and q. 
	public double euclid(int[] pCoordinance, int[] qCoordinance) {
		// instance variables
		double calculatedDistance = 0;

		// finds the length of the array, or the number of dimensions that
		// points p and q are in. And determines the number of sums 'eculid' must add
		// together.
		int summationIterator = pCoordinance.length;
		// iSums is the total of all the sums to be squared
		double iSums = 0;
		
		// iterate through arrays
		for (int i = 0; i <= summationIterator - 1; i++) {
			// sum of all i's: sqrt[(p-q)^2]
			iSums += Math.pow( (pCoordinance[i] - qCoordinance[i]), 2);
		}
		calculatedDistance = Math.sqrt(iSums);
		return calculatedDistance;
	}
}
