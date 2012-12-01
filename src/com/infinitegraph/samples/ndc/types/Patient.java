package com.infinitegraph.samples.ndc.types;

import com.infinitegraph.BaseVertex;

public class Patient extends BaseVertex {
	private int mmi;
	private String firstName;
	private String surname;
	private String middleName;
	private String addressLine1;
	private String addressLine2;
	private long ssn;
	private long dob;
	
	public int getMmi()
	{
		fetch();
		return mmi;
	}
	public void setMmi(int mmi)
	{
		markModified();
		this.mmi = mmi;
	}
	public String getFirstName()
	{
		fetch();
		return firstName;
	}
	public void setFirstName(String firstName)
	{
		markModified();
		this.firstName = firstName;
	}
	public String getSurname()
	{
		fetch();
		return surname;
	}
	public void setSurname(String surname)
	{
		markModified();
		this.surname = surname;
	}
	public String getMiddleName()
	{
		fetch();
		return middleName;
	}
	public void setMiddleName(String middleName)
	{
		markModified();
		this.middleName = middleName;
	}
	public String getAddressLine1()
	{
		fetch();
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1)
	{
		markModified();
		this.addressLine1 = addressLine1;
	}
	public String getAddressLine2()
	{
		fetch();
		return addressLine2;
	}
	public void setAddressLine2(String addressLine2)
	{
		markModified();
		this.addressLine2 = addressLine2;
	}
	public long getSsn()
	{
		fetch();
		return ssn;
	}
	public void setSsn(long ssn)
	{
		markModified();
		this.ssn = ssn;
	}
	public long getDob()
	{
		fetch();
		return dob;
	}
	public void setDob(long dob)
	{
		markModified();
		this.dob = dob;
	}
}
