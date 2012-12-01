package com.infinitegraph.samples.ndc.factory;


import com.infinitegraph.AccessMode;
import com.infinitegraph.GraphDatabase;
import com.infinitegraph.Transaction;
import com.infinitegraph.indexing.GenericIndex;
import com.infinitegraph.indexing.IndexException;
import com.infinitegraph.indexing.IndexManager;
import com.infinitegraph.samples.ndc.types.Patient;
import com.objy.db.app.*;
import com.objy.db.app.storage.*;
import com.objy.db.util.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientFactory {

	private static Logger logger = null;
	private static String OBJ_NAME_MMI_LOOKUP = "MMI-Lookup-Root";
	private static String DB_KEY_CATALOG_MAP = "NameCatalogMap";

	public static void CreatePatientInfrastructure(GraphDatabase graphDb) {
		logger = LoggerFactory.getLogger(PatientFactory.class);
		logger.info(">>> CreatePatientInfrastructure");
		Transaction tx = graphDb.beginTransaction(AccessMode.READ_WRITE);

		try {
			IndexManager.getGenericIndexByName(OBJ_NAME_MMI_LOOKUP);
		} catch (IndexException e) {
			logger.info("creating mmi index");

			try {
				IndexManager.createGenericIndex(OBJ_NAME_MMI_LOOKUP,
						Patient.class.getName(), "mmi");
			} catch (IndexException e1) {
				logger.error(e1.getMessage());
				e1.printStackTrace();
				tx.complete();
			}
		}
		tx.commit();

		logger.info("<<< CreatePatientInfrastructure");
	}

	public static Patient CreateNewPatient(GraphDatabase graphDb,
			GenericIndex<Patient> mmiIndex, int mmi, String first,
			String middle, String surname, Date dob) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dob);

		// Find the collection of patients with the same surname
		Patient newPatient = new Patient();
		graphDb.addVertex(newPatient);
		
		// Fill the patients attributes
		newPatient.setMmi(mmi);
		newPatient.setFirstName(first);
		newPatient.setMiddleName(middle);
		newPatient.setSurname(surname);
		newPatient.setDob(dob.getTime());

		// Add the patient to the primary MMI lookup
		addPatientToMMILookup(mmiIndex, newPatient);

		return newPatient;
	}

	public static Patient FindPatientByMMI(GenericIndex<Patient> mmiIndex,
			int mmi) {
		try {
			return (Patient) mmiIndex.getSingleResult((Integer) mmi);
		} catch (IndexException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object[] FindPatientByDetail(ooFDObj fd, String surname,
			int month, int day, int year) {
		// Get the id for the database where this patient will live
		String dbid = null;

		// Open the database and fine the name map
		// Lookup the database for the new patient
		ooDBObj database = null;
		ooMap surnameMap = null;
		ooTreeListX patientList = null;
		if (fd.hasDB(dbid) == true) {
			database = fd.lookupDB(dbid);
			surnameMap = (ooMap) database.lookupObj(DB_KEY_CATALOG_MAP);
		} else {
			throw new MissingResourceException(
					"Patient database not yet initialized", "Database", dbid);
		}

		// Find the collection of patients with the same surname
		if (surnameMap.isMember(surname)) {
			patientList = (ooTreeListX) surnameMap.lookup(surname);
			return patientList.toArray();
		}

		return null;
	}

	private static void addPatientToMMILookup(GenericIndex<Patient> mmiIndex,
			Patient patient) {
		try {
			mmiIndex.put(patient);
		} catch (IndexException e) {
			e.printStackTrace();
		}
	}
}
