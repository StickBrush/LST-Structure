package ut.mpc.testers;

import ut.mpc.kdt.ArrayTree;
import ut.mpc.kdt.KDTTree;
import ut.mpc.kdt.STStore;
import ut.mpc.setup.Init;

public class FindPathDemo {
	public static KDTTree kdtree;
	public static long timer;
	
	public static void main(String[] args){
		Init.setCabsDefaults();
		kdtree = new KDTTree(2,false);
		args = new String[]{"new_abboip.txt"};
		STStore[] trees = new STStore[]{kdtree};

		try {
	        long start = System.currentTimeMillis();
			CabSpottingWrapper.fillPointsFromFile(trees,args); //added comment to tester
	        System.out.println("Time: " + (System.currentTimeMillis() - start));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//kdtree.windowQuery(true, 1);
		Init.CoverageWindow.PLOT = true;
		Init.TEMPORAL_DECAY = 3;
		//Init.CURRENT_TIMESTAMP = 1211065169;
		//kdtree.CoverageOnPath(new double[]{-122.4223,37.75221}, new double[]{-122.43374, 37.72969}, true, false);
		kdtree.CoverageOnPath(new double[]{-122.1,37.61}, new double[]{-122.3, 37.9}, true, false);
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
