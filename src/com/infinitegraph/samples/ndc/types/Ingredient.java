package com.infinitegraph.samples.ndc.types;

import com.infinitegraph.BaseVertex;

public class Ingredient extends BaseVertex {
	public Ingredient()
	{
		
	}

	public Ingredient(
			String substanceName)
	{
		this.mSubstanceName = substanceName;
		
	}
	
	public void setSubstanceName(String substanceName)
	{
		markModified();
		this.mSubstanceName = substanceName;
	}
	public String getSubstanceName()
	{
		fetch();
		return this.mSubstanceName;
	}
	
	private String mSubstanceName = null;	//	MV Ingredient
	
	
}
