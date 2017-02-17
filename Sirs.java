import java.awt.Color;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Sirs {
	
	private int Nx = 100, Ny = 5;
	private int[][] state = new int[Nx][Ny];
	private int[] xPlus1 = new int[Nx], xMinus1 = new int[Nx],
			yPlus1 = new int[Ny], yMinus1 = new int[Ny];
	private double[] p = {1./3., 1./3., 1./3.}; // {p1, p2, p3}
	private final BufferedImage bi;
	private final Frame f = new Frame();
	
	public Sirs(int Nx, int Ny, double[] p){
		if(Nx > 0 && Ny > 0){
			this.Nx = Nx;
			this.Ny = Ny;
			this.state = new int[Nx][Ny];
			this.setAuxiliary();
			this.randomState();
			this.setProbs(p);
			this.bi = new BufferedImage(this.Nx, this.Ny,
					BufferedImage.TYPE_INT_RGB);
		} else throw new IllegalArgumentException("Nx, Ny > 0.");
	}
	
	private void randomState(){
		double rand;
		for(int i=0; i<Nx; i++)
			for(int j=0; j<Ny; j++){
				rand = Math.random();
				if(rand < 1./3.) this.state[i][j] = 0; // 0 means S: red
				else if(rand < 2./3.) this.state[i][j] = 1; // 1 means I: green
				else this.state[i][j] = 2; // 2 means R: blue
			}
	}
	
	private void setProbs(double[] p){
		if(p.length == 3 && p[0] >= 0 && p[0] <= 1 && 
				p[1] >= 0 && p[1] <= 1 && p[2] >= 0 && p[2] <= 1)
			this.p = p;
	}

	private void setAuxiliary(){
		this.xPlus1 = new int[Nx];
		this.xMinus1 = new int[Nx];
		this.yPlus1 = new int[Ny];
		this.yMinus1 = new int[Ny];
		for(int i=0; i<Nx; i++){
			xPlus1[i] = i+1;
			xMinus1[i] = i-1;
		}
		for(int j=0; j<Ny; j++){
			yPlus1[j] = j+1;
			yMinus1[j] = j-1;
		}
		xPlus1[Nx-1] = 0;
		xMinus1[0] = Nx-1;
		yPlus1[Ny-1] = 0;
		yMinus1[0] = Ny-1;
	}
	
	public void init() {
		/*
		 * This generates the window with the initial configuration
		 * of sirs.
		 */
		this.f.setIgnoreRepaint(true);
		this.f.setTitle("Randomised SIRS. p1:" + this.p[0] + 
				" p2:" + this.p[1] + " p3:" + this.p[2]);
		this.f.setVisible(true);
		this.f.setSize(100*this.Nx, 100*this.Ny + this.f.getInsets().top);
		this.f.addWindowListener(new WindowAdapter() 
		{public void windowClosing(WindowEvent we) {System.exit(0);}});
		
		
		for (int i = 0; i < this.bi.getWidth(); i++) 
			for (int j = 0; j < this.bi.getHeight(); j++) 
				this.bi.setRGB(i, j, this.state[i][j] == 0 ? Color.RED.getRGB()
						: (this.state[i][j] == 1 ? Color.GREEN.getRGB() 
								: Color.BLUE.getRGB()));
		this.f.getGraphics().drawImage(this.bi, 0, this.f.getInsets().top, 
				this.f.getWidth(), this.f.getHeight()-f.getInsets().top, null);
	}
	
	private void update() {
		/*
		 * Replot the sirs arrangement
		 */
		for(int i=0; i<this.bi.getWidth(); i++)
			for(int j=0; j<this.bi.getHeight(); j++)
				this.bi.setRGB(i, j, this.state[i][j] == 0 ? Color.RED.getRGB()
						: (this.state[i][j] == 1 ? Color.GREEN.getRGB() 
								: Color.BLUE.getRGB()));
		this.f.getGraphics().drawImage(this.bi, 0, this.f.getInsets().top, 
				this.f.getWidth(), this.f.getHeight()-f.getInsets().top, null);
	}
	
	private int[] getRandSite(){
		/*
		 * returns an int[] that determines a random site
		 */
		return new int[] {(int)(this.Nx * Math.random()), 
				(int)(this.Ny * Math.random())};
	}

	private boolean infectedNN(int[] randSite){
		/*
		 * Check infected Nearest Neighbours
		 */
		int i = randSite[0], j = randSite[1];
		if(this.state[this.xPlus1[i]][j] == 1) return true;
		else if(this.state[this.xMinus1[i]][j] == 1) return true;
		else if(this.state[i][this.yPlus1[j]] == 1) return true;
		else if(this.state[i][this.yMinus1[j]] == 1) return true;
		else return false;
	}
	
	private double[] averageSIR(){
		double[] avgSIR = {0., 0., 0.};
		double numSites = Nx*Ny;
		int siteState = 0;
		for(int i=0; i<Nx; i++)
			for(int j=0; j<Ny; j++){
				siteState = this.state[i][j];
				if(siteState == 0) avgSIR[0] += 1. / numSites;
				else if(siteState == 1) avgSIR[1] += 1. / numSites;
			}
		avgSIR[2] = 1. - avgSIR[0] - avgSIR[1];
		return avgSIR;
	}
	
	private void updateRandomSirs(){
		double rand = Math.random();
		int[] randSite = this.getRandSite();
		int siteState = this.state[randSite[0]][randSite[1]];
		if(siteState == 1 && rand <= this.p[1])
			this.state[randSite[0]][randSite[1]] = 2;
		else if(siteState == 2 && rand <= this.p[2])
			this.state[randSite[0]][randSite[1]] = 0;
		else if(siteState == 0 && rand <= this.p[0] && this.infectedNN(randSite))
			this.state[randSite[0]][randSite[1]] = 1;
	}
	
	private void updateN(int N, boolean random){
		for(int i=0; i<N; i++){
			if(random) this.updateRandomSirs();
			else this.updateParallelSirs();
		}
	}
	
	private void updateParallelSirs(){
		Sirs aux = this;
		int siteState;
		double rand;
		for(int i=0; i<Nx; i++)
			for(int j=0; j<Ny; j++){
				rand = Math.random();
				siteState = aux.state[i][j];
				if(siteState == 1 && rand <= aux.p[1])
					this.state[i][j] = 2;
				else if(siteState == 2 && rand <= aux.p[2])
					this.state[i][j] = 0;
				else if(siteState == 0 && rand <= aux.p[0] && aux.infectedNN(new int[] {i, j}))
					this.state[i][j] = 1;
			}
	}
	
	public void updateSirs(String outFile, int dataPoints, boolean random, boolean visual) 
			throws FileNotFoundException, UnsupportedEncodingException{
		double[] avgStates;
		PrintWriter writer = new PrintWriter(outFile, "UTF-8");
		if(visual) this.init();
		if(random){
			for(int n=0; n<(dataPoints*Nx*Ny); n++){
				this.updateRandomSirs();
				if(n%(Nx*Ny)==0){
					if(visual) this.update();
					avgStates = this.averageSIR();
					writer.println(n/(Nx*Ny) + " " + avgStates[0] + " " + 
					avgStates[1] + " " + avgStates[2]);
				}
			}
		}
		else{
			for(int n=0; n<dataPoints; n++){
				this.updateParallelSirs();
				if(visual) this.update();
				avgStates = this.averageSIR();
				writer.println(n + " " + avgStates[0] + " " + 
						avgStates[1] + " " + avgStates[2]);
			}
		}
		writer.close();
	}
	
	public void getp1_p3Data(String outFile, boolean random, boolean visual) 
			throws FileNotFoundException, UnsupportedEncodingException{
		/*
		 * p2 = 0.5
		 * sweep through p1 and p3.
		 */
		double[] prob = {0., 0.5, 0.}, avgAux = {0., 0., 0.};
		int n = 20, NStabilise = 10000000, NAvg = 10;
		PrintWriter writer = new PrintWriter(outFile, "UTF-8");
		if(visual) this.init();
		for(int i1=0; i1<n; i1++){
			prob[0] = i1 / (double)(n-1);
			for(int i3=0; i3<n; i3++){
				prob[2] = i3 / (double)(n-1);
				this.setProbs(prob);
				this.randomState();
				this.updateN(NStabilise, random);
				if(visual) this.update();
				double[] avgStates = {0., 0., 0.}, avgStates2 = {0., 0., 0.};
				for(int k=0; k<NAvg; k++){
					avgAux = this.averageSIR();
					avgStates[0] += (avgAux[0]/(double)NAvg);
					avgStates[1] += (avgAux[1]/(double)NAvg);
					avgStates[2] += (avgAux[2]/(double)NAvg);
					avgStates2[0] += (avgAux[0]*avgAux[0]/(double)NAvg);
					avgStates2[1] += (avgAux[1]*avgAux[1]/(double)NAvg);
					avgStates2[2] += (avgAux[2]*avgAux[2]/(double)NAvg);
					this.updateN(5, random);
				}
				writer.println(prob[0] + " " + prob[1] + " " + prob[2] + " " + 
						avgStates[0] + " " + avgStates[1] + " " + avgStates[2] + 
						" " + (avgStates2[0] - avgStates[0]*avgStates[0]) + 
						" " + (avgStates2[1] - avgStates[1]*avgStates[1]) + 
						" " + (avgStates2[2] - avgStates[2]*avgStates[2]));
			}
			writer.println();
		}
		writer.close();
		
	}
	
}

















