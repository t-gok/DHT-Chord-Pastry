import java.io.*;
import java.math.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer; 
import java.util.concurrent.TimeUnit;

public class Simulator{

	//Vector<Thread> nodeThreads;

	public Vector<Node> nodeList;
	public Vector<Integer> x_cord;
	public Vector<Integer> y_cord;
	

	public Simulator()
	{
		nodeList = new Vector<Node>();
		x_cord = new Vector<Integer>();
		y_cord = new Vector<Integer>();
	}

	/*public void QueueStatus(){
		boolean done = true;
		for(int i=0;i<nodeList.size();i++){
			//System.out.print(nodeList.elementAt(i).nodeId+":"+nodeList.elementAt(i).msg.empty()+","+nodeList.elementAt(i).msg.size()+" ");
			System.out.print(nodeList.elementAt(i).nodeId+":"+nodeList.elementAt(i).msg.empty()+" ");
			done = done && nodeList.elementAt(i).msg.empty();
			// if(!nodeList.elementAt(i).msg.empty()){
			// 	nodeList.elementAt(i).msg.peek().printm();
			// }
		}
		System.out.print(" "+done+"\n");

		for(int i=0;i<nodeList.size();i++){
			System.out.print(nodeList.elementAt(i).msg.size()+" ");
		}
		System.out.print("\n");

		//print the finger table of each node //
		for(int i=0;i<nodeList.size();i++)
		{	
			Node n = nodeList.elementAt(i);
			System.out.println("Node ID: "+n.nodeId);
			System.out.println("Successor: "+n.fingers[1].nodeId);
			System.out.println("Predecessor: "+n.predecessor);
			for(int j=1;j<=15;j++){
				System.out.print(n.fingers[j].nodeId+" ");
			}
			System.out.print("\n\n\n");
		}
	}*/

	public void QueueStatus(){
		boolean done = true;
		for(int i=0;i<nodeList.size();i++){
			//System.out.print(nodeList.elementAt(i).nodeId+":"+nodeList.elementAt(i).msg.empty()+","+nodeList.elementAt(i).msg.size()+" ");
			System.out.print(nodeList.elementAt(i).nodeId+":"+nodeList.elementAt(i).msg.size()+" ");
			done = done && nodeList.elementAt(i).msg.empty();
			// if(!nodeList.elementAt(i).msg.empty()){
			// 	nodeList.elementAt(i).msg.peek().printm();
			// }
		}
		System.out.print(" "+done+"\n");
	}

	public void CreateNode(Vector<String> tokens){
		//System.out.println("In create node function");
		int nodeId = Integer.parseInt(tokens.elementAt(1));
		int mId = Integer.parseInt(tokens.elementAt(2));
		int x = Integer.parseInt(tokens.elementAt(3));
		int y = Integer.parseInt(tokens.elementAt(4));

		Vector<Integer> predefined_fingers = new Vector<Integer>();
		for(int i=5;i<tokens.size();i++){
			predefined_fingers.addElement(Integer.parseInt(tokens.elementAt(i)));
			//System.out.print(tokens.elementAt(i)+" ");
		}
		//System.out.print("\n");

		int predecessor = -1;
		if(predefined_fingers.size()!=0)
			predecessor = Integer.parseInt(tokens.elementAt(tokens.size()-1));

		int minId = -1;
		double closestD = Integer.MAX_VALUE;
		for(int i=0;i<nodeList.size();i++){
			double d = Math.pow(x-x_cord.elementAt(i),2) + Math.pow(y-y_cord.elementAt(i),2);
			if(d < closestD){
				minId = nodeList.elementAt(i).nodeId;
				closestD = d;
			}
		}

		Node n;
		if(predefined_fingers.size() !=0)
		{
			n = new Node(nodeId,-1,mId,x,y,predefined_fingers,predecessor);
			nodeList.addElement(n);
			x_cord.addElement(x);
			y_cord.addElement(y);
		}
		else{
			n = new Node(nodeId,1,mId,x,y,predefined_fingers,predecessor);
			nodeList.addElement(n);
			x_cord.addElement(x);
			y_cord.addElement(y);
		}

		Thread t = new Thread(n,tokens.elementAt(1));
		t.start();
		System.out.println("Created a new node: "+nodeId+" "+"1");

		try{
			TimeUnit.SECONDS.sleep(2);
		}
		catch(Exception e){}



	}


	public void SearchKey(Vector<String> tokens){
		// 2, 12, 3, 18     // search 18 at 12
		int nodeId = Integer.parseInt(tokens.elementAt(1));
		int mId = Integer.parseInt(tokens.elementAt(2));
		int searchKey = Integer.parseInt(tokens.elementAt(3));

		Message m = new Message();
		m.type = 1;
		m.ttl = 10;
		m.srcid = nodeId;
		m.searchKey = searchKey;
		m.id = mId;
		m.fg = 0;

		sendMessage(m,nodeId);
		System.out.println("Search "+searchKey+" at "+nodeId);
	}

	private void sendMessage(Message m, int nodeId){
        for(int i=0;i<nodeList.size();i++){
            if(nodeList.elementAt(i).nodeId == nodeId){
                nodeList.elementAt(i).addMessage(m);
                break;
            }
        }  
    }

    public void KillNode(Vector<String> tokens){

    }
}