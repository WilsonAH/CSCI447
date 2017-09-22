import java.util.Scanner;

public class Project2Application {

	public static void main(String[] args){
		//Creates a multi-layer newtwork. ********
		MultilayerNetwork multiNetwork = null;
		
		//If 2 arguments are passed in through the command line, then they will 
		//be used as args[0] = layerCount and args[1] = nodeCount
		if(args.length>=2){
			try{//Try catch to protect String to Int parsing.
				int layerCount = Integer.parseInt(args[0]);
				int nodeCounts[] = new int[args.length-1];
				for(int position = 1; position<args.length; position++){
					nodeCounts[position-1]=Integer.parseInt(args[position]);
				}
				int nodeCount = Integer.parseInt(args[1]);
				multiNetwork = new MultilayerNetwork(layerCount,nodeCounts);
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("Error parsing arguments to integers. "
						+ "Program ending");
				System.exit(0);
			}
		}
		//If no arguments are passed in, then the program will ask for input.
		else{
			Scanner scanner = new Scanner(System.in);
			System.out.print("Number of layers: ");
			int layerCount = scanner.nextInt();
			int[] nodeCounts = new int[layerCount];
			for(int position = 1; position<=layerCount; position++){
				System.out.print("Number of nodes for layer " + position + ": ");
				nodeCounts[position-1] = scanner.nextInt();
			}
			scanner.close();
			multiNetwork = new MultilayerNetwork(layerCount,nodeCounts);
		}
		
		
		//Radial Basis Function
	}
}
