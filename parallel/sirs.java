import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class sirs {
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		System.out.println("HOLA");
		
		int Nx = Integer.parseInt(args[0]);
		int Ny = Integer.parseInt(args[0]);
		double[] p = {Double.parseDouble(args[1]), 
				Double.parseDouble(args[2]), Double.parseDouble(args[3])};		

		Sirs test = new Sirs(Nx, Ny, p);
		//test.updateSirs("out/averageSites_"+p[0]+"_"+p[1]+"_"+p[2]+".dat", 10000, true, true);
		test.getp1_p3Data("out/p2_0.5_p1."+p[0]+".-p3MARK.dat", 20, p[0]);
		//test.getp1_p3Data("out/p2_0.5_p1-p3MARK.dat", true, false);
		//test.getMCData("out/MCp2_0.5_p1-p3.dat", 10, 20, true, false);
		//test.getImmunity("out/immunityMARK.dat", true, false);
	}

}
