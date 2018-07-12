package com.sa.marketslayer.library.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.sa.marketslayer.library.databeans.MarketDay;
import com.sa.marketslayer.library.databeans.Security;
import com.sa.marketslayer.library.parsers.HDParser;
import com.sa.marketslayer.utils.PersistDBUtil;
import com.sa.marketslayer.utils.PersistFileUtil;

public class HistoricalDataManager {

	Logger logger = Logger.getLogger("HistoricalDataManager");
	
	public static int validateAllDateConsistencies(Map<String, Map<Date,MarketDay>> hists){
		int collisions = 0;
		
		for(String crt : hists.keySet()){
			Map<Date,MarketDay> crtSecCheck = hists.get(crt);
			int crtColls = validateDateConsistency(crtSecCheck);
			System.out.println(""+crtColls+" Collisions and bad dates found for "+crt);
			collisions+=crtColls;
		}
		return collisions;
	}
	
	public int updateDailyDataForAllActiveSecurities(Map<String, Security> activeSecurities){
		int counter =0;
		for(Security sec : activeSecurities.values()){
			Map<Date, MarketDay> history = PersistDBUtil.load_date_md_map(sec, sec.getId());
			
			MarketDay md = HDParser.updateDailyData(sec, history);
			if(md!=null){
				PersistDBUtil.store_md(md, sec.symbol);
				++counter;
			}	
		}
		//logger.info("----> "+counter+" new MarketDays found <----");
		
		return counter;
	}
	
	// Big O(n^2)
	public int cleanDailyData(Map<String, Security> securities, boolean deleteBadRecords){
		int counter =0;
		int it_counter=0;
		ArrayList<MarketDay> killMe = new ArrayList<MarketDay>();
		
		for(Security sec : securities.values()){
			Map<Date, MarketDay> history = PersistDBUtil.load_date_md_map(sec, sec.getId());
			it_counter++;
			if(it_counter%100==0)
				logger.info("Iterated over daily data of "+it_counter+" securities.");
			for(MarketDay md : history.values()){
				if(md.date==null||md.volume<=0){
					// report
					counter++;
					logger.info(counter+" data integrity "+sec.lookup+": "+(md.date==null?"md.date==null!!! ":"")
								+ (md.volume<=0?(md.date==null?"":""+md.date)+" volume: "+md.volume:""));
					// delete this record
					if(deleteBadRecords){	
						PersistDBUtil.deleteMarketDay(md);
					}
					//killMe.add(md);
				}
			}
/*			MarketDay md = HDParser.updateDailyData(sec, history);
			if(md!=null){
				PersistDBUtil.store_md(md, sec.symbol);
				++counter;
			}	*/
		}
/*		if(deleteBadRecords){	
			for(MarketDay md : killMe){
				logger.info("----> About to delete "+counter+" MarketDays with bad data integrity <----");
				PersistDBUtil.deleteMarketDay(md);
			}
		}*/
		
		
		return counter;
	}
	
/*	
    public void storeMarketDay(MarketDay md) {
    	if(md instanceof u_md){
    		System.out.println("this is not the problem");
            Session session = PersistDBUtil.getSessionFactory().openSession();
            session.beginTransaction();
            u_md u_md= (u_md) md;
            session.save(u_md);
            session.getTransaction().commit();
    	}
    }*/
    
   
/*	public static int validateAllDateConsistencies(Security sec, Map<Date,MarketDay> hist){
		
		int collisions = validateDateConsistency(hist);
		return collisions;
	}*/
	
	public static int validateDateConsistencies(Map<String, Security> secs){
		int collisions = 0;
		for(String sym: secs.keySet()){
			Security sec = secs.get(sym);
			collisions+=validateDateConsistency(sec);
		}
		
		return collisions;
	}
	
	public static int validateDateConsistency(Security sec){
		int collisions = 0;
		Map<Date, MarketDay> hist = PersistFileUtil.loadSingleSecurityHistoryFromFS(sec);// Object(SecuritiesManager.getHistoricalFilePath(sec.exchange, PersistFileUtil.getProperties(), sec.symbol));
		if(hist==null) return 0;
		int prev=hist.size();
		collisions = validateDateConsistency(hist);
		int after=hist.size();
		if(collisions>0) System.out.println(""+prev+" trading days. collision:"+collisions+" after:"+after+" for: "+sec.symbol+" "+sec.exchange);
/*		if(after<prev) {//save
			PersistFileUtil.saveHistorical(sec, hist);
			System.out.println("Saved historical data for "+sec.symbol);
		}*/
	
		return collisions;
	}
	
	public static int validateDateConsistency(Map<Date, MarketDay> hist){
		int collisions = 0;
		
		Set keySet = hist.keySet();
		Date[] dates = new Date[keySet.size()];
		dates = (Date[])keySet.toArray(dates);
		Arrays.sort(dates);
		
		HashSet hs = new HashSet();
		for(Date d : dates){
			MarketDay today = hist.get(d);
			
			if(!today.date.equals(d)){
				System.out.println("FATAL ERROR: "+today.date+" != "+d+" for "+today);
				//System.exit(0);
				collisions++;
			}
			if(hs.contains(d)){
				System.out.println("Collission found: "+today.date+" != "+d+" for "+today);
				collisions++;
			}
			if(d.getYear()>2000){
				System.out.println("Bad Year found, removing: (Y: "+today.date.getYear()+" M:"+today.date.getMonth()+" D:"+today.date.getDate()+")"+today.date+" != "+d+" for "+today);
				collisions++;
				hist.remove(d);
			}
			// analyze
			int day = d.getDate();
			int month= d.getMonth();
			int year = d.getYear();
			//System.out.println(d.toString()+" "+today.toString());
			hs.add(d);
		}
		return collisions;
	}
	
	public static void main(String[] args){
		
		HashMap<String, Security> allSecs = new HashMap<String, Security>();
		PersistFileUtil.loadSecuritySymbolsFromFS(allSecs);
		validateDateConsistencies(allSecs);
	}
/*	public static void saveAllHistories(){
		
	}*/
}
