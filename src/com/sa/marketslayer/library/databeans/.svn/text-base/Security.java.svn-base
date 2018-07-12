package com.sa.marketslayer.library.databeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Carl von Havighorst
 * 8/4/6
 */

public class Security implements Comparable, Serializable {

	private static final long serialVersionUID = -5672516484396104433L;

	private Long id;
	public String lookup;
	public String exchange;
	public String symbol;
	public String web;
	public String name;
	public MarketDay latestTradingDay;
	//public Date lastDailyUpdate;
	///////public Map<Date, MarketDay> historicalTradeData;
	//public Map<Date, ArrayList<String>> marketMakerActivityData;
	////////public Map<Date, News> newsData;
	//public Map<Date, Integer> employeeData;
	///////public Map<Date, SearchReport> webMonitor; 
	
	public int compareTo(Object other){
		return ((Security)other).symbol.compareTo(this.symbol);
	}
	
	public MarketDay getLatestTradingDay() {
		return latestTradingDay;
	}

	public Security(){
	}
	
	public Security(String lookup, String exchange, String symbol, String web, String name, MarketDay latestTradingDay) {
		super();
		this.lookup = lookup;
		this.exchange = exchange;
		this.symbol = symbol;
		this.web = web;
		this.name = name;
		this.latestTradingDay = latestTradingDay;
	}

	public void setLatestTradingDay(MarketDay latestTradingDay) {
		this.latestTradingDay = latestTradingDay;
	}
	
	public Security(String lookup, String exchange, String symbol, String web, String name) {
		super();
		this.lookup = lookup;
		this.exchange = exchange;
		this.symbol = symbol;
		this.web = web;
		this.name = name;
	}

	public Security(String lookup, String exchange, String symbol, String name) {
		super();
		this.lookup = lookup;
		this.exchange = exchange;
		this.symbol = symbol;
		this.name = name;
		//historicalTradeData = new HashMap<Date, MarketDay>();
		//marketMakerActivityData = new HashMap<Date, ArrayList<String>>();
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getLookup() {
		return lookup;
	}

	public void setLookup(String lookup) {
		this.lookup = lookup;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getWeb() {
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String toString() {
		/*public String lookup;
		public String exchange;
		public String symbol;
		public String web;
		public String name;*/
		return  ""+lookup+" "+
				name+" "+
				exchange;
	}
}

class SearchReport{
	
	public int numEmployees;
	
	public int msSymSearch;
	public int ySymSearch;
	public int gSymSearch;
	
	public int msNameSearch;
	public int yNameSearch;
	public int gNameSearch;
	
	public ArrayList<String> marketMakers;

	public SearchReport(int msSymSearch, int symSearch, int symSearch2, int msNameSearch, int nameSearch, int nameSearch2) {
		this.msSymSearch = msSymSearch;
		ySymSearch = symSearch;
		gSymSearch = symSearch2;
		this.msNameSearch = msNameSearch;
		yNameSearch = nameSearch;
		gNameSearch = nameSearch2;
	}
	
}

class News{
	public String title ="";
	public String url = null;
	public Date date = null;
}
