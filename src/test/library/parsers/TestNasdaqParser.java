package test.library.parsers;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.sa.marketslayer.library.databeans.Security;
import com.sa.marketslayer.library.parsers.NasdaqParser;

public class TestNasdaqParser extends TestCase {

	public void testUpdateSymbols(){
		
		Map<String, Security> secs = new HashMap<String, Security>();
		NasdaqParser np = new NasdaqParser();
		
		assertNotNull(np);
		assertNotNull(secs);
		
		assertTrue(np.updateSymbols(secs));
		int oldSize = secs.size();
		
		assertFalse(np.updateSymbols(secs));
		assertEquals(oldSize, secs.size());

	}
}
