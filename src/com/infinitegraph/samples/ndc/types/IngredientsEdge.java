package com.infinitegraph.samples.ndc.types;

import com.infinitegraph.BaseEdge;

public class IngredientsEdge extends BaseEdge {

	public IngredientsEdge()
	{
		
	}
	public IngredientsEdge(
			String strengthNumber,
			String strengthUnit,
			String pharm_Classes)
	{
		this.mStrengthNumber = strengthNumber;
		this.mStrengthUnit = strengthUnit;
		this.mPharm_Classes = pharm_Classes;
	}
	
	public void setStrengthNumber(String strengthNumber)
	{
		markModified();
		this.mStrengthNumber = strengthNumber;
	}
	public String getStrengthNumber()
	{
		fetch();
		return this.mStrengthNumber;
	}
	public void setStrengthUnit(String strengthUnit)
	{
		markModified();
		this.mStrengthUnit = strengthUnit;
	}
	public String getStrengthUnit()
	{
		fetch();
		return this.mStrengthUnit;
	}
	public void setPharm_Classes(String pharm_Classes)
	{
		markModified();
		this.mPharm_Classes = pharm_Classes;
	}
	public String getPharm_Classes()
	{
		fetch();
		return this.mPharm_Classes;
	}
	
	private String mStrengthNumber = null;	//	MV Ingredient
	private String mStrengthUnit = null;	//	MV Ingredient
	private String mPharm_Classes = null;	//	MV Ingredient
	
}
