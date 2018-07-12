package com.sa.marketslayer.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import com.sa.marketslayer.library.databeans.MarketDay;
import com.sa.marketslayer.library.databeans.Security;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/*
 * @author Cjvh
 * @date 7/16/06
 */

public class PersistFileUtil {

	private static Logger logger = Logger.getLogger("PersistFileUtil");
	private static Properties properties;
	
	public static Properties getProperties(){
		return PersistDBUtil.getProperties();
	}
	
	public static boolean saveObject(String filePath, Object obj){
		XStream xstream=new XStream(new DomDriver());
		
		//System.out.println("done reading xml "+xml);
		try {
			OutputStream closeMe = PersistFileUtil
					.getOutputStream(filePath);
			//if (obj.size() > 0)
				xstream.toXML(obj, closeMe);
			closeMe.close();
			//logger.info("Historical Data saved for "+testSym);
		} catch (IOException e) {
			logger.warning("Failed to store to "+filePath);
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static void loadSingleSecurityFromFS(Map<String, Security> secs, String sym, String exchangeID){
		Security sec = (Security) loadObject(PersistFileUtil.getExchangeFilePath(exchangeID)+"Securities/"+sym+".sec");
		if(sec!=null) secs.put(sym.toUpperCase(), sec);
	}
	
	public static void loadAmexSymbolsFromFS(Map<String, Security> secs){
		String exchange = getProperties().getProperty("AMEX_EXCHANGE_ID");
		loadExchangeSecuritySymbolsFromFS(secs, exchange);
	}
	
	public static void loadPSSymbolsFromFS(Map<String, Security> secs){
		String exchange = getProperties().getProperty("PS_EXCHANGE_ID");
		loadExchangeSecuritySymbolsFromFS(secs, exchange);
	}
	
	public static void loadOTCBBSymbolsFromFS(Map<String, Security> secs){
		String exchange = getProperties().getProperty("OTCBB_EXCHANGE_ID");
		loadExchangeSecuritySymbolsFromFS(secs, exchange);
	}
	
	public static void loadNASDAQSymbolsFromFS(Map<String, Security> secs){
		String exchange = getProperties().getProperty("NASDAQ_EXCHANGE_ID");
		loadExchangeSecuritySymbolsFromFS(secs, exchange);
	}

	
	private static void loadExchangeSecuritySymbolsFromFS(Map<String, Security> secs, String exchange){
		//String exchange = properties.getProperty("AMEX_EXCHANGE_ID");
		String filePath = getSymbolsFilePath(exchange, properties);
		
		File[] files = (new File(filePath)).listFiles();
		int count = 0;
		outer:for(File saved : files){ // all these files exist
			/*//TODO REMOVE THIS
			if(count>50) break outer;*/
			Security s = new Security();
			//boolean success = 
			s = (Security)PersistFileUtil.loadObject(saved.getAbsolutePath());
			//s.setSymbol(saved.getName().replace(".sec", ""));
			
			if(!secs.containsKey(s.symbol)){
				secs.put(s.symbol, s);
				++count;
				//logger.info("Loaded Symbol "+s.symbol+" "+s.name+" "+s.exchange);
			}
		}
		logger.info("Loaded "+count+" "+exchange+" security symbols.");
	}
	
	public static void loadSecuritySymbolsFromFS(Map<String, Security> secs){
		loadNASDAQSymbolsFromFS(secs);
		loadOTCBBSymbolsFromFS(secs);
		loadAmexSymbolsFromFS(secs);
		loadPSSymbolsFromFS(secs);
	}
	
	public static void saveAllSymbols(Map<String, Security> secs){
		for(String sym : secs.keySet()){
			saveSymbol(secs.get(sym));
		}
		
	}
	
	public static void saveSymbol(Security sec){
		//File stock = new File(filePath+sec.symbol+".sec");
		//if(!stock.exists() || (sec.latestTradingDay==null && stock.exists())){/////////////////////////////////////////////////////
			//logger.info("Found new "+sec.exchange+" security. Adding "+sec.name+" "+sec.symbol+" to "+sec.exchange+" Symbols repository. "+filePath+sec.symbol+".sec");
		saveObject(getSymbolsFilePath(sec.exchange)+sec.symbol+".sec", sec);
	}
	
	public static void saveHistorical(Security sec, Map<Date, MarketDay> hist){
		saveObject(getHistoricalFilePath(sec), hist);
	}
	
	public static Object loadObject(String filePath){
		XStream xstream=new XStream(new DomDriver());
		//System.out.println("done reading xml "+xml);
		try {
			InputStream closeMe = PersistFileUtil
					.getInputStream(filePath);
			if(closeMe==null) return null;
			Object obj = xstream.fromXML(closeMe);
			closeMe.close();
			return obj;
		} catch (IOException e) {
			logger.warning("Failed to load from "+filePath);
			e.printStackTrace();
			return null;
		}
	}
	
	private static String getSymbolsFilePath(String exchangeID){
		return getExchangeFilePath(exchangeID)+getProperties().getProperty("SECURITIES");
	}

	public static String getHistoricalFilePath(String exchangeID, String sym){
		properties = getProperties();
		return getExchangeFilePath(exchangeID)+properties.getProperty("HISTORICAL")+sym+".his";
	}
	
	public static String getHistoricalFilePath(Security sec){
		properties = getProperties();
		return getExchangeFilePath(sec.exchange)+properties.getProperty("HISTORICAL")+sec.symbol+".his";
	}
	
	private static String getExchangeFilePath(String exchangeID){
		properties = getProperties();
		if(exchangeID.equals(properties.getProperty("NASDAQ_EXCHANGE_ID"))){
			return properties.getProperty("NASDAQ_SYM_DIR");
		}
		else if(exchangeID.equals(properties.getProperty("OTCBB_EXCHANGE_ID"))){
			return properties.getProperty("OTCBB_SYM_DIR");
		}
		else if(exchangeID.equals(properties.getProperty("AMEX_EXCHANGE_ID"))){
			return properties.getProperty("AMEX_SYM_DIR");
		}
		else if(exchangeID.equals(properties.getProperty("PS_EXCHANGE_ID"))){
			return properties.getProperty("PINKS_SYM_DIR");
		}
		return null;
	}
	
/*	public static boolean saveSecurity(String filePath, Security obj){
		XStream xstream=new XStream(new DomDriver());
		
		//System.out.println("done reading xml "+xml);
		try {
			OutputStream closeMe = PersistFileUtil
					.getOutputStream(filePath);
			//if (obj.size() > 0)
				xstream.toXML(obj, closeMe);
			closeMe.close();
			//logger.info("Historical Data saved for "+testSym);
		} catch (IOException e) {
			logger.warning("Failed to store to "+filePath);
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static Security loadSecurity(String filePath, Security obj){
		XStream xstream=new XStream(new DomDriver());
		//System.out.println("done reading xml "+xml);
		try {
			InputStream closeMe = PersistFileUtil
					.getInputStream(filePath);
			obj = (Security)xstream.fromXML(closeMe);
			closeMe.close();
			///logger.warning("Failed to load from "+filePath);
		} catch (IOException e) {
			logger.warning("Failed to load from "+filePath);
			e.printStackTrace();
			return obj;
		}
		return obj;
	}*/
	
/*	public static void saveSecurity(){
		//TODO check exchange, get
	}*/

	public static OutputStream getOutputStream(String filePath){
		FileOutputStream reader = null;
		try {
			reader = new FileOutputStream(filePath);
			return reader;
			/*String current="";
			StringBuffer accum=new StringBuffer();
			while((current = reader.readLine())!=null){
				accum.append(current);
			}
			reader.close();
			return accum.toString();*/
		} catch (FileNotFoundException e) {
			//logger.warning("Couldn't find file "+filePath);
			//e.printStackTrace();
		}
		catch (IOException ioe) {
			logger.warning("IOException when trying to read "+filePath);
			ioe.printStackTrace();
		}
		//try {
		//	if(reader!=null) reader.close();
		//} catch (IOException e) {
		//	e.printStackTrace();
		//}
		
		return reader;
	}
	
	public static InputStream getInputStream(String filePath){
		FileInputStream reader = null;
		try {
			reader = new FileInputStream(filePath);
			return reader;
			/*String current="";
			StringBuffer accum=new StringBuffer();
			while((current = reader.readLine())!=null){
				accum.append(current);
			}
			reader.close();
			return accum.toString();*/
		} catch (FileNotFoundException e) {
			logger.info("Couldn't find file "+filePath);
			//e.printStackTrace();
		}
		catch (IOException ioe) {
			logger.warning("IOException when trying to read "+filePath);
			ioe.printStackTrace();
		}
		
		//try {
		//	if(reader!=null) reader.close();
		//} catch (IOException e) {
		//	e.printStackTrace();
		//}
		
		return null;
	}
	
	private static String getSymbolsFilePath(String exchangeID, Properties properties){
		return getExchangeFilePath(exchangeID, properties)+properties.getProperty("SECURITIES");
	}

	public static String getHistoricalFilePath(String exchangeID, Properties properties, String sym){
		return getExchangeFilePath(exchangeID, properties)+properties.getProperty("HISTORICAL")+sym+".his";
	}
	
	private static String getExchangeFilePath(String exchangeID, Properties properties){
		if(exchangeID.equals(properties.getProperty("NASDAQ_EXCHANGE_ID"))){
			return properties.getProperty("NASDAQ_SYM_DIR");
		}
		else if(exchangeID.equals(properties.getProperty("OTCBB_EXCHANGE_ID"))){
			return properties.getProperty("OTCBB_SYM_DIR");
		}
		else if(exchangeID.equals(properties.getProperty("AMEX_EXCHANGE_ID"))){
			return properties.getProperty("AMEX_SYM_DIR");
		}
		else if(exchangeID.equals(properties.getProperty("PS_EXCHANGE_ID"))){
			return properties.getProperty("PINKS_SYM_DIR");
		}
		return null;
	}
	
	public void loadSecurityHistoricalsFromFS(Map<String, Security> secs, Map<String, Map<Date,MarketDay>> hists){
		
		//String[] toLoad = secs.keySet().toArray();
		outer:for(String toLoad : secs.keySet()){
			Security sec = secs.get(toLoad);
			/*if(sec.exchange.equals(properties.getProperty("PS_EXCHANGE_ID")))// we dont have data yet
				continue outer;*/
			
			loadSecurityHistoryFromFS(sec, hists);			
		}
	}
	
	public void loadSecurityHistoryFromFS(Security sec, Map<String, Map<Date,MarketDay>> hists){
		String fileName = getHistoricalFilePath(sec.exchange, properties, sec.symbol);
		File history = new File(fileName);
		if (history.exists()) {
			Map<Date, MarketDay> secHistory = (Map<Date, MarketDay>) PersistFileUtil
					.loadObject(fileName);
			if (!hists.containsKey(sec.symbol)) {
				hists.put(sec.symbol, secHistory);
			} else
				logger
						.warning(sec.symbol
								+ " already has loaded historical data. Data load aborted.");
		}			
	}
	
	public static Map<Date,MarketDay> loadSingleSecurityHistoryFromFS(Security sec){
		String fileName = getHistoricalFilePath(sec);
		File history = new File(fileName);
		if (history.exists()) {
			Map<Date, MarketDay> hist = (Map<Date, MarketDay>) PersistFileUtil
					.loadObject(fileName);
		return hist;
		}
		else return new HashMap<Date, MarketDay>();
	}
	
	public void saveSymbols(String exchangeID, Map<String, Security> secs){
		
		String filePath = getSymbolsFilePath(exchangeID, properties);
		

		Security sec = null;
		for(String sym : secs.keySet()){
			sec = secs.get(sym);
			if(exchangeID.equals(sec.exchange)){
				File stock = new File(filePath+sec.symbol+".sec");
				if(!stock.exists() || (sec.latestTradingDay==null && stock.exists())){/////////////////////////////////////////////////////
					logger.info("Found new "+exchangeID+" security. Adding "+sec.name+" "+sec.symbol+" to "+exchangeID+" Symbols repository. "+filePath+sec.symbol+".sec");
					PersistFileUtil.saveObject(filePath+sec.symbol+".sec", sec);
				}
			}
		}
	}
	
	public static String openFile(String filePath){
		FileReader reader = null;
		try {
			reader = new FileReader(filePath);
			String current="";
			StringBuffer accum=new StringBuffer();
			while(accum.append(reader.read())!=null){
				//accum.append(reader.readLine());
			}
			reader.close();
			return accum.toString();
		} catch (FileNotFoundException e) {
			logger.info("Couldn't find file "+filePath);
			//e.printStackTrace();
		}
		catch (IOException ioe) {
			logger.warning("IOException when trying to read "+filePath);
			ioe.printStackTrace();
		}
		
		try {
			if(reader!=null) reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void overWriteFileWithContent(String filePath, String content){
		BufferedWriter writer=null;
		try {
			writer = new BufferedWriter(new FileWriter(filePath));
			writer.write(content);
			
			writer.close();
		} catch (FileNotFoundException e) {
			logger.warning("Couldn't find file "+filePath);
			try {
				logger.warning("Attempting to create file "+filePath);
				writer = new BufferedWriter(new FileWriter(new File(filePath)));
				writer.write(content);
				writer.close();
			} catch (IOException e1) {
				logger.severe("Couldn't find file or create file "+filePath);
				e1.printStackTrace();
			}
			
		}
		catch (IOException ioe) {
			logger.warning("IOException when trying to write "+filePath);
			ioe.printStackTrace();
		}
		
		try {
			if(writer!=null) writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
