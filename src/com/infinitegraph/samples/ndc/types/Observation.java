package com.infinitegraph.samples.ndc.types;

import com.infinitegraph.BaseVertex;

public class Observation extends BaseVertex
{
	private long date;
	private boolean deleted;
	public void setDate(long date)
	{
		markModified();
		this.date = date;
	}
	public long getDate()
	{
		fetch();
		return date;
	}
	public void setDeleted(boolean deleted)
	{
		markModified();
		this.deleted = deleted;
	}
	public boolean getDeleted()
	{
		fetch();
		return deleted;
	}
}
