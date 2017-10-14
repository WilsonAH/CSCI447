import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class Project2Application {

	public static void main(String[] args){
		//Creates a multi-layer newtwork. ********
		MultilayerNetwork multiNetwork = null;
		
		//If 2 arguments are passed in through the command line, then they will 
		//be used as args[0] = layerCount and args[1] = nodeCount
		if(args.length>1){
			try{//Try catch to protect String to Int parsing.
				int layerCount = Integer.parseInt(args[0]);
				int nodeCounts[] = new int[args.length-3];
				for(int position = 1; position<args.length-8; position++){
					nodeCounts[position-1]=Integer.parseInt(args[position]);
				}
				int nodeCount = Integer.parseInt(args[1]);
				double momentum = Double.parseDouble(args[args.length-8]);
				int dimensions = Integer.parseInt(args[args.length-7]);
				int maxDomain = Integer.parseInt(args[args.length-6]);
				int minDomain = Integer.parseInt(args[args.length-5]);
				double bias = Double.parseDouble(args[args.length-4]);
				double learningRate = Double.parseDouble(args[args.length-3]);
				int inputVectors = Integer.parseInt(args[args.length-2]);
				boolean useLogisticFunction = Boolean.parseBoolean(args[args.length-1].trim());
				multiNetwork = new MultilayerNetwork(layerCount,nodeCounts,momentum,dimensions,maxDomain,minDomain,bias,learningRate,inputVectors,useLogisticFunction);
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("Error parsing arguments to integers. "
						+ "Program ending");
				System.exit(0);
			}
		}
		else if(args.length==1){
			try {
				BufferedReader reader = new BufferedReader(new FileReader(args[0]));
				Scanner scanner = new Scanner(reader);
				int layerCount = scanner.nextInt();
				int[] nodeCounts = new int[layerCount];
				for(int layer = 0; layer < layerCount;layer++){
					nodeCounts[layer] = scanner.nextInt();
				}
				multiNetwork = new MultilayerNetwork(layerCount,nodeCounts,scanner.nextDouble(),scanner.nextInt(),scanner.nextInt(),scanner.nextInt(),scanner.nextDouble(),scanner.nextDouble(),scanner.nextInt(),scanner.nextBoolean());
			} catch (FileNotFoundException e) {
				System.out.println("Could not fine input file");
			}
		}
		//If no arguments are passed in, then the program will ask for input.
		else{
			System.out.println("Where are those arguments at?");
		}
		
		
		//Radial Basis Function
	}
}
