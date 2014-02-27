package ut.mpc.testers;

import java.io.BufferedReader;
import java.io.FileReader;

import ut.mpc.kdt.ArrayTree;
import ut.mpc.kdt.KDTTree;
import ut.mpc.kdt.STStore;
import ut.mpc.kdt.Temporal;
import ut.mpc.setup.Init;


//Note* Takes two parameters
//args[0] is data input file
//args[1] is iterations of inserting same data set
public class SmartInsertCompareCabs {
	public static KDTTree kdtree;
	public static ArrayTree arrtree;
	public static KDTTree fulltree;
	public static ArrayTree warmuptree;
	public static KDTTree warmuptree2;
	public static long timer;
	
	public static void main(String[] args){
		Init.setCabsDefaults();
		kdtree = new KDTTree(2,true);
		arrtree = new ArrayTree(true);
		fulltree = new KDTTree(2,false);
		warmuptree = new ArrayTree(true);
		warmuptree2 = new KDTTree(2,true);
		
		args = new String[]{"new_utlurva.txt", "new_abboip.txt", "new_urfhod.txt", "new_unquekov.txt"};

		getStable(args); //simply runs the benchmark once as a dry run to warm up the JIT
		Init.DEBUG_LEVEL1 = false;
		Init.DEBUG_LEVEL2 = false; //for some reason we get print overflow of space weigth ~ 100.004
		STStore[] trees;
			
		trees = new STStore[]{kdtree};
		try {
	        long start = System.currentTimeMillis();
	        for(int i = 0; i < args.length; i++){
	        	String[] input = new String[]{args[i]};
	        	fillPointsFromFile(trees,input); //added comment to tester
	        }
	        System.out.println("KDTree Insertion Time: " + (System.currentTimeMillis() - start));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		trees = new STStore[]{arrtree};
		try {
	        long start = System.currentTimeMillis();
	        for(int i = 0; i < args.length; i++){
	        	String[] input = new String[]{args[i]};
	        	fillPointsFromFile(trees,input); //added comment to tester
	        }
	        System.out.println("ArrTree Insertion Time: " + (System.currentTimeMillis() - start));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Helpers.prove("Trees have equal size", kdtree.getSize() == arrtree.getSize());
		System.out.println("Tree size = " + kdtree.getSize());
		System.out.println("Tree size w/o Smart Insert = " + fulltree.getSize());
		System.out.println("--------------------");
	}
	
	public static void getStable(String[] args){
		STStore[] trees = new STStore[]{fulltree};
		
		try {
			fillPointsFromFile(trees,args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		trees = new STStore[]{warmuptree};
		try {
			fillPointsFromFile(trees,args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		trees = new STStore[]{warmuptree2};
		try {
			fillPointsFromFile(trees,args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@pre: requires at least one point in file, otherwise seg. fault
	//@pre: requires first entry to be most recent and last entry to be least recent
	public static void fillPointsFromFile(STStore[] trees, String[] args) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/../Crawdad/cabspottingdata/" + args[0]));

		String line;
		Temporal temp;
		line = br.readLine();
		String[] split = new String[4];
		split = line.split(" ");
		Init.CURRENT_TIMESTAMP = Long.parseLong(split[3]);
		Init.REFERENCE_TIMESTAMP = Long.parseLong(split[3]);
		temp = new Temporal(Long.parseLong(split[3]),Double.parseDouble(split[1]),Double.parseDouble(split[0]));
		insertPoint(trees,temp);
		
		while ((line = br.readLine()) != null) {
		   split = new String[4];
		   split = line.split(" ");
		   Init.CURRENT_TIMESTAMP = Long.parseLong(split[3]);
		   temp = new Temporal(Long.parseLong(split[3]),Double.parseDouble(split[1]),Double.parseDouble(split[0]));
		   insertPoint(trees,temp);
		}
		br.close();
	}
	
	public static void insertPoint(STStore[] trees, Temporal temp){
		   for(int i = 0; i < trees.length; i++){
			   trees[i].insert(temp);
		   }
	}
}
