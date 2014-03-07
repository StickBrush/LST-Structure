package ut.mpc.testers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.management.ClassLoadingMXBean;
import java.util.ArrayList;

import ut.mpc.kdt.ArrayTree;
import ut.mpc.kdt.KDTTree;
import ut.mpc.kdt.STStore;
import ut.mpc.kdt.Temporal;
import ut.mpc.setup.Init;
import KDTree.*;

//Note: this tester requires input parameters
//args[0] - SmartInsertThresh
//args[1] - Data File
public class SmartThreshMobi {
	public static KDTTree kdtree;
	public static KDTTree kdtreeSmart;
	public static long timer;
	
	public static void main(String[] args){
		Init.setMobilityDefaults();
		
		args = new String[]{"50", "KAIST002.txt"};
		Init.INS_THRESH = Integer.valueOf(args[0]);
		kdtree = new KDTTree(2,false);
		kdtreeSmart = new KDTTree(2,true);

		
		STStore[] trees = new STStore[]{kdtree,kdtreeSmart};
		
		try {
	        long start = System.currentTimeMillis();
			MobilityWrapper.fillPointsFromFile(trees,new String[]{args[1]});
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
