package com.sa.marketslayer.library.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.sa.marketslayer.library.databeans.MBMessage;
import com.sa.marketslayer.library.databeans.RBMessageTrail;
import com.sa.marketslayer.utils.ParseUtil;


public class RBParser {

		private static Logger logger = Logger.getLogger("RBParser");
		
		private static final String POSTINGS_START_TOKEN = "<a name=\"posts\">"; // delimits beginning of postings
		private static final String POSTING_START_TOKEN = "        <td nowrap=\"nowrap\"";// delimits start of a posting
		//private static final String POSTING_END_TOKEN = "    </tr>";// delimits end of a posting
		private static final String POSTINGS_END_TOKEN = "</table>";// delimits end of all postings
		
		private static final String START_FROM_TOKEN = "&startfrom=";//+messageid
		
		private static final String url = "http://ragingbull.lycos.com/mboard/boards.cgi?board=";

		public String currentUrl;
		private String currentSymbol;
		
		public RBParser(String sym){
			currentSymbol = sym;
			currentUrl=url+sym;
		}
		// max == remote message id
		public RBParser(String sym, int max){
			currentSymbol = sym;
			currentUrl=url+sym+START_FROM_TOKEN+max;
			
		}
		
		public int parseAPage(RBMessageTrail messageTrail){
			
			int lastID = -1;
			
			try{
				URL Url = new URL(currentUrl);
				URLConnection con1 = Url.openConnection();
				BufferedReader br = new BufferedReader(new InputStreamReader(con1.getInputStream()));
				String line="";
				br.skip(11400);/* performance enhancement hack - fast forward */
				/*				int countchars = 0;
				for(int frw = 0; frw<220; frw++){
					String t = br.readLine();
					System.out.println(countchars+" -> "+t);
					countchars+=t.length();
					//br.readLine();
				}*/

				outer:while ((line = br.readLine()) != null) {
					if(line.startsWith(POSTINGS_START_TOKEN)){
						// found posts
						line = br.readLine();
						while(!line.startsWith(POSTINGS_END_TOKEN)){ // now find a post to parse until the end of postings
							line = br.readLine();
							if(line.startsWith(POSTING_START_TOKEN)){
								// found a posting, parse it
								//System.out.println(line);
								String id = line;// hot data
								id = id.replace("\"","");//'
								id = id.replace("'","");
								id = id.replace("\n","");
								id = removeHTMLtags(id).trim();
								while(id.length()<7)
									id = "0"+id;
								
								lastID = Integer.parseInt(id);
								String text = br.readLine();
								text = removeHTMLtags(text);
								text = text.replaceAll("[^a-zA-Z1234567890!@#$%^&*(),.;<>:\"]"," ");
								//text = text.replaceAll("[┤То\nл]"," ");
								//text = text.replace("Т"," ");
								//text = text.replace("о"," ");
								//text = text.replace("\n","");
								String user = br.readLine();
								
								// buggy solution ->
								while(!user.startsWith("        <td valign=\""))
									user = br.readLine();
								//if(!user.startsWith()) user = br.readLine();// skip the chatter
								//if(!user.startsWith("        <td valign=\"")) user = br.readLine();// skip the chatter
								//if(!user.startsWith("        <td valign=\"")) user = br.readLine();// skip the chatter
								
								user = removeHTMLtags(user).replace("&nbsp;"," ");
								user = user.replace("\n","");
								
								String date = br.readLine();

								if(messageTrail.containsKey(id)){// end of new data reached
									logger.info("Found end of new Data with messageID "+id+" "+currentSymbol);
									return lastID;
								}
								
								if(!text.startsWith("OT")&&!text.startsWith("Your ignore of ")){
									
									Date d = new Date();
									try {
										//System.out.println("About to getDate "+date);
										date = (removeHTMLtags(date)).replace("&nbsp;"," ");
										//System.out.println(lastID+" "+text+" "+user+" "+date);
										date = date.replace("\"","");
										//date = date.replace("\'","");
										date = date.replace("\n","");
										getDate(d, date);
									} catch (RuntimeException e) {
										logger.warning("Bad Date for "+lastID);
										e.printStackTrace();
									}
									
									MBMessage rbm = new MBMessage(
											id, 
											text, 
											user, 
											d);
									messageTrail.put(id,rbm);
								}
								
								//if (lastID<=1){
								//	return null;
									//break outer;
								//}
							}
						} // done readin postings on this page
						//boards.cgi?board=UCHB&startfrom=12079
						// current 12140 --> 12140-12079=61
						con1.getInputStream().close();
						//if(lastID>=1){
						if(lastID%50==0) {System.out.print(" "+lastID);System.out.println();}
						if(lastID<=1) return lastID;//done signal
						
						int newIDLimit = 0;
						if(lastID<61) 
							newIDLimit = 1;
						else
							newIDLimit = lastID-61;
							
						this.currentUrl = url + currentSymbol + START_FROM_TOKEN + (newIDLimit);
						System.out.print(".");
							
							
						return lastID;
							
						//}
					}// end of parsing postings
				}
			}
			catch (MalformedURLException mfue) {//MalformedURL
				logger.warning("MalformedURLException in RBParser for URL: "+currentUrl);
				mfue.printStackTrace();
				
			}
			catch (IOException ioe){
				logger.warning("IOException in RBParser: "+currentSymbol);
				ioe.printStackTrace();
				
			}
			catch (NumberFormatException nfe){
				logger.warning("NumberFormatException in RBParser: "+currentSymbol);
				nfe.printStackTrace();
				
			}
			catch (Exception e){
				logger.warning("Exception in RBParser: "+currentSymbol);
				e.printStackTrace();
			}
			
			return lastID;
		}
		
		public static String removeHTMLtags(String s){
			char[] chars = s.toCharArray();
			boolean read = false;
			String rst="";
			try{
				for (int i = 0; i<chars.length; i++){
					if (chars[i]=='>'){
						read = true;
					}
					else if (chars[i]=='<'){
						read = false;
					}
					if(read == true && chars[i]!='<' && chars[i]!='>'){
						rst+=chars[i];
					}			
				}	
			}
			catch(Exception e){		
			}
			return rst;
		}
		
		public void getDate(Date d, String s) throws Exception{
			if(s==null){
				logger.warning("Bad date detected. (null)");
				return;
			}

			StringTokenizer ST = new StringTokenizer(s);
			String day = (ST.nextToken());
			day = (day.replace(",","")).trim();
			d.setDate(Integer.parseInt(day));
			d.setMonth(ParseUtil.monthStringToInt(ST.nextToken().trim())-1);
			d.setYear(Integer.parseInt(ST.nextToken().trim())-1900);
			
			String t = ST.nextToken().trim();
			StringTokenizer ST2 = new StringTokenizer(t,":");
			d.setHours(Integer.parseInt(ST2.nextToken().trim()));
			d.setMinutes(Integer.parseInt(ST2.nextToken().trim()));
			d.setSeconds(0);
		}
		

}
