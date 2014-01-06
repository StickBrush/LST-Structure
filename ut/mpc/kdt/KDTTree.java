package ut.mpc.kdt;
import java.util.ArrayList;

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
		
		wc.printWindow();
		//System.out.println("Window Probability is: " + wc.getWindowProbability());
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
