package ut.mpc.kdt;

public class Demo {

	public static void main(String[] args){
		Demo tm = new Demo();
		for(int i = 0; i < 300; i++){
			tm.testMethod();
		}
	}
	
	protected void testMethod(){

		
		//Debug.startMethodTracing("testmethod");
		long startTime = System.nanoTime();    
		
		for(int i = 0; i < 200; i = i + 2 / 1){
			int j = i * 6;
			testMethod2(i);
		}
		long estimatedTime = System.nanoTime() - startTime;
		//Debug.stopMethodTracing();
		System.out.println("est time: " + estimatedTime);
	}
	
	protected void testMethod2(int i){
		int j = i * 2;
		String str = new String(j + "");
		System.out.println(str);
	}

}
