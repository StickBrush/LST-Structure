package ut.mpc.testers;

public class Helpers {
	private static double timer;
	
	public static void startTimer(){
		timer = System.nanoTime();    
	}
	
	//prints output to screen
	public static double endTimer(boolean print){
		double estimatedTime = System.nanoTime() - timer;
		if(print)
			System.out.println(">>>> execution time: " + estimatedTime / 1000000);
		return estimatedTime;
	}

	//tests for a value and prints out the confirm/fail
	//intended to output a false but not interrupt execution
	public static void prove(String label, boolean assertion){
		if(assertion)
			System.out.println("Prove >> " + label + " PASSED");
		else
			System.out.println("Prove >> " + label + " FAILED");
	}
	
	public static boolean withinThreePercent(double val1, double val2){
		if(val1 > val2){
			return ((1 - (val2 / val1)) < .03);
		} else {
			return ((1 - (val1 / val2)) < .03);
		}
	}
}
