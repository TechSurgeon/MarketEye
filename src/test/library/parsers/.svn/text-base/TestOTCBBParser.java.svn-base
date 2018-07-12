package test.library.parsers;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.sa.marketslayer.library.databeans.Security;
import com.sa.marketslayer.library.parsers.OTCBBParser;

public class TestOTCBBParser extends TestCase {

	public void testUpdateSymbols(){
		
		Map<String, Security> secs = new HashMap<String, Security>();
		OTCBBParser np = new OTCBBParser();
		
		assertNotNull(np);
		assertNotNull(secs);
		
		assertTrue(np.updateSymbols(secs));
		int oldSize = secs.size();
		//assertFalse(np.updateSymbols(secs));
		//assertEquals(oldSize, secs.size());

	}
}
