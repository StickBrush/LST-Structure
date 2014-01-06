package ut.mpc.kdt;

import java.util.ArrayList;

import org.omg.CORBA.INITIALIZE;

import ut.mpc.setup.Init;

/**
 * @author nathanielwendt
 *
 */
public class WindowCompute {
	private double xgridGranularity = Init.X_GRID_GRAN;
	private double ygridGranularity = Init.Y_GRID_GRAN;
	private double[] lowBound;
	private double[] upperBound;
	private ArrayList<Temporal> points = new ArrayList<Temporal>();
	
	
	public static void main(String[] args){
		double[] lowBound = new double[2];
		double[] upperBound = new double[2];
		ArrayList<Temporal> points = new ArrayList<Temporal>();
		
		lowBound[0] = 2; //x1
		upperBound[0] = 5; //x2
		lowBound[1] = 0; //y1
		upperBound[1] = 8; //y2
		
		Temporal temp = new Temporal(20,2,3);
		points.add(temp);
		temp = new Temporal(30,4,7);
		points.add(temp);
		temp = new Temporal(100,5,1);
		points.add(temp);
		temp = new Temporal(100,3,3);
		points.add(temp);
	
		WindowCompute wc = new WindowCompute(lowBound,upperBound,points);
		System.out.println("Window Prob Test -- Begin");
		System.out.println(wc.getWindowProbability());
		System.out.println("Window Prob Test  --Done");
		System.out.println("\nWindow Prob Test -- Begin OPT");
		System.out.println(wc.getWindowProbabilityOpt());
		System.out.println("Window Prob Test  --Done OPT");
		
		/*
		Double dub = new Double(.1);
		ArrayList<Double> tempt = new ArrayList<Double>();
		tempt.add(dub);
		dub = new Double(.1);
		tempt.add(dub);
		dub = new Double(.05);
		tempt.add(dub);
		ArrayList<Double> empty = new ArrayList<Double>();
		double[] sum = new double[2];
		sum[0] = 0.0;
		sum[1] = 0.0;
		wc.getAggProbability(sum,empty,tempt);
		System.out.println("finalsum= " + sum[0]);
		*/
		
	}
	
	//should assume points are within the bounds, for now
	public WindowCompute(double[] lowBound, double[] upperBound, ArrayList<Temporal> points){
		this.lowBound = lowBound;
		this.upperBound = upperBound;
		this.points = points;
	}
	
	//WARNING: untested
	public double getPointProbability(double[] point){
		double tileWeight = 0;
		double contribution;
		double distFromPoint;
		for(int i = 0; i < this.points.size(); i++){
			distFromPoint = WindowCompute.getDistanceBetween(this.points.get(i).getCoords(),point);
			contribution = (-Init.SPACE_WEIGHT / Init.SPACE_RADIUS) * distFromPoint + Init.SPACE_WEIGHT;
			tileWeight += contribution * this.points.get(i).getTimeRelevance(Init.CURRENT_TIMESTAMP,Init.REFERENCE_TIMESTAMP,Init.TEMPORAL_DECAY);
		}
		if(tileWeight > Init.SPACE_WEIGHT)
			System.out.println("getpoint overflow of space weight: greater probabilty than possible");
		return tileWeight / Init.SPACE_WEIGHT;
	}

	/**
	 * Not optimal for sparse tree coverages.  Will still compute for every tile regardless of how relevant it is
	 * @returns the single double value representing the coverage probability of the window 
	 */
	public double getWindowProbability(){
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
				ArrayList<Double> nearby = new ArrayList<Double>();
				for(int i = 0; i < this.points.size(); i++){
					iterations++;
					currPoint[0] = x;
					currPoint[1] = y;
					distFromPoint = WindowCompute.getDistanceBetween(this.points.get(i).getCoords(),currPoint);
					contribution = (-Init.SPACE_WEIGHT / Init.SPACE_RADIUS) * distFromPoint + Init.SPACE_WEIGHT;
					
					if(contribution > Init.SPACE_TRIM){
						nearby.add(contribution * this.points.get(i).getTimeRelevance(Init.CURRENT_TIMESTAMP,Init.REFERENCE_TIMESTAMP,Init.TEMPORAL_DECAY));
					}
				}
				double[] aggResults = new double[2];
				aggResults[0] = 0;
				aggResults[1] = 0;
				ArrayList<Double> empty = new ArrayList<Double>();
				this.getAggProbability(aggResults,empty,nearby);
				recurseIterations += aggResults[1];
				if(aggResults[0] > 0)
					tileWeight = aggResults[0];
				else
					tileWeight = 0.0;
				if(tileWeight > Init.SPACE_WEIGHT){
					Init.DebugPrint("non-opt overflow of space weight: greater probabilty than possible", 1);
					Init.DebugPrint("tileWeight: " + tileWeight, 1);
				}
				totalWeight += tileWeight;
			}
		}
		
		double maxWeight = ((x1 - x2) / xgridGranularity) * ((y1 - y2) / ygridGranularity) * Init.SPACE_WEIGHT;
		Init.DebugPrint("maxWeight: " + maxWeight, 1);
		Init.DebugPrint("#iterations: " + iterations, 1);
		Init.DebugPrint("Recurse Iterations: " + recurseIterations, 1);
		return (totalWeight / maxWeight) * 100;
	}
	
	
	/**
	 * Uses the inclusion-exclusion principle to determine the aggregate probability of points
	 * Each possible combination of points is generated and summed or subtracted according to the incl-excl principle
	 * @param sum - store the result in sum[0]
	 * @param active - active list of points, pass in an empty ArrayList
	 * @param rest - remaining list of points, pass in the list of points to be computed
	 */
	public void getAggProbability(double[] sum, ArrayList<Double> active, ArrayList<Double> rest){
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
			ArrayList<Double> next1 = new ArrayList<Double>(active);
			ArrayList<Double> next2 = new ArrayList<Double>(rest);
			ArrayList<Double> next3 = new ArrayList<Double>(active);
			next1.add(rest.get(0));
			next2.remove(0);

			//recursively call subsets
			this.getAggProbability(sum,next1,next2);
			this.getAggProbability(sum,next3,next2);
		}
	}
	
	/**
	 * Warning: This function does not currently include proper probability aggregates
	 * @return
	 */
	public double getWindowProbabilityOpt(){
		double x1 = this.lowBound[0];
		double x2 = this.upperBound[0];
		double y1 = this.lowBound[1];
		double y2 = this.upperBound[1];
		
		double[] currPoint = new double[2];
		double distFromPoint;
		double contribution;
		double totalWeight = 0.0;
		int iterations = 0;
		for(int i = 0; i < this.points.size(); i++){
			for(double x = x1; x <= x2 && x <= this.points.get(i).getXCoord() + Init.SPACE_RADIUS; x = x + this.xgridGranularity){
				for(double y = y1; y <= y2 && y <= this.points.get(i).getYCoord() + Init.SPACE_RADIUS; y = y + this.ygridGranularity){
					iterations++;
					currPoint[0] = x;
					currPoint[1] = y;
					distFromPoint = WindowCompute.getDistanceBetween(this.points.get(i).getCoords(),currPoint);
					contribution = (-Init.SPACE_WEIGHT / Init.SPACE_RADIUS) * distFromPoint + Init.SPACE_WEIGHT;
					if(contribution > 0)
						totalWeight += contribution * this.points.get(i).getTimeRelevance(Init.CURRENT_TIMESTAMP,Init.REFERENCE_TIMESTAMP,Init.TEMPORAL_DECAY);
				}
			}
		}
		double maxWeight = ((x1 - x2) / xgridGranularity) * ((y1 - y2) / ygridGranularity) * Init.SPACE_WEIGHT;
		System.out.println("maxWeight: " + maxWeight);
		System.out.println("totalWeight: " + totalWeight);
		System.out.println("#iterations: " + iterations);
		return (totalWeight / maxWeight) * 100;
	}
	
	
	
	/**
	 * Warning: This function does not include correct probability aggregates
	 */
	public void printWindow(){
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
				ArrayList<Double> nearby = new ArrayList<Double>();
				for(int i = 0; i < this.points.size(); i++){
					iterations++;
					currPoint[0] = x;
					currPoint[1] = y;
					distFromPoint = WindowCompute.getDistanceBetween(this.points.get(i).getCoords(),currPoint);
					contribution = (-Init.SPACE_WEIGHT / Init.SPACE_RADIUS) * distFromPoint + Init.SPACE_WEIGHT;
					
					if(contribution > Init.SPACE_TRIM){
						contribution /= 100; //convert to probabilty so getAggProb function can work properly
						nearby.add(contribution * this.points.get(i).getTimeRelevance(Init.CURRENT_TIMESTAMP,Init.REFERENCE_TIMESTAMP,Init.TEMPORAL_DECAY));
					}
				}
				double[] aggResults = new double[2];
				aggResults[0] = 0;
				aggResults[1] = 0;
				ArrayList<Double> empty = new ArrayList<Double>();
				this.getAggProbability(aggResults,empty,nearby);
				recurseIterations += aggResults[1];
				if(aggResults[0] > 0)
					tileWeight = aggResults[0] * 100;
				else
					tileWeight = 0.0;
				
				wc.addData(currPoint,new double[]{tileWeight});
				if(tileWeight > Init.SPACE_WEIGHT){
					Init.DebugPrint("size of nearby: " + nearby.size(), 1);
					Init.DebugPrint("non-opt print overflow of space weight: greater probabilty than possible", 1);
					Init.DebugPrint("tileWeight: " + tileWeight, 1);
				}
				totalWeight += tileWeight;

			}
		}
		wc.plot();
		
		/*
		for(double x = x1; x <= x2; x = x + this.xgridGranularity){
			for(double y = y1; y <= y2; y = y + this.ygridGranularity){
				double distFromPoint;
				double tileWeight = 0.0;
				double[] currPoint = new double[2];
				double contribution;
				for(int i = 0; i < this.points.size(); i++){
					currPoint[0] = x;
					currPoint[1] = y;
					distFromPoint = WindowCompute.getDistanceBetween(this.points.get(i).getCoords(),currPoint);
					contribution = (-Init.SPACE_WEIGHT / Init.SPACE_RADIUS) * distFromPoint + Init.SPACE_WEIGHT;
					if(contribution > 0)
						tileWeight += contribution * this.points.get(i).getTimeRelevance(Init.CURRENT_TIMESTAMP,Init.TEMPORAL_DECAY);
				}
				wc.addData(currPoint,new double[]{tileWeight});
				
				if(tileWeight > Init.SPACE_WEIGHT)
					System.out.println("overflow of space weight: greater probabilty than possible");
			}
		}
		wc.plot();
		*/
	}
	
	public static double getDistanceBetween(double[] p1, double[] p2){
		double xDiff = p1[0] - p2[0];
		double yDiff = p1[1] - p2[1];
		return Math.pow((Math.pow(xDiff, 2) + Math.pow(yDiff, 2)),0.5); //To-Do! don't return actual distance, save expensive divide
	}
	
	public double getAbsoluteValue(double val){
		return (val < 0) ? -val : val;
	}
}
