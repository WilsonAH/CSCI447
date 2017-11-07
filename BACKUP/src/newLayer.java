
public class newLayer {
	public newNode[] nodes;
	
	public newLayer(int nodeCount, int previousLayerNodeCount){
		nodes = new newNode[nodeCount];
		for(int node = 0; node < nodes.length;node++){
			nodes[node] = new newNode(previousLayerNodeCount);
		}
	}
}
