package test.library.parsers;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.sa.marketslayer.library.databeans.Security;
import com.sa.marketslayer.library.parsers.AMEXParser;
import com.sa.marketslayer.library.parsers.NasdaqParser;
import com.sa.marketslayer.library.parsers.OTCBBParser;
import com.sa.marketslayer.library.parsers.PinkSheetsParser;

public class TestAllParsers extends TestCase {

	public void testAllParses(){
		NasdaqParser nasdaq = new NasdaqParser();
		AMEXParser amex = new AMEXParser();
		OTCBBParser otcbb = new OTCBBParser();
		PinkSheetsParser pinks = new PinkSheetsParser();
		
		Map<String, Security> secs = new HashMap<String, Security>();
		
		//assertTrue(nasdaq.updateSymbols(secs));
		//assertTrue(amex.updateSymbols(secs));
		//assertTrue(otcbb.updateSymbols(secs));
		assertTrue(pinks.updateSymbols(secs));
		
		//String[] keys = (String[])secs.keySet().toArray();
		int counter = 0;
		for(String key : secs.keySet()){
			Security sym = secs.get(key);
			if("PS".equals(sym.exchange)){
				if(++counter%10==0)
					System.out.println(" "+sym.lookup);
				else
					System.out.print(" "+sym.lookup);
			}
		}
		System.out.println("\n PINKS: "+counter+" total: "+secs.keySet().size());
	}
}
