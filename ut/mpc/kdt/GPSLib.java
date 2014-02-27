package ut.mpc.kdt;

import ut.mpc.setup.Init;

public class GPSLib {
	
	public static void main(String[] args){
		GPSLib tester = new GPSLib();
		

		double[] temp = getCoordFromDist(37.75134, -122.39488, 10, 180);
		System.out.println(temp[0]);
		System.out.println(temp[1]);
	}
	
	//Returns in this order: xMin, xMax, yMin, yMax
	public static double[] getSpaceBound(double[] lowk, double[] uppk){
		double tempRet[] = new double[2];
		double spaceBound[] = new double[4];

		if(Init.COORD_TYPE == Init.CoordType.GPS){
			tempRet = GPSLib.getCoordFromDist(lowk[0], lowk[1], Init.SPACE_RADIUS, 270);
			spaceBound[0] = tempRet[0];
			tempRet = GPSLib.getCoordFromDist(uppk[0], lowk[1], Init.SPACE_RADIUS, 90);
			spaceBound[1] = tempRet[0];
			tempRet = GPSLib.getCoordFromDist(uppk[0], lowk[1], Init.SPACE_RADIUS, 180);
			spaceBound[2] = tempRet[1];
			tempRet = GPSLib.getCoordFromDist(uppk[0], uppk[1], Init.SPACE_RADIUS, 0);
			spaceBound[3] = tempRet[1];
		} else if(Init.COORD_TYPE == Init.CoordType.Meters){
			spaceBound[0] = lowk[0] - Init.SPACE_RADIUS;
			spaceBound[1] = uppk[0] + Init.SPACE_RADIUS;
			spaceBound[2] = lowk[1] - Init.SPACE_RADIUS;
			spaceBound[3] = uppk[1] + Init.SPACE_RADIUS;		
		}
		return spaceBound;
	}
	
	public static double[] getSpaceBoundQuick(double[] lowk, double[] uppk){
		double tempRet[] = new double[2];
		double spaceBound[] = new double[4];
		
		if(Init.COORD_TYPE == Init.CoordType.GPS){
			//Possible idea - get accurate distance for first point, find what distance is and use that fixed value
			double paddingY = .009 * Init.SPACE_RADIUS;
			double paddingX = .011 * Init.SPACE_RADIUS;
			spaceBound[0] = lowk[0] - paddingX;
			spaceBound[1] = uppk[0] + paddingX;
			spaceBound[2] = lowk[1] - paddingY;
			spaceBound[3] = uppk[1] + paddingY;
		} else if(Init.COORD_TYPE == Init.CoordType.Meters){
			spaceBound = getSpaceBound(lowk,uppk);	
		}
		return spaceBound;
	}
	
	//latitude in degrees
	//longitude in degrees
	//distance in km
	//bearing in degrees
	//formula from http://www.movable-type.co.uk/scripts/latlong.html
	public static double[] getCoordFromDist(double longitude, double latitude, double distance, double bearing){
		double R = 6371;
		double lat1 = Math.toRadians(latitude);
		double lon1 = Math.toRadians(longitude);
		double brng = Math.toRadians(bearing);
		double lat2 = Math.asin( Math.sin(lat1)*Math.cos(distance/R) + 
	              Math.cos(lat1)*Math.sin(distance/R)*Math.cos(brng) );
		double lon2 = lon1 + Math.atan2(Math.sin(brng)*Math.sin(distance/R)*Math.cos(lat1), 
	                     Math.cos(distance/R)-Math.sin(lat1)*Math.sin(lat2));
		double [] vals = new double[2];
		vals[0] = Math.toDegrees(lon2);
		vals[1] = Math.toDegrees(lat2);
		return vals;
	}
	
	//points in degrees (longitude,latitude)
	//returns -1 if Init.COORD_TYPE is undefined
	public static double getDistanceBetween(double[] p1, double[] p2){
		if(Init.COORD_TYPE == Init.CoordType.GPS){
			double R = 6371;
			double lat1 = Math.toRadians(p1[0]);
			double lat2 = Math.toRadians(p2[0]);
			double long1 = Math.toRadians(p1[1]);
			double long2 = Math.toRadians(p2[1]);
			double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) +
							     Math.cos(lat1) * Math.cos(lat2) *
							     Math.cos(long2 - long1)) * R;
			return d;
		} else if(Init.COORD_TYPE == Init.CoordType.Meters) {
			return Math.sqrt(Math.pow(p1[0] - p2[0],2) + Math.pow(p1[1] - p2[1],2));
		} else {
			return -1;
		}
	}
}
