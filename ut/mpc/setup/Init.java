package ut.mpc.setup;

public class Init {
	public enum CoordType{GPS,Meters};
	
	//To-Do make private data members with getters and setters
	public static CoordType COORD_TYPE = CoordType.GPS;
	public static double SPACE_WEIGHT = 100; //useful for scaling the magnitude of the computation numbers (good to stay within reasonable bounds to avoid rounding/overflow)
	public static double SPACE_RADIUS = 1;
	
	//for some reason by increasing this slightly (around .1), the total estimate will actually increase.  Could this be from rounding errors
	//by including low probability points?
	//SPACE_TRIM is almost made obsolete by trimNearby.  SPACE_TRIM is not based on percentage of current nearby points and is more of
	//a naive point limiting mechanism.  However, Using space trim may limit the number of entries that trimNearby must sort before it does its limitations.
	public static double SPACE_TRIM = .3 * SPACE_WEIGHT; //default to 0 for safest estimate, increase for less accurate but faster estimations
	public static int TRIM_THRESH = 10;
	
	public static boolean GRID_DEFAULT = false;
	public static double X_GRID_GRAN = (GRID_DEFAULT) ? SPACE_RADIUS / 10 : .001; //allow fine tuning by setting grid default to off
	public static double Y_GRID_GRAN = (GRID_DEFAULT) ? SPACE_RADIUS / 10 : .001; //allow fine tuning by setting grid default to off
	
	public static boolean NORMALIZE_PLOT = false; //generally won't see an effect of temporal decay with this set to true (will simply normalize scale)
	
	//slope of temporal decay;   timerelevance =  timereference / ( decay * timestamp )
	//if this is less than 1, there will be an overflow of space weight (which may be ok)
	public static double TEMPORAL_DECAY = 2;
	public static long CURRENT_TIMESTAMP = 0; //this is temporary for testing purposes only, eventually this will be a method call to current time
	public static long REFERENCE_TIMESTAMP = 0;
	
	public static double INS_THRESH = 80;
	
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
	
	public static  void setMobilityDefaults(){
		COORD_TYPE = CoordType.Meters;
		SPACE_WEIGHT = 100;
		SPACE_RADIUS = 30;
		SPACE_TRIM = .3 * SPACE_WEIGHT; 	
		GRID_DEFAULT = false;
		X_GRID_GRAN = (GRID_DEFAULT) ? SPACE_RADIUS / 10 : 5; 
		Y_GRID_GRAN = (GRID_DEFAULT) ? SPACE_RADIUS / 10 : 5; 	
		NORMALIZE_PLOT = false; 
		TEMPORAL_DECAY = 3;
		CURRENT_TIMESTAMP = 0; 
		REFERENCE_TIMESTAMP = 0;
		INS_THRESH = 80;		
		DEBUG_LEVEL1 = true;
		DEBUG_LEVEL2 = true;
		DEBUG_LEVEL3 = false;
	}
	
	public static  void setCabsDefaults(){
		COORD_TYPE = CoordType.GPS;
		SPACE_WEIGHT = 100;
		SPACE_RADIUS = 1; //km
		SPACE_TRIM = .3 * SPACE_WEIGHT; 	
		GRID_DEFAULT = false;
		X_GRID_GRAN = (GRID_DEFAULT) ? SPACE_RADIUS / 10 : .001; 
		Y_GRID_GRAN = (GRID_DEFAULT) ? SPACE_RADIUS / 10 : .001; 	
		NORMALIZE_PLOT = false; 
		TEMPORAL_DECAY = 3;
		CURRENT_TIMESTAMP = 0; 
		REFERENCE_TIMESTAMP = 0;
		INS_THRESH = 80;		
		DEBUG_LEVEL1 = true;
		DEBUG_LEVEL2 = true;
		DEBUG_LEVEL3 = false;
	}
}
