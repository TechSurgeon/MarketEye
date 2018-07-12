package com.sa.marketslayer.library.parsers;

import java.io.DataInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.sa.marketslayer.library.databeans.Security;
import com.sa.marketslayer.utils.PersistDBUtil;

public class AMEXParser {

	public static final String URL="http://www.amex.com/SaveAs.jsp?fileName=/equities/dataDwn/EQUITY_EODLIST_*****2006.csv";
	public static Logger logger = Logger.getLogger("AMEXParser");
	public static final String EXCHANGE_ID = "AMEX";

	//http://www.rightline.net/calendar/market-holidays.html
	public static final ArrayList<String>[] holidays = new ArrayList[2];
/*	{
		2006 { "02JAN", "16JAN", "20FEB", "14APR", "29MAY", "04JUL", "04SEP", "23NOV", "25DEC" },
		2007 { "01JAN", "15JAN", "19FEB", "06APR", "28MAY", "04JUL", "03SEP", "22NOV", "25DEC" }
	};*/
	
	static{
		holidays[0]=new ArrayList<String>();
		holidays[0].add("02JAN");
		holidays[0].add("16JAN");
		holidays[0].add("20FEB");
		holidays[0].add("14APR");
		holidays[0].add("29MAY");
		holidays[0].add("04JUL");
		holidays[0].add("04SEP");
		holidays[0].add("23NOV");
		holidays[0].add("25DEC");
		
		holidays[1]=new ArrayList<String>();
		holidays[1].add("01JAN");
		holidays[1].add("15JAN");
		holidays[1].add("19FEB");
		holidays[1].add("06APR");
		holidays[1].add("28MAY");
		holidays[1].add("04JUL");
		holidays[1].add("03SEP");
		holidays[1].add("22NOV");
		holidays[1].add("25DEC");
		
		holidays[2]=new ArrayList<String>();//2008
		holidays[2].add("01JAN");
		holidays[2].add("21JAN");
		holidays[2].add("18FEB");
		holidays[2].add("21MAR");
		holidays[2].add("26MAY");
		holidays[2].add("04JUL");
		holidays[2].add("01SEP");
		holidays[2].add("27NOV");
		holidays[2].add("25DEC");
	}
	
	private static String getDateString(long millis){
		Date now = new Date(millis);
		String dateString = now.toString();
		StringTokenizer st1 = new StringTokenizer(dateString, " ");
		String weekday = st1.nextToken();
		String month = st1.nextToken();
		String dayofmonth = st1.nextToken();
		return (dayofmonth+(month.toUpperCase())).trim();
	}
	
	public static String getDateToken(){
		String rst="";
		//long runTime;
		long currentTime = System.currentTimeMillis();
		Date now = new Date(currentTime);
		ArrayList<String> holidaysThisYear;
		String dateString = now.toString();
		StringTokenizer st1 = new StringTokenizer(dateString, " ");
		String weekday = st1.nextToken();
		String month = st1.nextToken();
		String dayofmonth = st1.nextToken();
		String year="";
		while(st1.hasMoreElements()){
			year = st1.nextToken();
		}
		
		if(year.equals("2006")){
			holidaysThisYear=holidays[0];
		}
		else if(year.equals("2007")){
			holidaysThisYear=holidays[1];
		}
		else if(year.equals("2008")){
			holidaysThisYear=holidays[2];
		}
		else throw new RuntimeException("NEED TO UPDATE YEAR SETTER!! FATAL EXCEPTION !");
		
		//runTime= currentTime;
		
		boolean goodDateFound = false;
		int counter = 0;
		
		while(!goodDateFound){
			++counter;

			String possibleRst = getDateString(currentTime);
			Date newNow = new Date(currentTime);
			String dateString2 = newNow.toString();
			StringTokenizer st2 = new StringTokenizer(dateString2, " ");
			String weekday2 = (st2.nextToken()).toUpperCase();
			
			if(holidaysThisYear.contains(possibleRst)){
				logger.info("Hit a holiday, re-tracing.");
				currentTime -=86400000;
			}
			else if (weekday2.equals("SAT")){
				logger.info("Hit a Saturday, re-tracing.");
				currentTime-=86400000;
			}
			else if (weekday2.equals("SUN")){
				logger.info("Hit a Sunday, re-tracing.");
				currentTime -=86400000;
			}
			else{
				return possibleRst;
			}
			
			if(counter>10){
				throw new RuntimeException("Can't find last trading day for AMEX update!!!!");
			}
			
		}
		
		
		//logger.info("now is: "+now);
		
		return rst;
	}
	
	public static void main(String[] args){
		
		String updateURL = URL.replace("*****", getDateToken());// = URL;
		//getDateToken();
		
		logger.info(updateURL);
	}
	
	public static Map<String, Security> updateSymbolsGetDiff(Map<String, Security> secs, Map<String, Security> activeSecurities){
		logger.info("parse start["+EXCHANGE_ID+"]:[" + (new Time(System.currentTimeMillis())).toString()
				+ "]");
		int counter = 0;
		String line = "";
		Map<String, Security> rsts = new HashMap<String, Security>();
		try {
			URL url = new URL(URL.replace("*****", getDateToken()));
			URLConnection con1 = url.openConnection();
			DataInputStream dis = new DataInputStream(con1.getInputStream());

			StringTokenizer ST;
			char apo;// = char (34);//((String)"'").charAt(0);// &#34;
			String currentSymbol = "";
			String companyName = "";
			
			//igonore first line: Company Name,Symbol
			line = dis.readLine();

			int apo1 = 0;
			int apo2 = 0;

			while (((line = dis.readLine()) != null) && (!line.equals(""))) {
				apo = line.charAt(0);
				apo2 = line.indexOf(apo, 2);

				companyName = line.substring(apo1, apo2);//((String) new
															  // String(ST.nextToken())).trim();
															  // // CompanyName
				line = line.substring(apo2 + 3, line.length());
				//out(record);
				//status_out(record);
				ST = new StringTokenizer(line, ",");
				companyName = companyName.replaceAll("" + apo, " ");
				companyName = companyName.trim();
				// now trim name
				StringTokenizer t = new StringTokenizer(companyName,"-");
				companyName = (t.nextToken()).trim();
				//companyName =companyName.replace("'", "");
				
				currentSymbol = ((String) ST.nextToken(",")).trim(); //StockSymbol/StockLookUp
				currentSymbol = currentSymbol.replaceAll("" + apo, " ");
				currentSymbol = currentSymbol.trim();

				if (currentSymbol.contains(".")) { // trim post-fix
					int m = currentSymbol.indexOf(".");
					currentSymbol = currentSymbol.substring(0, m);

				} // end of post-fix trim

				if( !activeSecurities.containsKey(currentSymbol)){// must add new active symbol
					if(!secs.containsKey(currentSymbol)){// dne yet, create and add
						Security newSecurity = new Security( currentSymbol, EXCHANGE_ID, currentSymbol,
								 companyName);
						counter++;
						secs.put(currentSymbol, newSecurity);
						rsts.put(currentSymbol, newSecurity);
						activeSecurities.put(currentSymbol, newSecurity);
						PersistDBUtil.storeSecurity(newSecurity);
						logger. info("Adding "+newSecurity.symbol+" "+newSecurity.exchange+" to system. "+newSecurity.name);
					}	
					else activeSecurities.put(currentSymbol, secs.get(currentSymbol));// exists, load and add to active
					//logger.info("Added Symbol "+currentSymbol+" as "+EXCHANGE_ID+" security "+companyName);
				}
			} // end of while
			logger.info("parse end:["
					+ (new Time(System.currentTimeMillis())).toString() + "] "+counter+" "+EXCHANGE_ID+" securities added");
		}// end of try
		catch (Exception badURL) {
			logger.warning(line);
			badURL.printStackTrace();
		}// end of catch	
		return rsts;
	}
	
	public static boolean updateSymbols(Map<String, Security> secs){
		logger.info("parse start["+EXCHANGE_ID+"]:[" + (new Time(System.currentTimeMillis())).toString()
				+ "]");
		int counter = 0;
		String line = "";
		try {
			URL url = new URL(URL.replace("*****", getDateToken()));
			URLConnection con1 = url.openConnection();
			DataInputStream dis = new DataInputStream(con1.getInputStream());

			//Date today = new Date();

			StringTokenizer ST;
			char apo;// = char (34);//((String)"'").charAt(0);// &#34;
			String currentSymbol = "";
			String companyName = "";
			
			//igonore first line: Company Name,Symbol
			line = dis.readLine();

			int apo1 = 0;
			int apo2 = 0;

			while (((line = dis.readLine()) != null) && (!line.equals(""))) {
				//Name,Symbol, Shares Outstanding(000s),Composite
				// Volume,High,Low,Close,Last Trade Price,Last Trade Date,Last
				// Trade Time
				//ABC Bancorp Capital,BHC.PR,3000,6500,10.8,10.72,
				// ,10.8,5/17/2005,2:59:46 PM
				apo = line.charAt(0);

				apo2 = line.indexOf(apo, 2);
				//status_out(""+apo);
				//ST = new StringTokenizer(record,""+apo);
				//apo = record.charAt(0);
				companyName = line.substring(apo1, apo2);//((String) new
															  // String(ST.nextToken())).trim();
															  // // CompanyName
				line = line.substring(apo2 + 3, line.length());
				//out(record);
				//status_out(record);
				ST = new StringTokenizer(line, ",");
				companyName = companyName.replaceAll("" + apo, " ");
				companyName = companyName.trim();
				// now trim name
				StringTokenizer t = new StringTokenizer(companyName,"-");
				companyName = (t.nextToken()).trim();
				//companyName =companyName.replace("'", "");
				
				currentSymbol = ((String) ST.nextToken(",")).trim(); //StockSymbol/StockLookUp
				currentSymbol = currentSymbol.replaceAll("" + apo, " ");
				currentSymbol = currentSymbol.trim();

				if (currentSymbol.contains(".")) { // trim post-fix
					int m = currentSymbol.indexOf(".");
					currentSymbol = currentSymbol.substring(0, m);

				} // end of post-fix trim

				if(!secs.containsKey(currentSymbol)){
					Security s = new Security( currentSymbol, EXCHANGE_ID, currentSymbol,
							 companyName);
					counter++;
					secs.put(currentSymbol, s);
					logger. info("Adding "+s.symbol+" "+s.exchange+" to system. "+s.name);
					//logger.info("Added Symbol "+currentSymbol+" as "+EXCHANGE_ID+" security "+companyName);
				}
			} // end of while
			logger.info("parse end:["
					+ (new Time(System.currentTimeMillis())).toString() + "] "+counter+" "+EXCHANGE_ID+" securities added");
			return true;
		}// end of try
		catch (Exception badURL) {
			logger.warning(line);
			badURL.printStackTrace();
		}// end of catch
		
		
		return false;
	}
	
}
