package com.infinitegraph.samples.ndc;

import java.util.*;
import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infinitegraph.AccessMode;
import com.infinitegraph.ConfigurationException;
import com.infinitegraph.GraphDatabase;
import com.infinitegraph.GraphFactory;
import com.infinitegraph.StorageException;
import com.infinitegraph.Transaction;
import com.infinitegraph.indexing.GenericIndex;
import com.infinitegraph.indexing.IndexException;
import com.infinitegraph.indexing.IndexManager;
import com.infinitegraph.samples.ndc.factory.*;
import com.infinitegraph.samples.ndc.generators.DOBGenerator;
import com.infinitegraph.samples.ndc.generators.NameGenerator;
import com.infinitegraph.samples.ndc.types.Patient;


public class ImportPatient {

	/**
	 * @param args
	 */

	private static GraphDatabase graphDb = null;
	private static Logger logger = null;

	private static String propertiesFileName = "config/NDCSample.properties";
	private static String graphDbName = "NDCSample";
	private static String OBJ_NAME_MMI_LOOKUP = "MMI-Lookup-Root";

	public static void main(String[] args) {
		logger = LoggerFactory.getLogger(ImportPatient.class);

		try {
			System.setOut(new PrintStream(new FileOutputStream(
					"PatientGenerator.txt")));

			String surnameStatsFile = null;
			String maleNameStatsFile = null;
			String femaleNameStatsFile = null;

			int useRandomSeed = 0;
			graphDb = GraphFactory.open(graphDbName, propertiesFileName);

			surnameStatsFile = "datasource/dist.all.last.txt";
			maleNameStatsFile = "datasource/dist.male.first.txt";
			femaleNameStatsFile = "datasource/dist.female.first.txt";

			logger.info("surnameStatsFile is {}", surnameStatsFile);
			logger.info("maleNameStatsFile is {}", maleNameStatsFile);
			logger.info("femaleNameStatsFile is {}", femaleNameStatsFile);

			int currentPatientCount = 0;
			int numberToAdd = 100000;
			int additionsPerTransaction = 10000;

			// Load Name distributions
			NameGenerator surnames = new NameGenerator();
			surnames.GenerateListFromStats(surnameStatsFile, useRandomSeed);

			NameGenerator maleNames = new NameGenerator();
			maleNames.GenerateListFromStats(maleNameStatsFile, useRandomSeed);

			NameGenerator femaleNames = new NameGenerator();
			femaleNames.GenerateListFromStats(femaleNameStatsFile,
					useRandomSeed);

			DOBGenerator dobGen = new DOBGenerator(useRandomSeed);

			// Create a connection and session to setup the dasabases for ingest
			logger.info("Creating FD connection and setup session...");

			Transaction tx = graphDb.beginTransaction(AccessMode.READ_WRITE);
			GenericIndex<Patient> mmiIndex = null;

			try {
				mmiIndex = IndexManager
						.getGenericIndexByName(OBJ_NAME_MMI_LOOKUP);
			} catch (IndexException e) {
				logger.error(e.getMessage());
				logger.info("mmi index does not exist, initialise graph database");
			}

			logger.info("Beginning Patient Ingest...");

			// Create a series of Patient objects
			// Break the task up into transactions of a set number
			int remainingCount = numberToAdd;
			while (remainingCount > 0) {
				tx.checkpoint();
				for (int i = currentPatientCount; (i < currentPatientCount
						+ additionsPerTransaction)
						&&
						(i < currentPatientCount + remainingCount); i++) {

					Date dob = dobGen.GenerateDOB();
					if (i % 2 == 0) {
						PatientFactory.CreateNewPatient(graphDb, mmiIndex, i,
								maleNames.GenerateName(),
								maleNames.GenerateName(),
								surnames.GenerateName(), dob);
					}

					else {
						PatientFactory.CreateNewPatient(graphDb, mmiIndex, i,
								femaleNames.GenerateName(),
								femaleNames.GenerateName(),
								surnames.GenerateName(), dob);
					}

					if ((i + 1) % additionsPerTransaction == 0) {
						logger.info("Created " + (i + 1) + " patient records");
					}
				}
				remainingCount -= additionsPerTransaction;
				currentPatientCount += additionsPerTransaction;
			}
			tx.commit();
			logger.info("\t Completed patient creation");
		}
		catch (StorageException se) {
			logger.error(se.getMessage());
			se.printStackTrace();
		} catch (ConfigurationException ce) {
			logger.error(ce.getMessage());
			ce.printStackTrace();
		} catch (Exception ee) {
			logger.error(ee.getMessage());
			ee.printStackTrace();
		}
	}
}
