package com.infinitegraph.samples.ndc.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infinitegraph.navigation.Path;
import com.infinitegraph.navigation.Qualifier;
import com.infinitegraph.plugins.ResultQualifier;
import com.infinitegraph.plugins.Parameter;
import com.infinitegraph.samples.ndc.Navigate;
import com.infinitegraph.samples.ndc.types.Drug;

@ResultQualifier
public class DrugNameResultQualifier implements Qualifier
{
	public static final boolean trace = false;
	private static Logger logger = null;
    @Parameter
    public String drugName = "";

    public long drugTypeId = 1000002;

	public DrugNameResultQualifier()
	{
		logger = LoggerFactory.getLogger(DrugNameResultQualifier.class);
		if (Navigate.trace) logger.info(">>> DrugNameResultQualifier (Result qualifier) default constructor");
		if (Navigate.trace) logger.info("<<< DrugNameResultQualifier (Result qualifier) default constructor");
	}
	
    // Qualify Drug Vertex by name
    @Override
    public boolean qualify(Path currentPath)
    {
    	if (Navigate.trace) logger.info(">>> DrugNameResultQualifier::qualify()");

		// check final hop is of Drug type
		if (currentPath.getFinalHop().getVertex().getTypeId() == this.drugTypeId) {
			//	check if drug is name
			if (((Drug)(currentPath.getFinalHop().getVertex())).getProprietaryName().equals(this.drugName) ){
				if (Navigate.trace)
					logger.info("<<< DrugNameResultQualifier::qualify() true");
				return true;
			}
			else
			{
				if (Navigate.trace)
					logger.info("<<< DrugNameResultQualifier::qualify() false - not same drug name");
				return false;
			}
		} else {
			if (Navigate.trace)
				logger.info("<<< DrugNameResultQualifier::qualify() false - not drug type id");
			return false;
		}
    }
    public void setDrugTypeId(long drugTypeId)
    {
    	this.drugTypeId = drugTypeId;
    }
}