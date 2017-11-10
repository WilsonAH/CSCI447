package evolution;

//Graphing Function taken Website: https://www.java-forums.org/new-java/7995-how-plot-graph-java-given-samples.html
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import javax.swing.JPanel;

public class graphPlotter extends JPanel{
	public int PAD = 20;
	public double[] data;
	
	//Sets y values to array passes in
	public graphPlotter(double[] points){
		 data = points;
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        // Draw ordinate.
        g2.draw(new Line2D.Double(PAD, PAD, PAD, h-PAD));
        // Draw abcissa.
        g2.draw(new Line2D.Double(PAD, h-PAD, w-PAD, h-PAD));
        double xInc = (double)(w - 2*PAD)/(data.length-1);
        double scale = (double)(h - 2*PAD)/getMax();
        // Mark data points.
        g2.setPaint(Color.red);
        for(int i = 0; i < data.length; i++) {
            double x = PAD + i*xInc;
            double y = h - PAD - scale*data[i];
            g2.fill(new Ellipse2D.Double(x-2, y-2, 4, 4));
        }
	}
	
	//Calculates max of points
	private double getMax() {
		double max = -Double.MAX_VALUE;
	    for(int i = 0; i < data.length; i++) {
	    	if(data[i] > max){
	    		max = data[i];
	        }
	    }
	    return max;
	}
}
