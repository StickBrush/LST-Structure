package ut.mpc.kdt;

import java.util.ArrayList;
import java.util.List;

import ut.mpc.balance.Transform;
import ut.mpc.setup.Init;

/**
 * @author nathanielwendt
 *
 */
public class CoverageWindow {
	private double xgridGranularity = Init.CoverageWindow.X_GRID_GRAN;
	private double ygridGranularity = Init.CoverageWindow.Y_GRID_GRAN;
	private double[] lowBound;
	private double[] upperBound;
	private List<Temporal> points = new ArrayList<Temporal>();
	private int iterations = 0;
	private int recurseIterations = 0;
	
	public CoverageWindow(){}
	
	//should assume points are within the bounds, for now
	public CoverageWindow(double[] lowBound, double[] upperBound, List<Temporal> points){
		this.lowBound = lowBound;
		this.upperBound = upperBound;
		this.points = points;
	}
	
	public int getIterations(){
		return this.iterations;
	}
	
	public int getRecurseIterations(){
		return this.recurseIterations;
	}
	
	public static double[] getBoundingBox(List<Temporal> points){
		if(points.size() == 0) { return new double[]{0,0,0,0,0,0,0,0}; }
		double[] corners = new double[8];
		Temporal[] borderPts = new Temporal[4];
		borderPts[0] = points.get(0);
		borderPts[1] = points.get(0);
		borderPts[2] = points.get(0);
		borderPts[3] = points.get(0);
		
		for(int i = 1; i < points.size(); ++i){
			Temporal point = points.get(i);
			if(point.getXCoord() < borderPts[0].getXCoord()){
				borderPts[0] = point;
			}
			if(point.getXCoord() > borderPts[1].getXCoord()){
				borderPts[1] = point;
			}
			if(point.getYCoord() < borderPts[2].getYCoord()){
				borderPts[2] = point;
			}
			if(point.getYCoord() > borderPts[3].getYCoord()){
				borderPts[3] = point;
			}
		}

		//note* shape is rectangle, not a square
		double[] tempRet = new double[2];
		tempRet = GPSLib.getCoordFromDist(borderPts[0].getYCoord(), borderPts[0].getXCoord(), Init.CoverageWindow.SPACE_RADIUS, 270);
		corners[0] = tempRet[0];
		tempRet = GPSLib.getCoordFromDist(borderPts[1].getYCoord(), borderPts[1].getXCoord(), Init.CoverageWindow.SPACE_RADIUS, 90);
		corners[1] = tempRet[0];
		tempRet = GPSLib.getCoordFromDist(borderPts[2].getYCoord(), borderPts[2].getXCoord(), Init.CoverageWindow.SPACE_RADIUS, 180);
		corners[2] = tempRet[1];
		tempRet = GPSLib.getCoordFromDist(borderPts[3].getYCoord(), borderPts[3].getXCoord(), Init.CoverageWindow.SPACE_RADIUS, 0);
		corners[3] = tempRet[1];

		return GPSLib.getSpaceBound(new double[]{borderPts[0].getXCoord(), borderPts[2].getYCoord()}, new double[]{borderPts[1].getXCoord(), borderPts[3].getYCoord()});
	}
	
	/**
	 * Uses the inclusion-exclusion principle to determine the aggregate probability of points
	 * Each possible combination of points is generated and summed or subtracted according to the incl-excl principle
	 * @param sum - store the result in sum[0], iteration count in sum[1]
	 * @param active - active list of points, pass in an empty List
	 * @param rest - remaining list of points, pass in the list of points to be computed
	 */
	public void getAggProbability(double[] sum, List<Double> active, List<Double> rest){
		sum[1]++;
		if(rest.size() == 0){
			double sign;
			if((active.size() + 1) % 2 == 1) //this acts as the (-1)^(k-1) term for alternating subtraction and addition
				sign = -1;
			else
				sign = 1;
			//perform the probability equivalent of AND of the "set", which is multiplication of the probabilities
			double andValue = 0.0;
			for(int i = 0; i < active.size(); i++){
				if(i == 0)
					andValue = active.get(i);
				else
					andValue *= active.get(i);
			}
			sum[0] += andValue * sign;
		} else {
			//shallow copy of lists
			List<Double> next1 = new ArrayList<Double>(active);
			List<Double> next2 = new ArrayList<Double>(rest);
			List<Double> next3 = new ArrayList<Double>(active);
			next1.add(rest.get(0));
			next2.remove(0);

			//recursively call subsets
			this.getAggProbability(sum,next1,next2);
			this.getAggProbability(sum,next3,next2);
		}
	}
	
	/*
	 * Optimized print window algorithm with kdtree ranges and nearby trimming
	 */
	public double calcWindowOpt(boolean printWindow){
		if(points.size() == 0) return 0.0;
		
		LSTTree pointsTree = Transform.makeBalancedKDTTree(points);
		WindowChart wc = new WindowChart("Window");
		//wc.setChartLabels("Coverage Window", "X Coords (GPS)", "Y Coords (GPS)");
		
		double x1 = this.lowBound[0];
		double x2 = this.upperBound[0];
		double y1 = this.lowBound[1];
		double y2 = this.upperBound[1];

		this.iterations = 0;
		this.recurseIterations = 0;
		
		int count = 0;
		double totalWeight = 0.0;
		for(double x = x1; x < x2; x = x + this.xgridGranularity){
			for(double y = y1; y < y2; y = y + this.ygridGranularity){
				count++;
				double[] corners = new double[4];
				corners = GPSLib.getSpaceBoundQuick(new double[]{x,y},new double[]{x,y});
				double[] lowk = new double[]{corners[0],corners[2]};
				double[] uppk = new double[]{corners[1],corners[3]};				
				Object[] objs = (Object[]) pointsTree.range(lowk,uppk);
				
				List<Temporal> activePoints = new ArrayList<Temporal>();
				for(int i = 0; i < objs.length; ++i){
					activePoints.add( (Temporal) objs[i]);
				}
				double tileWeight = this.getPointsProb(x,y,activePoints);
				if(printWindow){ wc.addData(new double[]{x,y},new double[]{tileWeight}); }
				totalWeight += tileWeight;
			}
		}
		if(printWindow){ wc.plot(); }
		double maxWeight = count * Init.CoverageWindow.SPACE_WEIGHT;
		//double maxWeight = ((x1 - x2) / xgridGranularity) * ((y1 - y2) / ygridGranularity) * Init.SPACE_WEIGHT;
		double windowProb = totalWeight / maxWeight * 100;
		Init.DebugPrint("maxWeight: " + maxWeight,3);
		Init.DebugPrint("totalWeight: " + totalWeight,3);
		Init.DebugPrint("loopcount: " + count, 3);
		Init.DebugPrint("#iterations: " + iterations,3);
		Init.DebugPrint("#recurse iterations: " + recurseIterations,3);
		Init.DebugPrint("Window Prob: " + windowProb,3);
		return windowProb;
	}
	
	
	/*
	 * Optimized print window algorithm with ARRAY TREES ranges and nearby trimming
	 */
	public double calcWindowOptArr(boolean printWindow){
		if(points.size() == 0) return 0.0;
		
		ArrayTree pointsTree = new ArrayTree(points);
		WindowChart wc = new WindowChart("Window"); 
		
		double x1 = this.lowBound[0];
		double x2 = this.upperBound[0];
		double y1 = this.lowBound[1];
		double y2 = this.upperBound[1];

		this.iterations = 0;
		this.recurseIterations = 0;
		
		int count = 0;
		double totalWeight = 0.0;
		for(double x = x1; x < x2; x = x + this.xgridGranularity){
			for(double y = y1; y < y2; y = y + this.ygridGranularity){
				count++;
				double[] corners = new double[4];
				corners = GPSLib.getSpaceBoundQuick(new double[]{x,y},new double[]{x,y});
				double[] lowk = new double[]{corners[0],corners[2]};
				double[] uppk = new double[]{corners[1],corners[3]};
				
				List<Temporal> activePoints = pointsTree.range(lowk,uppk);
				double tileWeight = this.getPointsProb(x,y,activePoints);
				if(printWindow){ wc.addData(new double[]{x,y},new double[]{tileWeight}); }
				totalWeight += tileWeight;
			}
		}
		if(printWindow){ wc.plot(); }
		double maxWeight = count * Init.CoverageWindow.SPACE_WEIGHT;
		//double maxWeight = ((x1 - x2) / xgridGranularity) * ((y1 - y2) / ygridGranularity) * Init.SPACE_WEIGHT;
		double windowProb = totalWeight / maxWeight * 100;
		Init.DebugPrint("maxWeight: " + maxWeight,3);
		Init.DebugPrint("totalWeight: " + totalWeight,3);
		Init.DebugPrint("#iterations: " + iterations,3);
		Init.DebugPrint("#recurse iterations: " + recurseIterations,3);
		Init.DebugPrint("Window Prob: " + windowProb,3);
		return windowProb;
	}
	
	public double getPointsProb(double x, double y, List<Temporal> activePoints){
		double distFromPoint, contribution, tileWeight;
		List<Double> nearby = new ArrayList<Double>();
		tileWeight = 0;
		for(int i = 0; i < activePoints.size(); i++){
			iterations++;
			distFromPoint = GPSLib.getDistanceBetween(activePoints.get(i).getCoords(),new double[]{x,y});
			contribution = (-Init.CoverageWindow.SPACE_WEIGHT / Init.CoverageWindow.SPACE_RADIUS) * distFromPoint + Init.CoverageWindow.SPACE_WEIGHT;
			if(contribution > Init.CoverageWindow.SPACE_TRIM){
				contribution /= 100; //convert to probability so getAggProb function can work properly
				nearby.add(contribution * activePoints.get(i).getTimeRelevance(
																Init.CoverageWindow.CURRENT_TIMESTAMP,
																Init.CoverageWindow.REFERENCE_TIMESTAMP,
																Init.CoverageWindow.TEMPORAL_DECAY));
			}
		}
		
		if(activePoints.size() > 0 && nearby.size() > 0){ //make sure not to add a point with an empty activePoints tree
			double[] aggResults = new double[2];
			aggResults[0] = 0;
			aggResults[1] = 0;
			List<Double> empty = new ArrayList<Double>();
			this.trimNearby(nearby);
			this.getAggProbability(aggResults,empty,nearby);
			recurseIterations += aggResults[1];
			if(aggResults[0] > 0)
				tileWeight = aggResults[0] * 100;
			else
				tileWeight = 0.0;
		}
		
		if(tileWeight > 100){
			Init.DebugPrint("size of nearby: " + nearby.size(), 1);
			Init.DebugPrint("non-opt print overflow of space weight: greater probabilty than possible", 1);
			Init.DebugPrint("tileWeight: " + tileWeight, 1);
		}
		return tileWeight;
	}
	
	//Pre- nearby contains all points in the nearby set
	//Post- nearby contains a trimmed set
	//To-Do, sort nearby set first?
	public void trimNearby(List<Double> nearby){
		int removedNearby = 0;
		Quicksort qs = new Quicksort();
		qs.sort(nearby, 0, nearby.size() - 1);
		while(nearby.size() > Init.CoverageWindow.TRIM_THRESH){
			nearby.remove(0);
		}
	}
	
	/**
	 * Worst algorithm, used for comparison
	 */
	public double calcWindow(boolean printWindow){
		if(points.size() == 0) return 0;
		
		WindowChart wc = new WindowChart("Window");
		
		double x1 = this.lowBound[0];
		double x2 = this.upperBound[0];
		double y1 = this.lowBound[1];
		double y2 = this.upperBound[1];

		int iterations = 0;
		double totalWeight = 0.0;
		int recurseIterations = 0;
		for(double x = x1; x <= x2; x = x + this.xgridGranularity){
			for(double y = y1; y <= y2; y = y + this.ygridGranularity){
				double distFromPoint;
				double tileWeight = 0.0;
				double[] currPoint = new double[2];
				double contribution;
				List<Double> nearby = new ArrayList<Double>();
				for(int i = 0; i < this.points.size(); i++){
					iterations++;
					currPoint[0] = x;
					currPoint[1] = y;
					distFromPoint = GPSLib.getDistanceBetween(this.points.get(i).getCoords(),currPoint);
					contribution = (-Init.CoverageWindow.SPACE_WEIGHT / Init.CoverageWindow.SPACE_RADIUS) * distFromPoint + Init.CoverageWindow.SPACE_WEIGHT;
					
					if(contribution > Init.CoverageWindow.SPACE_TRIM){
						contribution /= 100; //convert to probability so getAggProb function can work properly
						nearby.add(contribution * this.points.get(i).getTimeRelevance(
																			Init.CoverageWindow.CURRENT_TIMESTAMP,
																			Init.CoverageWindow.REFERENCE_TIMESTAMP,
																			Init.CoverageWindow.TEMPORAL_DECAY));
					}
				}
				double[] aggResults = new double[2];
				aggResults[0] = 0;
				aggResults[1] = 0;
				List<Double> empty = new ArrayList<Double>();
				this.trimNearby(nearby);
				this.getAggProbability(aggResults,empty,nearby);
				recurseIterations += aggResults[1];
				if(aggResults[0] > 0)
					tileWeight = aggResults[0] * 100;
				else
					tileWeight = 0.0;
				
				if(printWindow){ wc.addData(currPoint,new double[]{tileWeight}); }
				if(tileWeight > 100){
					Init.DebugPrint("size of nearby: " + nearby.size(), 1);
					Init.DebugPrint("non-opt print overflow of space weight: greater probabilty than possible", 1);
					Init.DebugPrint("tileWeight: " + tileWeight, 1);
				}
				totalWeight += tileWeight;

			}
		}
		if(printWindow){ wc.plot(); }		
		double maxWeight = ((x1 - x2) / xgridGranularity) * ((y1 - y2) / ygridGranularity) * Init.CoverageWindow.SPACE_WEIGHT;
		double windowProb = totalWeight / maxWeight * 100;
		Init.DebugPrint("maxWeight: " + maxWeight,3);
		Init.DebugPrint("totalWeight: " + totalWeight,3);
		Init.DebugPrint("#iterations: " + iterations,3);
		Init.DebugPrint("#recurse iterations: " + recurseIterations,3);
		Init.DebugPrint("Window Prob: " + windowProb,3);
		return windowProb;
	}
	
	public double getAbsoluteValue(double val){
		return (val < 0) ? -val : val;
	}
}
