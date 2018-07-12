package com.sa.marketslayer.library.databeans;

import java.util.Date;


/**
 * @author Carl
 * @date 3/4/6
 */

public class EmailHit implements Comparable{
	private long id;	
	private long mySecId;
	private int numRecipients;
	private String symbol;
	private String lookup;
	private String webUrl;
	private Date sentDate;
	private Date rcvdDate;
	private String senderName;
	private String senderEmail;
	
	public int compareTo(Object other){
		return sentDate.compareTo((Date)other);
	}

	public long getMySecId() {
		return mySecId;
	}

	public void setMySecId(long mySecId) {
		this.mySecId = mySecId;
	}

	public Date getRcvdDate() {
		return rcvdDate;
	}

	public void setRcvdDate(Date rcvdDate) {
		this.rcvdDate = rcvdDate;
	}

	public String getLookup() {
		return lookup;
	}

	public void setLookup(String lookup) {
		this.lookup = lookup;
	}

	public int getNumRecipients() {
		return numRecipients;
	}

	public void setNumRecipients(int numRecipients) {
		this.numRecipients = numRecipients;
	}

	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}
}

