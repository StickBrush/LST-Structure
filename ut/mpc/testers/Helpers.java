package ut.mpc.testers;

public class Helpers {
	private static long timer;
	
	public static void startTimer(){
		timer = System.nanoTime();    
	}
	
	//prints output to screen
	public static long endTimer(boolean print){
		long estimatedTime = System.nanoTime() - timer;
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
	
	public static boolean withinOnePercent(long val1, long val2){
		if(val1 > val2){
			return (1 - (val2 / val1) > .1);
		} else {
			return (1 - (val1 / val2) > .1);
		}
	}
}
