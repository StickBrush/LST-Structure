package ut.mpc.kdt;
import java.util.ArrayList;

import ut.mpc.setup.Init;
import KDTree.KDTree;

public class KDTTree extends KDTree {
	
	public KDTTree (int k) {
		super(k);
	}
	
	public void rangeSummary(double[] lowk, double[] uppk) {
		Object[] objs = (Object[]) this.range(lowk,uppk);

		ArrayList<Temporal> points = new ArrayList<Temporal>();
		for(int i = 0; i < objs.length; ++i){
			points.add( (Temporal) objs[i]);
		}
		WindowCompute wc = new WindowCompute(lowk,uppk,points);
		double[] corners = new double[4];
		corners = wc.getBoundingBox();
		double[] lowers = new double[2];
		lowers[0] = corners[0];
		lowers[1] = corners[2];
		double[] uppers = new double[2];
		uppers[0] = corners[1];
		uppers[1] = corners[3];
		System.out.println(lowers[0]);
		System.out.println(lowers[1]);
		System.out.println(uppers[0]);
		System.out.println(uppers[1]);
		WindowCompute wc2 = new WindowCompute(lowers,uppers,points);
		
		wc2.printWindow();
		//System.out.println("Window Probability is: " + wc2.getWindowProbability());
	}
	
	public double compareWindows(double[] lowInner, double[] uppInner, double[] lowOuter, double[] uppOuter){
		Object[] objsInner = (Object[]) this.range(lowInner,uppInner);
		Object[] objsOuter = (Object[]) this.range(lowOuter,uppOuter);
		
		ArrayList<Temporal> innerPoints = new ArrayList<Temporal>();
		ArrayList<Temporal> outerPoints = new ArrayList<Temporal>();
		
		for(int i = 0; i < objsInner.length; ++i){
			innerPoints.add( (Temporal) objsInner[i]);
		}
		for(int i = 0; i < objsOuter.length; ++i){
			outerPoints.add( (Temporal) objsOuter[i]);
		}
		
		WindowCompute wcInner = new WindowCompute(lowInner, uppInner, innerPoints);
		WindowCompute wcOuter = new WindowCompute(lowOuter,uppOuter, outerPoints);
		System.out.println("Inner Prob: " + wcInner.getWindowProbabilityOpt());
		System.out.println("Outer Prob: " + wcOuter.getWindowProbabilityOpt());
		
		System.out.println("Delta: " + (wcOuter.getWindowProbabilityOpt() - wcInner.getWindowProbabilityOpt()));
		return 10.0;
	}
	
}
