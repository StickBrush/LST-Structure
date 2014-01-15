package ut.mpc.kdt;
import java.util.ArrayList;

import ut.mpc.balance.Transform;
import ut.mpc.setup.Init;
import KDTree.KDNode;
import KDTree.KDTree;

public class KDTTree extends KDTree {
	
	public KDTTree (int k) {
		super(k);
	}
	
	//Tester function to balance tree for benchmarking
	public KDTTree balanceTree(double[] lowk, double[] uppk){
		Object[] objs = (Object[]) this.range(lowk,uppk);

		ArrayList<Temporal> points = new ArrayList<Temporal>();
		for(int i = 0; i < objs.length; ++i){
			points.add( (Temporal) objs[i]);
		}
		
		return Transform.makeBalancedKDTTree(points);
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
		WindowCompute wc2 = new WindowCompute(lowers,uppers,points);
		
		System.out.println("*******  Optimized Print Window *******");
        long start = System.currentTimeMillis();
		wc2.printWindowOpt();
        System.out.println("Time: " + (System.currentTimeMillis() - start));
		System.out.println("*******  Un-Optimized Print Window *******");
        long start2 = System.currentTimeMillis();
		wc2.printWindow();
        System.out.println("Time: " + (System.currentTimeMillis() - start2));
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
	
	public void print(){
		//printRecurse(m_root);
		printBinaryTree(m_root,0);
	}
	
	public static void printBinaryTree(KDNode root, int level){
	    if(root==null)
	         return;
	    printBinaryTree(root.getRightNode(), level+1);
	    if(level!=0){
	        for(int i=0;i<level-1;i++)
	            System.out.print("|\t");
	            System.out.print("|-------");
	            root.print();
	            System.out.print("\n");
	    }
	    else
	        root.print();
	    printBinaryTree(root.getLeftNode(), level+1);
	} 
	
	public void printRecurse(KDNode node){
		if(node != null){
			printRecurse(node.getLeftNode());
			System.out.println();
			printRecurse(node.getRightNode());
			System.out.println();
			node.print();
		}
	}
}
