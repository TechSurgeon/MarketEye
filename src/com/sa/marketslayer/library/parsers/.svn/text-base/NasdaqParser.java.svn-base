package com.sa.marketslayer.library.parsers;

import java.io.DataInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.sa.marketslayer.library.databeans.Security;
import com.sa.marketslayer.utils.PersistDBUtil;

public class NasdaqParser {
	
	public static Logger logger = Logger.getLogger("NasdaqParser");
	public static final String URL = "http://www.nasdaqtrader.com/dynamic/SymDir/nasdaq.txt";
	public static final String EXCHANGE_ID = "NASDAQ";

	public static Map<String, Security> updateSymbolsGetDiff(Map<String, Security> secs, Map<String, Security> activeSecs){
		logger.info("parse start[NASDAQ]:[" + (new Time(System.currentTimeMillis())).toString()
				+ "] from "+URL);
		Map<String, Security> rsts = new HashMap<String, Security>();
		try {
			URL url = new URL(URL);
			URLConnection con1 = url.openConnection();
			DataInputStream dis = new DataInputStream(con1.getInputStream());
			String line = dis.readLine(); // read in first line and ignore
			StringTokenizer ST;
			StringTokenizer companyTrimmer;
			String sym = "";
			String companyName = "";
			String active = "";
			int counter = 0;
			
			String trash="";

			outer:while ((line = dis.readLine()) != null) {
				
				if(line.startsWith("File Creation Time"))
					break outer;
					//Symbol|Security Name|Market Category|Reserved|Market
					// Statistics|Test Issue|Financial Status
					//db: StockUNID StockSymbol StockLookUp CompanyName Exchange
					// DateAdded
				ST = new StringTokenizer(line, "|");
	
				sym = ((String) new String(ST.nextToken("|")))
						.trim(); //StockSymbol/StockLookUp
				sym = (sym.trim()).toUpperCase();
				if(!activeSecs.containsKey(sym)){// must add new active symbol
					//if(!secs.containsKey(sym)){// dne yet, create and add
						companyName = ((String) new String(ST.nextToken("|"))).trim(); // CompanyName
						//companyTrimmer = new StringTokenizer(db_companyName, "-"); // trim
																				   // companyName
						//trash = ((String) new String(companyTrimmer.nextToken())).trim();
						StringTokenizer t = new StringTokenizer(companyName,"-");
						companyName = (t.nextToken()).trim();
						// The market it trades on, either Nasdaq National Market(NNM)
						// or Nasdaq SmallCap Market (SCM)
						trash = ((String) new String(ST.nextToken("|")))
								.trim(); // Market Category||Test Issue
						trash = ((String) new String(ST.nextToken("|"))).trim(); //Reserved
						active = ((String) new String(ST.nextToken("|")))
								.trim(); //  test issue
			
						if(active.equals("N")&&sym!=null&&companyName!=null) {// active symbol found
							if(!secs.containsKey(sym)){// dne yet, create and add
								Security newSecurity = new Security(sym, EXCHANGE_ID, sym, companyName);//active.equals("N")
								newSecurity.exchange = EXCHANGE_ID;
								logger.info("Adding "+newSecurity.symbol+" "+newSecurity.exchange+" to system. "+newSecurity.name);
								secs.put(sym, newSecurity);
								rsts.put(sym,newSecurity);
								activeSecs.put(sym, newSecurity);
								PersistDBUtil.storeSecurity(newSecurity);
								counter++;
								//logger.info("Added Symbol "+sym+" as "+EXCHANGE_ID+" security "+companyName);
							}
							else activeSecs.put(sym, secs.get(sym));
						}
						trash = ((String) new String(ST.nextToken("|")))
						.trim();// skip financial status
					//}// exists already, load
					//else activeSecs.put(sym, secs.get(sym));
				}
			} //end of parse line
			
			logger.info("parse end:[" + (new Time(System.currentTimeMillis())).toString()
					+ "] "+counter+" "+EXCHANGE_ID+" securities added");
			return rsts;
		}// end of try
		catch (Exception bad) {
			bad.printStackTrace();
		}// end of catch
		return rsts;
	}
	
	public static boolean updateSymbols(Map<String, Security> secs){
		logger.info("parse start[NASDAQ]:[" + (new Time(System.currentTimeMillis())).toString()
				+ "] from "+URL);
		try {
			URL url = new URL(URL);
			URLConnection con1 = url.openConnection();
			DataInputStream dis = new DataInputStream(con1.getInputStream());
			String line = dis.readLine(); // read in first line and ignore
			StringTokenizer ST;
			StringTokenizer companyTrimmer;
			String sym = "";
			String companyName = "";
			String active = "";
			int counter = 0;
			
			String trash="";

			outer:while ((line = dis.readLine()) != null) {
				
			if(line.startsWith("File Creation Time"))
				break outer;
				//Symbol|Security Name|Market Category|Reserved|Market
				// Statistics|Test Issue|Financial Status
				//db: StockUNID StockSymbol StockLookUp CompanyName Exchange
				// DateAdded
			ST = new StringTokenizer(line, "|");

			sym = ((String) new String(ST.nextToken("|")))
					.trim(); //StockSymbol/StockLookUp
			sym = (sym.trim()).toUpperCase();
			if(!secs.containsKey(sym)){
						companyName = ((String) new String(ST.nextToken("|")))
						.trim(); // CompanyName
				//companyTrimmer = new StringTokenizer(db_companyName, "-"); // trim
																		   // companyName
				//trash = ((String) new String(companyTrimmer.nextToken())).trim();
				StringTokenizer t = new StringTokenizer(companyName,"-");
				companyName = (t.nextToken()).trim();
				// The market it trades on, either Nasdaq National Market(NNM)
				// or Nasdaq SmallCap Market (SCM)
				trash = ((String) new String(ST.nextToken("|")))
						.trim(); // Market Category||Test Issue
				trash = ((String) new String(ST.nextToken("|"))).trim(); //Reserved
				active = ((String) new String(ST.nextToken("|")))
						.trim(); //  test issue
	
				Security s = new Security(sym, EXCHANGE_ID, sym, companyName);//active.equals("N")
				s.exchange = EXCHANGE_ID;
				if(active.equals("N")&&sym!=null&&companyName!=null) {
				logger.info("Adding "+s.symbol+" "+s.exchange+" to system. "+s.name);
				secs.put(sym, s);
				counter++;
				//logger.info("Added Symbol "+sym+" as "+EXCHANGE_ID+" security "+companyName);
				}
				
				trash = ((String) new String(ST.nextToken("|")))
						.trim();// skip financial status
			}
					
				
			} //end of parse line
			
			logger.info("parse end:[" + (new Time(System.currentTimeMillis())).toString()
					+ "] "+counter+" "+EXCHANGE_ID+" securities added");
			return true;
/*			if (counter == 0){
				logger.info("No "+EXCHANGE_ID+" Symbols updated.");
				return false;}
			else{
				logger.info("No "+EXCHANGE_ID+" Symbols updated.");
				return true;
			}*/
			//return results;
		}// end of try
		catch (Exception bad) {
			bad.printStackTrace();
		}// end of catch

		
		return false;
	}
}
