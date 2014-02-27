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

public class BalanceMobi {
	public static KDTTree kdtree;
	public static KDTTree kdtreeSmart;
	public static long timer;
	
	public static void main(String[] args){
		Init.setMobilityDefaults();
		kdtree = new KDTTree(2,false);
		kdtreeSmart = new KDTTree(2,true);
		
		STStore[] trees = new STStore[]{kdtree,kdtreeSmart};
		
		try {
	        long start = System.currentTimeMillis();
			MobilityWrapper.fillPointsFromFile(trees,args);
	        System.out.println("Time: " + (System.currentTimeMillis() - start));
		} catch (Exception e) {
			e.printStackTrace();
		}
		KDTTree kdtreeBal = kdtree.balanceTree();
		KDTTree kdtreeSmartBal = kdtreeSmart.balanceTree();
		
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
