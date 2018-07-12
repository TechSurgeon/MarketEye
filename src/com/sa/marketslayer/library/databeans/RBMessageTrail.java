package com.sa.marketslayer.library.databeans;

import java.util.HashMap;

public class RBMessageTrail extends HashMap<String, MBMessage> {

	int lastId;

	public RBMessageTrail() {
		lastId = 0;
	}
	
	public int getLastId() {
		return lastId;
	}

	public void setLastId(int lastId) {
		this.lastId = lastId;
	}
}
