import java.util.*;

public class Message{
	public int id;
	public int type; // 1,2,3,4 search,other_update,init,reply
	public int srcid;
	public int searchKey;
	public Vector<Integer> lLeaf;
	public Vector<Integer> rLeaf;
	public Vector<Integer> nbrs;
	public Vector< Vector<Integer>> rTable;
	public int ttl; // 10, 9, 8, ...
	public int fg; // 0 cont, 1 F, 2 G
	public int x;
	public int y;

	public void printm(){
		System.out.print("("+id+","+type+","+srcid+","+searchKey+","+ttl+","+fg+") ");	
	}
}