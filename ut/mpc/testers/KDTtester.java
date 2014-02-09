package ut.mpc.testers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.management.ClassLoadingMXBean;

import ut.mpc.kdt.ArrayTree;
import ut.mpc.kdt.KDTTree;
import ut.mpc.kdt.STStore;
import ut.mpc.setup.Init;
import KDTree.*;


public class KDTtester {
	public static KDTTree kdtree;
	public static ArrayTree arrtree;
	public static KDTTree naivetree;
	public static long timer;
	
	public static void main(String[] args){
		kdtree = new KDTTree(2);
		arrtree = new ArrayTree();
		naivetree = new KDTTree(2,false);
		
		STStore[] trees = new STStore[]{kdtree,arrtree};
		
		//new_okavkau.txt is very spread out, long tails
		//new_abboip.txt is medium spread, 2 tails
		//new_erulghiv.txt pretty good spread, circular
		//new_epemvagu.txt fairly compact
		try {
	        long start = System.currentTimeMillis();
			CabSpottingWrapper.fillPointsFromFile(trees,new String[]{"new_epemvagu.txt"}); //added comment to tester
	        System.out.println("Time: " + (System.currentTimeMillis() - start));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		Temporal temp = new Temporal(40,2,3);
		insertPoint(temp.getXCoord(),temp.getYCoord(),kdtree,temp);
		temp = new Temporal(30,4,7);
		insertPoint(temp.getXCoord(),temp.getYCoord(),kdtree,temp);
		temp = new Temporal(100,5,1);
		insertPoint(temp.getXCoord(),temp.getYCoord(),kdtree,temp);
		temp = new Temporal(100,3,3);
		insertPoint(temp.getXCoord(),temp.getYCoord(),kdtree,temp);
		*/
		
		double[] lowRange = new double[2];
		lowRange[1] = 37.74;
		lowRange[0] = -122.45;
		
		double[] highRange = new double[2];
		highRange[1] = 37.75;	
		highRange[0] = -122.40;
		
		
		double[] lowRangeSample = new double[2];
		lowRangeSample[0] = 0;
		lowRangeSample[1] = 0;
		
		double[] highRangeSample = new double[2];
		highRangeSample[0] = 9;
		highRangeSample[1] = 9;

		//kdtree.print();
		KDTTree balTree = kdtree.balanceTree();
		double[] key = new double[]{-122.39488, 37.75134,};
		//double val = kdtree.getPointProbability(key,1);
		
		double lowk[] = new double[2];
		double uppk[] = new double[2];
		lowk[0] = -122.437; 
		lowk[1] = 37.77;
		uppk[0] = -122.436;
		uppk[1] = 37.78;
		
        long start = System.currentTimeMillis();
        //System.out.println("Window Prob: " + arrtree.windowQuery(lowRange,highRange,false,1));
        //System.out.println("Time: " + (System.currentTimeMillis() - start));
        
        System.out.println(arrtree.getSize());
		try {
			CabSpottingWrapper.fillPointsRand(arrtree.getSize(),new STStore[]{naivetree},new String[]{"new_epemvagu.txt"});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(naivetree.getSize());
        System.out.println(kdtree.getSize());
        System.out.println(balTree.getSize());
        
        //kdtree.windowQuerySweep(1);
        //kdtree.windowQuery(true,1);
        
        //arrtree.windowQuery(false,1);
        //kdtree.windowQuery(false,1);
        //balTree.windowQuery(false,1);
        //kdtree.print();
        System.out.println("*************** Begin Benchmarking ********************");
        Helpers.startTimer();
        //System.out.println("Window prob: " + arrtree.windowQuery(false,1));
        
        kdtree.windowQuerySweep(1);
        Helpers.endTimer();
        Helpers.startTimer();
        balTree.windowQuerySweep(1);
        //System.out.println("Window prob: " + kdtree.windowQuery(false,1));
        Helpers.endTimer();
        Helpers.startTimer();
        balTree.windowQuery(false, 1);
        Helpers.endTimer();
        
        Helpers.startTimer();
        balTree.windowQuery(false, 1);
        Helpers.endTimer();
        
        Helpers.startTimer();
        balTree.windowQuery(false, 1);
        Helpers.endTimer();
        
		
        System.out.println("*******  Print Window *******");
        long start2 = System.currentTimeMillis();
        for(int i = 0; i < 1; i++){
        	//System.out.println("Point Prob: " + arrtree.getPointProbability(key,1));
        	//System.out.println("Point Prob: " + kdtree.getPointProbability(key,1));
        	//System.out.println("Point Prob: " + balTree.getPointProbability(key,1));
        }
        System.out.println("Time: " + (System.currentTimeMillis() - start2));
	}
	

	
}
