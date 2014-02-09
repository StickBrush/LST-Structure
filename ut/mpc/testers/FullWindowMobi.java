package ut.mpc.testers;

import ut.mpc.kdt.ArrayTree;
import ut.mpc.kdt.KDTTree;
import ut.mpc.kdt.STStore;
import ut.mpc.setup.Init;

public class FullWindowMobi {
	public static KDTTree kdtree;
	public static ArrayTree arrtree;

	public static long timer;
	
	public static void main(String[] args){
		Init.setMobilityDefaults();
		kdtree = new KDTTree(2,false);
		arrtree = new ArrayTree(false);
		
		STStore[] trees = new STStore[]{kdtree,arrtree};

		
		//001 medium - good # points
		//010 spread - good # points
		//017 compact - good # points
		try {
	        long start = System.currentTimeMillis();
			MobilityWrapper.fillPointsFromFile(trees,args); //added comment to tester
	        System.out.println("Time: " + (System.currentTimeMillis() - start));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Helpers.prove("trees match in size",kdtree.getSize() == arrtree.getSize());
		getStable();
		System.out.println("Set Name >> " + args[0]);
		System.out.println("Size: " + kdtree.getSize());
        System.out.println("[KDTree]");
        Helpers.startTimer();
        System.out.println(kdtree.windowQuery(true, 1));
        Helpers.endTimer(true);
        
        System.out.println("[ArrayTree]");
        Helpers.startTimer();
        System.out.println(arrtree.windowQuery(false, 1));
        Helpers.endTimer(true);
	}
	
	public static void getStable(){
		long time1 = 0;
		long time2 = 0;
		do {
			Helpers.startTimer();
			kdtree.windowQuery(false,1);
			time1 = Helpers.endTimer(false);
			Helpers.startTimer();
			kdtree.windowQuery(false,1);
			time2 = Helpers.endTimer(false);
		} while(!Helpers.withinOnePercent(time1,time2));
	}
}
