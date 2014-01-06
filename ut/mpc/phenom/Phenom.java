package ut.mpc.phenom;

public class Phenom {
	public String phenom;
	public LocationWrapper startPos;
	public LocationWrapper endPos;
	public float bearingTo;
	public float observedDist;

	
	//Use as static Phenom (ie only stores sensing information, no position changes)
	public Phenom(String phenom){
		this.phenom = phenom;
	}
	
	/* Don't use dynamic Phenom for now
	//Use as dynamic Phenom (ie phenomenon is sensed to be moving in a direction)
	public Phenom(String phenom, LocationWrapper startPos, LocationWrapper endPos){
		this.phenom = phenom;
		this.startPos = startPos;
		this.endPos = endPos;
		float[] distance = new float[3];
		startPos.distanceTo(endPos, distance);
		this.observedDist = distance[0];
		this.bearingTo = distance[1];
	}
	*/
	
	//returns the string phenom value with no location/movement information
	public String toStringStatic(){
		return this.phenom;
	}
	
	/*For quality metrics, sensors may be unreliable for
	 * Speed of motion
	 * Distance traveled (< 2-3 meters)
	 * To-Do develop quality heuristic to represent data reliability
	 * return 1-10 measure of quality
	 * */
	public int getQuality(){
		return 0;
	}
}