package com.infinitegraph.samples.ndc;

import com.infinitegraph.samples.ndc.types.BrandsEdge;
import com.infinitegraph.samples.ndc.types.Drug;
import com.infinitegraph.samples.ndc.types.FoundInEdge;
import com.infinitegraph.samples.ndc.types.GenericDrug;
import com.infinitegraph.samples.ndc.types.GenericEdge;
import com.infinitegraph.samples.ndc.types.Ingredient;
import com.infinitegraph.samples.ndc.types.IngredientsEdge;

import java.io.*;

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
import com.infinitegraph.indexing.IndexIterable;
import com.infinitegraph.indexing.IndexManager;
import com.infinitegraph.navigation.qualifiers.VertexTypes;

public class PrintDrugs {
	/**
	 * @param args
	 */
	private static Logger logger = null;
	private static String dbName = "NDCSample";
	private static String propFile = "config/NDCSample.properties";
	private static String INDEX_PROPRIETARYNAME_DRUG_LOOKUP = "DrugMap";
	private static String INDEX_NONPROPRIETARYNAME_GENERIC_LOOKUP = "GenericMap";
	private static String INDEX_SUBSTANCENAME_INGREDIENT_LOOKUP = "IngredientMap";
	
	private static GraphDatabase graphDb = null;
	private static String typeIdOutput;
	private static GenericIndex<Ingredient> ingredientIndex = null;
	private static GenericIndex<Drug> drugIndex = null;
	private static GenericIndex<GenericDrug> genericDrugIndex = null;

	public static void main(String[] args) {
		logger = LoggerFactory.getLogger(PrintDrugs.class);
		logger.info(">>> PrintDrugs");
		
		Transaction tx = null;

		try {

			// open the graph database
			graphDb = GraphFactory.open(dbName, propFile);
			tx = graphDb.beginTransaction(AccessMode.READ);
			typeIdOutput = "Drug TypeId = "+ graphDb.getTypeId(Drug.class.getName());
			typeIdOutput += "\nGenericDrug TypeId = "+ graphDb.getTypeId(GenericDrug.class.getName());
			typeIdOutput += "\nIngredient TypeId = "+ graphDb.getTypeId(Ingredient.class.getName());
			typeIdOutput += "\nBrandsEdge TypeId = "+ graphDb.getTypeId(BrandsEdge.class.getName());
			typeIdOutput += "\nFoundInEdge TypeId = "+ graphDb.getTypeId(FoundInEdge.class.getName());
			//	ignore these types in EdgeTypePathQualifier
			typeIdOutput += "\nIngredientsEdge TypeId = "+ graphDb.getTypeId(IngredientsEdge.class.getName());
			typeIdOutput += "\nGenericEdge TypeId = "+ graphDb.getTypeId(GenericEdge.class.getName());
			logger.info(typeIdOutput);

			try {
				ingredientIndex = IndexManager
						.getGenericIndexByName(INDEX_SUBSTANCENAME_INGREDIENT_LOOKUP);
			} catch (IndexException e) {
				logger.error(e.getMessage());
				logger.info("ingredient index does not exist, initialise graph databaase");
			}

			try {
				drugIndex = IndexManager
						.getGenericIndexByName(INDEX_PROPRIETARYNAME_DRUG_LOOKUP);
			} catch (IndexException e) {
				logger.error(e.getMessage());
				logger.info("drug index does not exist, initialise graph databaase");
			}

			try {
				genericDrugIndex = IndexManager
						.getGenericIndexByName(INDEX_NONPROPRIETARYNAME_GENERIC_LOOKUP);
			} catch (IndexException e) {
				logger.info("generic drug index does not exist, initialise graph databaase");
			}

			printDrugInfo();
			printGenericDrugInfo();
			printIngredientInfo();
			
		} catch (StorageException se) {
			logger.error(se.getMessage());
			se.printStackTrace();
		} catch (Exception ee) {
			logger.error(ee.getMessage());
			ee.printStackTrace();
		} finally {
			tx.complete();
			graphDb.close();
			logger.info("<<< PrintDrugs");
		}
	}
	
	private static void printDrugInfo() throws FileNotFoundException, IndexException
	{
		logger.info("Printing Drug information");
		System.setOut(new PrintStream(new FileOutputStream(
				"PrintDrugs.txt")));
		System.out.println(typeIdOutput);
		
		//	get all the drugs in the drug index
		IndexIterable<Drug> drugIter = drugIndex.getAllElements();
		int drugCount = 0;
		for (Drug drug : drugIter) {
			drugCount++;
			System.out.println("Drug : " + drug.getProprietaryName()
					+ " has GenericDrug");
			GenericDrug genericDrug = null;
			//	for each drug get the connected generic drugs (should be one generic)
			Iterable<VertexHandle> genericDrugIter = drug
					.getNeighbors(new VertexTypes(graphDb
							.getTypeId(GenericDrug.class.getName())));
			for (VertexHandle g : genericDrugIter) {
				genericDrug = (GenericDrug) g.getVertex();
				System.out.println("\tGenericDrug : "
						+ genericDrug.getNonProprietaryName()
						+ " has Ingredients");
				Ingredient genericDrugIngredient = null;
				//	for each generic drug get the connected ingredients (ingredients)
				Iterable<VertexHandle> drugIngredientIter = genericDrug
						.getNeighbors(new VertexTypes(graphDb
								.getTypeId(Ingredient.class.getName())));
				for (VertexHandle i : drugIngredientIter) {
					genericDrugIngredient = (Ingredient) i
							.getVertex();

					System.out.println("\t\t Ingredient: "
							+ genericDrugIngredient.getSubstanceName());
				}
			}
		}
		logger.info("Drug count = {}",drugCount);
	}
	
	private static void printGenericDrugInfo() throws FileNotFoundException, IndexException
	{
		logger.info("Printing Generic Drug information");
		System.setOut(new PrintStream(new FileOutputStream(
				"PrintGenericDrugs.txt")));
		System.out.println(typeIdOutput);

		//	get all generic drugs in generic drug index
		IndexIterable<GenericDrug> genericDrugIter = genericDrugIndex
				.getAllElements();
		int genericDrugCount = 0;
		for (GenericDrug genericDrug : genericDrugIter) {
			genericDrugCount++;
			System.out.println("\tGeneric Drug "
					+ genericDrug.getNonProprietaryName()
					+ " in Branded Drugs");
			//	for each generic drug get the connected drugs (brands)
			Iterable<VertexHandle> drugIter = genericDrug
					.getNeighbors(new VertexTypes(graphDb
							.getTypeId(Drug.class.getName())));
			for (VertexHandle d : drugIter) {
				Drug drug = (Drug) d.getVertex();
				System.out.println("\t\tbrands "
						+ drug.getProprietaryName());
			}
			System.out.println("\tGenericDrug : "
					+ genericDrug.getNonProprietaryName()
					+ " has Ingredients");
			Ingredient genericDrugIngredient = null;
			//	for each generic drug get the connected ingredients (ingredients)
			Iterable<VertexHandle> drugIngredientIter = genericDrug
					.getNeighbors(new VertexTypes(graphDb
							.getTypeId(Ingredient.class.getName())));
			for (VertexHandle i : drugIngredientIter) {
				genericDrugIngredient = (Ingredient) i
						.getVertex();

				System.out.println("\t\t Ingredient: "
						+ genericDrugIngredient.getSubstanceName());
			}

		}
		logger.info("Generic Drug count = {}",genericDrugCount);
	}
	
	private static void printIngredientInfo() throws FileNotFoundException, IndexException
	{
		logger.info("Printing Ingredient information");
		System.setOut(new PrintStream(new FileOutputStream(
				"PrintIngredients.txt")));
		System.out.println(typeIdOutput);

		//	get all ingredients in ingredient index
		IndexIterable<Ingredient> ingredientIter = ingredientIndex
				.getAllElements();
		int ingredientCount = 0;
		for (Ingredient ingredient : ingredientIter) {
			ingredientCount++;
			System.out.println("Ingredient " + ingredient.getSubstanceName()
					+ " is found in:");
			GenericDrug genericDrug = null;
			//	for each ingredient get the connected generic drugs (foundIn)
			Iterable<VertexHandle> genericDrugIter = ingredient
					.getNeighbors(new VertexTypes(graphDb
							.getTypeId(GenericDrug.class.getName())));
			for (VertexHandle g : genericDrugIter) {
				genericDrug = (GenericDrug) g.getVertex();
				System.out.println("\tGeneric Drug "
						+ genericDrug.getNonProprietaryName()
						+ " in Branded Drugs");
				//	for each generic drug get the connected drugs (brands)
				Iterable<VertexHandle> drugIter = genericDrug
						.getNeighbors(new VertexTypes(graphDb
								.getTypeId(Drug.class.getName())));
				for (VertexHandle d : drugIter) {
					Drug drug = (Drug) d.getVertex();
					System.out.println("\t\tbrands "
							+ drug.getProprietaryName());
				}
			}
		}
		logger.info("Ingredient count = {}",ingredientCount);
	}
}
