package ut.mpc.testers;

import java.io.BufferedReader;
import java.io.FileReader;

import ut.mpc.kdt.STStore;
import ut.mpc.kdt.Temporal;
import ut.mpc.setup.Init;

public class CabSpottingWrapper {
	
	//@pre: requires at least one point in file, otherwise seg. fault
	//@pre: requires first entry to be most recent and last entry to be least recent
	public static void fillPointsFromFile(STStore[] trees, String[] args) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/../Crawdad/cabspottingdata/" + args[0]));
		//BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/../Crawdad/cabspottingdata/new_abboip.txt"));
		//BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/1_unbalanced.txt"));

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
	
	//Must insert at least 1 point
	//@pre: requires at least one point in file, otherwise seg. fault
	//@pre: requires first entry to be most recent and last entry to be least recent
	public static void fillPointsRand(int numbPoints, STStore[] trees, String[] args) throws Exception{
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
		insertPoint(trees,temp);
		
		int inserted = 1;
		while ((line = br.readLine()) != null) {
		   if(Math.random() > .5){
			   Init.REFERENCE_TIMESTAMP = Long.parseLong(split[3]);
			   if(trees[0].getSize() >= numbPoints)
				   break;
			   split = new String[4];
			   split = line.split(" ");
			   temp = new Temporal(Long.parseLong(split[3]),Double.parseDouble(split[1]),Double.parseDouble(split[0]));
			   insertPoint(trees,temp);
			   inserted++;
		   }
		}
		br.close();
	}
	
	public static void insertPoint(STStore[] trees, Temporal temp){
	   for(int i = 0; i < trees.length; i++){
		   trees[i].insert(temp);
	   }
	}
}
