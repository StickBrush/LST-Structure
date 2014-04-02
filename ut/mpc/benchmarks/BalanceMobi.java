package ut.mpc.benchmarks;

import ut.mpc.kdt.LSTTree;
import ut.mpc.kdt.STStore;
import ut.mpc.setup.Init;

public class BalanceMobi {
	public static LSTTree kdtree;
	public static LSTTree kdtreeSmart;
	public static long timer;
	
	public static void main(String[] args){
		Init.setMobilityDefaults();
		kdtree = new LSTTree(2,false);
		kdtreeSmart = new LSTTree(2,true);
		
		STStore[] trees = new STStore[]{kdtree,kdtreeSmart};
		
		try {
	        long start = System.currentTimeMillis();
			MobilityWrapper.fillPointsFromFile(trees,args);
	        System.out.println("Time: " + (System.currentTimeMillis() - start));
		} catch (Exception e) {
			e.printStackTrace();
		}
		LSTTree kdtreeBal = kdtree.balanceTree();
		LSTTree kdtreeSmartBal = kdtreeSmart.balanceTree();
		
		Helpers.prove("KDtree size matches balanced",kdtreeBal.getSize() == kdtree.getSize());
		Helpers.prove("KDtreesmart size matches balanced", kdtreeSmart.getSize() == kdtreeSmartBal.getSize());

		//Don't need to establish stability of JVM since no execution time measurements are required
		System.out.println("Set Name >> " + args[0]);
        System.out.println("[KDTree - Balancedness]");
        System.out.println("Size is: " + kdtree.getSize());
        System.out.println("Balance is: " + kdtree.getBalance() / (kdtree.getSize() - 1));
        System.out.println("Hand Balanced is: " + kdtreeBal.getBalance() / (kdtreeBal.getSize() - 1));
        
        System.out.println("[KDTree SmartIns - Balancedness]");
        System.out.println("Size is: " + kdtreeSmart.getSize());
        System.out.println("Balance is: " + kdtreeSmart.getBalance() / (kdtreeSmart.getSize() - 1));
        System.out.println("Hand Balanced is: " + kdtreeSmartBal.getBalance() / (kdtreeSmartBal.getSize() - 1));
        System.out.println("---------------------------");
	}
	
	public static void getStable(){
		double time1 = 0;
		double time2 = 0;
		do {
			Init.DEBUG_LEVEL3 = false;
			Helpers.startTimer();
			kdtree.windowQuery(false,1);
			time1 = Helpers.endTimer(false);
			Helpers.startTimer();
			kdtree.windowQuery(false,1);
			time2 = Helpers.endTimer(false);
		} while(!Helpers.withinThreePercent(time1,time2));
	}
	
}
