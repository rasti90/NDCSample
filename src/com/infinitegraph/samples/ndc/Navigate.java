package com.infinitegraph.samples.ndc;

import java.io.FileOutputStream;

import com.infinitegraph.navigation.*;
import com.infinitegraph.plugins.PluginException;
import com.infinitegraph.plugins.PluginManagementFactory;
import com.infinitegraph.plugins.PluginManager;
import com.infinitegraph.plugins.ResultQualifier;
import com.infinitegraph.plugins.types.FormatterBundle;
import com.infinitegraph.plugins.types.NavigatorBundle;
import com.infinitegraph.samples.ndc.factory.DrugFactory;
import com.infinitegraph.samples.ndc.factory.PatientFactory;
import com.infinitegraph.samples.ndc.plugins.*;
import com.infinitegraph.samples.ndc.types.Drug;
import com.infinitegraph.samples.ndc.types.Patient;
import com.infinitegraph.transformation.FormatterBridge;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infinitegraph.AccessMode;
import com.infinitegraph.GraphDatabase;
import com.infinitegraph.GraphFactory;
import com.infinitegraph.StorageException;
import com.infinitegraph.Transaction;
import com.infinitegraph.indexing.GenericIndex;
import com.infinitegraph.indexing.IndexException;
import com.infinitegraph.indexing.IndexManager;

import com.infinitegraph.policies.PolicyChain;
import com.infinitegraph.navigation.policies.UseJavaOnlyNavigator;
import com.infinitegraph.navigation.GraphView;

public class Navigate {

	/**
	 * @param args
	 */

	public static final boolean trace = false;

	private static Logger logger = null;

	private static String propertiesFileName = "config/NDCSample.properties";
	private static String graphDbName = "NDCSample";
	private static String INDEX_NAME_DRUG_LOOKUP = "DrugMap";
	private static String OBJ_NAME_MMI_LOOKUP = "MMI-Lookup-Root";

	public static void main(String[] args) {
		logger = LoggerFactory.getLogger(Navigate.class);
		logger.info(">>> Navigate");
		GraphDatabase graphDb = null;
		Transaction tx = null;
		String navigatorPluginName = "NDCNavigatorPlugin";
		String formatterPluginName = "NDCFormatterPlugin";

		// Parameters for the medication
		int patientMMI = 12827;
		String drugName = "Famciclovir";
		long drugTypeId = 0;


		// used args if present
		if (args.length == 2) {
			patientMMI = Integer.parseInt(args[0]);
			drugName = args[1];
		}

		try {
			// open the graph database
			graphDb = GraphFactory.open(graphDbName, propertiesFileName);

			// get the plugin management factory
			PluginManagementFactory pluginFactory = graphDb
					.getPluginManagementFactory();

			// get the navigator plugin manager
			PluginManager<NavigatorBundle> navigatorPluginManager = pluginFactory
					.getManager(NavigatorBundle.class);

			// get the required navigator plugin bundle (jar)
			NavigatorBundle navBundle = null;
			try {
				navBundle = navigatorPluginManager
						.getBundle(navigatorPluginName);
			} catch (PluginException pe) {
				pe.getMessage();
			}

			// get the formatter plugin manager
			PluginManager<FormatterBundle> formatterPluginManager = pluginFactory
					.getManager(FormatterBundle.class);

			// get the required formatter plugin bundle (jar)
			FormatterBundle formatterBundle = null;
			try {
				formatterBundle = formatterPluginManager
						.getBundle(formatterPluginName);
			} catch (PluginException pe) {
				pe.getMessage();
			}
			// start a graph database transaction
			tx = graphDb.beginTransaction(AccessMode.READ);

			// get the patient index
			GenericIndex<Patient> mmiIndex = null;
			try {
				mmiIndex = IndexManager
						.getGenericIndexByName(OBJ_NAME_MMI_LOOKUP);
			} catch (IndexException e) {
				logger.error(e.getMessage());
				logger.info("mmi index does not exist, initialise graph database");
			}

			// find the patient by mmi
			Patient patient = PatientFactory.FindPatientByMMI(mmiIndex,
					patientMMI);
			logger.info("Found patient " + patient.getMmi() + " : "
					+ patient.getFirstName() + " " + patient.getMiddleName()
					+ " " + patient.getSurname());

			// get the drug index
			GenericIndex<Drug> drugIndex = null;
			try {
				drugIndex = IndexManager
						.getGenericIndexByName(INDEX_NAME_DRUG_LOOKUP);
			} catch (IndexException e) {
				logger.error(e.getMessage());
				logger.info("drug index does not exist, initialise graph database");
			}
			// find the drug by name that we want to administer
			Drug drug = null;
			drug = DrugFactory.GetDrugByName(drugIndex, drugName);
			if (drug == null) {
				logger.error("Drug {} not found", drugName);
				tx.complete();
				graphDb.close();
				return;
			}

			//	get the path and result qualifiers
			EdgeTypePathQualifier pathQualifier = (EdgeTypePathQualifier) navBundle
					.getPathQualifier();
			DrugNameResultQualifier resultQualifier = (DrugNameResultQualifier) navBundle
					.getResultQualifier();
			//	get the guide
			SearchOrderGuide searchOrderGuide = (SearchOrderGuide) navBundle.getGuide();
			//	set up the print path result handler
			PrintPathFormatResultHandler resultPrinter = (PrintPathFormatResultHandler) formatterBundle
					.getFormatHandler();
			FileOutputStream formatterStream = new FileOutputStream("FormattedOutput.txt");
			resultPrinter.setOutputStream(formatterStream);
			//	set the drug name and type id fields for the result qualifier
			try {
				navBundle.setFieldValue(ResultQualifier.class, "drugName",
						drugName);
				// get the drug type id from the database
				drugTypeId = graphDb.getTypeId(Drug.class.getName());
				resultQualifier.setDrugTypeId(drugTypeId);
			} catch (PluginException pe) {
				pe.getMessage();
			}

			logger.info("Patient mmi {}", patient.getMmi());
			logger.info("Prescribing drug {} (drug type id {})", drugName,
					drugTypeId);

			// perform the navigation
			GraphView graphView = null;
			PolicyChain javaOnlyPolicies = new PolicyChain(new UseJavaOnlyNavigator());
			Navigator navigator = patient.navigate(
					graphView, // default graph View
					searchOrderGuide, // breadth first search
					pathQualifier, // path qualifier (Qualifier.FOREVER)
					resultQualifier, // result qualifier (Qualifier.ANY)
					javaOnlyPolicies, //Policy Chain
					new FormatterBridge(resultPrinter)); // handle results
			navigator.start();
			navigator.stop();

			// commit the transaction
			tx.commit();
			logger.info("Navigation complete... ");

		} catch (StorageException se) {
			logger.error(se.getMessage());
			se.printStackTrace();
		} catch (Exception ee) {
			logger.error(ee.getMessage());
			ee.printStackTrace();
		} finally {
			tx.complete();
			graphDb.close();
			logger.info(">>> Navigate");
		}
	}
}
