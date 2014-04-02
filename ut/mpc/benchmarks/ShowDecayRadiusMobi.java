package ut.mpc.benchmarks;

import ut.mpc.kdt.LSTTree;
import ut.mpc.kdt.STStore;
import ut.mpc.setup.Init;

public class ShowDecayRadiusMobi {
	public static LSTTree kdtree;
	public static long timer;
	
	public static void main(String[] args){
		Init.setMobilityDefaults();
		kdtree = new LSTTree(2,false);
		args = new String[]{"KAIST011.txt"};
		STStore[] trees = new STStore[]{kdtree};

		try {
	        long start = System.currentTimeMillis();
			MobilityWrapper.fillPointsFromFile(trees,args); //added comment to tester
	        System.out.println("Time: " + (System.currentTimeMillis() - start));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Init.CoverageWindow.SPACE_RADIUS = 200;
		Init.CoverageWindow.TEMPORAL_DECAY = 4;
        kdtree.windowQuery(true, 1);
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
