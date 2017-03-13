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
	private double[] sir = new double[3]; // This stores populations, so no time is lost averaging.
	private int[] xPlus1 = new int[Nx], xMinus1 = new int[Nx],
			yPlus1 = new int[Ny], yMinus1 = new int[Ny]; // For Nearest Neighbours calculations.
	private double[] p = {1./3., 1./3., 1./3.}; // {p1, p2, p3}
	private final BufferedImage bi;
	private final Frame f = new Frame();
	
	public Sirs(int Nx, int Ny, double[] p){
		if(Nx > 0 && Ny > 0){
			this.Nx = Nx;
			this.Ny = Ny;
			this.state = new int[Nx][Ny];
			this.setAuxiliary();// For Nearest Neighbours calculations.
			this.randomState();
			this.setProbs(p);
			this.bi = new BufferedImage(this.Nx, this.Ny,
					BufferedImage.TYPE_INT_RGB); // For visualisation.
		} else throw new IllegalArgumentException("Nx, Ny > 0.");
	}
	
	private void randomState(){
	    /*
	     * This initialises the lattice with equal probability of 
	     * Susceptible: red
	     * Infected: green
	     * Recovered: blue
	     * It fills the population array: sir[] = {S, I, R}
	     */
		double rand;
		this.sir[0] = 0.;
		this.sir[1] = 0.;
		this.sir[2] = 0.;
		for(int i=0; i<Nx; i++)
			for(int j=0; j<Ny; j++){
				rand = Math.random();
				if(rand < 1./3.){
					this.state[i][j] = 0; // 0 means S: red
					this.sir[0] += 1;
				}
				else if(rand < 2./3.){
					this.state[i][j] = 1; // 1 means I: green
					this.sir[1] += 1;
				}
				else{
					this.state[i][j] = 2; // 2 means R: blue
					this.sir[2] += 1;
				}
			}
	}
	
	private void randomState(double fracImmune){
	    /*
	     * This initialises the lattice with the given fraction
	     * of states being Immune (Imm) to the infection (blue). The
	     * rest of states are distributed equally as: 
	     * Susceptible: red
	     * Infected: green
	     * Recovered: blue
	     * It fills the population array: sir[] = {S, I, R+Imm}
	     */
		double rand;
		this.sir[0] = 0.;
		this.sir[1] = 0.;
		this.sir[2] = 0.;
		for(int i=0; i<Nx; i++)
			for(int j=0; j<Ny; j++){
				rand = Math.random();
				if(rand < fracImmune){
					this.state[i][j] = -1; // -1 means R (immune): blue
					this.sir[2] += 1;
				}
				else if(rand < fracImmune + (1-fracImmune)/3.){
					this.state[i][j] = 0; // 0 means S: red
					this.sir[0] += 1;
				}
				else if(rand < fracImmune + 2.*(1-fracImmune)/3.){
					this.state[i][j] = 1; // 1 means I: green
					this.sir[1] += 1;
				}
				else{
					this.state[i][j] = 2; // 2 means R: blue
					this.sir[2] += 1;
				}
			}
	}
	
	private void setProbs(double[] p){
		if(p.length == 3 && p[0] >= 0 && p[0] <= 1 && 
				p[1] >= 0 && p[1] <= 1 && p[2] >= 0 && p[2] <= 1)
			this.p = p;
	}

	private void setAuxiliary(){
	    /*
	     * This creates arrays used to get to Nearest Neighbours easily.
	     * Boundary Conditions: PERIODIC
	     */
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
		return this.state[this.xPlus1[i]][j] == 1  || this.state[this.xMinus1[i]][j] == 1 || 
				this.state[i][this.yPlus1[j]] == 1 || this.state[i][this.yMinus1[j]] == 1;
	}
	
	/*private double[] averageSIR(){
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
	}*/
	
	private void updateRandomSirs(){
	    /*
	     * Access one state and use probabilities given to change
	     * or keep equal. It modifies the sir[] array if there is
	     * a change in the state.
	     */
		double rand = Math.random();
		int[] randSite = this.getRandSite();
		int siteState = this.state[randSite[0]][randSite[1]];
		if(siteState == 1 && rand <= this.p[1]){
			this.state[randSite[0]][randSite[1]] = 2;
			this.sir[1] -= 1;
			this.sir[2] += 1;
		}
		else if(siteState == 2 && rand <= this.p[2]){
			this.state[randSite[0]][randSite[1]] = 0;
			this.sir[2] -= 1;
			this.sir[0] += 1;
		}
		else if(siteState == 0 && rand <= this.p[0] && this.infectedNN(randSite)){
			this.state[randSite[0]][randSite[1]] = 1;
			this.sir[0] -= 1;
			this.sir[1] += 1;
		}
	}
	
	private void updateParallelSirs(){
	    /*
	     * Loop through all the lattice changing states depending on 
	     * the given probabilities.
	     */
		Sirs aux = this;
		int siteState;
		double rand;
		for(int i=0; i<Nx; i++)
			for(int j=0; j<Ny; j++){
				rand = Math.random();
				siteState = aux.state[i][j];
				if(siteState == 1 && rand <= aux.p[1]){
					this.state[i][j] = 2;
					this.sir[1] -= 1;
					this.sir[2] += 1;
				}
				else if(siteState == 2 && rand <= aux.p[2]){
					this.state[i][j] = 0;
					this.sir[2] -= 1;
					this.sir[0] += 1;
				}
				else if(siteState == 0 && rand <= aux.p[0] && aux.infectedNN(new int[] {i, j})){
					this.state[i][j] = 1;
					this.sir[0] -= 1;
					this.sir[1] += 1;
				}
				if(sir[1]==0) break;
			}
	}
	
	private void updateN(int N, boolean random){
	    /*
	     * Updates N states with the specified method.
	     */
		for(int i=0; i<N; i++){
			if(random) this.updateRandomSirs();
			else this.updateParallelSirs();
			if(sir[1]==0) break;
		}
	}
	
	public void updateSirs(String outFile, int dataPoints, boolean random, boolean visual) 
			throws FileNotFoundException, UnsupportedEncodingException{
	    /* 
 	     * Evolves a randomly generated system with the class' probabilities.
 	     * 
 	     * Prints dataPoints normalised populations to 'outFile'.
 	     * Avoid throwing exceptions by using adecuate paths to the file.
 	     *
 	     * If random==True, use updateRandomSirs().
	     * If visual==True, display visualisation.
	     */
		PrintWriter writer = new PrintWriter(outFile, "UTF-8");
		if(visual) this.init();
		if(random){
			for(int n=0; n<(dataPoints*Nx*Ny); n++){
				this.updateRandomSirs();
				if(n%(Nx*Ny)==0){
					// divide the SIR array by number of spins to get the fraction of each state. 
					if(visual) this.update();
					writer.println(n + " " + this.sir[0]/(Nx*Ny) + " " + 
					this.sir[1]/(Nx*Ny) + " " + this.sir[2]/(Nx*Ny));
				}
			}
		}
		else{
			for(int n=0; n<dataPoints; n++){
				this.updateParallelSirs();
				if(visual) this.update();
				// divide the SIR array by number of spins to get the fraction of each state.
				writer.println(n + " " + this.sir[0]/(Nx*Ny) + " " + 
						this.sir[1]/(Nx*Ny) + " " + this.sir[2]/(Nx*Ny));
			}
		}
		writer.close();
	}

	public void getp1_p3Data(String outFile, int n, double p1) throws FileNotFoundException, UnsupportedEncodingException{
		double[] prob = {p1, 0.5, 0.}, avgAux = {0., 0., 0.};
		int NStabilise = 100000000, NAvg = 10000;
		PrintWriter writer = new PrintWriter(outFile, "UTF-8");
		for(int i3=0; i3<n; i3++){ // p3 for loop
                                System.out.println(p1 + " " + i3);
                                prob[2] = i3 / (double)(n-1);
                                this.setProbs(prob);
                                this.randomState();
                                this.updateN(NStabilise, true);
                                double[] avgStates = {0., 0., 0.}, avgStates2 = {0., 0., 0.};
                                for(int k=0; k<NAvg; k++){
                                        avgAux[0] = sir[0]/(Nx*Ny);
                                        avgAux[1] = sir[1]/(Nx*Ny);
                                        avgAux[2] = sir[2]/(Nx*Ny);
                                        avgStates[0] += (avgAux[0]/(double)NAvg);
                                        avgStates[1] += (avgAux[1]/(double)NAvg);
                                        avgStates[2] += (avgAux[2]/(double)NAvg);
                                        avgStates2[0] += (avgAux[0]*avgAux[0]/(double)NAvg);
                                        avgStates2[1] += (avgAux[1]*avgAux[1]/(double)NAvg);
                                        avgStates2[2] += (avgAux[2]*avgAux[2]/(double)NAvg);
                                        this.updateN(100000, true);
                                }
                                writer.println(prob[0] + " " +                                          // 1: p1
                                                prob[1] + " " +                                         // 2: p2
                                                prob[2] + " " +                                         // 3: p3
                                                avgStates[0] + " " +                                    // 4: S/N
                                                avgStates[1] + " " +                                    // 5: I/N
                                                avgStates[2] + " " +                                    // 6: R/N
                                                (avgStates2[0] - avgStates[0]*avgStates[0]) + " " +     // 7: <(dS)^2>
                                                (avgStates2[1] - avgStates[1]*avgStates[1]) + " " +     // 8: <(dI)^2>
                                                (avgStates2[2] - avgStates[2]*avgStates[2]));           // 9: <(dR)^2>
		}
		writer.println();
		writer.close();
	}
	
	public void getp1_p3Data(String outFile, boolean random, boolean visual) 
			throws FileNotFoundException, UnsupportedEncodingException{
		/*
		 * Sweeps p1=[0:1] and p3=[0:1]
		 * Evolves a randomly generated system with p2=0.5, and the given p1 and p3.
		 * 
		 * Prints normalised populations to 'outFile'.
		 * Avoid throwing exceptions by using adecuate paths to the file.
		 *
		 * Plot this in gnuplot with "p 'outfile' u 1:3:5 w image" to get
		 * the fraction of Infected sites.
		 *
		 * If random==True, use updateRandomSirs().
	     	 * If visual==True, display visualisation.
		 */
		double[] prob = {0., 0.5, 0.}, avgAux = {0., 0., 0.};
		int n = 20, NStabilise = 10000000, NAvg = 100000;
		PrintWriter writer = new PrintWriter(outFile, "UTF-8");
		if(visual) this.init();
		for(int i1=0; i1<n; i1++){ // p1 for loop
			prob[0] = i1 / (double)(n-1);
			for(int i3=0; i3<n; i3++){ // p3 for loop
				System.out.println(i1 + " " + i3);
				prob[2] = i3 / (double)(n-1);
				this.setProbs(prob);
				this.randomState();
				this.updateN(NStabilise, random);
				if(visual) this.update();
				double[] avgStates = {0., 0., 0.}, avgStates2 = {0., 0., 0.};
				for(int k=0; k<NAvg; k++){
					avgAux[0] = sir[0]/(Nx*Ny);
					avgAux[1] = sir[1]/(Nx*Ny);
					avgAux[2] = sir[2]/(Nx*Ny);
					avgStates[0] += (avgAux[0]/(double)NAvg);
					avgStates[1] += (avgAux[1]/(double)NAvg);
					avgStates[2] += (avgAux[2]/(double)NAvg);
					avgStates2[0] += (avgAux[0]*avgAux[0]/(double)NAvg);
					avgStates2[1] += (avgAux[1]*avgAux[1]/(double)NAvg);
					avgStates2[2] += (avgAux[2]*avgAux[2]/(double)NAvg);
					this.updateN(10000, random);
				}
				writer.println(prob[0] + " " + 						// 1: p1
						prob[1] + " " + 					// 2: p2
						prob[2] + " " + 					// 3: p3
						avgStates[0] + " " + 					// 4: S/N
						avgStates[1] + " " + 					// 5: I/N
						avgStates[2] + " " + 					// 6: R/N
						(avgStates2[0] - avgStates[0]*avgStates[0]) + " " + 	// 7: <(dS)^2>
						(avgStates2[1] - avgStates[1]*avgStates[1]) + " " + 	// 8: <(dI)^2>
						(avgStates2[2] - avgStates[2]*avgStates[2]));		// 9: <(dR)^2>
			}
			writer.println(); // For gnuplot 'splot'
		}
		writer.close();
		
	}
	
	public void getImmunity(String outFile, boolean random, boolean visual) throws FileNotFoundException, UnsupportedEncodingException{
		/*
		 * Change the fraction of immune agents and
		 * the probability of transition from S to I (p1).
		 * Map the info on outFile
		 *
		 * Print normalised populations to 'outFile'
		 * Avoid throwing exceptions by using adecuate paths to the file.
		 *
		 * Plot this in gnuplot with "p 'outFile' u 1:2:6 w image" 
		 *
		 * if random==True, use updateRandomSirs().
		 * if visual==True, display visualisation.
		 */
		double[] prob = {0., 0.5, 0.5}, avgAux = {0., 0., 0.};
		double fracImmune = 0.;
		int n = 30, NStabilise = 10000000, NAvg = 1000;
		PrintWriter writer = new PrintWriter(outFile, "UTF-8");
		if(visual) this.init();
		for(int i0=0; i0<n; i0++){
			fracImmune = i0 / (double)(n-1);
			for(int i1=0; i1<n; i1++){
				System.out.println(i0 + " " + i1);
				prob[0] = i1 / (double)(n-1);
				this.setProbs(prob);
				this.randomState(fracImmune);
				this.updateN(NStabilise, random);
				if(visual) this.update();
				double[] avgStates = {0., 0., 0.}, avgStates2 = {0., 0., 0.};
				for(int k=0; k<NAvg; k++){
					avgAux[0] = sir[0]/(Nx*Ny);
					avgAux[1] = sir[1]/(Nx*Ny);
					avgAux[2] = sir[2]/(Nx*Ny);
					avgStates[0] += (avgAux[0]/(double)NAvg);
					avgStates[1] += (avgAux[1]/(double)NAvg);
					avgStates[2] += (avgAux[2]/(double)NAvg);
					avgStates2[0] += (avgAux[0]*avgAux[0]/(double)NAvg);
					avgStates2[1] += (avgAux[1]*avgAux[1]/(double)NAvg);
					avgStates2[2] += (avgAux[2]*avgAux[2]/(double)NAvg);
					this.updateN(30000, random);
				}
				writer.println(fracImmune + " " + prob[0] + " " + prob[1] + " " + prob[2] + " " + 
						avgStates[0] + " " + avgStates[1] + " " + avgStates[2] + 
						" " + (avgStates2[0] - avgStates[0]*avgStates[0]) + 
						" " + (avgStates2[1] - avgStates[1]*avgStates[1]) + 
						" " + (avgStates2[2] - avgStates[2]*avgStates[2]));
			}
			writer.println();
		}
		writer.close();
	}
	
	private double[][] getData(int nGridPoints, boolean random, boolean visual){
		
		double[] prob = {0., 0.5, 0.}, avgAux = {0., 0., 0.};
		int n = nGridPoints, NStabilise = 10000000, NAvg = 100000;
		double[][] data = new double[nGridPoints*nGridPoints][9];
		if(visual) this.init();
		int i=0;
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
					avgAux[0] = sir[0]/(Nx*Ny);
					avgAux[1] = sir[1]/(Nx*Ny);
					avgAux[2] = sir[2]/(Nx*Ny);
					avgStates[0] += (avgAux[0]/(double)NAvg);
					avgStates[1] += (avgAux[1]/(double)NAvg);
					avgStates[2] += (avgAux[2]/(double)NAvg);
					avgStates2[0] += (avgAux[0]*avgAux[0]/(double)NAvg);
					avgStates2[1] += (avgAux[1]*avgAux[1]/(double)NAvg);
					avgStates2[2] += (avgAux[2]*avgAux[2]/(double)NAvg);
					this.updateN(100, random);
				}
				data[i][0] = prob[0]; data[i][1] = prob[1]; data[i][2] = prob[2];
				data[i][3] = avgStates[0]; data[i][4] = avgStates[1]; data[i][5] = avgStates[2];
				data[i][6] = avgStates2[0] - avgStates[0]*avgStates[0];
				data[i][7] = avgStates2[1] - avgStates[1]*avgStates[1];
				data[i][8] = avgStates2[2] - avgStates[2]*avgStates[2];
				i += 1;
			}
		}
		
		return data;
	}

	public void getMCData(String outFile, int nMC, int nGridPoints, boolean random, boolean visual) throws FileNotFoundException, UnsupportedEncodingException{
		double[][] data = new double[nGridPoints*nGridPoints][9], allData = new double[nGridPoints*nGridPoints][9];
		PrintWriter writer = new PrintWriter(outFile, "UTF-8");
		for(int i=0; i<nMC; i++){
			data = this.getData(nGridPoints, random, visual);
			System.out.println((i+1)+"/"+nMC);
			for(int j=0; j<nGridPoints*nGridPoints; j++)
				for(int k=0; k<9; k++)
					allData[j][k] += data[j][k] / (double)nMC;
		}
		
		for(int i=0; i<nGridPoints*nGridPoints; i++){
			if(i%nGridPoints==0) writer.println();
			writer.println(allData[i][0]+" "+allData[i][1]+" "+allData[i][2]+" "+allData[i][3]+" "+
					allData[i][4]+" "+allData[i][5]+" "+allData[i][6]+" "+allData[i][7]+" "+allData[i][8]);
		}
		writer.close();
	}
}


