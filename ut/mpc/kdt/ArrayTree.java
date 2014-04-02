package ut.mpc.kdt;

import java.util.ArrayList;
import java.util.List;

import ut.mpc.setup.Init;

public class ArrayTree implements STStore {
	private boolean smartInsert = true;
	private boolean keepDuplicates = false;
	private List<Temporal> points = new ArrayList<Temporal>();
	
	public ArrayTree(){}
	
	public ArrayTree(boolean smartInsert){
		this.smartInsert = smartInsert;
	}
	
	public ArrayTree(List<Temporal> points){
		this.points = points;
	}
	
	public int getSize(){
		return points.size();
	}
	
	public void insert(Temporal point){
		if(smartInsert)
			this.smartInsert(point);
		else
			this.stdInsert(point);
	}
	
	private void stdInsert(Temporal point){
		if(this.keepDuplicates){
			this.points.add(point);
		} else {
			if(this.find(point) == -1)
				this.points.add(point);
		}
	}	
	
	/*
	 * Simple smart insert with upper threshold.
	 * In the future: include a lower bound, include temporal clustering, etc.
	 */
	private void smartInsert(Temporal point) {
		//test key
		double pointProb = this.getPointProbability(new double[]{point.getXCoord(),point.getYCoord()},1);
		
		//Should we establish a lower bound based on temporal aspects?
		
		//Simple
		if(pointProb <= Init.SmartInsert.INS_THRESH)
			this.stdInsert(point);
	}
	
	public double getPointProbability(double[] point, int optLevel){
		double[] lowEff = new double[2];
		double[] uppEff = new double[2];
		double[] spaceBound = GPSLib.getSpaceBound(new double[]{point[0],point[1]}, new double[]{point[0],point[1]});
		lowEff[0] = spaceBound[0];
		uppEff[0] = spaceBound[1];
		lowEff[1] = spaceBound[2];
		uppEff[1] = spaceBound[3];
		List<Temporal> activePoints = this.range(lowEff,uppEff);
		CoverageWindow wc = new CoverageWindow();
		return wc.getPointsProb(point[0],point[1],activePoints);
	}
	
	//no window parameters are given so it will print the entire window
	public double windowQuery(boolean printWindow, int optLevel){
		double[] lowEff = new double[2];
		double[] uppEff = new double[2];
		lowEff[0] = -Double.MAX_VALUE;
		lowEff[1] = -Double.MAX_VALUE;
		uppEff[0] = Double.MAX_VALUE;
		uppEff[1] = Double.MAX_VALUE;
		
		//collect all possible values with max negative and positive as range bounds
		List<Temporal> foundPoints = this.range(lowEff,uppEff);
		
		double[] corners = new double[4];
		double[] lowers = new double[2];
		double[] uppers = new double[2];
		corners = CoverageWindow.getBoundingBox(foundPoints);
		lowers[0] = corners[0];
		lowers[1] = corners[2];
		uppers[0] = corners[1];
		uppers[1] = corners[3];
		
		CoverageWindow wc = new CoverageWindow(lowers,uppers,foundPoints);
		if(optLevel == 1)
			return wc.calcWindowOptArr(printWindow);
		else
			return wc.calcWindow(printWindow);
	}
	
	public double windowQuery(double[] lowk, double[] uppk, boolean printWindow, int optLevel){
		List<Temporal> foundPoints = this.range(lowk,uppk);
		CoverageWindow wc = new CoverageWindow(lowk,uppk,foundPoints);
		if(optLevel == 1)
			return wc.calcWindowOptArr(printWindow);
		else
			return wc.calcWindow(printWindow);
	}
	
	public double windowQueryExt(double[] lowk, double[] uppk, boolean printWindow, int optLevel){
		double[] lowEff = new double[2];
		double[] uppEff = new double[2];	
		double[] spaceBound = GPSLib.getSpaceBound(lowk, uppk);
		lowEff[0] = spaceBound[0];
		uppEff[0] = spaceBound[1];
		lowEff[1] = spaceBound[2];
		uppEff[1] = spaceBound[3];
		
		List<Temporal> foundPoints = this.range(lowEff,uppEff);

		CoverageWindow wc = new CoverageWindow(lowk,uppk,foundPoints);
		if(optLevel == 1)
			return wc.calcWindowOptArr(printWindow);
		else
			return wc.calcWindow(printWindow);
	}
	
	//returns -1 if couldn't find point
	private int find(Temporal point){
		int i;
		for(i = 0; i < this.points.size(); ++i){
			if(this.points.get(i).getXCoord() == point.getXCoord() && 
			   this.points.get(i).getYCoord() == point.getYCoord())
				return i;
		}
		return -1;
	}
	
	public List<Temporal> range(double[] lowk, double[] uppk){
		List<Temporal> foundPoints = new ArrayList<Temporal>();
		for(int i = 0; i < this.points.size(); i++){
			Temporal currPoint = this.points.get(i);
			if(currPoint.getXCoord() >= lowk[0] 
				&& currPoint.getXCoord() <= uppk[0] 
				&& currPoint.getYCoord() >= lowk[1] 
				&& currPoint.getYCoord() <= uppk[1]){
					foundPoints.add(currPoint);
			}
		}
		return foundPoints;
	}
}
