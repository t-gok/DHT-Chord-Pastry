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

	public void QueueStatus(){
		boolean done = true;
		int d = 0;
		for(int i=0;i<nodeList.size();i++){
			//System.out.print(nodeList.elementAt(i).nodeId+":"+nodeList.elementAt(i).msg.empty()+","+nodeList.elementAt(i).msg.size()+" ");
			//System.out.print(nodeList.elementAt(i).nodeId+":"+nodeList.elementAt(i).msg.empty()+" ");
			done = done && nodeList.elementAt(i).msg.empty();
			if(nodeList.elementAt(i).msg.empty())
				d++;
			// if(!nodeList.elementAt(i).msg.empty()){
			// 	nodeList.elementAt(i).msg.peek().printm();
			// }
		}
		System.out.print(d+"/"+nodeList.size()+" "+done+"\n");	
	}

	public void CreateNode(Vector<String> tokens){
		int nodeId = Integer.parseInt(tokens.elementAt(1));
		int mId = Integer.parseInt(tokens.elementAt(2));
		int x = Integer.parseInt(tokens.elementAt(3));
		int y = Integer.parseInt(tokens.elementAt(4));

		int minId = -1;
		double closestD = Integer.MAX_VALUE;
		for(int i=0;i<nodeList.size();i++){
			double d = Math.pow(x-x_cord.elementAt(i),2) + Math.pow(y-y_cord.elementAt(i),2);
			if(d < closestD){
				minId = nodeList.elementAt(i).nodeId;
				closestD = d;
			}
		}

		Node n = new Node(nodeId,minId,mId,x,y);
		nodeList.addElement(n);
		x_cord.addElement(x);
		y_cord.addElement(y);

		Thread t = new Thread(n,tokens.elementAt(1));
		t.start();
		System.out.println("Created a new node: "+nodeId);

		// try{
		// 	TimeUnit.SECONDS.sleep(1);
		// }
		// catch(Exception e){}

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

    public void KillNode(Vector<String> tokens){
		int nodeId = Integer.parseInt(tokens.elementAt(1));
		int mId = Integer.parseInt(tokens.elementAt(2));
		
        for(int i=0;i<nodeList.size();i++){
            if(nodeList.elementAt(i).nodeId == nodeId){

				Vector<Integer> temp = new Vector<Integer>();
				temp.addAll(nodeList.elementAt(i).lLeaf);
				temp.addAll(nodeList.elementAt(i).rLeaf);
				temp.addAll(nodeList.elementAt(i).nbrs);
				for(int i1=0;i1<8;i1++){
					for(int j=0;j<16;j++){
						if(nodeList.elementAt(i).rTable.elementAt(i1).elementAt(j)!=-1)
							temp.addElement(nodeList.elementAt(i).rTable.elementAt(i1).elementAt(j));
					}
				}

				Set<Integer> hs = new HashSet<Integer>(temp);
				temp.clear();
				temp.addAll(hs);

				for(int i1=0;i1<temp.size();i1++){
					Message m = new Message();
					m.type = 4;
					m.ttl = 10;
					m.srcid = nodeId;
					m.id = mId;
					m.fg = 0;
					sendMessage(m,temp.elementAt(i1));
				}
				nodeList.elementAt(i).msg.clr();
				break;
            }
        }  
		System.out.println("Kill "+nodeId);
    }

	private void sendMessage(Message m, int nodeId){
        for(int i=0;i<nodeList.size();i++){
            if(nodeList.elementAt(i).nodeId == nodeId){
                nodeList.elementAt(i).addMessage(m);
                break;
            }
        }  
    }

}