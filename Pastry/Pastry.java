import java.io.*;
import java.math.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit; 

public class Pastry{

	public static void main(String[] args) throws IOException{
		
		FileReader fr = new FileReader(args[0]);
		BufferedReader br = new BufferedReader(fr);

		Simulator sim = new Simulator();

		String cmd;
		while((cmd = br.readLine()) != null){
			StringTokenizer st = new StringTokenizer(cmd,",");
			Vector<String> tokens = new Vector<String>();
			while(st.hasMoreTokens())
				tokens.addElement(st.nextToken());

			switch (Integer.parseInt(tokens.elementAt(0)))
			{
				case 0:
					sim.CreateNode(tokens);
					break;
				case 1:
					sim.KillNode(tokens);
					break;
				case 2:
					sim.SearchKey(tokens);
					break;	
				default:
					System.out.println("Unknown command: "+cmd);
			}
		}
		while(true){
			sim.QueueStatus();
			try{
				TimeUnit.SECONDS.sleep(2);
			}
			catch(Exception e){}
			
		}
	}
}