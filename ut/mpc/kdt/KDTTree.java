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
	
	/*
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
		
		
	}
	*/
	
	//no window parameters are given so it will print the entire window
	public double windowQuery(boolean printWindow, int optLevel){
		double[] lowk = new double[2];
		double[] uppk = new double[2];
		lowk[0] = -Double.MAX_VALUE;
		lowk[1] = -Double.MAX_VALUE;
		uppk[0] = Double.MAX_VALUE;
		uppk[1] = Double.MAX_VALUE;
		
		//collect all possible values with max negative and positive as range bounds
		Object[] objs = (Object[]) this.range(lowk,uppk);
		ArrayList<Temporal> points = new ArrayList<Temporal>();
		for(int i = 0; i < objs.length; ++i){
			points.add( (Temporal) objs[i]);
		}
		double[] corners = new double[4];
		double[] lowers = new double[2];
		double[] uppers = new double[2];
		corners = WindowCompute.getBoundingBox(points);
		lowers[0] = corners[0];
		lowers[1] = corners[2];
		uppers[0] = corners[1];
		uppers[1] = corners[3];
		
		double[] effLowk = new double[]{corners[4],corners[6]};
		double[] effUppk = new double[]{corners[5],corners[7]};
		
		WindowCompute wc = new WindowCompute(lowers,uppers,points);
		if(optLevel == 1)
			return wc.calcWindowOpt(printWindow);
		else
			return wc.calcWindow(printWindow);
	}
	
	public double windowQuery(double[] lowk, double[] uppk, boolean printWindow, int optLevel){
		Object[] objs = (Object[]) this.range(lowk,uppk);

		ArrayList<Temporal> points = new ArrayList<Temporal>();
		for(int i = 0; i < objs.length; ++i){
			points.add( (Temporal) objs[i]);
		}
		double[] corners = new double[4];
		double[] lowers = new double[2];
		double[] uppers = new double[2];
		corners = WindowCompute.getBoundingBox(points);
		
		lowers[0] = corners[0];
		lowers[1] = corners[2];
		uppers[0] = corners[1];
		uppers[1] = corners[3];
		/*
		WindowCompute wc = new WindowCompute(lowers,uppers,points);
		*/
		
		WindowCompute wc = new WindowCompute(lowk,uppk,points);
		if(optLevel == 1)
			return wc.calcWindowOpt(printWindow);
		else
			return wc.calcWindow(printWindow);
	}
	
	public double getPointProbability(double[] point, int optLevel){
		double[] lowk = new double[2];
		double[] uppk = new double[2];
		double[] tempRet = new double[2];
		tempRet = GPSLib.getCoordFromDist(point[0], point[1], Init.SPACE_RADIUS, 270);
		lowk[0] = tempRet[1];
		tempRet = GPSLib.getCoordFromDist(point[0], point[1], Init.SPACE_RADIUS, 90);
		uppk[0] = tempRet[1];
		tempRet = GPSLib.getCoordFromDist(point[0], point[1], Init.SPACE_RADIUS, 180);
		lowk[1] = tempRet[0];
		tempRet = GPSLib.getCoordFromDist(point[0], point[1], Init.SPACE_RADIUS, 0);
		uppk[1] = tempRet[0];
		
		System.out.println(lowk[0]);
		System.out.println(uppk[0]);
		System.out.println(lowk[1]);
		System.out.println(uppk[1]);
		return this.windowQuery(lowk, uppk, false, optLevel);
	}
	
	/*
	 * Simple smart insert with upper threshold.
	 * In the future: include a lower bound, include temporal clustering, etc.
	 */
	public void smartInsert(double[] key, Object value) {
		//test key
		double pointProb = this.getPointProbability(key,1);
		
		//Should we establish a lower bound based on temporal aspects?
		
		//Simple
		if(pointProb <= Init.INS_THRESH)
			this.insert(key, value);
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
