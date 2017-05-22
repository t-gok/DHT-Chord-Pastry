import java.io.*;
import java.util.*;
import java.lang.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class Node implements Runnable{
    /**
     * Each node has its own message queue. Create thread for each node
     */

    public int nodeId;// Unique node id for each node
    private static Vector<Node> nodeList = new Vector<Node>();
    public static int noOfNodes = 0;
    public static HashMap<Integer,Integer> nodeToIndex = new HashMap<Integer,Integer>();
    public int m = 15;
    private int capacity = 100; 
    public MessageQueue<Message> msg= new MessageQueue<Message>(capacity);
    public int state; //0 - initializing, 1 - UP, 2- DOWN
    private FileWriter log=null;
	private	BufferedWriter bw = null;
	private	FileWriter fw = null;
    public int x,y;
    public Finger[] fingers;
    public int predecessor;
    Lock stable;




	public void log(String content){
        try{
			fw = new FileWriter(Integer.toString(nodeId)+".log",true);
			bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
			fw.close();
		}
		catch(Exception e){}
	}
	public void logm(String s, Message m, int des){
		String content = s+","+m.id+","+m.srcid+","+des+","+m.ttl+","+m.type+","+m.searchKey+","+m.fg+"\n";
		log(content);
	}

	public static synchronized void incrementCount(){
		noOfNodes = noOfNodes + 1;
	}


    public Node(int nodeid,int closestGNodeId,int mid,int xc,int yc,Vector<Integer> predefined_fingers,int predefined_predecessor){
        this.nodeId=nodeid;
        x = xc;
        y = yc;
        state = 0;
        fingers = new Finger[m+1];//2**32 nodes at max 
        //nodeToIndex = new HashMap<Integer,Integer>();
        stable = new ReentrantLock();

         // add the necessary arguments for a node in the constructor
        if(true){ // not first node
            Message m = new Message();
            m.type = 0;
            m.id = mid;
            m.srcid = closestGNodeId;
            m.ttl = 10;
            msg.add(m);
            state = 0;
        }
        nodeToIndex.put(nodeId,nodeList.size());
        nodeList.addElement(this);

        

        //since the finger index starts from 1 add a dummy finger at the starting
        Finger f = new Finger();
        f.start = -1;
        f.end = -1;
        f.nodeId = -1;
        fingers[0] = f;
        //fingers.addElement(f);

        for(int i=1;i<=m;i++){
        	f = new Finger();
    		f.start = (nodeId + (int)Math.pow(2,i-1))%((int)Math.pow(2,m));
    		f.end = (nodeId + (int)Math.pow(2,i) - 1)%((int)Math.pow(2,m));

    		if(predefined_fingers.size() != 0)
    		{
    			f.nodeId = predefined_fingers.elementAt(i-1);
    		}

        	fingers[i] = f;
        }

        predecessor = predefined_predecessor;

        log(Integer.toString(nodeId)+" node created\n");
	}

	// BASIC FUNCTIONS REQUIRED FOR THE FUNCTIONING OF CHORD //
	public int findSuccessor(int id,int mid){
		log("In findSuccessor function "+id+"\n");
		/*if(noOfNodes == 1){
			return nodeList.elementAt(0).nodeId;
		}*/
		int n = findPredecessor(id,mid);
		//log("predecessor: "+n+"\n");
		Node n1 = nodeList.elementAt(nodeToIndex.get(n));
		log(mid+":"+n1.fingers[1].nodeId+"\n");
		return n1.fingers[1].nodeId; // return succeessor of n1
	}

	public boolean inRange(int key,int lower_bound,int higher_bound){
		int n1 = (higher_bound - key)%((int)Math.pow(2,m));
		int n2 = (key - lower_bound)%((int) Math.pow(2,m));
		int n3 = (higher_bound - lower_bound)%((int) Math.pow(2,m));

		
		if(n1<0)
			n1 += (int) Math.pow(2,m);
		if(n2<0)
			n2 += (int) Math.pow(2,m);
		if(n3<0)
			n3 += (int) Math.pow(2,m);

		//log("In range function "+n1+" "+n2+" "+n3);
		if((n1 + n2) == n3){
		//	log(" true\n");
			return true;
		}
		else{
		//	log(" false\n");
			return false;
		}
	}

	public int findPredecessor(int id,int mid){
		log("In findPredecessor function\n");
		/*if(noOfNodes == 1){
			return nodeList.elementAt(0).nodeId;
		}*/
		int ndash = nodeId;
		Node ndash_node = nodeList.elementAt(nodeToIndex.get(ndash));

		log(mid+":"+ndash+"\n");
		/*int lower_bound = ndash_node.nodeId;
		int higher_bound = ndash_node.fingers[1].nodeId;

		if(higher_bound < lower_bound)
			higher_bound = higher_bound + (int) Math.pow(2,m);*/



		while(!(inRange(id,ndash,ndash_node.fingers[1].nodeId) && id!=nodeId)){
			int ndash1 = ndash_node.closestPreceedingFinger(id);
			/*if(ndash == ndash1){
				return ndash;
			}
			else
				ndash = ndash1;*/
			log(mid+":"+ndash1+"\n");
			ndash = ndash1;	
			ndash_node = nodeList.elementAt(nodeToIndex.get(ndash));
			
			/*lower_bound = ndash_node.nodeId;
			higher_bound = ndash_node.fingers[1].nodeId;

			if(higher_bound < lower_bound)
				higher_bound = higher_bound + (int) Math.pow(2,m);*/
		}
		return ndash_node.nodeId;
	}

	public int closestPreceedingFinger(int id){
		log("In closestPreceedingFinger function\n");
		/*if(noOfNodes == 1){
			return nodeList.elementAt(0).nodeId;
		}*/
		/*int lower_bound = nodeId;
			int higher_bound = id;
			if(higher_bound < lower_bound)
				higher_bound = higher_bound + (int) Math.pow(2,m);*/
		for(int i=m;i>=1;i--){
			if(inRange(fingers[i].nodeId,nodeId,id) && fingers[i].nodeId != nodeId && fingers[1].nodeId != id)
				return fingers[i].nodeId;
		}
		return nodeId;
	}


	public synchronized void join(int closestGNodeId){
		log("In join function "+closestGNodeId+"\n");

		if(closestGNodeId != -1)
		{	
			//acquire lock on that node //
			Node n = nodeList.elementAt(nodeToIndex.get(closestGNodeId));
			//acquire all locks
			while(n.state == 0);
			stable.lock();
			n.stable.lock();
			
			initFingerTable(closestGNodeId);
			updateOthers();
			//nodeList.elementAt(0).stabilize();
			incrementCount(); // noOfNodes = noOfNodes + 1
			
			//n.stabilize();
			//n.fix_fingers();

			/*for(int i=1;i<=m;i++){
				int lower_bound = n.fingers[i].start;
				int higher_bound = n.fingers[i].nodeId;
				if(higher_bound <= lower_bound)
					higher_bound += (int) Math.pow(2,m);

				if(nodeId>=lower_bound && nodeId<higher_bound)
					n.fingers[i].nodeId = nodeId;
			}*/

			//stabilize();
			//fix_fingers();
			n.stable.unlock();
			stable.unlock();
			
		}
		/*else // This is the only node in the system
		{	
			stable.lock();
			for(int i=1;i<=m;i++){
				fingers[i].nodeId = nodeId;
			}
			fingers[1].nodeId = nodeId;
			predecessor = nodeId;
			incrementCount();
			stable.unlock();
		}*/
		state = 1;
		log("Finished Joining\n");

		
	}

	public void initFingerTable(int ndash){
		log("In init finger table function\n");
		Node ndash_node = nodeList.elementAt(nodeToIndex.get(ndash));
		fingers[1].nodeId = ndash_node.findSuccessor(fingers[1].start,-1);
		//log("successor: "+fingers[1].nodeId+"\n");
		//log("fingers[1].start: "+fingers[1].start+"\n");
		//successor = fingers[1].nodeId;
		Node successor_node = nodeList.elementAt(nodeToIndex.get(fingers[1].nodeId));
		predecessor = successor_node.predecessor;
		Node predecessor_node = nodeList.elementAt(nodeToIndex.get(predecessor));
		successor_node.predecessor = nodeId;
		predecessor_node.fingers[1].nodeId = nodeId;

		for(int i=1;i<=m-1;i++){
			/*int lower_bound = nodeId;
			int higher_bound = fingers[i].nodeId;
			if(higher_bound < lower_bound)
				higher_bound += (int)Math.pow(2,m);*/

			if(inRange(fingers[i+1].start,nodeId,fingers[i].nodeId) && (fingers[i+1].start != fingers[i].nodeId))
			{
				fingers[i+1].nodeId = fingers[i].nodeId;
			}
			else{
				fingers[i+1].nodeId = ndash_node.findSuccessor(fingers[i+1].start,-1);
				// If our node is in between the succesor and fingrs[i+1].start then our node is the successor

				/*lower_bound = fingers[i+1].start;
				higher_bound = fingers[i+1].nodeId;
				if(higher_bound <= lower_bound)
					higher_bound += (int) Math.pow(2,m);*/

				/*if(nodeId>=lower_bound && nodeId<higher_bound)
					fingers[i+1].nodeId = nodeId;*/
			}
		}

		log("PRINTING FINGERS\n");
		for(int i=1;i<=m;i++){
			log(fingers[i].nodeId+" ");
		}
		log("\n");

	}

	public void updateOthers(){
		log("In updateOthers fucntion\n");
		for(int i=1;i<=m;i++){
			int pred = findPredecessor((nodeId - (int)(Math.pow(2,i-1)))%((int)Math.pow(2,m)),-1);
			//log("Predecessor: "+pred+"\n");
			if(pred < 0)
				pred = pred + (int) Math.pow(2,m);
			Node pred_node = nodeList.elementAt(nodeToIndex.get(pred));
			pred_node.updateFingerTable(nodeId,i);
		}
	}

	public void updateFingerTable(int n,int i){
		log("In updateFingerTable function "+n+" "+i+"\n");
		/*int lower_bound = nodeId;
		int higher_bound = fingers[i].nodeId;
		if(higher_bound <= lower_bound)
			higher_bound = higher_bound + (int)Math.pow(2,m);*/
		if(inRange(n,nodeId,fingers[i].nodeId) && n!=fingers[i].nodeId){
			fingers[i].nodeId = n;
			Node p = nodeList.elementAt(nodeToIndex.get(predecessor));
			while(p.state == 0);
			p.updateFingerTable(n,i);
		}
	}

	public void stabilize(){
		log("In stabilize function\n");
		Node successor_node = nodeList.elementAt(nodeToIndex.get(fingers[1].nodeId));
		int x = successor_node.predecessor;
		/*int lower_bound = nodeId;
		int higher_bound = fingers[1].nodeId;
		if(higher_bound <= lower_bound)
			higher_bound = higher_bound + (int)Math.pow(2,m);*/
		if(inRange(x,nodeId,fingers[1].nodeId) && x!=nodeId && x!=fingers[1].nodeId){
			fingers[1].nodeId = x;
		}
		while(successor_node.state == 0);
		successor_node.notify(nodeId);
	}

	public void notify(int ndash){
		log("In notify fuinction\n");
		/*int lower_bound = predecessor;
		int higher_bound = nodeId;
		if(higher_bound <= lower_bound)
			higher_bound += Math.pow(2,m);*/
		if(predecessor == -1 || (inRange(ndash,predecessor,nodeId) && ndash!=predecessor && ndash!=nodeId))
		{
			predecessor = ndash;
		}
	}

	public void fix_fingers(){
		// Random r = new Random();
		// int Low = 10;
		// int High = 100;
		// int Result = r.nextInt(High-Low) + Low;
		//Random r = new Random();
		//int i = r.nextInt(m-1) + 1;
		for(int i=1;i<=m;i++)
			fingers[i].nodeId = findSuccessor(fingers[i].start,-1);
	}

    /**
     *Each thread will perform its function inside run
     */
    public void run(){
        //TODO
		while(true){
			//log("Inside run\n");
			Message m = getNextMessage();
			switch(m.type){
                case 0:
                    FirstMessage(m);
                    break;
				case 1: 
					Search(m);
					break;
				default:
					log("Unknown Message \n");
			}
		}
    }


    public void FirstMessage(Message mssg){
    	log("In FirstMessage function\n");
        int closestGNodeId = mssg.srcid;
        join(closestGNodeId);
        log("join is Complete\n");
       // logm("FirstMessage",mssg,closestGNodeId);

        
    }

    private void Search(Message m){
    	stable.lock();
    	log("In search function "+m.searchKey+"\n");
    	int searchKey = m.searchKey;
    	//if(noOfNodes !=1){
    	int n  = findSuccessor(searchKey,m.id);
    	stable.unlock();
    	log("Search Complete \n");
    }

    public int getNodeListLength(){
        return nodeList.size();
    }
   
    public void addMessage(Message mssg) {
        msg.add(mssg);
    }
   
    /* Blocking Version*/
    private Message getNextMessage(){
        return msg.getMessage();
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

