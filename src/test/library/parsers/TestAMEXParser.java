package test.library.parsers;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.sa.marketslayer.library.databeans.Security;
import com.sa.marketslayer.library.parsers.AMEXParser;

public class TestAMEXParser extends TestCase {

	public void testUpdateSymbols(){
		
		Map<String, Security> secs = new HashMap<String, Security>();
		AMEXParser np = new AMEXParser();
		
		assertNotNull(np);
		assertNotNull(secs);
		
		assertTrue(np.updateSymbols(secs));
		int oldSize = secs.size();
		//assertFalse(np.updateSymbols(secs));
		//assertEquals(oldSize, secs.size());

	}
}
