import java.util.*;
import java.util.concurrent.*;


public class MessageQueue<Message> {
    
    private int capacity;//Number of messages that can be stored in the queue.

    private Queue<Message> queue = new ConcurrentLinkedQueue<Message>();//The queue for receiving all incoming messages.

    /**
     * Constructor, initializes the queue.
     * 
     * @param capacity The number of messages allowed in the queue.
     */
    public MessageQueue(int capacity) {
        this.capacity = capacity;
    }

    
    public synchronized void send(Message message) {
        //TODO check
    }
	
    public synchronized int receive() {
        //TODO check
        
	return 0;
    }
 
    public void add(Message mssg){
    	queue.add(mssg);
    }

    public Message getMessage(){
    	while(queue.isEmpty());
        return queue.poll();
    }

    public boolean empty(){
    	return queue.isEmpty();
    }

    public int size(){
    	return queue.size();
    }

    public Message peek(){
    	return queue.peek();
    }
}
