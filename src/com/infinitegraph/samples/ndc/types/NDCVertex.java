package com.infinitegraph.samples.ndc.types;

import com.infinitegraph.BaseVertex;

public class NDCVertex extends BaseVertex {

	public NDCVertex()
	{
		
	}
	public NDCVertex(String ndc)
	{
		this.mProductNDC = ndc;
	}
	public void setNdc(String ndc)
	{
		markModified();
		this.mProductNDC = ndc;
	}
	public String getNdc()
	{
		fetch();
		return this.mProductNDC;
	}
	private String mProductNDC;
}
