package com.sa.marketslayer.library.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.hibernate.Session;

import com.sa.marketslayer.library.databeans.MarketDay;
import com.sa.marketslayer.library.databeans.Security;
import com.sa.marketslayer.library.parsers.AMEXParser;
import com.sa.marketslayer.library.parsers.HDParser;
import com.sa.marketslayer.library.parsers.NasdaqParser;
import com.sa.marketslayer.library.parsers.OTCBBParser;
import com.sa.marketslayer.library.parsers.PinkSheetsParser;
import com.sa.marketslayer.utils.PersistDBUtil;
import com.sa.marketslayer.utils.PersistFileUtil;

public class SecuritiesManager {

	private static SecuritiesManager sm = new SecuritiesManager();
	
	private static Logger logger = Logger.getLogger("SecuritiesManager");
	private static Properties properties = PersistDBUtil.getProperties();
	private static Map<String, Security> securities;
	private static Map<String, Security> activeSecurities;
	
	public SecuritiesManager(){
		securities = PersistDBUtil.loadSecurities();
	}
	
	public static void main(String[] args){
		
		//SecuritiesManager sm = new SecuritiesManager();
		HistoricalDataManager hm = new HistoricalDataManager();
		if(args!=null&&args.length>0){
			if((args[0].toUpperCase()).equals("UPDATEFSHISTORY"))
				sm.updateDailyDataForAllSecuritiesFS();
			else if((args[0].toUpperCase()).equals("UPDATEHISTORY")){
				int count = hm.updateDailyDataForAllActiveSecurities(sm.updateActiveSecurities());
			}
			else if((args[0].toUpperCase()).equals("CLEANALLHISTORIES")){
				int count = hm.cleanDailyData(sm.securities, true);
			}
/*			else if((args[0].toUpperCase()).equals("CLEANHISTORIES")){
				int count = hm.cleanDailyData(sm.activeSecurities, true);
			}*/
			else if((args[0].toUpperCase()).equals("REPORTALLBADHISTORIES")){
				int count = hm.cleanDailyData(sm.securities, false);
				System.out.println("bad data records: "+count);
			}
/*			else if((args[0].toUpperCase()).equals("REPORTBADHISTORIES")){
				int count = hm.cleanDailyData(sm.activeSecurities, false);
			}*/
		}
		
		//ohshit();
		//sm.updateActiveSecurities();
/*		for(Security s : securities.values()){
			System.out.println(s.toString());
		}
		System.out.println(securities.size()+" found in system (DB).");
		*/
		//sm.tryToLoadMoreHistoricalData();
	}
	
/*	public static boolean ohshit(){
		for( Security s : securities.values()){
			String firstLetterName = s.name.substring(0,1);
			if(!s.symbol.startsWith(firstLetterName))
				System.out.println(s.symbol+" != "+s.name);
		}
		
		return true;
	}*/
	
	public static Map<String, Security> updateActiveSecurities(){
		int before = securities.size();
		
		
		Map<String, Security> activeSecs = new HashMap<String, Security>();
		
		Map<String, Security> amexNew = AMEXParser.updateSymbolsGetDiff(securities, activeSecs);
		Map<String, Security> nasNew = NasdaqParser.updateSymbolsGetDiff(securities, activeSecs);
		Map<String, Security> otcNew = OTCBBParser.updateSymbolsGetDiff(securities, activeSecs);
		Map<String, Security> psNew = PinkSheetsParser.updateSymbolsGetDiff(securities, activeSecs);
		int after = securities.size();
		logger.info(before+" securities found before update");
		logger.info(securities.size()+" securities found in system after update");
		logger.info("----> "+activeSecs.size()+" ACTIVE securities found after update <----");
		
		if(before==after){
			logger.info("No new securities found during update");
		}
		else{
			logger.info((after-before)+" new securities found.\n"+
					amexNew.size()+" new AMEX, "+
					nasNew.size()+" new NASDAQ, "+
					otcNew.size()+" new OTCBB, "+
					psNew.size()+" new Pink Sheet securities found");
			
			String newSyms ="";
			newSyms += "\nNew AMEX: \n";
			for(Security sec: amexNew.values()){
				newSyms += sec.symbol+"\n";
			}
			
			newSyms += "New NASDAQ: \n";
			for(Security sec: nasNew.values()){
				newSyms += sec.symbol+"\n";
			}
			
			newSyms += "New OTCBB: \n";
			for(Security sec: otcNew.values()){
				newSyms += sec.symbol+"\n";
			}
			
			newSyms += "New PINKSHEETS: \n";
			for(Security sec: psNew.values()){
				newSyms += sec.symbol+"\n";
			}
			
			logger.info(newSyms);
		}
	activeSecurities = activeSecs; // SIDE EFFECT !
	return activeSecs;
	}
	
	public void tryToLoadMoreHistoricalDataToFS(){
		logger.info("Loading Securities in System.");
		
		Map<String, Security> secs = new HashMap<String, Security>();
		
		PersistFileUtil.loadSecuritySymbolsFromFS(secs);
		
		logger.info("Successfully loaded "+secs.size()+" securities from filesystem. Updating symbols.");
/*		NasdaqParser nasdaqParser = new NasdaqParser();
		AMEXParser amexParser = new AMEXParser();
		OTCBBParser otcbbParser = new OTCBBParser();
		PinkSheetsParser pinksParser = new PinkSheetsParser();
		
		boolean nas = nasdaqParser.updateSymbols(secs);
		boolean amex = amexParser.updateSymbols(secs);
		boolean otc = otcbbParser.updateSymbols(secs);
		boolean ps = pinksParser.updateSymbols(secs);*/	
		updateYHistoricals(secs);
		//PersistFileUtil.saveAllSymbols(secs);
	}
	
	public void updateDailyDataForAllSecuritiesFS(){
		logger.info("Daily Trading Data Update started. Loading Securities in System.");
		
		Map<String, Security> secs = new HashMap<String, Security>();
		
		PersistFileUtil.loadSecuritySymbolsFromFS(secs);
		
		logger.info("Successfully loaded "+secs.size()+" securities from filesystem. Updating symbols.");
		NasdaqParser nasdaqParser = new NasdaqParser();
		AMEXParser amexParser = new AMEXParser();
		OTCBBParser otcbbParser = new OTCBBParser();
		PinkSheetsParser pinksParser = new PinkSheetsParser();
		
		boolean nas = nasdaqParser.updateSymbols(secs);
		boolean amex = amexParser.updateSymbols(secs);
		boolean otc = otcbbParser.updateSymbols(secs);
		boolean ps = pinksParser.updateSymbols(secs);
		logger.info((nas&&amex&&otc&&ps?"Successfully ":"")+"Updated "+secs.size()+" securities from filesystem. Updating symbols. nas:"+nas+" amex:"+amex+" otc:"+otc+" ps:"+ps);
		
		// now update daily data, one by one
		int[] rsts = updateDailyDatasOnFS(secs);
		
		System.out.println("Successes: "+rsts[0]+" Failures: "+rsts[1]+" total: "+(rsts[0]+rsts[1])+" ?= "+secs.size());

		
/*		for(String sym : secs.keySet()){
			Security sec = secs.get(sym);
			Map<Date, MarketDay> hist = new HashMap<Date, MarketDay>();
			PersistFileUtil.loadSingleSecurityHistoryFromFS(sec, hist);
			int prev = hist.size();
			System.out.println(updateDailyData(sec, hist)?"SUCCESSFULLY updated history for "+sec.symbol);
		}*/
		
//		PersistFileUtil.saveAllSymbols(secs);
	}
	
	public int updateYHistoricals(Map<String, Security> secs){
		String filePath = null;
		int countUpdatesOrAdds = 0;
		HDParser hdp = new HDParser();
		int previousSize = 0;
		HashMap<Date, MarketDay> historicalTradeData = null;
		long start = 0;
		long end = 0; 
		for(String s : secs.keySet()){

			Security sec = secs.get(s);
			inner:if(!properties.getProperty("PS_EXCHANGE_ID").equals(sec.exchange)){// found a non pink sheet
				//attempt to load file
				previousSize = 0;
				filePath = PersistFileUtil.getHistoricalFilePath(sec);
				File f = new File(filePath);
				historicalTradeData = new HashMap<Date, MarketDay>();
				if(f.exists()){
					historicalTradeData = (HashMap<Date, MarketDay>)PersistFileUtil.loadObject(filePath);
					previousSize = historicalTradeData.size();
				}

				if(previousSize>100 && f.exists()){
					//System.out.println("Enough data already exists for "+sec.symbol+" "+sec.exchange);
					previousSize = 0;
					historicalTradeData = null;
					break inner;
				}
				
				// give yhoo a break
				start = System.currentTimeMillis();
				end = start + 100;// end in milli seconds
				while(System.currentTimeMillis()<end){
				}
				
				//System.out.print(".");
				hdp.downloadUpdateSaveHistorical(sec, historicalTradeData);
				if(previousSize!=historicalTradeData.size()) logger.info(sec.symbol+" "+sec.exchange+" previously "+previousSize+" days, now "+historicalTradeData.size());
				if(historicalTradeData.size()>previousSize){
					if(previousSize==0)
						logger.info("ADDING "+previousSize+" --> "+historicalTradeData.size()+" days, history for "+sec.symbol+" "+sec.exchange);
					else
						logger.info("UPDATING "+previousSize+" --> "+historicalTradeData.size()+" days, history for "+sec.symbol+" "+sec.exchange);

					PersistFileUtil.saveObject(filePath, historicalTradeData);
					countUpdatesOrAdds++;
				}
				previousSize = 0;
				historicalTradeData = null;
				
			}

		}
		return countUpdatesOrAdds;
	}
	

	
	public boolean updateDailyDataOnFS(Security sec, Map<Date,MarketDay> historicalTradeData){
		int prev = historicalTradeData.size();
		MarketDay success = HDParser.updateDailyData(sec, historicalTradeData);
		int after = historicalTradeData.size();
		if(success!=null && after-1==prev){
			return true;
		}
		//else if(!success) logger.info("Failed to obtain new data for "+sec.symbol+" "+sec.exchange);
		//else logger.info("No new Daily Data found for "+sec.symbol+" "+sec.exchange);
		return false;
	}
	
	public int[] updateDailyDatasOnFS(Map<String, Security> secs){
		int[] rst = new int[2];
		int counter = 1;
		
		for(String sym : secs.keySet()){
			Security sec = secs.get(sym);
			Map<Date, MarketDay> hist = (HashMap<Date, MarketDay>)PersistFileUtil.loadSingleSecurityHistoryFromFS(sec);
			if(hist==null)
				hist = new HashMap<Date, MarketDay>();
			
			int prev = hist.size();
			boolean success = updateDailyDataOnFS(sec, hist);
			
			if(success && (prev+1)==hist.size()){
				logger.info((counter++)+": Updated from "+prev+" to "+hist.size()+" MarketDays for "+sec.symbol+" "+sec.exchange+" "+sec.latestTradingDay.toString()+" ++++++++++ SAVED ++++++++++");
				PersistFileUtil.saveHistorical(sec, hist);
				PersistFileUtil.saveSymbol(sec);
				++rst[0];
			}
			else ++rst[1];
				
		}
		
		return rst;
	}


	
/*	public Security loadSecurity(String sym, String exchangeID){
		Security sec = (Security) PersistFileUtil.loadObject(getSymbolsFilePath(exchangeID, properties));
		return sec;
	}*/
	
/*	public int[] updateAndSaveDailyData(){
		int[] report = new int[3];
		
		
		return report;
	}*/
}
