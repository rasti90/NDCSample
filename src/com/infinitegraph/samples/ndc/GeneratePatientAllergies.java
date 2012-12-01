package com.infinitegraph.samples.ndc;


import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
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
import com.infinitegraph.samples.ndc.factory.*;
import com.infinitegraph.samples.ndc.generators.MMIGenerator;
import com.infinitegraph.samples.ndc.types.BrandsEdge;
import com.infinitegraph.samples.ndc.types.Drug;
import com.infinitegraph.samples.ndc.types.FoundInEdge;
import com.infinitegraph.samples.ndc.types.GenericDrug;
import com.infinitegraph.samples.ndc.types.GenericEdge;
import com.infinitegraph.samples.ndc.types.Ingredient;
import com.infinitegraph.samples.ndc.types.IngredientsEdge;
import com.infinitegraph.samples.ndc.types.Patient;

public class GeneratePatientAllergies {
	/**
	 * @param args
	 */
	private static Logger logger = null;
	private static String propertiesFileName = "config/NDCSample.properties";
	private static String graphDbName = "NDCSample";
	private static String INDEX_NAME_INGREDIENT_LOOKUP = "IngredientMap";
	private static String OBJ_NAME_MMI_LOOKUP = "MMI-Lookup-Root";

	public static void main(String[] args) {
		logger = LoggerFactory.getLogger(GeneratePatientAllergies.class);
		logger.info(">>> GeneratePatientAllergies");
		GraphDatabase graphDb = null;
		Transaction tx = null;

		try {
			// Parameters for the loop below
			int numberPatients = 100000;
			int lowestMMI = 0;
			int highestMMI = numberPatients;
			int numberOfAllergiesEach = 5;
			int numberPerTransaction = 10000;
			int numberLoops = 25;
					
			if (args.length == 1)
			{
				numberLoops = Integer.parseInt(args[0]);
				if (numberLoops == 0 || numberLoops > numberPatients)
				{
					numberLoops = numberPatients;
				}
			}
			logger.info("Generating {} allergies for {} patients",numberOfAllergiesEach,numberLoops);
			
			// Use generator to ensure access is random
			int useRandomSeed = 0;
			MMIGenerator mmiGen = new MMIGenerator(lowestMMI, highestMMI,
					useRandomSeed);
			// Use a random number generator for the Ingredients
			Random indexGenerator = new Random(System.currentTimeMillis());

				// open the graph database
				graphDb = GraphFactory.open(graphDbName, propertiesFileName);
				tx = graphDb
						.beginTransaction(AccessMode.READ_WRITE);
				logger.info("Drug TypeId = {}",graphDb.getTypeId(Drug.class.getName()));
				logger.info("GenericDrug TypeId = {}",graphDb.getTypeId(GenericDrug.class.getName()));
				logger.info("Ingredient TypeId = {}",graphDb.getTypeId(Ingredient.class.getName()));
				logger.info("BrandsEdge TypeId = {}",graphDb.getTypeId(BrandsEdge.class.getName()));
				logger.info("FoundInEdge TypeId = {}",graphDb.getTypeId(FoundInEdge.class.getName()));
				logger.info("IngredientsEdge TypeId = {}",graphDb.getTypeId(IngredientsEdge.class.getName()));
				logger.info("GenericEdge TypeId = {}",graphDb.getTypeId(GenericEdge.class.getName()));
				
				GenericIndex<Ingredient> ingredientIndex = null;
				try {
					ingredientIndex = IndexManager
							.getGenericIndexByName(INDEX_NAME_INGREDIENT_LOOKUP);
				} catch (IndexException e) {
					logger.error(e.getMessage());
					logger.info("Ingredient index does not exist");
				}
				
				GenericIndex<Patient> mmiIndex = null;
				try {
					mmiIndex = IndexManager
							.getGenericIndexByName(OBJ_NAME_MMI_LOOKUP);
				} catch (IndexException e) {
					logger.error(e.getMessage());
					logger.info("mmi index does not exist");
				}
				
				// Get all of the ingredient names from the database
				ArrayList<String> allergenNames = DrugFactory
						.GetIngredientNames(graphDb, ingredientIndex);
				Patient target = null;
				String allergen = null;
				System.setOut(new PrintStream(new FileOutputStream(
						"GeneratePatientAllergies.txt")));
				for (int i = lowestMMI; i < numberLoops; i++) {
					if (i != 0 && i % numberPerTransaction == 0) {
						logger.info("Completed {} patients", i);
						tx.checkpoint();
						//	System.gc();
					}
					// Choose a patient (either specific MMI, sequential[i], or
					// generate random)
					target = PatientFactory.FindPatientByMMI(mmiIndex,
							mmiGen.GenerateMMI());
					
					System.out.println(" for patient mmi = " + target.getMmi() );
					for (int j = 0; j < numberOfAllergiesEach; j++) {
						// Choose the allergen
						
						allergen = allergenNames.get(indexGenerator
								.nextInt(allergenNames.size()));
						// Generate the allergy
						AllergyFactory.GenerateDrugAllergy(graphDb,
								ingredientIndex, target, allergen);
						
						System.out.println(" with allergy to ingredient " + allergen);
					}
				}
				tx.commit();

				logger.info("Completed allergy generation...");
		} catch (StorageException se) {
			logger.error(se.getMessage());
			se.printStackTrace();
		} catch (Exception ee) {
			logger.error(ee.getMessage());
			ee.printStackTrace();
		} finally {
			tx.complete();
			graphDb.close();
			logger.info("<<< GeneratePatientAllergies");
		}
	}
}
