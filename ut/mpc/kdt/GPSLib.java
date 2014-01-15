package ut.mpc.kdt;

public class GPSLib {
	
	public static void main(String[] args){
		GPSLib tester = new GPSLib();
		
		//37.75134 -122.39488 0 1213084687
		double[] temp = getCoordFromDist(37.75134, -122.39488, 10, 180);
		System.out.println(temp[0]);
		System.out.println(temp[1]);
	}
	
	//latitude in degrees
	//longitude in degrees
	//distance in km
	//bearing in degrees
	//formula from http://www.movable-type.co.uk/scripts/latlong.html
	public static double[] getCoordFromDist(double latitude, double longitude, double distance, double bearing){
		double R = 6371;
		double lat1 = Math.toRadians(latitude);
		double lon1 = Math.toRadians(longitude);
		double brng = Math.toRadians(bearing);
		double lat2 = Math.asin( Math.sin(lat1)*Math.cos(distance/R) + 
	              Math.cos(lat1)*Math.sin(distance/R)*Math.cos(brng) );
		double lon2 = lon1 + Math.atan2(Math.sin(brng)*Math.sin(distance/R)*Math.cos(lat1), 
	                     Math.cos(distance/R)-Math.sin(lat1)*Math.sin(lat2));
		double [] vals = new double[2];
		vals[0] = Math.toDegrees(lat2);
		vals[1] = Math.toDegrees(lon2);
		return vals;
	}
	
	//points in degrees (longitude,latitude)
	public static double getDistanceBetween(double[] p1, double[] p2){
		
		double R = 6371;
		double lat1 = Math.toRadians(p1[0]);
		double lat2 = Math.toRadians(p2[0]);
		double long1 = Math.toRadians(p1[1]);
		double long2 = Math.toRadians(p2[1]);
		double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) +
						     Math.cos(lat1) * Math.cos(lat2) *
						     Math.cos(long2 - long1)) * R;
		return d;
	}
}
