package ut.mpc.testers;

import java.io.BufferedReader;
import java.io.FileReader;

import ut.mpc.kdt.STStore;
import ut.mpc.kdt.Temporal;
import ut.mpc.setup.Init;

public class MobilityWrapper {
	
	//@pre: requires at least one point in file, otherwise seg. fault
	//@pre: requires first entry to be most recent and last entry to be least recent
	public static void fillPointsFromFile(STStore[] trees, String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/../Crawdad/mobilitydata/Statefair/" + args[0]));
		String line;
		Temporal temp;
		line = br.readLine();
		String[] split = new String[3];
		split = line.split("\t");
		Long timestamp = Double.valueOf(split[0]).longValue();
		Init.REFERENCE_TIMESTAMP = timestamp; //should be 0.00 for mobility data
		Init.CURRENT_TIMESTAMP = timestamp; //continually update current to simulate real-time insertion of points
		temp = new Temporal(timestamp,Double.parseDouble(split[1]),Double.parseDouble(split[2]));
		insertPoint(trees,temp);
		
		while ((line = br.readLine()) != null) {
		   split = line.split("\t");
		   timestamp = Double.valueOf(split[0]).longValue();
		   Init.CURRENT_TIMESTAMP = timestamp; //continually update current to simulate real-time insertion of points
		   temp = new Temporal(timestamp,Double.parseDouble(split[1]),Double.parseDouble(split[2]));
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
