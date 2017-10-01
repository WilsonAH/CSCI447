import java.util.Random;

public class KMeansClustering {
	public double[][] findKClusterCenters(int k, double inputs[][]){
		int dimensionOfInputs = inputs[0].length;//Assumes every input has the same dimension
		
		//Find min and max of inputs
		double[] minimum = new double[dimensionOfInputs];
		double[] maximum = new double[dimensionOfInputs];
		for(int loop = 0; loop < minimum.length;loop++){
			minimum[loop] = Double.MAX_VALUE;
			maximum[loop] = Double.MIN_VALUE;
		}
		for(int loop = 0; loop < inputs.length; loop++){
			for(int innerLoop = 0; innerLoop < dimensionOfInputs; innerLoop++){
				if(inputs[loop][innerLoop]<minimum[innerLoop]){
					minimum[innerLoop]=inputs[loop][innerLoop];
				}
				if(inputs[loop][innerLoop]>maximum[innerLoop]){
					maximum[innerLoop]=inputs[loop][innerLoop];
				}
			}
		}
		
		//Randomly assign centers
		double[][] centers = new double[k][dimensionOfInputs];
		Random rand = new Random();
		for(int loop = 0; loop < k; loop++){
			for(int innerLoop = 0; innerLoop < dimensionOfInputs; innerLoop++){
				centers[loop][innerLoop] = (maximum[innerLoop] - minimum[innerLoop])*rand.nextDouble() + minimum[innerLoop];
			}
		}
		
		
		int[] centerForInput = new int[inputs.length];
		
		//Repeat until centers converge
		boolean changeHappened = true;
		while(changeHappened){
			changeHappened = false;
			
			
			//Assign points to centers using Euclidean distance
			for(int inputLoop = 0; inputLoop < inputs.length; inputLoop++){
				double[] distances = new double[k];
				for(int centerLoop = 0; centerLoop < k; centerLoop++){
					double sumOfDistancesSquared = 0;
					for(int dimensionLoop = 0; dimensionLoop<dimensionOfInputs; dimensionLoop++){
						sumOfDistancesSquared += Math.pow((centers[centerLoop][dimensionLoop]-inputs[inputLoop][dimensionLoop]),2);
					}
					distances[centerLoop] = Math.sqrt(sumOfDistancesSquared);
				}
				
				//Find minimum distance
				double minimumDistance = Double.MAX_VALUE;
				int closestCenter = -1;
				for(int centerLoop = 0; centerLoop < k; centerLoop++){
					if(distances[centerLoop]<minimumDistance){
						minimumDistance = distances[centerLoop];
						closestCenter = centerLoop;
					}
				}
				
				//Update which input a center belongs to
				if(centerForInput[inputLoop] != closestCenter){
					changeHappened = true;
					centerForInput[inputLoop] = closestCenter;
				}
			}
			
			//Update center values by averaging all inputs pointing to that center
			if(changeHappened){
				int[] numberOfInputs = new int[k];
				
				//Set centers to 0
				for(int centerLoop = 0; centerLoop < k; centerLoop++){
					for(int dimensionLoop = 0; dimensionLoop<dimensionOfInputs; dimensionLoop++){
						centers[centerLoop][dimensionLoop] = 0;
					}
				}
				
				//Begins averaging inputs at their centers
				for(int inputLoop = 0; inputLoop < inputs.length; inputLoop++){
					numberOfInputs[centerForInput[inputLoop]]++;//Adds up number of inputs that have this center
					for(int dimensionLoop = 0; dimensionLoop<dimensionOfInputs; dimensionLoop++){
						centers[centerForInput[inputLoop]][dimensionLoop]+=inputs[inputLoop][dimensionLoop];//Adds up all the input values
					}
				}
				
				//Completes averaging for inputs at their centers
				for(int centerLoop = 0; centerLoop < k; centerLoop++){
					for(int dimensionLoop = 0; dimensionLoop<dimensionOfInputs; dimensionLoop++){
						centers[centerLoop][dimensionLoop] = centers[centerLoop][dimensionLoop]/numberOfInputs[centerLoop];
					}
				}
				
			}
		}
		return centers;
	}
}
