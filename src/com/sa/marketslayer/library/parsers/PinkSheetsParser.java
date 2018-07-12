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

public class PinkSheetsParser{
	
	public static Logger logger = Logger.getLogger("PinkSheetsParser");
	public static final String URL = "http://www.pinksheets.com/companysearch/ps_list.jsp?sletter=";
	public static final String EXCHANGE_ID = "PS";
	public static final String URL_PAGENUM = "&nump=";

	public static Map<String, Security> updateSymbolsGetDiff(Map<String, Security> secs, Map<String, Security> activeSecurities){
		
		logger.info("parse start[PINKS]:[" + (new Time(System.currentTimeMillis())).toString()
				+ "] from "+URL);
		int counter = 0;
		int loopCounter = 0;
		Map<String, Security> rsts = new HashMap<String, Security>();
		try {
			// we need to parse in all pages A-Z and 0-9
			String[] pagesAZ = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
					"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
					"V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6",
					"7", "8", "9" };
			int start;
			int end;
			outer: for (int letter = 0; letter < pagesAZ.length; letter++) {
				boolean morePagesToParse = true;
				int pageNum = 0;
				String exchange = "";//= exchange.name;
				String currentSymbol = "";
				String companyName = "";
				inner: while (morePagesToParse) {
					pageNum++;
					//logger.info("Attempting to parse "+URL + pagesAZ[letter] + URL_PAGENUM + pageNum);
					//System.out.print(""+ (++loopCounter%50==0?".\n":"."));
					URL url = new URL(URL + pagesAZ[letter]
							+ URL_PAGENUM + pageNum);

					//if(debug_on){
					//	out("Parsing: " + PINKSURL + pagesAZ[letter]
					//			+ pinksheetsSymbolListPageNumURL + pageNum);
					//}
					URLConnection con1 = url.openConnection();
					DataInputStream dis = new DataInputStream(con1
							.getInputStream());
					String line = dis.readLine(); // read in first line and
												  // ignore
					// first, fast forward 250 lines, all crap
					//dis.skip(7300);
					int g=0;
					int linecounter=0;
					while(g++<170){
						//String s = dis.readLine();
						//linecounter+=s.length();
						//System.out.println(s);
						if(dis.readLine()== null){
							morePagesToParse = false;
							break inner;
						}
					}
					//System.out.println("numlines: "+linecounter);
					
					while (!line
							.startsWith("<TD VALIGN=\"TOP\" NOWRAP><b>SYMBOL</B></td>")) {
						line = dis.readLine();
						if (line == null || line.startsWith("</html>")) {// no
																		 // data
																		 // on
																		 // this
																		 // page,
																		 // move
																		 // on
																		 // to
																		 // next
																		 // letter
							morePagesToParse = false;
							break inner;
						}
					}

					// we should now be right before the symbols data is listed

					String db_activeStock = "N";
					//int counter = 0;
					
					while ((line = dis.readLine()) != null
							&& !line.contains("</table>")) {// data was found,
															// search
						if (line.contains("BGCOLOR")) {// next line contains
													   // company name
							line = dis.readLine();
							start = line.indexOf("AP>") + 3;
							end = line.indexOf("</td>");
							companyName = ((line.substring(start, end)))
									.trim();
							//split if it includes a "-", usually - common stock etc, not part of company name
							StringTokenizer t = new StringTokenizer(companyName,"-");
							companyName = (t.nextToken()).trim();
							

							line = dis.readLine();//contains exchange name
							start = line.indexOf("AP>") + 3;
							end = line.indexOf("</td>");
							exchange = ((line.substring(start, end))).trim();

							line = dis.readLine();//skip
							line = dis.readLine();// contains symbol name
							start = line.indexOf("ol=") + 3;
							//end = line.indexOf("</td>");
							line = line.substring(start, line.length());
							start = line.indexOf(">") + 1;
							end = line.indexOf("<");
							currentSymbol = ((line.substring(start, end))).trim().toUpperCase()+".pk";
							//out("company name: "+db_companyName+" exchange:
							// "+db_exchange+" symbol: "+db_currentSymbol);
							if ( (!activeSecurities.containsKey(currentSymbol) && exchange.equals("PS")) && (
																	!currentSymbol.startsWith("0") &&
																	!currentSymbol.startsWith("1") &&
																	!currentSymbol.startsWith("2") &&
																	!currentSymbol.startsWith("3") &&
																	!currentSymbol.startsWith("4") &&
																	!currentSymbol.startsWith("5") &&
																	!currentSymbol.startsWith("6") &&
																	!currentSymbol.startsWith("7") &&
																	!currentSymbol.startsWith("8") &&
																	!currentSymbol.startsWith("9")
																	)) {
								if(!secs.containsKey(currentSymbol)){// dne yet, create and add
									Security newSecurity = new Security(
											currentSymbol,
											EXCHANGE_ID, 
											currentSymbol,
											companyName);
									newSecurity.exchange = exchange;
									secs.put(currentSymbol, newSecurity);
									rsts.put(currentSymbol, newSecurity);
									activeSecurities.put(currentSymbol, newSecurity);
									PersistDBUtil.storeSecurity(newSecurity);
									logger. info("Adding "+newSecurity.symbol+" "+newSecurity.exchange+" to system. "+newSecurity.name);
									counter++;
								}
								else activeSecurities.put(currentSymbol, secs.get(currentSymbol));// exists, load and add to active

							}
						}
					} //end of parse line - continue with next
					//if(counter<2) out("Unable to connect and acquire Data.");
					//return results;
					//out("");
				}
				//break outer;//-- for debug
			}

		}// end of try
		catch (Exception badURL) {
			badURL.printStackTrace();
		}// end of catch
		logger.info("parse end:[" + (new Time(System.currentTimeMillis())).toString()
				+ "] "+counter+" "+EXCHANGE_ID+" securities added");
		return rsts;
	}

	public static boolean updateSymbols(Map<String, Security> secs){
		
		logger.info("parse start[PINKS]:[" + (new Time(System.currentTimeMillis())).toString()
				+ "] from "+URL);
		int counter = 0;
		int loopCounter = 0;
		try {
			// we need to parse in all pages A-Z and 0-9
			String[] pagesAZ = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
					"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
					"V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6",
					"7", "8", "9" };
			int start;
			int end;
			outer: for (int letter = 0; letter < pagesAZ.length; letter++) {
				boolean morePagesToParse = true;
				int pageNum = 0;
				String exchange = "";//= exchange.name;
				String currentSymbol = "";
				String companyName = "";
				inner: while (morePagesToParse) {
					pageNum++;
					//logger.info("Attempting to parse "+URL + pagesAZ[letter] + URL_PAGENUM + pageNum);
					//System.out.print(""+ (++loopCounter%50==0?".\n":"."));
					URL url = new URL(URL + pagesAZ[letter]
							+ URL_PAGENUM + pageNum);

					//if(debug_on){
					//	out("Parsing: " + PINKSURL + pagesAZ[letter]
					//			+ pinksheetsSymbolListPageNumURL + pageNum);
					//}
					URLConnection con1 = url.openConnection();
					DataInputStream dis = new DataInputStream(con1
							.getInputStream());
					String line = dis.readLine(); // read in first line and
												  // ignore
					// first, fast forward 250 lines, all crap
					//dis.skip(7300);
					int g=0;
					int linecounter=0;
					while(g++<170){
						//String s = dis.readLine();
						//linecounter+=s.length();
						//System.out.println(s);
						if(dis.readLine()== null){
							morePagesToParse = false;
							break inner;
						}
					}
					//System.out.println("numlines: "+linecounter);
					
					while (!line
							.startsWith("<TD VALIGN=\"TOP\" NOWRAP><b>SYMBOL</B></td>")) {
						line = dis.readLine();
						if (line == null || line.startsWith("</html>")) {// no
																		 // data
																		 // on
																		 // this
																		 // page,
																		 // move
																		 // on
																		 // to
																		 // next
																		 // letter
							morePagesToParse = false;
							break inner;
						}
					}

					// we should now be right before the symbols data is listed

					String db_activeStock = "N";
					//int counter = 0;
					
					while ((line = dis.readLine()) != null
							&& !line.contains("</table>")) {// data was found,
															// search
						if (line.contains("BGCOLOR")) {// next line contains
													   // company name
							line = dis.readLine();
							start = line.indexOf("AP>") + 3;
							end = line.indexOf("</td>");
							companyName = ((line.substring(start, end)))
									.trim();
							//split if it includes a "-", usually - common stock etc, not part of company name
							StringTokenizer t = new StringTokenizer(companyName,"-");
							companyName = (t.nextToken()).trim();
							

							line = dis.readLine();//contains exchange name
							start = line.indexOf("AP>") + 3;
							end = line.indexOf("</td>");
							exchange = ((line.substring(start, end))).trim();

							line = dis.readLine();//skip
							line = dis.readLine();// contains symbol name
							start = line.indexOf("ol=") + 3;
							//end = line.indexOf("</td>");
							line = line.substring(start, line.length());
							start = line.indexOf(">") + 1;
							end = line.indexOf("<");
							currentSymbol = ((line.substring(start, end)))
									.trim();
							//out("company name: "+db_companyName+" exchange:
							// "+db_exchange+" symbol: "+db_currentSymbol);
							if ( (!secs.containsKey(currentSymbol + ".pk") && exchange.equals("PS")) && (
																	!currentSymbol.startsWith("0") &&
																	!currentSymbol.startsWith("1") &&
																	!currentSymbol.startsWith("2") &&
																	!currentSymbol.startsWith("3") &&
																	!currentSymbol.startsWith("4") &&
																	!currentSymbol.startsWith("5") &&
																	!currentSymbol.startsWith("6") &&
																	!currentSymbol.startsWith("7") &&
																	!currentSymbol.startsWith("8") &&
																	!currentSymbol.startsWith("9")
																	)) {
								Security s = new Security(
										currentSymbol + ".pk",
										EXCHANGE_ID, 
										currentSymbol,
										companyName);
								s.exchange = exchange;
								secs.put(currentSymbol + ".pk", s);
								logger. info("Adding "+s.symbol+" "+s.exchange+" to system. "+s.name);
								counter++;
							}
						}
					} //end of parse line - continue with next
					//if(counter<2) out("Unable to connect and acquire Data.");
					//return results;
					//out("");
				}
				//break outer;//-- for debug
			}

		}// end of try
		catch (Exception badURL) {
			badURL.printStackTrace();
		}// end of catch
		logger.info("parse end:[" + (new Time(System.currentTimeMillis())).toString()
				+ "] "+counter+" "+EXCHANGE_ID+" securities added");
		return true;
	}

	public class BadCharSequence implements CharSequence {
		
		String seq;
		public BadCharSequence(String s){
			seq = s;
		}
		
		public char charAt(int i){
			return seq.charAt(i);
		}
		
		public int length(){
			return seq.length();
		}
		
		public CharSequence subSequence(int a, int b){
			return seq.subSequence(a, b);
		}
		
			
	}
}
