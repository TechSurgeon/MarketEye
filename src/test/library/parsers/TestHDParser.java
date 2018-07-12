package test.library.parsers;

import java.util.Date;
import java.util.HashMap;

import junit.framework.TestCase;

import com.sa.marketslayer.library.databeans.MarketDay;
import com.sa.marketslayer.library.databeans.Security;
import com.sa.marketslayer.library.parsers.HDParser;
import com.sa.marketslayer.utils.PersistFileUtil;

public class TestHDParser extends TestCase{

	public void testParser(){
		
		Security test = new Security("CULS", "NASDAQ", "CULS", "Google Inc.");
		
		HashMap<Date, MarketDay> history = new HashMap<Date, MarketDay>();
		
		HDParser hdtest= new HDParser();
		
		hdtest.downloadUpdateSaveHistorical(test, history);
		
		PersistFileUtil.saveObject("C:/0_My_Real_Documents/Eclipse/eclipse/workspace/MarketSlayer/src/test/testdata/NASDAQ/"+test.symbol+".his", history);
	}
}
