package com.infinitegraph.samples.ndc;

import com.infinitegraph.ConfigurationException;
import com.infinitegraph.GraphDatabase;
import com.infinitegraph.GraphFactory;
import com.infinitegraph.StorageException;
import com.infinitegraph.samples.ndc.factory.DrugFactory;
import com.infinitegraph.samples.ndc.factory.PatientFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StagePatientDatabase {

	/**
	 * @param args
	 */
	private static Logger logger = null;

	private static String graphDbName = "NDCSample";
	private static String propertiesFileName = "config/NDCSample.properties";

	public static void main(String[] args) 
	{
		logger = LoggerFactory.getLogger(StagePatientDatabase.class);
		logger.info(">>> StagePatientDatabase");
		GraphDatabase graphDb = null;
		try
		{
			try
			{
				//	Remove any existing graph database
				GraphFactory.delete(graphDbName, propertiesFileName);
			} 
			catch (StorageException e)
			{
				logger.warn(e.getMessage());
			} 
			//	create new graph database
			GraphFactory.create(graphDbName, propertiesFileName);
			graphDb = GraphFactory.open(graphDbName, propertiesFileName);
			
			//	Call the patient factory to prepare patient storage
	        PatientFactory.CreatePatientInfrastructure(graphDb);
	        
	        //	Call the DrugFactory to prepare drug storage
	        DrugFactory.CreateDrugInfrastructure(graphDb);
			
		}
		catch (StorageException e)
		{
			logger.error(e.getMessage());
			e.printStackTrace();
			return;
		} 
		catch (ConfigurationException e)
		{
			logger.error(e.getMessage());
			e.printStackTrace();
			return;
		} 
		finally {
			if (graphDb != null)
				graphDb.close();
			logger.info("<<<< ImportProduct");
			logger.info("On Exit: Closed Graph Database");
		}
	}
}
