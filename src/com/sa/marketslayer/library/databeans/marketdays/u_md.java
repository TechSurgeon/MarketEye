package com.sa.marketslayer.library.databeans.marketdays;

import java.util.Date;

import com.sa.marketslayer.library.databeans.MarketDay;

public class u_md extends MarketDay {

	public u_md(){}
	
	public u_md(MarketDay md){
		super(md);
	}
	
	@Override
	public float getHigh() {
		return super.getHigh();
	}
	
	@Override
	public float getLow() {
		
		return super.getLow();
	}

	@Override
	public long getMySecId() {
		
		return super.getMySecId();
	}

	@Override
	public long getVolume() {
		
		return super.getVolume();
	}

	@Override
	public void setClose(float close) {
		
		super.setClose(close);
	}

	@Override
	public void setDate(Date date) {
		
		super.setDate(date);
	}

	@Override
	public void setGap(float gap) {
		
		super.setGap(gap);
	}

	@Override
	public void setHigh(float high) {
		
		super.setHigh(high);
	}

	@Override
	public void setLow(float low) {
		
		super.setLow(low);
	}

	@Override
	public void setMySecId(long mySecId) {
		
		super.setMySecId(mySecId);
	}

	@Override
	public void setOpen(float open) {
		
		super.setOpen(open);
	}

	@Override
	public void setVolume(long volume) {
		
		super.setVolume(volume);
	}

	@Override
	public float getGap() {
		return super.getGap();
	}
	
	@Override
	public Date getDate() {
		return super.getDate();
	}
	
	@Override
	public float getOpen(){
		return super.getOpen();
	}
	
	@Override
	public float getClose(){
		return super.getClose();
	}
	
}
