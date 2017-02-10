import java.awt.Color;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Vida{
	
	private int N = 100;
	private int[][] life = new int[N][N];
	private final BufferedImage bi;
	private int[] indexPlus = new int[N];
	private int[] indexMinus = new int[N];
	private final Frame f = new Frame();

	public Vida(int N){
		if(N<=0) throw new IllegalArgumentException("N has to be larger than 0.");
		else{
			this.N = N;
			life = new int[N][N];
			indexPlus = new int[N];
			indexMinus = new int[N];
			for(int i=0; i<N; i++){
				indexPlus[i] = i+1;
				indexMinus[i] = i-1;
			}
			indexPlus[0] = N-1;
			indexMinus[N-1] = 0;
			
			this.bi = new BufferedImage(N, N, BufferedImage.TYPE_INT_RGB);
		}
	}
	
	public void startRandom(){
		/*
		 * Initialise spins randomly.
		 */
		for(int i=0; i<N; i++)
			for(int j=0; j<N; j++){
				double rand = 1 - 2 * Math.random();
				if(rand > 0) life[i][j] = 1;
				else if(rand <= 0) life[i][j] = 0;
			}
	}
	
	public void init() {
		/*
		 * This generates the window with the initial configuration
		 * of life/death.
		 */
		f.setIgnoreRepaint(true);
		f.setTitle("Game of Life");
		f.setVisible(true);
		f.setSize(N, N + f.getInsets().top);
		f.setExtendedState(Frame.MAXIMIZED_BOTH);
		f.addWindowListener(new WindowAdapter() 
		{public void windowClosing(WindowEvent we) {System.exit(0);}});
		
		for (int i = 0; i < bi.getWidth(); i++) 
			for (int j = 0; j < bi.getHeight(); j++) 
				bi.setRGB(i, j, life[i][j] == 1 ? Color.YELLOW.getRGB()
						: Color.BLUE.getRGB());
		f.getGraphics().drawImage(bi, 0, f.getInsets().top, 
				f.getWidth(), f.getHeight()-f.getInsets().top, null);
	}
	
	public void update() {
		/*
		 * Replot the life/death arrangement
		 */
		for(int i=0; i<this.bi.getWidth(); i++)
			for(int j=0; j<this.bi.getHeight(); j++)
				this.bi.setRGB(i, j, this.life[i][j] == 1 ? Color.YELLOW.getRGB()
						: Color.BLUE.getRGB());
		this.f.getGraphics().drawImage(this.bi, 0, this.f.getInsets().top, 
				this.f.getWidth(), this.f.getHeight()-f.getInsets().top, null);
	}

	

	public static void main(String[] args){
		Vida Tyrone = new Vida(500);
		
		Tyrone.startRandom();
		Tyrone.init();
		Tyrone.update();
	}
		
}
