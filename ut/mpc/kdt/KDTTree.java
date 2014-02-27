package ut.mpc.kdt;
import java.util.ArrayList;
import java.util.List;

import ut.mpc.balance.Transform;
import ut.mpc.setup.Init;
import KDTree.KDNode;
import KDTree.KDTree;

public class KDTTree extends KDTree implements STStore {
	private boolean smartInsert = true;
	
	public KDTTree (int k) {
		super(k);
	}
	
	public KDTTree (int k, boolean smartInsert) {
		super(k);
		this.smartInsert = smartInsert;
	}
	
	public int getSize(){
		return this.m_count;
	}
	
	public void insert(Temporal point){
		if(smartInsert)
			this.smartInsert(point);
		else
			this.stdInsert(point);
	}
	
	private void stdInsert(Temporal point){
		double[] key = new double[]{point.getXCoord(),point.getYCoord()};
		super.insert(key, point);
	}
	
	/*
	 * Simple smart insert with upper threshold.
	 * In the future: include a lower bound, include temporal clustering, etc.
	 */
	private void smartInsert(Temporal point) {
		double[] key = new double[]{point.getXCoord(),point.getYCoord()};
		double pointProb = this.getPointProbability(key,1);

		if(pointProb <= Init.INS_THRESH)
			this.stdInsert(point);
	}
	
	public double getPointProbability(double[] point, int optLevel){
		double[] lowEff = new double[2];
		double[] uppEff = new double[2];
		double[] spaceBound = GPSLib.getSpaceBound(new double[]{point[0],point[1]}, new double[]{point[0],point[1]});
		lowEff[0] = spaceBound[0];
		uppEff[0] = spaceBound[1];
		lowEff[1] = spaceBound[2];
		uppEff[1] = spaceBound[3];

		Object[] objs = (Object[]) this.range(lowEff,uppEff);

		List<Temporal> activePoints = new ArrayList<Temporal>();
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
		
		List<Temporal> points = new ArrayList<Temporal>();
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
		
		List<Temporal> points = new ArrayList<Temporal>();
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

		List<Temporal> points = new ArrayList<Temporal>();
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

		List<Temporal> points = new ArrayList<Temporal>();
		for(int i = 0; i < objs.length; ++i){
			points.add( (Temporal) objs[i]);
		}
		
		WindowCompute wc = new WindowCompute(lowk,uppk,points);
		if(optLevel == 1)
			return wc.calcWindowOpt(printWindow);
		else
			return wc.calcWindow(printWindow);
	}
	
	public double coverageQuery(long startT, long endT, boolean printWindow, int optLevel){
		List<Object> objs = this.getSequence(startT, endT, true);
		
		List<Temporal> points = new ArrayList<Temporal>();
		for(int i = 0; i < objs.size(); ++i){
			points.add( (Temporal) objs.get(i));
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
	
	public List<Temporal> findPath(double[] startP, double[] endP, boolean chrono){
		Temporal p1 = (Temporal)this.nearest(startP);
		Temporal p2 = (Temporal)this.nearest(endP);
		
		List<Object> seq = this.getSequence(p1.getCoords(), p2.getCoords(), chrono);
		List<Temporal> path = new ArrayList<Temporal>();
		for(int i = 0; i < seq.size(); ++i){
			path.add((Temporal) seq.get(i));
		}
		return path;
	}
	
	public List<Temporal> findPath(long startT, long endT, boolean chrono){		
		List<Object> seq = this.getSequence(startT, endT, chrono);
		List<Temporal> path = new ArrayList<Temporal>();
		for(int i = 0; i < seq.size(); ++i){
			path.add((Temporal) seq.get(i));
		}
		return path;
	}
	
	public double CoverageOnPath(double[] startP, double[] endP, boolean chrono, boolean includeAdj){
		List<Temporal> path = findPath(startP,endP,chrono);
		
		double[] corners = new double[4];
		double[] lowers = new double[2];
		double[] uppers = new double[2];
		corners = WindowCompute.getBoundingBox(path);
		lowers[0] = corners[0];
		lowers[1] = corners[2];
		uppers[0] = corners[1];
		uppers[1] = corners[3];
		
		WindowCompute wc;
		if(includeAdj){ //Note, this does not include all points that could affect the bound
			Object[] objs = (Object[]) this.range(lowers,uppers);
			List<Temporal> points = new ArrayList<Temporal>();
			/*
			if(chrono){
				Init.CURRENT_TIMESTAMP = ((Temporal)objs[objs.length]).getTimeStamp();
				Init.REFERENCE_TIMESTAMP = ((Temporal)objs[0]).getTimeStamp();
			} else {
				Init.REFERENCE_TIMESTAMP = ((Temporal)objs[objs.length]).getTimeStamp();
				Init.CURRENT_TIMESTAMP = ((Temporal)objs[0]).getTimeStamp();
			}
			*/
			for(int i = 0; i < objs.length; ++i){
				points.add( (Temporal) objs[i]);
			}		
			wc = new WindowCompute(lowers,uppers,points);
		} else {
			wc = new WindowCompute(lowers,uppers,path);
		}
		if(Init.CoverageWindow.OPT_LEVEL == 1)
			return wc.calcWindowOpt(Init.CoverageWindow.PLOT);
		else
			return wc.calcWindow(Init.CoverageWindow.PLOT);
	}
	
	//Currently does not maintain balance of tree, this can be fixed fairly simply
	public KDTTree balanceTree(){
		double[] lowEff = new double[2];
		double[] uppEff = new double[2];
		lowEff[0] = -Double.MAX_VALUE;
		lowEff[1] = -Double.MAX_VALUE;
		uppEff[0] = Double.MAX_VALUE;
		uppEff[1] = Double.MAX_VALUE;
		Object[] objs = (Object[]) this.range(lowEff,uppEff);

		List<Temporal> points = new ArrayList<Temporal>();
		for(int i = 0; i < objs.length; ++i){
			points.add( (Temporal) objs[i]);
		}
		
		return Transform.makeBalancedKDTTree(points);
	}
	
	//Keep for testing if want to compare against this naive approach
	public List<Temporal> getTrajectory(){
		double[] lowEff = new double[2];
		double[] uppEff = new double[2];
		lowEff[0] = -Double.MAX_VALUE;
		lowEff[1] = -Double.MAX_VALUE;
		uppEff[0] = Double.MAX_VALUE;
		uppEff[1] = Double.MAX_VALUE;
		Object[] objs = (Object[]) this.range(lowEff,uppEff);
		List<Temporal> points = new ArrayList<Temporal>();
		for(int i = 0; i < objs.length; ++i){
			points.add( (Temporal) objs[i]);
		}
		Quicksort qs = new Quicksort();
		qs.sortT(points, 0, points.size() - 1);
		return points;
	}
	
	//depends on Initiliazation.Current_Timestamp
	//in a deployed application, this might sample the current time
	public List<Temporal> getTrajectory(boolean chrono){
		return this.findPath(0,Init.CURRENT_TIMESTAMP,true);
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
	
	public double getBalance(){
		double[] retVals = getBalanceRecurse(m_root);
		return retVals[1];
	}
	
	//returns an array [0] - number of subchildren including itself
	//returns an array [1] - balance of this subtree including itself
	public static double[] getBalanceRecurse(KDNode root){
		double[] retVals = new double[2];
		if(root == null){
			retVals[0] = 0;
			retVals[1] = 0;
			return retVals;
		}
		double[] valsLeft = getBalanceRecurse(root.getLeftNode());
		double[] valsRight = getBalanceRecurse(root.getRightNode());
		retVals[0] = valsLeft[0] + valsRight[0];
		if(retVals[0] == 0){
			retVals[1] = 0;
			retVals[0] = 1;
		} else {
			retVals[1] = valsLeft[1] + valsRight[1] + (Math.abs(root.getBalance()) / retVals[0]);
			retVals[0] += 1;
		}
		return retVals;
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
