package ut.mpc.setup;

public class Init {
	public static final double SPACE_WEIGHT = 100; //useful for scaling the magnitude of the computation numbers (good to stay within reasonable bounds to avoid rounding/overflow)
	public static final double SPACE_RADIUS = 1;
	
	//for some reason by increasing this slightly (around .1), the total estimate will actually increase.  Could this be from rounding errors
	//by including low probability points?
	public static final double SPACE_TRIM = .3 * SPACE_WEIGHT; //default to 0 for safest estimate, increase for less accurate but faster estimations
	
	public static final boolean GRID_DEFAULT = false;
	public static final double X_GRID_GRAN = (GRID_DEFAULT) ? SPACE_RADIUS / 10 : .001; //allow fine tuning by setting grid default to off
	public static final double Y_GRID_GRAN = (GRID_DEFAULT) ? SPACE_RADIUS / 10 : .001; //allow fine tuning by setting grid default to off
	
	public static final boolean NORMALIZE_PLOT = false; //generally won't see an effect of temporal decay with this set to true (will simply normalize scale)
	
	//slope of temporal decay;   timerelevance =  timereference / ( decay * timestamp )
	//if this is less than 1, there will be an overflow of space weight (which may be ok)
	public static final double TEMPORAL_DECAY = 2;
	public static long CURRENT_TIMESTAMP = 0; //this is temporary for testing purposes only, eventually this will be a method call to current time
	public static long REFERENCE_TIMESTAMP = 0;
	
	public static double INS_THRESH = 2;
	
	public static final boolean DEBUG_LEVEL1 = true;
	public static final boolean DEBUG_LEVEL2 = true;
	public static final boolean DEBUG_LEVEL3 = true;
	
	//level 1 for unit testing
	//level 2 for other testing
	//level 3 for performance benchmarking
	public static final void DebugPrint(String str, int level){
		if(level == 1 && DEBUG_LEVEL1){
			System.out.println(str);
		} else if(level == 2 && DEBUG_LEVEL2){
			System.out.println(str);
		} else if(level == 3 && DEBUG_LEVEL3){
			System.out.println(str);
		}
	}
}
