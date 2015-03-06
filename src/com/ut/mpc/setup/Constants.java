package com.ut.mpc.setup;

import com.ut.mpc.utils.GPSLib;
import com.ut.mpc.utils.STPoint;

public class Constants {
	public enum SpatialType{GPS,Meters};
	public static SpatialType SPATIAL_TYPE = SpatialType.GPS;

    public static final float FLOAT_DEC_BUDGE = .00001f; //about 10m
    public static final STPoint POS_FLOAT_BUDGE = new STPoint(FLOAT_DEC_BUDGE, FLOAT_DEC_BUDGE, 5000);
    public static final STPoint NEG_FLOAT_BUDGE = new STPoint(-FLOAT_DEC_BUDGE, -FLOAT_DEC_BUDGE, -5000);

    public static class PoK {
		public static float TOTAL_WEIGHT = 2;
		public static float SPACE_WEIGHT = 1;
        public static float TEMPORAL_WEIGHT = TOTAL_WEIGHT - 1;

        public static float SPACE_RADIUS = 1f;
        public static float TEMPORAL_RADIUS = 1;

        public static int TRIM_THRESH = 10;
        public static boolean GRID_DEFAULT = true; //default is grid size == radius size, GRID_FACTOR == 1
        public static float GRID_FACTOR = 2; // number of grids per radius, should never be less than 1

        public static float LONG_KM_EST = .009f;
        public static float LAT_KM_EST = .001f;

        //TODO: add SPACE-TEMPORAL Trim so nearby doesn't need to trim if they are insignificant points

        public static void updateConfig(STPoint refPoint){
            if(SPATIAL_TYPE == SpatialType.GPS){
                X_RADIUS = GPSLib.longOffsetFromDistance(refPoint, PoK.SPACE_RADIUS);
                Y_RADIUS = GPSLib.latOffsetFromDistance(refPoint, PoK.SPACE_RADIUS);
            } else {
                X_RADIUS = PoK.SPACE_RADIUS;
                Y_RADIUS = PoK.SPACE_RADIUS;
            }
            T_RADIUS = PoK.TEMPORAL_RADIUS;

            X_GRID_GRAN  = (PoK.GRID_DEFAULT) ? X_RADIUS : X_RADIUS / PoK.GRID_FACTOR;
            Y_GRID_GRAN = (PoK.GRID_DEFAULT) ? Y_RADIUS : Y_RADIUS / PoK.GRID_FACTOR;
            T_GRID_GRAN = (PoK.GRID_DEFAULT) ? T_RADIUS : T_RADIUS / PoK.GRID_FACTOR;

            SPACE_DECAY = PoK.SPACE_WEIGHT / PoK.SPACE_RADIUS;
            TEMP_DECAY = PoK.TEMPORAL_WEIGHT / PoK.TEMPORAL_RADIUS;
        }
        public static float X_RADIUS, Y_RADIUS, T_RADIUS;
        public static float X_GRID_GRAN, Y_GRID_GRAN, T_GRID_GRAN;
        public static float SPACE_DECAY, TEMP_DECAY;

	}
	
	public static class SmartInsert{
		public static double INS_THRESH = .8;
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

//	/*
//	 * Defaults configured for the Crawded Mobi Data Set
//	 * Distance is in meters
//	 */
//	public static  void setMobilityDefaults(){
//		SPATIAL_TYPE = SpatialType.Meters;
//		PoK.SPACE_WEIGHT = 100;
//		PoK.SPACE_RADIUS = 30;
//        PoK.TEMPORAL_RADIUS = 500;
//		PoK.GRID_DEFAULT = false;
//		PoK.X_GRID_GRAN = (PoK.GRID_DEFAULT) ? PoK.SPACE_RADIUS / 10 : 5;
//		PoK.Y_GRID_GRAN = (PoK.GRID_DEFAULT) ? PoK.SPACE_RADIUS / 10 : 5;
//		//PoK.NORMALIZE_PLOT = false;
//		PoK.TEMPORAL_DECAY = 8;
//		//PoK.CURRENT_TIMESTAMP = 0;
//		//PoK.REFERENCE_TIMESTAMP = 0;
//		SmartInsert.INS_THRESH = 80;
//		DEBUG_LEVEL1 = true;
//		DEBUG_LEVEL2 = true;
//		DEBUG_LEVEL3 = false;
//	}
//
//	/*
//	 * Defaults configured for the Cabspotting MObility Data Set
//	 * 	Distance is in kilometers
//	 */
//	public static  void setCabsDefaults(){
//		SPATIAL_TYPE = SpatialType.GPS;
//
//        PoK.SPACE_WEIGHT = 1f;
//        PoK.TEMPORAL_WEIGHT = 1f;
//        PoK.TOTAL_WEIGHT = 2f;
//        PoK.SPACE_RADIUS = .001f;
//        PoK.TEMPORAL_RADIUS = 1000 * 60 * 5f;
//        PoK.SPACE_DECAY = PoK.SPACE_WEIGHT / (PoK.SPACE_RADIUS);
//        PoK.TEMPORAL_DECAY = PoK.TEMPORAL_WEIGHT / (PoK.TEMPORAL_RADIUS);
//        //PoK.SPACE_RADIUS = .0035f;
//        //PoK.TEMPORAL_RADIUS = 1000 * 60 * 20;
//
//		PoK.GRID_DEFAULT = false;
//		PoK.X_GRID_GRAN = (PoK.GRID_DEFAULT) ? PoK.SPACE_RADIUS / 10 : .001f;
//		PoK.Y_GRID_GRAN = (PoK.GRID_DEFAULT) ? PoK.SPACE_RADIUS / 10 : .001f;
//	}
	
}
