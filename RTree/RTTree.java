package RTree;

import java.util.ArrayList;

import ut.mpc.balance.Transform;
import ut.mpc.kdt.STStore;
import ut.mpc.kdt.Temporal;
import ut.mpc.kdt.WindowCompute;
import ut.mpc.setup.Init;
import KDTree.KDNode;
import KDTree.KDTree;

public class RTTree<T> extends RTree<T> implements STStore {
	
	public RTTree (int k) {
		super(10,3,k);
	}
	
	public int getSize(){
		return this.size();
	}
	
	public void insert(Temporal point){
		double[] key = new double[]{point.getXCoord(),point.getYCoord(),point.getXCoord(),point.getYCoord()};
		super.insert(key, point);
	}
	
	/*
	 * Simple smart insert with upper threshold.
	 * In the future: include a lower bound, include temporal clustering, etc.
	 */
	public void smartInsert(Temporal point) {
		double[] key = new double[]{point.getXCoord(),point.getYCoord()};
		double pointProb = this.getPointProbability(key,1);
		
		//Should we establish a lower bound based on temporal aspects?
		
		//Simple
		if(pointProb <= Init.INS_THRESH)
			this.insert(key, point);
	}
	
	public double getPointProbability(double[] point, int optLevel){
		double[] lowEff = new double[2];
		double[] uppEff = new double[2];
		double[] spaceBound = GPSLib.getSpaceBound(new double[]{point[0],point[1]}, new double[]{point[0],point[1]});
		lowEff[0] = spaceBound[0];
		uppEff[0] = spaceBound[1];
		lowEff[1] = spaceBound[2];
		uppEff[1] = spaceBound[3];
		
		//return this.windowQuery(lowEff,uppEff,false,optLevel);

		Object[] objs = (Object[]) this.range(lowEff,uppEff);

		ArrayList<Temporal> activePoints = new ArrayList<Temporal>();
		for(int i = 0; i < objs.length; ++i){
			activePoints.add( (Temporal) objs[i]);
		}
		
		WindowCompute wc = new WindowCompute();
		return wc.getPointsProb(point[0],point[1],activePoints);
	}
	
	public void windowQuerySweep(int optLevel){
		double[] lowEff = new double[2];
		double[] uppEff = new double[2];
		lowEff[0] = -Double.MAX_VALUE;
		lowEff[1] = -Double.MAX_VALUE;
		uppEff[0] = Double.MAX_VALUE;
		uppEff[1] = Double.MAX_VALUE;
		
		//collect all possible values with max negative and positive as range bounds
		Object[] objs = (Object[]) this.range(lowEff,uppEff);
		
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
		
		int numSquares = 200;
		double xDiff = uppers[0] - lowers[0];
		double yDiff = uppers[1] - lowers[1];
		double xStep = xDiff / numSquares;
		double yStep = yDiff / numSquares;
		System.out.println(xStep);
		System.out.println(yStep);
		
		File gt = new File("gt.txt");
		File gtExt = new File("gtEXT.txt");
		File diff = new File("diff.txt");
		
		for(double x = lowers[0]; x < uppers[0]; x = x + xStep){
			for(double y = lowers[1]; y < uppers[1]; y = y + yStep){
				double[] lowk = new double[]{x,y};
				double[] uppk = new double[]{x + xStep, y + yStep};
				double smallWindow = this.windowQuery(lowk,uppk,false,optLevel);
				double largeWindow = this.windowQueryExt(lowk,uppk,false,optLevel);
				
				if(smallWindow > 0){
					gt.write(smallWindow); gt.write("\n");
					gtExt.write(largeWindow); gtExt.write("\n");
					diff.write(largeWindow - smallWindow); diff.write(",");
					//System.out.println("GT      (" + x + ", " + y + ") ---" + smallWindow);
					//System.out.println("GT(EXT) (" + x + ", " + y + ") ---" + largeWindow);
				}
			}
		}
		
		gt.close();
		gtExt.close();
		diff.close();
	}
	
	public void print(String str){
		
	}
	
	//no window parameters are given so it will print the entire window
	public double windowQuery(boolean printWindow, int optLevel){
		double[] lowEff = new double[2];
		double[] uppEff = new double[2];
		lowEff[0] = -Double.MAX_VALUE;
		lowEff[1] = -Double.MAX_VALUE;
		uppEff[0] = Double.MAX_VALUE;
		uppEff[1] = Double.MAX_VALUE;
		
		//collect all possible values with max negative and positive as range bounds
		Object[] objs = (Object[]) this.range(lowEff,uppEff);
		
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
		
		WindowCompute wc = new WindowCompute(lowk,uppk,points);
		double returnVal;
		if(optLevel == 1)
			returnVal = wc.calcWindowOpt(printWindow);
		else
			returnVal = wc.calcWindow(printWindow);
        return returnVal;
	}
	
	public double windowQueryExt(double[] lowk, double[] uppk, boolean printWindow, int optLevel){
		double[] lowEff = new double[2];
		double[] uppEff = new double[2];	
		double[] spaceBound = GPSLib.getSpaceBound(lowk, uppk);
		lowEff[0] = spaceBound[0];
		uppEff[0] = spaceBound[1];
		lowEff[1] = spaceBound[2];
		uppEff[1] = spaceBound[3];
		
		Object[] objs = (Object[]) this.range(lowEff,uppEff);

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
	
	//Tester function to balance tree for benchmarking
	public KDTTree balanceTree(){
		double[] lowEff = new double[2];
		double[] uppEff = new double[2];
		lowEff[0] = -Double.MAX_VALUE;
		lowEff[1] = -Double.MAX_VALUE;
		uppEff[0] = Double.MAX_VALUE;
		uppEff[1] = Double.MAX_VALUE;
		Object[] objs = (Object[]) this.range(lowEff,uppEff);

		ArrayList<Temporal> points = new ArrayList<Temporal>();
		for(int i = 0; i < objs.length; ++i){
			points.add( (Temporal) objs[i]);
		}
		
		return Transform.makeBalancedKDTTree(points);
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

