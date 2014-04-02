package ut.mpc.balance;

import java.io.BufferedReader;
import java.io.FileReader;

import ut.mpc.kdplustree.KDPlusTree;
import ut.mpc.kdt.LSTTree;
import ut.mpc.kdt.Temporal;
import ut.mpc.setup.Init;

public class BalanceTester {
	public static LSTTree kdtree;
	
	public static void main(String[] args){
		kdtree = new LSTTree(2);
			
		
		try {
			fillPointsFromFile(); //added comment to tester
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@pre: requires at least one point in file, otherwise seg. fault
	//@pre: requires first entry to be most recent and last entry to be least recent
	public static void fillPointsFromFile() throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/../Crawdad/cabspottingdata/new_abboip.txt"));
		//BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/data.txt"));

		String line;
		Temporal temp;
		line = br.readLine();
		String[] split = new String[4];
		split = line.split(" ");
		Init.CoverageWindow.CURRENT_TIMESTAMP = Long.parseLong(split[3]);
		temp = new Temporal(Long.parseLong(split[3]),Double.parseDouble(split[0]),Double.parseDouble(split[1]));
		insertPoint(temp.getXCoord(),temp.getYCoord(),kdtree,temp);
		
		while ((line = br.readLine()) != null) {
		   split = new String[4];
		   split = line.split(" ");
		   temp = new Temporal(Long.parseLong(split[3]),Double.parseDouble(split[1]),Double.parseDouble(split[0]));
		   insertPoint(temp.getXCoord(),temp.getYCoord(),kdtree,temp);
		}
		Init.CoverageWindow.REFERENCE_TIMESTAMP = Long.parseLong(split[3]);
		br.close();
	}
	
	public static void insertPoint(double x, double y, KDPlusTree kdtree, Temporal temp){
		double[] tempKey = new double[2];
		tempKey[0] = x;
		tempKey[1] = y;
		kdtree.insert(tempKey,temp);
	}
}
