package com.sa.marketslayer.library.parsers;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.sa.marketslayer.library.databeans.MarketDay;
import com.sa.marketslayer.library.databeans.Security;
import com.sa.marketslayer.utils.ParseUtil;

public class HDParser {
	
	private static Logger logger = Logger.getLogger("HDParser");

	public static String url = "http://ichart.finance.yahoo.com/table.csv?s=";
	public static String urlDaily = "http://finance.yahoo.com/d/quotes.csv?s=**LOOKUP**&f=sl1d1t1c1ohgv&e=.csv";
	
	// http://finance.yahoo.com/d/quotes.csv?s=AAPL+ROKU+LULU&f=sl1d1t1c1ohgva2m3m4t8kjf6j1j2eb4rs7&e=.csv
	
	public static MarketDay updateDailyData(Security stock, Map<Date, MarketDay> historicalTradeData){
		
		boolean done = false;
		int redo = 0;
		//float prevClose = 0;
		boolean retry = true;
		while(retry){
			retry = false;
			try {
				//"GOOG",368.50,"8/11/2006","4:00pm",-5.70,374.35,375.28,368.00,3768869
				//sym, CLOSE, DATE, time, change, OPEN, HIGH, LOW, VOLUME
				while(redo<=6 && done==false){
					URL t = new URL(urlDaily.replace("**LOOKUP**", stock.lookup));
					String record = "";
					URLConnection con1 = t.openConnection();
					DataInputStream dis = new DataInputStream(con1.getInputStream());
					record = dis.readLine();
					if(record.contains("Invalid Ticker Symbol"))
						return null;
					//System.out.println(record);
					if(record != null && record.length()>30){// seams to be good record
						StringTokenizer parse = new StringTokenizer(record, ",");
						String symbol = parse.nextToken().replace("\"", "");//"GOOG"-->GOOG
/*						if(!symbol.startsWith(stock.symbol)) {
							//System.out.println();
							throw new RuntimeException("Wrong Daily Data found for "+symbol+" <--> "+stock.symbol);
						}*/
						String _close = parse.nextToken();
						float close = !_close.contains("N")?Float.parseFloat(_close):0;//Float.parseFloat(_close);
						float gap = 0;
	/*					if(prevClose!=0) gap = close - prevClose;
						prevClose = close;*/
						StringTokenizer dater = new StringTokenizer(parse.nextToken().replace("\"", ""), "/");
						
						int month = 0;
						int day = 0;
						int year = 0;
						Date date = null;
						
						String _month = dater.nextToken();
						String _day = dater.nextToken();
						String _year = null;
						if(dater.hasMoreTokens()) _year = dater.nextToken();
						
						if(_year!=null|| !_month.startsWith("N")){
							// date appears ok
							month = Integer.parseInt(_month);
							day = Integer.parseInt(_day);
							year = Integer.parseInt(_year);
							date = new Date(year-1900, month-1, day, 16 ,0 ,0 );
							redo=7;
							done = true;
						}
						else return null;//date = new Date(106, 11, 1, 16 ,0 ,0 );
						
						//logger.info(date.toString());
						parse.nextToken();//"4:00pm" - time
						parse.nextToken();//-5.70 - change
						
						String _open = parse.nextToken();
						String _high = parse.nextToken();
						String _low = parse.nextToken();
						String _vol = parse.nextToken();
						
						float open = !_open.contains("N")?Float.parseFloat(_open):0;
						float high = !_high.contains("N")?Float.parseFloat(_high):0;
						float low = !_low.contains("N")?Float.parseFloat(_low):0;
						long vol = !_vol.contains("N")?Long.parseLong(_vol):0;
						//Long.parseLong(parse.nextToken());
						
						MarketDay md = new MarketDay(date,open,gap,close,high,low, vol);
						md.setMySecId(stock.getId());
						
						//System.out.println(record);
						//System.out.println(date.toString()+" "+md.toString());
						//logger.info(date.toString()+" "+md.toString());
						if(!historicalTradeData.containsKey(date)){
							if(md.volume>0){
								if(stock.latestTradingDay==null || (!stock.latestTradingDay.equals(date)) ){
									//System.out.println("Updated last trading day on "+stock.symbol+" "+stock.exchange+" "+md.toString());
									stock.setLatestTradingDay(md);
								}
								historicalTradeData.put(date,md);
								return md; // return to save
							}
							else return null;//dont save, but done

							//logger.info("Successfully loaded new daily data for "+stock.symbol+" "+stock.exchange);
						}
						

						return null;
					}
					else{
						long start = System.currentTimeMillis();
						long end = start + 350;// end in milli seconds
						while(System.currentTimeMillis()<end){
						}
						redo++;
					}
				}
				
				return null;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} 
			catch(ConnectException ce){
		 		//ce.printStackTrace();
				logger.warning("Connection timed out for "+stock.symbol+" "+stock.exchange+" "+urlDaily+"\nRetrying after 3 seconds...");
				retry = true;
				long start = System.currentTimeMillis();
				long end = start + 3000;// end in milli seconds
				while(System.currentTimeMillis()<end){
				}
		 	}
			catch(IOException ioe){
		 		//ce.printStackTrace();
				logger.warning("IOException for "+stock.symbol+" "+stock.exchange+" "+urlDaily+"\nRetrying after 3 seconds...");
				retry = true;
				long start = System.currentTimeMillis();
				long end = start + 3000;// end in milli seconds
				while(System.currentTimeMillis()<end){
				}
		 	}catch(Exception ie){
		 		ie.printStackTrace();
		 	}
			if(!retry)
				return null;
		}
		return null;
	}
	
	public boolean downloadUpdateSaveHistorical(Security stock, HashMap<Date, MarketDay> historicalTradeData){
	 	try{
	 		// 10 redos, then give up
	 		
	 		int counter = 0;
	 		int redo = 0;
	 		int maxredo = 3;
			while(counter < 10 && redo<maxredo){
		 		URL t = new URL(url+stock.lookup);
				String record = "";
				
				URLConnection con1 = t.openConnection();
				DataInputStream dis = new DataInputStream(con1.getInputStream());
				float prevClose = 0;
				///logger.info(url+stock.lookup+" URLDataStream established.");
				

				//logger.info(fo.exists()?"Opened ":"Created "+savDir+"/"+stock.exchange+"/"+stock.symbol+".his ");                        
				record = dis.readLine(); // read in header
				if(record==null||record.length()<=1) System.out.println("Nothing Found");
				//System.out.println(record);
				inner:while(record!=null){
					record = dis.readLine();
					if(record == null ||record.startsWith("31-Dec-04")) {
						//logger.info("Aborting data parse. Found end of data or hit pivot[31-Dec-04] for "+stock.symbol);
						redo = maxredo; break inner;
					}
					StringTokenizer st = new StringTokenizer(record, ",");

					String t1 = st.nextToken();
					//System.out.println("t1 "+t1);
					
					StringTokenizer dater = new StringTokenizer(t1, "-");
					
					int day = Integer.parseInt(dater.nextToken());
					String tmpMonth = dater.nextToken();
					int month = ParseUtil.monthStringToInt(tmpMonth);
					int year = Integer.parseInt(dater.nextToken());
					
					Date date = new Date(year+100, month-1, day, 16 ,0 ,0 );
					////System.out.println("month: "+(month-1)+" day: "+day+" year"+year+100);
					
					float open = Float.parseFloat(st.nextToken());
					float high = Float.parseFloat(st.nextToken());
					float low = Float.parseFloat(st.nextToken());
					float close = Float.parseFloat(st.nextToken());
					float gap = ((prevClose!=0)?(open-prevClose):0);
					prevClose = close;
					long vol = Long.parseLong(st.nextToken());
					
					MarketDay md = new MarketDay(date,open,gap,close,high,low, vol);
					
					if(!historicalTradeData.containsKey(date)){
						historicalTradeData.put(date,md);
					}
					////System.out.println("Date Found: "+date+" o:"+open+" g:"+gap+" h:"+high+" l:"+low+" c:"+close+" v:"+vol);
					counter++;
				}
				//fw.close();
				dis.close();
				// now double check
				//fr = new FileReader(fo);
				//fo = new File(savDir+"/"+stock.exchange+"/"+stock.symbol+".his");
				//FileInputStream fis = new FileInputStream(fo);
				//dis = new DataInputStream(fis);
				//record = dis.readLine();
				//record = dis.readLine();// if this is null, REDO
				if(counter <50 && redo<maxredo){
				if(record==null) redo++;
				else if(record.startsWith("31-Dec-04"))
					redo = maxredo;
				else
					redo++;
				}
			    //tmp.myChart = stock.symbol+"_"+date+".png";
			}
			//logger.info(counter+" days of data found online for "+stock.symbol);
			return true;
	 	}
	 	catch(FileNotFoundException fnfe){
	 		//fnfe.printStackTrace();
	 		//logger.info("No historical data found online for "+stock.symbol);
	 	}
	 	catch(Exception ie){
	 		ie.printStackTrace();
	 	}
	 	return false;
	 }

}
