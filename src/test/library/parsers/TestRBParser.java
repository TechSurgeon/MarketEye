package test.library.parsers;

import java.util.TreeMap;

import junit.framework.TestCase;

import com.sa.marketslayer.library.databeans.MBMessage;
import com.sa.marketslayer.library.databeans.RBMessageTrail;
import com.sa.marketslayer.library.parsers.RBParser;


public class TestRBParser extends TestCase{
	
	public void testSimpleBoardCompleteParse(){
		
		RBParser rbparser = new RBParser("UCHB");
		
		RBMessageTrail hm = new RBMessageTrail();
		long start = System.currentTimeMillis();
		int counter =0;
		//boolean done=false;
		int last = 0;
		outer:while(last!=1){
		last = rbparser.parseAPage(hm);
		counter++;
		if(counter>15)// emergency break
			break outer;
		}

		long end = System.currentTimeMillis();
		System.out.println("Parsed "+hm.size()+" new messages in "+((double)(end-start)/60000)+" minutes.");
		
		Object[] keys = hm.keySet().toArray();
		Object[] ascendingOrder = new Object[keys.length];

		java.util.Arrays.sort(keys);
		
		for(int k=0;k<keys.length;k++){
			ascendingOrder[k]=keys[keys.length-(k+1)];
		}
		
		for(Object mbm : ascendingOrder){
			System.out.println((hm.get(mbm)).toString());
		}
	}
	
	

}
