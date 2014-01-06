package ut.mpc.phenom;

public class LocationWrapper {
	public double longitude;
	public double latitude;
	
	//@standard constructor for coordinate based construction
	public LocationWrapper(double longitude, double latitude){
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public String toString(){
		String result;
		result = String.valueOf(this.longitude);
		result += ", ";
		result += String.valueOf(this.latitude);
		result += "\n";
		return result;
	}
	
	public void printCoordinates(){
		System.out.println("Longitude: " + this.longitude);
		System.out.println("Latitude: " + this.latitude);
	}
	
	public double getLongitude(){
		return this.longitude;
	}
	
	public double getLatitude(){
		return this.latitude;
	}
	
	

}