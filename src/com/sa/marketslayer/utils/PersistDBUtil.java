package com.sa.marketslayer.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.sa.marketslayer.library.databeans.MarketDay;
import com.sa.marketslayer.library.databeans.Security;
import com.sa.marketslayer.library.databeans.marketdays.a_md;
import com.sa.marketslayer.library.databeans.marketdays.b_md;
import com.sa.marketslayer.library.databeans.marketdays.c_md;
import com.sa.marketslayer.library.databeans.marketdays.d_md;
import com.sa.marketslayer.library.databeans.marketdays.e_md;
import com.sa.marketslayer.library.databeans.marketdays.f_md;
import com.sa.marketslayer.library.databeans.marketdays.g_md;
import com.sa.marketslayer.library.databeans.marketdays.h_md;
import com.sa.marketslayer.library.databeans.marketdays.i_md;
import com.sa.marketslayer.library.databeans.marketdays.j_md;
import com.sa.marketslayer.library.databeans.marketdays.k_md;
import com.sa.marketslayer.library.databeans.marketdays.l_md;
import com.sa.marketslayer.library.databeans.marketdays.m_md;
import com.sa.marketslayer.library.databeans.marketdays.n_md;
import com.sa.marketslayer.library.databeans.marketdays.o_md;
import com.sa.marketslayer.library.databeans.marketdays.p_md;
import com.sa.marketslayer.library.databeans.marketdays.q_md;
import com.sa.marketslayer.library.databeans.marketdays.r_md;
import com.sa.marketslayer.library.databeans.marketdays.s_md;
import com.sa.marketslayer.library.databeans.marketdays.t_md;
import com.sa.marketslayer.library.databeans.marketdays.u_md;
import com.sa.marketslayer.library.databeans.marketdays.v_md;
import com.sa.marketslayer.library.databeans.marketdays.w_md;
import com.sa.marketslayer.library.databeans.marketdays.x_md;
import com.sa.marketslayer.library.databeans.marketdays.y_md;
import com.sa.marketslayer.library.databeans.marketdays.z_md;
import com.sa.marketslayer.library.managers.HistoricalDataManager;
import com.sa.marketslayer.library.managers.SecuritiesManager;

public class PersistDBUtil {

	private static String CONFIG_FILE_NAME = "./config/config.cfg";
	private static Logger logger = Logger.getLogger("PersistDBUtil");
	private static Properties properties;
	static Connection my_DB_Connection;
	
    private static final SessionFactory sessionFactory;

    static {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            sessionFactory = new Configuration().configure().buildSessionFactory();
            logger.info("---------------> Successfully spawned SessionFactory <---------------");
            CONFIG_FILE_NAME = "./config/config.cfg" ;
            if(getProperties()==null){
            	logger.info("---------------> Failed to load properties <---------------");
               	System.exit(0);
            }
            else{
            	logger.info("---------------> Successfully loaded "+getProperties().size()+" properties <---------------");
            }

        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
	
	public static Properties getProperties(){
		if(properties==null){
			properties = new Properties();
			try {
				File f = new File(CONFIG_FILE_NAME);
				FileInputStream is = new FileInputStream(f);
				properties.load(is);
			} catch (FileNotFoundException e) {
				logger.warning("Could not find Properties file "+CONFIG_FILE_NAME);
				e.printStackTrace();
			}
			catch (IOException ioe) {
				logger.warning("Failed to load Properties from file "+CONFIG_FILE_NAME);
				ioe.printStackTrace();
			}
		}

		return properties;
	}	

	public static void main(String[] args){
		
		logger.info("PersistDBUtil main, properties: "+properties.toString());
		
		//Security sec = PersistDBUtil.loadSecurity("UCHB.ob");
		//System.out.println("TestLoad: "+sec.toString());
		//populateSecuritiesTableFromFSData();
	}
	
	/**
	 * Loads all securities from Filesystem,
	 * populates DB table (Securities)
	 * then loads HistoricalData from FS for each symbol, and
	 * stores it in the db tables a_md to z_md, depending on first character
	 * in security's symbol.
	 */
	public static void populateSecuritiesTableFromFSData(){
		HashMap<String, Security> secs = new HashMap<String, Security>();
		
		PersistFileUtil.loadSecuritySymbolsFromFS(secs);
		SecuritiesManager sm = new SecuritiesManager();
		HistoricalDataManager hm = new HistoricalDataManager();
		
		for( String key: secs.keySet()){
			Security sec = secs.get(key);
			storeSecurity(sec);
			System.out.println(sec.lookup+" id:"+sec.getId());
		}
		
		for( String key: secs.keySet()){
			//if(key.startsWith("A")){
				Security sec = secs.get(key);
				HashMap<Date, MarketDay> hist= (HashMap<Date, MarketDay>)PersistFileUtil.loadSingleSecurityHistoryFromFS(sec);
				outer:for(Date d : hist.keySet()){
					//System.out.println("year: "+d.getYear());
					if(d.getYear()>200) continue outer;// bad date
					
					MarketDay crt = hist.get(d);
					crt.setDate(d);
					crt.setMySecId(sec.getId());
					store_md(crt, sec.symbol);
				}
			//}
		}
		PersistDBUtil.getSessionFactory().close();
	}
	
/*	public static HashMap<Date, MarketDay> getSortedMap(HashMap<Date, MarketDay> hmap)
	{
		HashMap<Date, MarketDay> map = new LinkedHashMap<Date, MarketDay>();
		List mapKeys = new ArrayList<Date>(hmap.keySet());
		List mapValues = new ArrayList<MarketDay>(hmap.values());
		hmap.clear();
		TreeSet<MarketDay> sortedSet = new TreeSet<MarketDay>(mapValues);
		MarketDay[] sortedArray = (MarketDay[])sortedSet.toArray();
		int size = sortedArray.length;
//		a) Ascending sort
 
		for (int i=0; i<size; i++)
		{
 
		map.put((Date)mapKeys.get(mapValues.indexOf(sortedArray[i])), (MarketDay)sortedArray[i]);
 
		}
		return map;
	}*/

	
    /*public boolean check_for_table(String table_Name){
        Statement selectStatement=null;
        ResultSet resultSet=null;
        try{
        my_DB_Connection = connecttoDB();
        selectStatement = my_DB_Connection.createStatement();
        resultSet = selectStatement.executeQuery( "SELECT * FROM "+table_Name);
        return true;
        }// end of try
        catch(Exception e){
        	e.printStackTrace();
        	return false;
        }
    }
	
    public Connection connecttoDB(){
    	if(my_DB_Connection!=null) return my_DB_Connection;
    	
	    try {
	        // Load the JDBC driver
	        Properties properties = new Properties();;
	        String driverName = getProperties().getProperty("driverName");//"org.gjt.mm.mysql.Driver"; // MySQL MM JDBC driver
	        Class.forName(driverName);
	        // Create a connection to the database
	        String serverName = getProperties().getProperty("serverName");//"localhost";
	        String mydatabase = getProperties().getProperty("mydatabase");//"test";
	        String url = "jdbc:mysql://" + serverName +  "/" + mydatabase; // a JDBC url
	        String username = "Administrator";
	        String password = "letsgo777";

	        properties.put ( "user", username );
	        properties.put ( "password", password );
	        my_DB_Connection = DriverManager.getConnection(url, properties);//username, password);
	        my_DB_Connection.setAutoCommit(true);
	        return my_DB_Connection;
	
	    } catch (ClassNotFoundException e) {
	    	logger.warning("Could not find the database driver. "+e.toString());
	        // Could not find the database driver
	    } catch (SQLException e) {
	    	logger.warning("Could not connect to the database. "+e.toString()+" \n");
	        // Could not connect to the database
	    }
	    return null;
    }*/

/*	public static void populateSecuritiesTableFromFSDataTEST(){
		HashMap<String, Security> secs = new HashMap<String, Security>();
		
		PersistFileUtil.loadOTCBBSymbolsFromFS(secs);	
		SecuritiesManager sm = new SecuritiesManager();
		HistoricalDataManager hm = new HistoricalDataManager();
		int stop =0;
		outer:for(String key: secs.keySet()){
			if(stop++>1) break outer;
			Security sec = secs.get(key);
			storeSecurity(sec);
			System.out.println(sec.lookup+" id:"+sec.getId());
		}
		
		stop =0;
		outer:for( String key: secs.keySet()){
			if(stop++>1) break outer;
			Security sec = secs.get(key);
			if(sec.lookup.startsWith("A"))
			{
				HashMap<Date, MarketDay> hist= (HashMap<Date, MarketDay>)PersistFileUtil.loadSingleSecurityHistoryFromFS(sec);
				inner:for(Date d : hist.keySet()){
					if(d.getYear()>200) continue inner;// bad date
					
					MarketDay crt = hist.get(d);
					crt.setDate(d);
					if(sec==null)System.out.println("security object is null");
					if(crt==null)System.out.println("Marketday for "+d+" is null");
					crt.setMySecId(sec.getId());
					store_md(crt, sec.symbol);

				}
			}

			
		}
		Security aamuSec = secs.get("AAMU");
		
		MarketDay aamu = load_md(aamuSec, (long)1);
		System.out.println(aamu.toString());
		
		List al=load_md_list(aamuSec, (long)aamuSec.getId());

		for(int o=0;o<al.size();o++){
			System.out.println(((MarketDay)al.get(o)).toString());
		}
		PersistDBUtil.getSessionFactory().close();	
	}*/
	
    public static void store_md(MarketDay md, String symbol) {
        Session session = PersistDBUtil.getSessionFactory().openSession();
        session.beginTransaction();
        
    	if(symbol.startsWith("A")){
    		session.save(new a_md(md));
        }
        else if(symbol.startsWith("B")){
        	session.save(new b_md(md));
        }
        else if(symbol.startsWith("C")){
        	session.save(new c_md(md));
        }
        else if(symbol.startsWith("D")){
        	session.save(new d_md(md));
        }
        else if(symbol.startsWith("E")){
        	session.save(new e_md(md));
        }
        else if(symbol.startsWith("F")){
        	session.save(new f_md(md));
        }
        else if(symbol.startsWith("G")){
        	session.save(new g_md(md));
        }
        else if(symbol.startsWith("H")){
        	session.save(new h_md(md));
        }
        else if(symbol.startsWith("I")){
        	session.save(new i_md(md));
        }
        else if(symbol.startsWith("J")){
        	session.save(new j_md(md));
        }
        else if(symbol.startsWith("K")){
        	session.save(new k_md(md));
        }
        else if(symbol.startsWith("L")){
        	session.save(new l_md(md));
        }
        else if(symbol.startsWith("M")){
        	session.save(new m_md(md));
        }
        else if(symbol.startsWith("N")){
        	session.save(new n_md(md));
        }
        else if(symbol.startsWith("O")){
        	session.save(new o_md(md));
        }
        else if(symbol.startsWith("P")){
        	session.save(new p_md(md));
        }
        else if(symbol.startsWith("Q")){
        	session.save(new q_md(md));
        }
        else if(symbol.startsWith("R")){
        	session.save(new r_md(md));
        }
        else if(symbol.startsWith("S")){
        	session.save(new s_md(md));
        }
        else if(symbol.startsWith("T")){
        	session.save(new t_md(md));
        }
        else if(symbol.startsWith("U")){
        	session.save(new u_md(md));
        }
        else if(symbol.startsWith("V")){
        	session.save(new v_md(md));
        }
        else if(symbol.startsWith("W")){
        	session.save(new w_md(md));
        }
        else if(symbol.startsWith("X")){
        	session.save(new x_md(md));
        }
        else if(symbol.startsWith("Y")){
        	session.save(new y_md(md));
        }
        else if(symbol.startsWith("Z")){
        	session.save(new z_md(md));
        }
        
    	session.getTransaction().commit();
    }
    
	
    public static void storeSecurity(Security sec) {
        Session session = PersistDBUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(sec);
        session.getTransaction().commit();
    }
	
    public static void updateSecurity(Security sec) {
        Session session = PersistDBUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.update(sec);
        session.getTransaction().commit();
    }
    
    public static Security loadSecurity(String secLookup) {
		Session session = PersistDBUtil.getSessionFactory().openSession();
        session.beginTransaction();
        //from eg.Cat as cat where cat.name='Fritz'
        String sqlQuery = "from Security as Security where Security.lookup='"+secLookup+"'";
        //System.out.println("SQL QUERY SELCT LIST: "+sqlQuery);
        Query q = session.createQuery(sqlQuery);//, u_md.class);
               
        Security sec = (Security)q.uniqueResult();
        session.close();
        return sec;
        }
    
/*    public static MarketDay load_md(Security sec) {
    	Session session = PersistDBUtil.getSessionFactory().openSession();
        session.beginTransaction();   
        MarketDay md = (MarketDay)session.load(EntityResolver.resolveMarketDay(sec.symbol), sec.getId());
        session.close();
        return md;
    }*/
/*
    public static List clean_md_list(Security sec, long mySecId){
        Session session = PersistDBUtil.getSessionFactory().openSession();
        session.beginTransaction();
        //from eg.Cat as cat where cat.name='Fritz'
        String sqlQuery = "from "+(sec.name.substring(0,1).toLowerCase())+"_md as "+(sec.name.substring(0,1).toLowerCase())+"_md where "+(sec.name.substring(0,1).toLowerCase())+"_md.mySecId='"+mySecId+"'";
        //System.out.println("SQL QUERY SELCT LIST: "+sqlQuery);
        Query q = session.createQuery(sqlQuery);//, u_md.class);
        session.close();
        
        
        return q.list();
    }*/
    
    public static List load_md_list(Security sec, long mySecId){
        Session session = PersistDBUtil.getSessionFactory().openSession();
        session.beginTransaction();
        //from eg.Cat as cat where cat.name='Fritz'
        String sqlQuery = "from "+(sec.symbol.substring(0,1).toLowerCase())+"_md as "+(sec.symbol.substring(0,1).toLowerCase())+"_md where "+(sec.symbol.substring(0,1).toLowerCase())+"_md.mySecId='"+mySecId+"' order by a_md.date";
        //System.out.println("SQL QUERY SELCT LIST: "+sqlQuery);
        Query q = session.createQuery(sqlQuery);//, u_md.class);
        session.close();
        return q.list();
    }
    
    public static Map<Date, MarketDay> load_date_md_map(Security sec, long mySecId){
        Session session = PersistDBUtil.getSessionFactory().openSession();
        session.beginTransaction();
        //from eg.Cat as cat where cat.name='Fritz'
        
        String sqlQuery = "from "+(sec.symbol.substring(0,1).toLowerCase())+"_md as "+(sec.symbol.substring(0,1).toLowerCase())+"_md where "+(sec.symbol.substring(0,1).toLowerCase())+"_md.mySecId='"+mySecId+"'";// order by a_md.date";
        //System.out.println("SQL QUERY SELCT LIST: "+sqlQuery);
        Query q = session.createQuery(sqlQuery);//, u_md.class);
        Map<Date, MarketDay> rt = new HashMap<Date, MarketDay>();
        
        for(MarketDay md : (List<MarketDay>)q.list()){
        	rt.put(md.date, md);
        }
        session.close();
        return rt;
    }
	
    public static void updateMarketDay(MarketDay md) {
        Session session = PersistDBUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.update(md);
        session.getTransaction().commit();
        session.close();
    }
    
    public static void deleteMarketDay(MarketDay md) {
        Session session = PersistDBUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(md);
        session.getTransaction().commit();
        session.close();
    }
 
	public static Map<String, Security> loadSecurities(){
		Session session = PersistDBUtil.getSessionFactory().openSession();
        session.beginTransaction();
        //from eg.Cat as cat where cat.name='Fritz'
        String sqlQuery = "from Security as Security";
        //System.out.println("SQL QUERY SELCT LIST: "+sqlQuery);
        Query q = session.createQuery(sqlQuery);//, u_md.class);
               
        List<Security> list = (List<Security>)q.list();
        Map<String, Security> secs = new HashMap<String, Security>();
        for(Security sec : list)
        	secs.put(sec.lookup, sec);
        
        session.close();
        return secs;
        }
	
}
    
