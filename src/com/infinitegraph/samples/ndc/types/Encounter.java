package com.infinitegraph.samples.ndc.types;

import com.infinitegraph.BaseVertex;


public class Encounter extends BaseVertex {

	private String facility;
	private long date;
	
	public String getFacility()
	{
		fetch();
		return facility;
	}
	public void setFacility(String facility)
	{
		markModified();
		this.facility = facility;
	}
	public long getDate()
	{
		fetch();
		return date;
	}
	public void setDate(long date)
	{
		markModified();
		this.date = date;
	}
}
