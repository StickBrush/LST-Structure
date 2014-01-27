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
	
	public int getSize(){
		return this.m_count;
	}
	
	//Tester function to balance tree for benchmarking
	public KDTTree balanceTree(){
		double[] lowk = new double[2];
		double[] uppk = new double[2];
		lowk[0] = -Double.MAX_VALUE;
		lowk[1] = -Double.MAX_VALUE;
		uppk[0] = Double.MAX_VALUE;
		uppk[1] = Double.MAX_VALUE;
		Object[] objs = (Object[]) this.range(lowk,uppk);

		ArrayList<Temporal> points = new ArrayList<Temporal>();
		for(int i = 0; i < objs.length; ++i){
			points.add( (Temporal) objs[i]);
		}
		
		return Transform.makeBalancedKDTTree(points);
	}
	
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
		
		System.out.println(points.size());
		
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
		
		System.out.println(points.size());
		WindowCompute wc = new WindowCompute(lowk,uppk,points);
		double returnVal;
		if(optLevel == 1)
			returnVal = wc.calcWindowOpt(printWindow);
		else
			returnVal = wc.calcWindow(printWindow);
        return returnVal;
	}
	
	public double windowQueryExt(double[] lowk, double[] uppk, boolean printWindow, int optLevel){
		double[] tempRet = new double[2];
		double[] lowers = new double[2];
		double[] uppers = new double[2];
		
		tempRet = GPSLib.getCoordFromDist(uppk[1], lowk[0], Init.SPACE_RADIUS, 270);
		lowers[0] = tempRet[1];
		tempRet = GPSLib.getCoordFromDist(uppk[1], uppk[0], Init.SPACE_RADIUS, 90);
		uppers[0] = tempRet[1];
		tempRet = GPSLib.getCoordFromDist(lowk[1], uppk[0], Init.SPACE_RADIUS, 180);
		lowers[1] = tempRet[0];
		tempRet = GPSLib.getCoordFromDist(uppk[1], lowk[1], Init.SPACE_RADIUS, 0);
		uppers[1] = tempRet[0];
		
		Object[] objs = (Object[]) this.range(lowers,uppers);

		ArrayList<Temporal> points = new ArrayList<Temporal>();
		for(int i = 0; i < objs.length; ++i){
			points.add( (Temporal) objs[i]);
		}
		
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

		return this.windowQuery(new double[]{point[1]-Init.Y_GRID_GRAN,point[0]-Init.X_GRID_GRAN}, 
								new double[]{point[1],point[0]}, false, optLevel);
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
