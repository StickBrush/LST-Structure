package ut.mpc.testers;

import ut.mpc.kdt.ArrayTree;
import ut.mpc.kdt.KDTTree;
import ut.mpc.kdt.STStore;
import ut.mpc.setup.Init;

public class Demo2 {
	public static KDTTree kdtree;
	public static ArrayTree arrtree;
	public static long timer;
	
	public static void main(String[] args){
		Init.setMobilityDefaults();
		kdtree = new KDTTree(2,false);
		arrtree = new ArrayTree(false);
		
		args = new String[]{"KAIST002.txt"};
		STStore[] trees = new STStore[]{kdtree,arrtree};

		//KAIST019.txt - spread
		//KAIST013.txt - medium
		//KAIST045.txt - compact - few points ~300?
		//KAIST055.txt - compact - many points 1482
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
		System.out.println("Size is: " + kdtree.getSize());
        System.out.println("[KDTree]");
        Helpers.startTimer();
        kdtree.windowQuery(true, 1);
        Helpers.endTimer(true);
        
        System.out.println("[ArrayTree]");
        Helpers.startTimer();
        arrtree.windowQuery(false, 1);
        Helpers.endTimer(true);
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
