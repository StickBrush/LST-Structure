package ut.mpc.kdt;

import java.io.BufferedReader;
import java.io.FileReader;

import ut.mpc.setup.Init;
import KDTree.*;


public class KDTtester {
	public static KDTTree kdtree;
	public static ArrayTree arrtree;
	public static long timer;
	
	public static void main(String[] args){
		kdtree = new KDTTree(2);
		arrtree = new ArrayTree();
		
		try {
			fillPointsFromFile(new String[]{"new_abboip.txt"}); //added comment to tester
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
        System.out.println(kdtree.getSize());
        System.out.println(balTree.getSize());
        
        arrtree.windowQuery(false,1);
        kdtree.windowQuery(false,1);
        balTree.windowQuery(false,1);
        
        System.out.println("*************** Begin Benchmarking ********************");
        startTimer();
        System.out.println("Window prob: " + arrtree.windowQuery(false,1));
        endTimer();
        startTimer();
        System.out.println("Window prob: " + kdtree.windowQuery(false,1));
        endTimer();
        startTimer();
        balTree.windowQuery(false, 1);
        endTimer();
		
        System.out.println("*******  Print Window *******");
        long start2 = System.currentTimeMillis();
        for(int i = 0; i < 1; i++){
        	//System.out.println("Point Prob: " + arrtree.getPointProbability(key,1));
        	//System.out.println("Point Prob: " + kdtree.getPointProbability(key,1));
        	//System.out.println("Point Prob: " + balTree.getPointProbability(key,1));
        }
        System.out.println("Time: " + (System.currentTimeMillis() - start2));
	}
	
	public static void startTimer(){
		timer = System.nanoTime();    
	}
	
	//prints output to screen
	public static void endTimer(){
		long estimatedTime = System.nanoTime() - timer;
		System.out.println(">>>> execution time:  " + estimatedTime / 1000000 + "ms");
	}
	
	//@pre: requires at least one point in file, otherwise seg. fault
	//@pre: requires first entry to be most recent and last entry to be least recent
	public static void fillPointsFromFile(String[] args) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/../Crawdad/cabspottingdata/" + args[0]));
		//BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/../Crawdad/cabspottingdata/new_abboip.txt"));
		//BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/1_unbalanced.txt"));

		String line;
		Temporal temp;
		line = br.readLine();
		String[] split = new String[4];
		split = line.split(" ");
		Init.CURRENT_TIMESTAMP = Long.parseLong(split[3]);
		temp = new Temporal(Long.parseLong(split[3]),Double.parseDouble(split[1]),Double.parseDouble(split[0]));
		insertPoint(temp.getXCoord(),temp.getYCoord(),temp);
		
		while ((line = br.readLine()) != null) {
		   split = new String[4];
		   split = line.split(" ");
		   temp = new Temporal(Long.parseLong(split[3]),Double.parseDouble(split[1]),Double.parseDouble(split[0]));
		   insertPoint(temp.getXCoord(),temp.getYCoord(),temp);
		}
		
		Init.REFERENCE_TIMESTAMP = Long.parseLong(split[3]);
		br.close();
	}
	
	public static void insertPoint(double x, double y, Temporal temp){
		kdtree.smartInsert(temp);
		arrtree.smartInsert(temp);
	}
}
