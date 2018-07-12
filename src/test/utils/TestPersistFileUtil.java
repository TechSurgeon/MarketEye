package test.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;

import com.sa.marketslayer.library.databeans.RBMessageTrail;
import com.sa.marketslayer.library.parsers.RBParser;
import com.sa.marketslayer.utils.PersistFileUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class TestPersistFileUtil extends TestCase{
	/*
	public void testOpenFile(){
		
		String output = PersistFileUtil.openFile("C:/0_My_Real_Documents/Eclipse/eclipse/workspace/Hermes/src/test/testdata/PersistFileUtilTestFile.dat");
		
		//System.out.println("output: "+output);

		String junk = "cqweqewqrywei31256358!@#$%^&*()~_{}\">?,.,;oasdg";
		assertEquals("Open and Read or create file didn't work", junk,output);
	}

	public void testWriteFile(){
		String testFileLoc = "C:/0_My_Real_Documents/Eclipse/eclipse/workspace/Hermes/src/test/testdata/PersistFileUtilTestFile.dat";
		String output = PersistFileUtil.openFile(testFileLoc);
		
		//System.out.println("output: "+output);

		String junk = "ZXCVBNM<>?ASDFGHJKL:\"QWERTYUIOP{}!@#$%^()_+";
		
		PersistFileUtil.overWriteFileWithContent(testFileLoc, junk);
		
		String output2 = PersistFileUtil.openFile(testFileLoc);
		assertEquals("Write to file didnt work", junk,output2);
		
		PersistFileUtil.overWriteFileWithContent(testFileLoc, "cqweqewqrywei31256358!@#$%^&*()~_{}\">?,.,;oasdg");
		
		String output3 = PersistFileUtil.openFile(testFileLoc);
		assertEquals("Write to file didnt work", "cqweqewqrywei31256358!@#$%^&*()~_{}\">?,.,;oasdg",output3);
		}
*/
	
	public void testReadParseWriteRBMBTrail(){
		
		String testSym="UCHB";
		String testFileLoc = "C:/0_My_Real_Documents/Eclipse/eclipse/workspace/Hermes/src/test/testdata/"+testSym.toUpperCase()+".rb";

		RBMessageTrail tm = new RBMessageTrail();
		XStream xstream=new XStream(new DomDriver());
		
		
		//String xml = PersistFileUtil.openFile(testFileLoc);
		
		//System.out.println("done reading xml "+xml);
		File tempRBFile = new File(testFileLoc);
		if (tempRBFile.exists()){
			try {
				InputStream closeMe = PersistFileUtil.getInputStream(testFileLoc);
				tm = (RBMessageTrail) xstream.fromXML(closeMe);//closeMe);
				closeMe.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if(tm.values().size()>0){
			//tm = (TreeMap<String,MBMessage>)xstream.fromXML(xml);
			System.out.println("Old data loaded from FS for "+testSym+". Entries: "+tm.size()+" updating ...");
		}
		else{
			System.out.println("No data found for "+testSym+". Parsing in new data. Entries: "+tm.size());
		}
		
		
		RBParser rbp = new RBParser(testSym);
		int counter = 1;
		boolean done=false;
		int last=0;
		int previousLast = tm.getLastId();
		
		int prevSize = tm.size();
		last = rbp.parseAPage(tm);
		last +=59;
		//if(tm.getLastId()==0) tm.setLastId(last);

		//tm.setLastId(last);t
		previousLast = tm.getLastId();
		long start = System.currentTimeMillis();
		//System.out.println("last: "+last+"   prev: "+previousLast+" ");
		outer: while(last!=1 && ( !(previousLast - last<0) || previousLast==0) ) {
		//System.out.println("start: "+(System.currentTimeMillis()/1000));
			//System.out.print("last: "+last+"   prev: "+previousLast+" ");
			last = rbp.parseAPage(tm);

			counter++;
			
			if(counter>80 )// emergency break for over num counter pages
				break outer;
			last +=59;
			previousLast = last;
			//System.out.println(" last: "+last+"   prev: "+previousLast);
			
			
		}
		tm.setLastId(last);
		long end = System.currentTimeMillis();
		System.out.println("\nLast id: "+last+" Updated data for "+testSym+". New Entries: "+(tm.size()-prevSize)+" Total Entries: "+tm.size());
			
		System.out.println(counter+" page(s) parsed in: "+((end-start))+" milli seconds.");
		
		if ((tm.size()-prevSize)>0) {
			try {
				OutputStream closeMe = PersistFileUtil
						.getOutputStream(testFileLoc);
				if (tm.size() > 0)
					xstream.toXML(tm, closeMe);
				closeMe.close();
				//logger.info("Historical Data saved for "+testSym);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}		
		
		
	}

/*
public void testDebugReadParseWriteRBMBTrail(){
	
	String testSym="goog";
	String testFileLoc = "C:/0_My_Real_Documents/Eclipse/eclipse/workspace/Hermes/src/test/testdata/killme/"+testSym.toUpperCase()+".dat";

	TreeMap<String,MBMessage> tm = new TreeMap<String,MBMessage>();
	XStream xstream=new XStream(new DomDriver());

	System.out.println("Performing test parse for symbol "+testSym+". Parsing in new data. Entries: "+tm.size());
	
	RBParser rbp = new RBParser(testSym);
	int counter = 0;
	boolean done=false;
	while(!done){
		done = rbp.parseAPage(tm);
		counter++;
		if(counter>40000)// emergency break for over 40k million messages
			break;
	}
	System.out.println("Updated data for "+testSym+". Entries: "+tm.size());
		try {
			if(tm.size()>0){
				// check all messages
				Iterator it = tm.values().iterator();
				while(it.hasNext()){
					MBMessage mbm = (MBMessage) it.next();
					OutputStream closeMe = PersistFileUtil.getOutputStream(testFileLoc+mbm.local_messageID);
					xstream.toXML(mbm, closeMe);
					closeMe.close();
				}
				//
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if(tm.size()>0){
				// check all messages
				Iterator it = tm.values().iterator();
				while(it.hasNext()){
					MBMessage mbm = (MBMessage) it.next();
					System.out.println(mbm);
					InputStream closeMe = PersistFileUtil.getInputStream(testFileLoc+mbm.local_messageID);
					mbm = (MBMessage) xstream.fromXML(closeMe);
					closeMe.close();
				}
				//
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	//}
	
	System.out.println("done");
	
}*/

}
