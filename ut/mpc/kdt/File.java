package ut.mpc.kdt;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class File {
	PrintWriter writer;
	
	public File(String name){
		try {
			this.writer = new PrintWriter(name, "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void write(Double dub){
		writer.print(dub);
	}
	
	public void write(String str){
		writer.print(str);
	}
	
	public void close(){
		writer.close();
	}
	
}
