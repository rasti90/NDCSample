package com.infinitegraph.samples.ndc.types;

import com.infinitegraph.BaseVertex;

public class GenericDrug extends BaseVertex {
	
	public GenericDrug()
	{
		
	}
	public GenericDrug(
			String nonProprietaryName)
	{
		this.mNonProprietaryName = nonProprietaryName;
	}
	public void setNonProprietaryName(String nonProprietaryName)
	{
		markModified();
		this.mNonProprietaryName = nonProprietaryName;
	}
	public String getNonProprietaryName()
	{
		fetch();
		return this.mNonProprietaryName;
	}
	private String mNonProprietaryName = null;	//	MV GenericDrug
}
