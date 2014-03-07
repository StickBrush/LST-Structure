package ut.mpc.testers;

import java.io.BufferedReader;
import java.io.FileReader;

import ut.mpc.kdt.ArrayTree;
import ut.mpc.kdt.KDTTree;
import ut.mpc.kdt.STStore;
import ut.mpc.setup.Init;
import KDTree.*;

public class FullWindowCabs {
	public static KDTTree kdtree;
	public static ArrayTree arrtree;
	public static KDTTree naivetree;
	public static long timer;
	
	public static void main(String[] args){
		Init.setCabsDefaults();
		kdtree = new KDTTree(2,false);
		arrtree = new ArrayTree(false);
		
		STStore[] trees = new STStore[]{kdtree,arrtree};
		
		//new_aucjun.txt is small - 6413 points
		//new_atsfiv.txt is small - 4235
		//new_atzumbon.txt runs out of java heap space
		
		//new_epemvagu.txt - compact
		//new_ucgewft.txt - medium
		//new_ucvepnuv.txt - spread
		try {
	        long start = System.currentTimeMillis();
			CabSpottingWrapper.fillPointsFromFile(trees,args); //added comment to tester
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
        kdtree.windowQuery(false, 1);
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
