import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

public class Mandelbrot {
	
	// starting position
	private int pointX = 0;
	private int pointY = 0;
	
	// frame size
	private int fHeight = 500;
	private int fWidth = 500;
	
	// drawing colour
	private Color colour = Color.white;
	private Color[] palette;
	private int paletteSize = 10;
	
	// drawing area
	private MyDrawPanel drawPanel;
	BufferedImage img;
	
	// ??
	static int n = 2;

	/**
	* Program starting point.
	*/
	public static void main(String [] args) {
		if (args.length > 0) {
			n = Integer.parseInt(args[0]);
		}

		Mandelbrot mandel = new Mandelbrot();
		mandel.go();
	}
	
	/**
	* Put everything in motion.
	*/
	public void go() {
		// set up UI
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		drawPanel = new MyDrawPanel();
		frame.getContentPane().add(drawPanel);
		
		frame.setSize(fWidth, fHeight);
		frame.setVisible(true);

		palette = new Color[paletteSize];
		generateColourPalette1();
		
		img = new BufferedImage(fWidth, fHeight, BufferedImage.TYPE_INT_RGB);
		
		// run escape time algorithm on each pixel
		for (int i = 0; i < fWidth; i++) {
			for (int j = 0; j < fHeight; j++) {
				System.out.println(String.format("Pixel: (%d,%d)",i,j));
				escapeTime(i,j);
			}
		}
	}


	/**
	* Escapte time algorithm implementation.
	* Simplest algorithm for generating a representation of the Mandelbrot set.
	* 
	* Repeating calculation is performed for each x, y point in the plot area
	* and based on the behaviour of that calculation, a color is chosen for that
	* pixel.
	*/
	public void escapeTime(int pX, int pY) {
	
		double x0 = scale(pX, 0, fWidth - 1, -2.5, 1);
		double y0 = scale(pY, 0, fHeight - 1, -1, 1);
		
		double x = 0;
		double y = 0;
		
		int iteration = 0;
		int maxIteration = 1000;

		double xTemp = 0;
		
		while ((x*x + y*y) < 2*2 && (iteration < maxIteration)) {
		
			if (n == 2) {
				xTemp = x*x - y*y + x0;
				y = 2*x*y + y0;
				x = xTemp;
			} else {
				xTemp = Math.pow(x*x+y*y, (n/2))*Math.cos(n*Math.atan2(y,x)) + x0;
				y = Math.pow(x*x+y*y, (n/2))*Math.sin(n*Math.atan2(y,x)) + y0;
				x = xTemp;
			}
			iteration++;
		}
		
		if (iteration < maxIteration) {
			double zn = Math.sqrt(x*x + y*y);
			double mu = Math.log(Math.log(zn) / Math.log(2)) / Math.log(2);
			iteration = iteration + 1 - (int)mu;
		}
		
		System.out.println(String.format("\tIterations: %d ",iteration));
		
		// get colour from palette
		int co1 = iteration | (iteration << 8);
		int co2 = (iteration+1) | (iteration << 8);
		int co3 = lerp(co1, co2, iteration % 1);
		img.setRGB(pX,pY, co3);
		
		// plot
		drawPanel.repaint();
	}
	
	private int lerp(int co1, int co2, int t) {
		return co1 + (co2-co1)*t;
	}
	
	private void generateColourPalette2() {
		int r, g, b;
		for (int i = 0; i < paletteSize; i++) {
			r = (int)scale(i,0,255, 0, 128);
			g = (int)scale(i,0,255, 0, 255);
			b = i;
			palette[i] = new Color(r,g,b);
		}
	}
	
	private void generateColourPalette1() {
		int r, g, b;
		for (int i = 0; i < paletteSize; i++) {
		
			r = (int)(Math.random() * 255 * i);
			r = (int)scale(r, 0, 255 * i, 0, 255);
			
			g = (int)(Math.random() * 255 * i);
			g = (int)scale(g, 0, 255 * i, 0, 255);
			
			b = (int)(Math.random() * 255 * i);
			b = (int)scale(b, 0, 255 * i, 0, 255);
						
			palette[i] = new Color(r,g,0);
		}
	}
	
	private double scale(int x, int min, int max, double a, double b) {
		return ((b - a) * (x - min) / (max - min)) + a;
	}
	
	private double[] mandelbrotPowerN(double x, double y, double xTemp, int n, double a, double b) {
		xTemp = Math.pow(x*x+y*y, (n/2))*Math.cos(n*Math.atan2(y,x)) + a;
		y = Math.pow(x*x+y*y, (n/2))*Math.sin(n*Math.atan2(y,x)) + b;
		x = xTemp;
		return new double[] {xTemp, x, y};
	}
	
	class MyDrawPanel extends JPanel {
		public void paintComponent(Graphics g) {
			g.drawImage(img,0,0,null);
		}
	}
}
