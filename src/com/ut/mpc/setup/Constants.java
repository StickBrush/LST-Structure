package com.ut.mpc.setup;

public class Constants {
	public enum SpatialType{GPS,Meters};
	public static SpatialType SPATIAL_TYPE = SpatialType.GPS;
	
	public static class CoverageWindow{
		public static float SPACE_WEIGHT = 100; //useful for scaling the magnitude of the computation numbers (good to stay within reasonable bounds to avoid rounding/overflow)
		public static float SPACE_RADIUS = 1;
		public static float TIME_RADIUS = 1;
		//public static float TIME_
		
		//SPACE_TRIM is almost made obsolete by trimNearby.  SPACE_TRIM is not based on percentage of current nearby points and is more of
		//a naive point limiting mechanism.  However, Using space trim may limit the number of entries that trimNearby must sort before it does its limitations.
		public static float SPACE_TRIM = .3f * SPACE_WEIGHT; //default to 0 for safest estimate, increase for less accurate but faster estimations
		public static int TRIM_THRESH = 10;
		
		//slope of temporal decay;   timerelevance =  timereference / ( decay * timestamp )
		//if this is less than 1, there will be an overflow of space weight (which may be ok)
		public static float TEMPORAL_DECAY = 2;
		public static long CURRENT_TIMESTAMP = 0; //this is temporary for testing purposes only, eventually this will be a method call to current time
		public static long REFERENCE_TIMESTAMP = 0;
		
		public static float LONG_KM_EST = .099f;
		public static float LAT_KM_EST = .011f;
		
		public static boolean GRID_DEFAULT = false;
		public static float X_GRID_GRAN = (GRID_DEFAULT) ? SPACE_RADIUS / 2 : .001f; //allow fine tuning by setting grid default to off
		public static float Y_GRID_GRAN = (GRID_DEFAULT) ? SPACE_RADIUS / 2 : .001f; //allow fine tuning by setting grid default to off
		public static float T_GRID_GRAN = (GRID_DEFAULT) ? TIME_RADIUS / 2 : .01f;
		public static int OPT_LEVEL = 1;
		public static boolean PLOT = false;
		public static boolean NORMALIZE_PLOT = false; //generally won't see an effect of temporal decay with this set to true (will simply normalize scale)
	}
	
	public static class SmartInsert{
		public static int INS_THRESH = 80;	
	}
	
	public static boolean DEBUG_LEVEL1 = true;
	public static boolean DEBUG_LEVEL2 = true;
	public static boolean DEBUG_LEVEL3 = false;
	
	//level 1 for unit testing
	//level 2 for other testing
	//level 3 for performance benchmarking
	public static  void DebugPrint(String str, int level){
		if(level == 1 && DEBUG_LEVEL1){
			System.out.println(str);
		} else if(level == 2 && DEBUG_LEVEL2){
			System.out.println(str);
		} else if(level == 3 && DEBUG_LEVEL3){
			System.out.println(str);
		}
	}

	/*
	 * Defaults configured for the Crawded Mobi Data Set
	 * Distance is in meters
	 */
	public static  void setMobilityDefaults(){
		SPATIAL_TYPE = SpatialType.Meters;
		CoverageWindow.SPACE_WEIGHT = 100;
		CoverageWindow.SPACE_RADIUS = 30;
		CoverageWindow.SPACE_TRIM = .3f * CoverageWindow.SPACE_WEIGHT; 	
		CoverageWindow.GRID_DEFAULT = false;
		CoverageWindow.X_GRID_GRAN = (CoverageWindow.GRID_DEFAULT) ? CoverageWindow.SPACE_RADIUS / 10 : 5; 
		CoverageWindow.Y_GRID_GRAN = (CoverageWindow.GRID_DEFAULT) ? CoverageWindow.SPACE_RADIUS / 10 : 5; 	
		CoverageWindow.NORMALIZE_PLOT = false; 
		CoverageWindow.TEMPORAL_DECAY = 8;
		CoverageWindow.CURRENT_TIMESTAMP = 0; 
		CoverageWindow.REFERENCE_TIMESTAMP = 0;
		SmartInsert.INS_THRESH = 80;		
		DEBUG_LEVEL1 = true;
		DEBUG_LEVEL2 = true;
		DEBUG_LEVEL3 = false;
	}
	
	/*
	 * Defaults configured for the Cabspotting MObility Data Set
	 * 	Distance is in kilometers
	 */
	public static  void setCabsDefaults(){
		SPATIAL_TYPE = SpatialType.GPS;
		CoverageWindow.SPACE_WEIGHT = 100;
		CoverageWindow.SPACE_RADIUS = 1; //km
		CoverageWindow.SPACE_TRIM = .3f * CoverageWindow.SPACE_WEIGHT; 	
		CoverageWindow.GRID_DEFAULT = false;
		CoverageWindow.X_GRID_GRAN = (CoverageWindow.GRID_DEFAULT) ? CoverageWindow.SPACE_RADIUS / 10 : .001f; 
		CoverageWindow.Y_GRID_GRAN = (CoverageWindow.GRID_DEFAULT) ? CoverageWindow.SPACE_RADIUS / 10 : .001f; 	
		CoverageWindow.NORMALIZE_PLOT = false; 
		CoverageWindow.TEMPORAL_DECAY = 3;
		CoverageWindow.CURRENT_TIMESTAMP = 0; 
		CoverageWindow.REFERENCE_TIMESTAMP = 0;
		SmartInsert.INS_THRESH = 80;		
		DEBUG_LEVEL1 = true;
		DEBUG_LEVEL2 = true;
		DEBUG_LEVEL3 = false;
	}
	
}
