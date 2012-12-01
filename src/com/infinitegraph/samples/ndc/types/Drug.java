package com.infinitegraph.samples.ndc.types;

import com.infinitegraph.BaseVertex;

public class Drug extends BaseVertex {

	public Drug()
	{
		
	}
	public void setProductTypeName(String productTypeName)
	{
		markModified();
		this.mProductTypeName = productTypeName;
	}
	public String getProductTypeName()
	{
		fetch();
		return this.mProductTypeName;
	}
	
	public void setProprietaryName(String proprietaryName)
	{
		markModified();
		this.mProprietaryName = proprietaryName;
	}
	public String getProprietaryName()
	{
		fetch();
		return this.mProprietaryName;
	}
	
	public void setProprietaryNameSuffix(String proprietaryNameSuffix)
	{
		markModified();
		this.mProprietaryNameSuffix = proprietaryNameSuffix;
	}
	public String getProprietaryNameSuffix()
	{
		fetch();
		return this.mProprietaryNameSuffix;
	}
	
	public void setDosageForm(String dosageForm)
	{
		markModified();
		this.mDosageForm = dosageForm;
	}
	public String getDosageForm()
	{
		fetch();
		return this.mDosageForm;
	}
	
	public void setRouteName(String routeName)
	{
		markModified();
		this.mRouteName = routeName;
	}
	public String getRouteName()
	{
		fetch();
		return this.mRouteName;
	}
	
	public void setStartMarketingDate(String startMarketingDate)
	{
		markModified();
		this.mStartMarketingDate = startMarketingDate;
	}
	public String getStartMarketingDate()
	{
		fetch();
		return this.mStartMarketingDate;
	}
	
	public void setEndMarketingDate(String endMarketingDate)
	{
		markModified();
		this.mEndMarketingDate = endMarketingDate;
	}
	public String getEndMarketingDate()
	{
		fetch();
		return this.mEndMarketingDate;
	}
	
	public void setMarketingCategoryName(String marketingCategoryName)
	{
		markModified();
		this.mMarketingCategoryName = marketingCategoryName;
	}
	public String getMarketingCategoryName()
	{
		fetch();
		return this.mMarketingCategoryName;
	}
	
	public void setApplicationNumber(String applicationNumber)
	{
		markModified();
		this.mApplicationNumber = applicationNumber;
	}
	public String getApplicationNumber()
	{
		fetch();
		return this.mApplicationNumber;
	}
	
	public void setLabelerName(String labelerName)
	{
		markModified();
		this.mLabelerName = labelerName;
	}
	public String getLabelerName()
	{
		fetch();
		return this.mLabelerName;
	}
	
	public void setDEASchedule(String dEASchedule)
	{
		markModified();
		this.mDEASchedule = dEASchedule;
	}
	public String getDEASchedule()
	{
		fetch();
		return this.mDEASchedule;
	}
	
	public Drug(
			String productTypeName,
			String proprietaryName,
			String proprietaryNameSuffix,
			String dosageForm,
			String routeName,
			String startMarketingDate,
			String endMarketingDate,
			String marketingCategoryName,
			String applicationNumber,
			String labelerName,
			String dEASchedule)
	{
		this.mProductTypeName = productTypeName;
		this.mProprietaryName = proprietaryName;
		this.mProprietaryNameSuffix = proprietaryNameSuffix;
		this.mDosageForm = dosageForm;
		this.mRouteName = routeName;
		this.mStartMarketingDate = startMarketingDate;
		this.mEndMarketingDate = endMarketingDate;
		this.mMarketingCategoryName = marketingCategoryName;
		this.mApplicationNumber = applicationNumber;
		this.mLabelerName = labelerName;
		this.mDEASchedule = dEASchedule;
	}
	
	
	private String mProductTypeName = null;
	private String mProprietaryName = null;
	private String mProprietaryNameSuffix = null;
	private String mDosageForm = null;
	private String mRouteName = null;	//	MV
	private String mStartMarketingDate = null;
	private String mEndMarketingDate = null;
	private String mMarketingCategoryName = null;
	private String mApplicationNumber = null;
	private String mLabelerName = null;
	private String mDEASchedule = null;
}
