package ut.mpc.balance;

import java.util.ArrayList;
import java.util.List;

import ut.mpc.kdt.Temporal;
import ut.mpc.kdt.KDTTree;
import ut.mpc.kdt.Quicksort;
import ut.mpc.kdt.Temporal;

//Warning that balancing a tree will invalidate the sequence portion of the structure
public class Transform {
	private static KDTTree kdtree;
	
	public static KDTTree makeBalancedKDTTree(List<Temporal> points){
		kdtree = new KDTTree(2);
		int treeSize = points.size();
		makeTreeRecurse(points,0,treeSize - 1, true);
		return kdtree;
	}
	
	public static KDTTree makeRandomKDTTree(List<Temporal> points){
		kdtree = new KDTTree(2);
		for(int i = 0; i < points.size(); i++){
			Temporal thisPoint = points.get(i);
			double[] tempKey = new double[]{thisPoint.getXCoord(),thisPoint.getYCoord()};
			kdtree.insert(tempKey, thisPoint);
		}
		return kdtree;
	}
	
	private static void makeTreeRecurse(List<Temporal> points, int start, int end, boolean isX){
		if(start <= end){

			Quicksort qsort = new Quicksort();
			qsort.sort(points, start, end, isX);
			
			//find median and insert into new tree
			int median = ((end - start) / 2) + start;
			
			double[] tempKey = new double[2];
			tempKey[0] = points.get(median).getXCoord();
			tempKey[1] = points.get(median).getYCoord();
			kdtree.insert(tempKey, points.get(median));
			
			//recurse on left part of array
			makeTreeRecurse(points, start, median - 1, !isX);
	
			//recurse on right part of array
			makeTreeRecurse(points, median + 1, end, !isX);
		}
	}
}
