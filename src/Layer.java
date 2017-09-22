
public class Layer {
	
	private Node nodes[];
	
	public Layer(int nodeCount){
		nodes = new Node[nodeCount];
		for(int loop = 0; loop < nodes.length; loop++){
			nodes[loop] = new Node();
		}
	}
}
