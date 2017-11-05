package evolution;
//Author: Wilson Harris

public class Layer {
	public Node[] nodes;
	
	/**
	 * Constructor for Layer
	 */
	public Layer(int nodeCount, int forwardLayerNodeCount, Layer nextLayer, boolean useActivationFunction){
		//Creates an array of nodes
		nodes = new Node[nodeCount];
		for(int node = 0; node < nodes.length;node++){
			nodes[node] = new Node(forwardLayerNodeCount,nextLayer,useActivationFunction);
		}
	}
}
