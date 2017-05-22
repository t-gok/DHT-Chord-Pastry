import java.io.*;
import java.util.*;
import java.lang.*;
import java.util.Collections;
public class Node implements Runnable{
    /**
     * Each node has its own message queue. Create thread for each node
     */

    public int nodeId;// Unique node id for each node
    private static Vector<Node> nodeList = new Vector<Node>();
    private int capacity = 100; 
    public MessageQueue<Message> msg= new MessageQueue<Message>(capacity);
    public int state; //0 - initializing, 1 - UP, 2- DOWN
    public Vector<Boolean> dataReceived;
    public int fId = -1;
    private FileWriter log=null;
	private	BufferedWriter bw = null;
	private	FileWriter fw = null;
    public Vector<Integer> lLeaf;
    public Vector<Integer> rLeaf;
    public Vector<Integer> nbrs;
    public Vector< Vector<Integer>> rTable;
    public int x,y;
//    public HashMap<Integer,Boolean> hb=new HashMap<Integer,Boolean>();

	public void log(String content){
        try{
			fw = new FileWriter("Log/"+Integer.toString(nodeId)+".log",true);
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

    public Node(int nodeid,int closestGNodeId,int mid,int xc,int yc){
        this.nodeId=nodeid;
        x = xc;
        y = yc;
        state = 1;
        rTable = new Vector< Vector<Integer>>();
        lLeaf = new Vector<Integer>();     
        rLeaf = new Vector<Integer>();
        nbrs = new Vector<Integer>();
         // add the necessary arguments for a node in the constructor
        if(closestGNodeId != -1){ // not first node
            Message m = new Message();
            m.type = 0;
            m.id = mid;
            m.srcid = closestGNodeId;
            m.ttl = 10;
            msg.add(m);
            state = 0;
        }
        nodeList.addElement(this);

        //create the routing table       
        for(int i=0;i<8;i++){
            Vector<Integer> temp = new Vector<Integer>();
            for(int j=0;j<16;j++){
                temp.addElement(-1);
            }
            rTable.addElement(temp);
        } 
        dataReceived = new Vector<Boolean>();
        for(int i=0;i<10;i++)
        	dataReceived.addElement(false);

        log(Integer.toString(nodeId)+" node created\n");
	}

    /**
     *Each thread will perform its function inside run
     */
    public void run(){
        //TODO

    	// int count=0;

		while(true){
			// if(count==50){
			// 	// send heartbeat
			// 	Send_heartbeat();
			// }
			// if(count==100){
			// 	count=0;
			// 	// check heartbeat and update table
			// 	Check_hb();
            // }
			// count+=1;

			while(msg.empty()){
				Thread.yield();
			}
			Message m = getNextMessage();
			switch(m.type){
                case 0:
                    FirstMessage(m);
                    break;
				case 1: 
					Search(m);
					break;
				case 2:
					UpdateRT(m);
					break;
				case 3:
					Init(m);
					break;
				case 4: //kill
					//Heartbeat(m);
                    RemoveNode(m);
					break;	
				default:
					log("Unknown Message \n");
			}
		}
    }

    public void RemoveNode(Message m){
        int s = m.srcid;
        lLeaf.remove((Integer)s);
        rLeaf.remove((Integer)s);
        nbrs.remove((Integer)s);
        for(int i=0;i<8;i++){
            for(int j=0;j<16;j++){
                if(rTable.elementAt(i).elementAt(j)==s)
                    rTable.elementAt(i).set(j,-1);
            }
        }
    }


  //   public void Check_hb(){
		// for(int i=0; i<lLeaf.size();i++){
		// 	if(hb.get(lLeaf.elementAt(i)))
		// 		continue;
		// 	else{
		// 		lLeaf.remove(i);
		// 		i--;
		// 	}
		// }
		// for(int i=0; i<rLeaf.size();i++){
		// 	if(hb.get(rLeaf.elementAt(i)))
		// 		continue;
		// 	else{
		// 		rLeaf.remove(i);
		// 		i--;
		// 	}
		// }
		// for(int i=0; i<nbrs.size();i++){
		// 	if(hb.get(nbrs.elementAt(i)))
		// 		continue;
		// 	else{
		// 		nbrs.remove(i);
		// 		i--;
		// 	}
		// }
		// for(int i=0;i<8;i++){
		// 	for(int j=0;j<16;j++){
		// 		if(hb.get(rTable.elementAt(i).elementAt(j)))
		// 			continue;
		// 		else{
		// 			rTable.elementAt(i).set(j,-1);
		// 		}
		// 	}
		// }
  //   	for(Map.Entry m:hb.entrySet()){  
		// 	hb.put(m.getKey(),false);			
		// }
  //   }

  //   public void Heartbeat(Message m){
  //   	hb.put(m.srcid,true);
  //   }

  //   public void Send_heartbeat(){
		// Vector<Integer> temp = new Vector<Integer>();
		// temp.addAll(lLeaf);
		// temp.addAll(rLeaf);
		// temp.addAll(nbrs);
		// for(int i=0;i<8;i++){
		// 	for(int j=0;j<16;j++){
		// 		if(rTable.elementAt(i).elementAt(j)!=-1)
		// 			temp.addElement(rTable.elementAt(i).elementAt(j));
		// 	}
		// }
		// Set<Integer> hs = new HashSet<Integer>(temp);
		// temp.clear();
		// temp.addAll(hs);
		// for(int i=0;i<temp.size();i++){
		// 	Message n = new Message();
		// 	n.id = -1;
		// 	n.type = 4;
		// 	n.srcid = nodeId;
		// 	n.ttl = 10;
		// 	sendMessage(n,temp.elementAt(i));
		// 	//logm("SendHB",n,temp.elementAt(i));
		// }
  //   }

    public void FirstMessage(Message mssg){
        int closestGNodeId = mssg.srcid;
        //log(" First Message sent to "+Integer.toString(closestGNodeId)+"\n");
        mssg.srcid = nodeId;
        mssg.type = 3;
        mssg.ttl = 10;
        mssg.x=x;mssg.y=y;
        sendMessage(mssg,closestGNodeId);
        logm("FirstMessage",mssg,closestGNodeId);
    }

    private void Search(Message m){
    	// if(m.ttl==0){
    	// 	return;
    	// }

    	//log("Search\n");
    	int difference = Math.abs(m.searchKey - nodeId);
    	if((difference==0) || (m.fg==3)){
    		log("Search1,"+m.id+","+nodeId+",0,"+m.ttl+","+m.type+","+m.searchKey+","+m.fg+"\n");
    	}
    	else{
			int index1=0;
			int id1 = nodeId;
			int id2 = m.searchKey ; 
			while(((int)(id1/Math.pow(16,7-index1)))%16 == ((int)(id2/Math.pow(16,7-index1)))%16){
				index1++;
			}
			int index2 = ((int)(id2/Math.pow(16,7-index1)))%16;

            if(rTable.elementAt(index1).elementAt(index2) != -1){
                Message n1 = new Message();
                n1.x=x;n1.y=y;
                n1.type = 1;
                n1.id = m.id;
                n1.ttl = m.ttl - 1;
                n1.srcid = m.srcid;
                n1.searchKey = m.searchKey;
                n1.fg = 0;
                sendMessage(n1,rTable.elementAt(index1).elementAt(index2));
                log("Search2,"+n1.id+","+nodeId+","+rTable.elementAt(index1).elementAt(index2)+","+n1.ttl+","+n1.type+","+n1.searchKey+","+n1.fg+"\n");
            }
            else{
                //search over L, R, Nbrs
                //send to the min element type 3
                int minId = -1; // id of the node
                int minDifference = Integer.MAX_VALUE;

                for(int i=0;i<lLeaf.size();i++){
                    if(Math.abs(lLeaf.elementAt(i) - m.searchKey) < minDifference){
                        minId = lLeaf.elementAt(i);
                        minDifference = Math.abs(lLeaf.elementAt(i) - m.searchKey);
                    }
                }

                for(int i=0;i<rLeaf.size();i++){
                    if(Math.abs(rLeaf.elementAt(i) - m.searchKey) < minDifference){
                        minId = rLeaf.elementAt(i);
                        minDifference = Math.abs(rLeaf.elementAt(i) - m.searchKey);
                    }
                }

                for(int i=0;i<nbrs.size();i++){
                    if(Math.abs(nbrs.elementAt(i) - m.searchKey) < minDifference){
                        minId = nbrs.elementAt(i);
                        minDifference = Math.abs(nbrs.elementAt(i) - m.searchKey);
                    }
                }

                if((minId == -1) || (minDifference >= (Math.abs(m.searchKey - nodeId)))){
                    log("Search3,"+m.id+","+nodeId+",0,"+m.ttl+","+m.type+","+m.searchKey+","+m.fg+"\n");
                }
                else{
                    Message n1 = new Message();
                    n1.x=x;n1.y=y;
                    n1.type = 1;
                    n1.id = m.id;
                    n1.ttl = m.ttl - 1;
                    n1.srcid = m.srcid;
                    n1.fg = 3;    
                    n1.searchKey = m.searchKey;
                    sendMessage(n1,minId); 
                    log("Search4,"+n1.id+","+nodeId+","+minId+","+n1.ttl+","+n1.type+","+n1.searchKey+","+n1.fg+"\n");  // implies final node
                }
            }
    	}
    }

    private void UpdateRT(Message m){
    	// if(m.ttl==0){
    	// 	return;
    	// }

    	// Update RT first
    	log("UpdateRT,"+m.id+","+nodeId+",0,"+m.ttl+","+m.type+","+m.searchKey+","+m.fg+"\n");
    	
    	int index1=0;
    	int id1 = nodeId;
    	int id2 = m.srcid; 
    	while(((int)(id1/Math.pow(16,7-index1)))%16 == ((int)(id2/Math.pow(16,7-index1)))%16){
    		index1++;
    	}

    	for(int i=0;i<=index1;i++){
    		for(int j=0;j<16;j++){
    			if(rTable.elementAt(i).elementAt(j) == -1){
    				rTable.elementAt(i).set(j,m.rTable.elementAt(i).elementAt(j));
    			}
    		}
    		int index2 = ((int)(id1/Math.pow(16,7 - i)))%16;
    		rTable.elementAt(i).set(index2,-1);
    	}
    	int index2 = ((int)(id2/Math.pow(16,7-index1)))%16;
    	rTable.elementAt(index1).set(index2,m.srcid);

    	//Update lLeaf and rLeaf
    	for(int i=0;i<m.lLeaf.size();i++){
    		if(m.lLeaf.elementAt(i) < nodeId){
    			lLeaf.addElement(m.lLeaf.elementAt(i));
    		}
    		else if(m.lLeaf.elementAt(i) > nodeId){
    			rLeaf.addElement(m.lLeaf.elementAt(i));	
    		}
    	}

    	for(int i=0;i<m.rLeaf.size();i++){
    		if(m.rLeaf.elementAt(i) < nodeId){
    			lLeaf.addElement(m.rLeaf.elementAt(i));
    		}
    		else if(m.rLeaf.elementAt(i) > nodeId){
    			rLeaf.addElement(m.rLeaf.elementAt(i));	
    		}
    	}

    	if(m.srcid < nodeId){
    		lLeaf.addElement(m.srcid);
    	}
    	else if(m.srcid > nodeId){
    		rLeaf.addElement(m.srcid);
    	}

    	Set<Integer> hs = new HashSet<Integer>(lLeaf);
    	lLeaf.clear();
    	lLeaf.addAll(hs);
        Collections.sort(lLeaf);
		
		hs = new HashSet<Integer>(rLeaf);
    	rLeaf.clear();
    	rLeaf.addAll(hs);
        Collections.sort(rLeaf);

    	if(lLeaf.size() > 8){
    		lLeaf.subList(0,lLeaf.size()-8).clear();
    	}

    	if(rLeaf.size() > 8){
    		rLeaf.subList(8,rLeaf.size()).clear();
    	}


    	//update neighbours
    	nbrs.addAll(m.nbrs);
    	nbrs.addElement(m.srcid);
    	hs = new HashSet<Integer>(nbrs);
    	nbrs.clear();
    	nbrs.addAll(hs);
    	nbrs.remove((Integer)nodeId);

        if(nbrs.size() > 8){
            nbrs.subList(8,nbrs.size()).clear();
        }

   		if(m.fg == 0 || m.fg == 1){
   			dataReceived.set(m.ttl,true);
   			if(m.fg == 1)
   				fId = m.ttl;

   			if(fId != -1){
   				boolean done = true; 
   				for(int i=9;i>=fId;i--){
   					if(!dataReceived.elementAt(i)){
   						done = false;
   						break;
   					}
   				}

   				if(done){
   					//send the g message
   					//log("Send G message\n");
   					Vector<Integer> temp = new Vector<Integer>();
   					temp.addAll(lLeaf);
   					temp.addAll(rLeaf);
   					temp.addAll(nbrs);
   					for(int i=0;i<8;i++){
   						for(int j=0;j<16;j++){
   							if(rTable.elementAt(i).elementAt(j)!=-1)
   								temp.addElement(rTable.elementAt(i).elementAt(j));
   						}
   					}

   					hs = new HashSet<Integer>(temp);
   					temp.clear();
   					temp.addAll(hs);

   					for(int i=0;i<temp.size();i++){
   						Message n = new Message();
                        n.x=x;n.y=y;
   						n.id = m.id;
   						n.type = 2;
   						n.fg = 2;
   						n.srcid = nodeId;
   						n.ttl = m.ttl-1;
   						n.rTable = (Vector< Vector<Integer>>) rTable.clone();
   						n.lLeaf = (Vector<Integer>) lLeaf.clone();
   						n.rLeaf = (Vector<Integer>) rLeaf.clone();
   						n.nbrs = (Vector<Integer>) nbrs.clone();

   						sendMessage(n,temp.elementAt(i));
   						logm("UpdateRT",n,temp.elementAt(i));
   					}
   				}	
   			}
   		}

   		//Printing the entire rTable
   		// for(int i=0;i<8;i++){
   		// 	for(int j=0;j<16;j++){
   		// 		log(rTable.elementAt(i).elementAt(j)+" ");
   		// 	}
   		// 	log("\n");
   		// }
   		// log("\n");
    }
    
    private void Init(Message m){
    	// if(m.ttl==0){
    	// 	return;
    	// }

    	int searchId = m.srcid;
        int difference = Math.abs(searchId - nodeId);

        Message n = new Message();
        n.x=x;n.y=y;
        n.type = 2;
        n.ttl = m.ttl - 1;
        n.rTable = (Vector< Vector<Integer>>)rTable.clone();
        n.lLeaf = (Vector<Integer>)lLeaf.clone();
        n.rLeaf = (Vector<Integer>)rLeaf.clone();
        n.nbrs = (Vector<Integer>)nbrs.clone(); 
        n.srcid = nodeId;
        n.id = m.id;

        if((difference == 0) || (m.fg==3)){ 
            n.fg = 1;
        }
        else{
			int index1=0;
			int id1 = nodeId;
			int id2 = searchId; 
			while(((int)(id1/Math.pow(16,7-index1)))%16 == ((int)(id2/Math.pow(16,7-index1)))%16){
				index1++;
			}
			int index2 = ((int)(id2/Math.pow(16,7-index1)))%16;

            if(rTable.elementAt(index1).elementAt(index2) != -1){
                Message n1 = new Message();
                n1.x=x;n1.y=y;
                n1.type = 3;
                n1.id = m.id;
                n1.ttl = m.ttl - 1;
                n1.srcid = m.srcid;
                n.fg = 0;    
				sendMessage(n1,rTable.elementAt(index1).elementAt(index2));
				log("Init,"+n1.id+","+nodeId+","+rTable.elementAt(index1).elementAt(index2)+","+n1.ttl+","+n1.type+","+n1.searchKey+","+n1.fg+"\n");
            }
            else{
                //search over L, R, Nbrs
                //send to the min element type 3
                int minId = -1; // id of the node
                int minDifference = Integer.MAX_VALUE;

                for(int i=0;i<lLeaf.size();i++){
                    if(Math.abs(lLeaf.elementAt(i) - searchId) < minDifference){
                        minId = lLeaf.elementAt(i);
                        minDifference = Math.abs(lLeaf.elementAt(i) - searchId);
                    }
                }

                for(int i=0;i<rLeaf.size();i++){
                    if(Math.abs(rLeaf.elementAt(i) - searchId) < minDifference){
                        minId = rLeaf.elementAt(i);
                        minDifference = Math.abs(rLeaf.elementAt(i) - searchId);
                    }
                }

                for(int i=0;i<nbrs.size();i++){
                    if(Math.abs(nbrs.elementAt(i) - searchId) < minDifference){
                        minId = nbrs.elementAt(i);
                        minDifference = Math.abs(nbrs.elementAt(i) - searchId);
                    }
                }

                if(minId == -1 || minDifference >= Math.abs(searchId - nodeId)){
                    n.fg = 1;
                    log("Init,"+m.id+","+nodeId+",0,"+(m.ttl-1)+","+m.type+","+m.searchKey+","+m.fg+"\n");
                }
                else{
                    n.fg = 0;
                    Message n1 = new Message();
                    n1.x=x;n1.y=y;
                    n1.type = 3;
                    n1.id = m.id;
                    n1.ttl = m.ttl - 1;
                    n1.srcid = m.srcid;
                    n1.fg = 3; // implies last message    
                    sendMessage(n1,minId); 
                    log("Init,"+n1.id+","+nodeId+","+minId+","+n1.ttl+","+n1.type+","+n1.searchKey+","+n1.fg+"\n");
                }
            }
        }
        sendMessage(n,m.srcid);
        logm("Init",n,m.srcid);

    }
    // public Node getNode(int index) {
    //     return nodeList.get(index);
    // }

    public int getNodeListLength(){
        return nodeList.size();
    }
   
    public void addMessage(Message mssg) {
        msg.add(mssg);
    }
   
    // public void addNode(Node node)
    // {
    //      // add this node to the list
    // }

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

    /* Non blocking version */
    // private Message pollMessage(){
    //     return msg.poll();
    // }

}

