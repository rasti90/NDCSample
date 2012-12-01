package com.infinitegraph.samples.ndc.types;

public class Allergy extends Observation
{
	private long dateNoticed;
	public long getDateNoticed()
	{
		fetch();
		return dateNoticed;
	}
	public void setDateNoticed(long dateNoticed)
	{
		markModified();
		this.dateNoticed = dateNoticed;
	}
}
