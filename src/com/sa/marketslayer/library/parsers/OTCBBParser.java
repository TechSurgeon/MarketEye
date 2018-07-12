package com.sa.marketslayer.library.parsers;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.sa.marketslayer.library.databeans.Security;
import com.sa.marketslayer.utils.PersistDBUtil;

public class OTCBBParser {
	
	public static Logger logger = Logger.getLogger("OTCBBParser");
	public static final String URL = "http://www.otcbb.com/dynamic/tradingdata/download/allotcbb.txt";
	public static final String EXCHANGE_ID = "OTCBB";

	public static Map<String, Security> updateSymbolsGetDiff(Map<String, Security> secs, Map<String, Security> activeSecs){
		logger.info("parse start["+EXCHANGE_ID+"]:[" + (new Time(System.currentTimeMillis())).toString()
				+ "] from "+URL);
		Map<String, Security> rsts = new HashMap<String, Security>();
		try {
			URL url = new URL(URL);
			URLConnection con1 = url.openConnection();
			DataInputStream dis = new DataInputStream(con1.getInputStream());
			String line = dis.readLine(); // read in first line and ignore
			StringTokenizer ST;
			String currentSymbol = "";
			String companyName = "";
			String empty = "";
			String activeStock = "";
			int counter = 0;

			while ((line = dis.readLine()) != null) {
				//AABT|Common Stock|ASIAN AM BK & TR(MA)|ACTIVE
				//db: StockUNID StockSymbol StockLookUp CompanyName Exchange
				// DateAdded
				ST = new StringTokenizer(line, "|");

				currentSymbol = (((String) new String(ST.nextToken("|"))).trim().toUpperCase())+".ob"; //StockSymbol/StockLookUp
				if(!activeSecs.containsKey(currentSymbol)){// must add new active symbol
						empty = ((String) new String(ST.nextToken("|"))).trim(); // Common
						 // stock?
						companyName = ((String) new String(ST.nextToken("|"))).trim();
						
						StringTokenizer t = new StringTokenizer(companyName,"-");
						companyName = (t.nextToken()).trim();
						// The market it trades on, either Nasdaq National Market(NNM)
						// or Nasdaq SmallCap Market (SCM)
						//db_marketcategory = ((String) new
						// String(ST.nextToken("|"))).trim(); // Market Category||Test
						// Issue
						//reserved = ((String) new String(ST.nextToken("|"))).trim();
						// //Reserved
						activeStock = ((String) new String(ST.nextToken("|"))).trim(); //  test issue
						
						if(activeStock.equals("ACTIVE")){
							if(!secs.containsKey(currentSymbol)){// dne yet, create and add
								Security newSecurity = new Security(currentSymbol, EXCHANGE_ID, currentSymbol, companyName);
								counter++;
								secs.put(currentSymbol, newSecurity);
								rsts.put(currentSymbol, newSecurity);
								activeSecs.put(currentSymbol, newSecurity);
								PersistDBUtil.storeSecurity(newSecurity);
								logger. info("Adding "+newSecurity.symbol+" "+newSecurity.exchange+" to system. "+newSecurity.name);	
							}
							else activeSecs.put(currentSymbol, secs.get(currentSymbol));
						}
						// do not add inactive stocks
					}

			} //end of parse line
			logger.info("parse end:["
					+ (new Time(System.currentTimeMillis())).toString() + "] "+counter+" "+EXCHANGE_ID+" securities added");
			return rsts;
		}// end of try
		catch (Exception badURL) {
			badURL.printStackTrace();
		}// end of catch
		return rsts;
	}
	
	public static boolean updateSymbols(Map<String, Security> secs){
		logger.info("parse start["+EXCHANGE_ID+"]:[" + (new Time(System.currentTimeMillis())).toString()
				+ "] from "+URL);
		try {
			URL url = new URL(URL);
			URLConnection con1 = url.openConnection();
			DataInputStream dis = new DataInputStream(con1.getInputStream());
			String line = dis.readLine(); // read in first line and ignore
			StringTokenizer ST;
			String currentSymbol = "";
			String companyName = "";
			String empty = "";
			String activeStock = "";
			int counter = 0;

			while ((line = dis.readLine()) != null) {
				//AABT|Common Stock|ASIAN AM BK & TR(MA)|ACTIVE
				//db: StockUNID StockSymbol StockLookUp CompanyName Exchange
				// DateAdded
				ST = new StringTokenizer(line, "|");

				currentSymbol = ((String) new String(ST.nextToken("|")))
						.trim(); //StockSymbol/StockLookUp
				empty = ((String) new String(ST.nextToken("|"))).trim(); // Common
																		 // stock?
				companyName = ((String) new String(ST.nextToken("|"))).trim();
				
				StringTokenizer t = new StringTokenizer(companyName,"-");
				companyName = (t.nextToken()).trim();
				// The market it trades on, either Nasdaq National Market(NNM)
				// or Nasdaq SmallCap Market (SCM)
				//db_marketcategory = ((String) new
				// String(ST.nextToken("|"))).trim(); // Market Category||Test
				// Issue
				//reserved = ((String) new String(ST.nextToken("|"))).trim();
				// //Reserved
				activeStock = ((String) new String(ST.nextToken("|")))
						.trim(); //  test issue

				if (activeStock.equals("ACTIVE")) {
					activeStock = "Y";
				} else {
					activeStock = "N";
				}

				currentSymbol = (currentSymbol.trim()).toUpperCase();
				if(!secs.containsKey(currentSymbol)){
					Security s = new Security(currentSymbol, EXCHANGE_ID, currentSymbol, companyName);
					counter++;
					secs.put(currentSymbol, s);
					logger. info("Adding "+s.symbol+" "+s.exchange+" to system. "+s.name);
				}
			} //end of parse line
			logger.info("parse end:["
					+ (new Time(System.currentTimeMillis())).toString() + "] "+counter+" "+EXCHANGE_ID+" securities added");
			return true;
		}// end of try
		catch (Exception badURL) {
			badURL.printStackTrace();
		}// end of catch
		
		
		return false;
	}

	public static void readL2(){
		try {
			URL url = new URL("http://app.quotemedia.com/quotetools/clientForward?webmasterId=89753&targetURL=http://www.otcbb.com/asp/quote_module.asp&targetsym=symbol&symbol=pspj&action=showDetailedQuote");
			URLConnection con1 = url.openConnection();
			DataInputStream dis = new DataInputStream(con1.getInputStream());
			String line = dis.readLine(); // read in first line and ignore
			StringTokenizer ST;
			String currentSymbol = "";
			String companyName = "";
			String empty = "";
			String activeStock = "";
			int counter = 0;

			while ((line = dis.readLine()) != null) {
				System.out.println(line);
			}

		
		
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		readL2();
	}
}
