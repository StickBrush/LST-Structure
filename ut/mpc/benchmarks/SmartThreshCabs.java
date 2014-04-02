package ut.mpc.benchmarks;

import ut.mpc.kdt.LSTTree;
import ut.mpc.kdt.STStore;
import ut.mpc.setup.Init;

//Note: this tester requires input parameters
//args[0] - SmartInsertThresh
//args[1] - Data File
public class SmartThreshCabs {
	public static LSTTree kdtree;
	public static LSTTree kdtreeSmart;
	public static long timer;
	
	public static void main(String[] args){
		Init.setCabsDefaults();
		
		Init.SmartInsert.INS_THRESH = Integer.valueOf(args[0]);
		kdtree = new LSTTree(2,false);
		kdtreeSmart = new LSTTree(2,true);
		
		STStore[] trees = new STStore[]{kdtree,kdtreeSmart};
		
		try {
	        long start = System.currentTimeMillis();
			CabSpottingWrapper.fillPointsFromFile(trees,new String[]{args[1]});
	        System.out.println("Time: " + (System.currentTimeMillis() - start));
		} catch (Exception e) {
			e.printStackTrace();
		}

		getStable();		
		System.out.println("Set Name >> " + args[1]);
		System.out.println("Smart Insertion Thresh: " + args[0]);
        System.out.println("[KDTree - Standard]");
        System.out.println("Size is: " + kdtree.getSize());
        Helpers.startTimer();
        double kdprob = kdtree.windowQuery(false, 1);
        Helpers.endTimer(true);
        System.out.println("WQ prob: " + kdprob);
        
        System.out.println("[KDTree SmartInsert]");
        System.out.println("Size is: " + kdtreeSmart.getSize());
        Helpers.startTimer();
        double kdprobsmart = kdtreeSmart.windowQuery(false, 1);
        Helpers.endTimer(true);
        System.out.println("WQ prob: " + kdprobsmart);
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
