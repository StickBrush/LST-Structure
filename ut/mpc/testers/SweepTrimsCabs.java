package ut.mpc.testers;

import ut.mpc.kdt.ArrayTree;
import ut.mpc.kdt.KDTTree;
import ut.mpc.kdt.STStore;
import ut.mpc.setup.Init;

public class SweepTrimsCabs {
	public static KDTTree kdtree;
	public static long timer;
	
	public static void main(String[] args){
		Init.setCabsDefaults();
		kdtree = new KDTTree(2,false);
		
		STStore[] trees = new STStore[]{kdtree};

		try {
	        long start = System.currentTimeMillis();
			CabSpottingWrapper.fillPointsFromFile(trees,args); //added comment to tester
	        System.out.println("Time: " + (System.currentTimeMillis() - start));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getStable();
		Init.DEBUG_LEVEL3 = true;
		System.out.println("Set Name >> " + args[0]);
		System.out.println("Size is: " + kdtree.getSize());
        System.out.println("[KDTree] Trim Accurate - Space=.05 Trim=20");
        Init.SPACE_TRIM = .1;
        Init.TRIM_THRESH = 18;
        Helpers.startTimer();
        kdtree.windowQuery(false, 1);
        Helpers.endTimer(true);
		
		Init.DEBUG_LEVEL3 = true;
		System.out.println("Set Name >> " + args[0]);
		System.out.println("Size is: " + kdtree.getSize());
        System.out.println("[KDTree] Trim Safe - Space=.1 Trim=15");
        Init.SPACE_TRIM = .1;
        Init.TRIM_THRESH = 15;
        Helpers.startTimer();
        kdtree.windowQuery(false, 1);
        Helpers.endTimer(true);
        
        System.out.println("[KDTree] Trim Standard - Space=.3 Trim=10");
        Init.SPACE_TRIM = .3;
        Init.TRIM_THRESH = 10;
        Helpers.startTimer();
        kdtree.windowQuery(false, 1);
        Helpers.endTimer(true);
        
        System.out.println("[KDTree] Trim Aggressive - Space=.4 Trim=5");
        Init.SPACE_TRIM = .4;
        Init.TRIM_THRESH = 5;
        Helpers.startTimer();
        kdtree.windowQuery(false, 1);
        Helpers.endTimer(true);
        System.out.println("-----------------------");
	}
	
	public static void getStable(){
		double time1 = 0;
		double time2 = 0;
		do {
			Helpers.startTimer();
			kdtree.windowQuery(false,1);
			time1 = Helpers.endTimer(false);
			Helpers.startTimer();
			kdtree.windowQuery(false,1);
			time2 = Helpers.endTimer(false);
		} while(!Helpers.withinThreePercent(time1,time2));
	}
}
