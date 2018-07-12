package com.sa.marketslayer.library.parsers;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.URLName;
import javax.mail.internet.MimeMultipart;

import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3SSLStore;

public class InboxParser {
	
	private static Logger logger = Logger.getLogger("InboxParser");

	public boolean debug = true;
	
	private String email = "Marketeerorama@gmail.com";
	private String pw = "ade7Fyg$f28*@#hdJhJ" +
			"";
	private String host = "pop.gmail.com";
	private String port = "995";
	
	public void parse(){

//			 Setup properties
			Properties props = System.getProperties();
			props.put("mail.pop3.host", host);
			props.put("mail.pop3.ssl", "true");
			props.put("mail.pop3.port", port);
			props.put("mail.pop3.socketFactory.port", port);
			String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
			//Replace socket factory
			props.put("mail.pop3.socketFactory.class", SSL_FACTORY);
			
			/*pop3Props.setProperty("mail.pop3.user", username);
			pop3Props.setProperty("mail.pop3.passwd", password);
			pop3Props.setProperty("mail.pop3.ssl", "true");
			pop3Props.setProperty("mail.pop3.host", "pop.gmail.com");*/
			
//			 Setup authentication, get session
			Authenticator auth = new PopupAuthenticator();
			Session session = 
			  Session.getDefaultInstance(props, auth);
			//session.
//			 Get the store
			POP3SSLStore store = null;
			//SMTPSSLTransport smtp = new SMTPSSLTransport();
			
			
				URLName url = new URLName("pop3://" + email + ":" + pw + "@"
						+ host + ":" + port);
	 
				session = Session.getInstance(props, null);
				store = new POP3SSLStore(session, url);
				
				//store = (POP3SSLStore)session.getStore("pop3");
			
			
			try {
				//System.out.println(store.getURLName());
				//POP3SSLStore storessl = new POP3SSLStore(session, new URLName("pop.gmail.com"));
				
				store.connect(host, email, pw);
				//Folder folder = store.getDefaultFolder();
//				 Get folder
				POP3Folder folder = (POP3Folder) store.getFolder("INBOX");

				folder.open(Folder.READ_WRITE);
				out("# new messages:"+folder.getMessageCount()+", new messages: "+folder.hasNewMessages());
				//folder.getMessage(0);
//				 Get directory
				//Message k = folder.getMessage(0);
				//System.out.println(k.toString());
				
				Message message[] = folder.getMessages();

				for(Message m : message){
					out("+++++++++++++++++++++++++++++  Message start");
						try {
							//Eunumeration f = m.getAllHeaders();
							Date receivedDate = m.getReceivedDate();
							String subject = m.getSubject();
							Date sentDate = m.getSentDate();
							int numRecipients = m.getAllRecipients().length;
							out("Number of recipients: "+numRecipients);
							out("SentDate: "+sentDate+" ReceivedDate: "+receivedDate);
							for(Address s : m.getAllRecipients()){
								out("Recipient: "+s.toString());
							}
							out("Subject: "+subject);
							
							if(m.getContent() instanceof MimeMultipart){
								MimeMultipart k = (MimeMultipart)m.getContent();
							
								//k.getCount()
								for(int f=0; f<k.getCount();f++){
									out((String)(k.getBodyPart(f)).getContent());		
								}
							}
							else if(m.getContent() instanceof String){
								out("not mimemultipart content: "+m.getContent());
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					out("+++++++++++++++++++++++++++++  Message end +++++++++++\n");

				}
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			

			//InternetAddress emailAddress = new InternetAddress(email);
	}
	
	public static void main(String[] args){
		InboxParser ip = new InboxParser();
		ip.parse();
	}
	class PopupAuthenticator 
	extends Authenticator {

		public PasswordAuthentication getPasswordAuthentication() {
	/*	String username, password;
		
		String result = JOptionPane.showInputDialog(
		  "Enter 'username,password'");
		
		StringTokenizer st = new StringTokenizer(result, ",");*/
		String username = email;//"Marketeerorama@gmail.com";
		String password = pw;//"ade7Fyg$f28*@#hdJhJ";
		
		return new PasswordAuthentication(
		  username, password);
		}
	}
	
	private void out(String s){
		if(debug)
			System.out.println(s);
	}
}