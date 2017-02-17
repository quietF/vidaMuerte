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
		test.updateSirs("out/averageSites_0.87_0.5_0.79.dat", 100, true, true);
		//test.getp1_p3Data("p2_0.5_p1-p3.dat", true, true);
	}

}
