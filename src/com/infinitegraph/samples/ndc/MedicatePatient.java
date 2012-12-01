package com.infinitegraph.samples.ndc;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infinitegraph.AccessMode;
import com.infinitegraph.GraphDatabase;
import com.infinitegraph.GraphFactory;
import com.infinitegraph.StorageException;
import com.infinitegraph.Transaction;
import com.infinitegraph.VertexHandle;
import com.infinitegraph.indexing.GenericIndex;
import com.infinitegraph.indexing.IndexException;
import com.infinitegraph.indexing.IndexManager;
import com.infinitegraph.navigation.qualifiers.VertexTypes;
import com.infinitegraph.samples.ndc.factory.DrugFactory;
import com.infinitegraph.samples.ndc.factory.PatientFactory;
import com.infinitegraph.samples.ndc.types.Drug;
import com.infinitegraph.samples.ndc.types.DrugAllergy;
import com.infinitegraph.samples.ndc.types.Encounter;
import com.infinitegraph.samples.ndc.types.GenericDrug;
import com.infinitegraph.samples.ndc.types.Ingredient;
import com.infinitegraph.samples.ndc.types.Patient;

public class MedicatePatient {

	/**
	 * @param args
	 */
	private static Logger logger = null;
	private static boolean trace = false;
	private static String propertiesFileName = "config/NDCSample.properties";
	private static String graphDbName = "NDCSample";
	private static String INDEX_NAME_DRUG_LOOKUP = "DrugMap";
	private static String OBJ_NAME_MMI_LOOKUP = "MMI-Lookup-Root";

	public static void main(String[] args) {
		logger = LoggerFactory.getLogger(MedicatePatient.class);
		logger.info(">>> MedicatePatient");
		GraphDatabase graphDb = null;
		Transaction tx = null;
		try {
			// Parameters for the medication
			int patientMMI = 12827;
			String drugName = "Famciclovir";

			if (args.length == 2) {
				patientMMI = Integer.parseInt(args[0]);
				drugName = args[1];
			}

			try {
				// open the graph database
				graphDb = GraphFactory.open(graphDbName, propertiesFileName);
				tx = graphDb.beginTransaction(AccessMode.READ);

				GenericIndex<Drug> drugIndex = null;
				try {
					drugIndex = IndexManager
							.getGenericIndexByName(INDEX_NAME_DRUG_LOOKUP);
				} catch (IndexException e) {
					logger.error(e.getMessage());
					logger.info("drug index does not exist, initialise graph database");
				}

				GenericIndex<Patient> mmiIndex = null;
				try {
					mmiIndex = IndexManager
							.getGenericIndexByName(OBJ_NAME_MMI_LOOKUP);
				} catch (IndexException e) {
					logger.error(e.getMessage());
					logger.info("mmi index does not exist, initialise graph database");
				}

				// Find the patient
				Patient targetPatient = PatientFactory.FindPatientByMMI(mmiIndex,
						patientMMI);
				if (targetPatient == null) {
					logger.error("Patient not found {}", patientMMI);
					return;
				}
				logger.info("Found patient " + targetPatient.getMmi() + " : "
						+ targetPatient.getFirstName() + " " + targetPatient.getMiddleName()
						+ " " + targetPatient.getSurname());

				// Find the drug that we want to administer
				Drug drug = null;
				drug = DrugFactory.GetDrugByName(drugIndex, drugName);
				if (drug == null) {
					logger.error("Drug not found {}", drugName);
					return;
				}
				logger.info("Found drug {}", drug.getProprietaryName());
				
				//	get the generic for the drug
				GenericDrug genericDrug = null;
				Iterable<VertexHandle> generics = drug
						.getNeighbors(new VertexTypes(graphDb
								.getTypeId(GenericDrug.class.getName())));
				for (VertexHandle g : generics) {
					genericDrug = (GenericDrug) g.getVertex();
					if (genericDrug == null) {
						logger.error("Database inconsistency: Generic Drug not found");
					}
					if (MedicatePatient.trace) logger.info("\tFound generic drug : {}",
							genericDrug.getNonProprietaryName());


					// Check the patient for allergies

					//	iterate across this patients encounters
					Encounter encounter = null;
					Iterable<VertexHandle> encountersIter = targetPatient.getNeighbors(new VertexTypes(graphDb.getTypeId(Encounter.class.getName())));
					for (VertexHandle encounters : encountersIter)
					{
						encounter = (Encounter)encounters.getVertex();
						if (MedicatePatient.trace) logger.info("\t\tFound encounter : ");
						// Iterate across allergies
						DrugAllergy drugAllergy = null;
						Iterable<VertexHandle> allergiesIter = encounter
								.getNeighbors(new VertexTypes(graphDb
										.getTypeId(DrugAllergy.class.getName())));
						for (VertexHandle allergies : allergiesIter) {
							drugAllergy = (DrugAllergy) allergies.getVertex();
							if (MedicatePatient.trace) logger.info("\t\t\tFound drug allergy : ");

							// Iterate across allergen
							Ingredient allergyIngredient = null;
							Iterable<VertexHandle> allergenIter = drugAllergy
									.getNeighbors(new VertexTypes(graphDb
											.getTypeId(Ingredient.class.getName())));
							for (VertexHandle allergen : allergenIter) {
								// Test if allergen is one of the ingredients
								allergyIngredient = (Ingredient) allergen.getVertex();
								if (MedicatePatient.trace) logger.info("\t" + targetPatient.getFirstName()
										+ " is allergic to "
										+ allergyIngredient.getSubstanceName());
								Ingredient genericDrugIngredient = null;
								Iterable<VertexHandle> drugIngredientIter = genericDrug
										.getNeighbors(new VertexTypes(graphDb
												.getTypeId(Ingredient.class.getName())));
								for (VertexHandle i : drugIngredientIter) {
									genericDrugIngredient = (Ingredient) i.getVertex();
									if (genericDrugIngredient.getSubstanceName() == allergyIngredient
											.getSubstanceName()) {
										logger.info("\t" + targetPatient.getFirstName()
												+ " is allergic to "
												+ allergyIngredient.getSubstanceName());
										logger.info("*****\t" + "DO NOT PRESCRIBE : "
												+ drug.getProprietaryName() + " contains "
												+ allergyIngredient.getSubstanceName() + " in generic "
												+ genericDrug.getNonProprietaryName());
									}
								}
							}
						}
					}
				}
				tx.commit();

				logger.info("Medication complete... ");

			} catch (StorageException se) {
				logger.error(se.getMessage());
				se.printStackTrace();
			} catch (Exception ee) {
				logger.error(ee.getMessage());
				ee.printStackTrace();
			}
		} finally {
			tx.complete();
			graphDb.close();
		}
	}
}
