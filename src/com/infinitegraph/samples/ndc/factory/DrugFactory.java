package com.infinitegraph.samples.ndc.factory;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infinitegraph.samples.ndc.ImportProduct;
import com.infinitegraph.samples.ndc.types.BrandsEdge;
import com.infinitegraph.samples.ndc.types.Drug;
import com.infinitegraph.samples.ndc.types.FoundInEdge;
import com.infinitegraph.samples.ndc.types.GenericDrug;
import com.infinitegraph.samples.ndc.types.GenericEdge;
import com.infinitegraph.samples.ndc.types.Ingredient;
import com.infinitegraph.samples.ndc.types.IngredientsEdge;

import com.infinitegraph.AccessMode;
import com.infinitegraph.EdgeKind;
import com.infinitegraph.GraphDatabase;
import com.infinitegraph.Transaction;
import com.infinitegraph.indexing.GenericIndex;
import com.infinitegraph.indexing.IndexException;
import com.infinitegraph.indexing.IndexIterable;
import com.infinitegraph.indexing.IndexManager;

public class DrugFactory {
	private static String INDEX_PROPRIETARYNAME_DRUG_LOOKUP = "DrugMap";
	private static String INDEX_NONPROPRIETARYNAME_GENERIC_LOOKUP = "GenericMap";
	private static String INDEX_SUBSTANCENAME_INGREDIENT_LOOKUP = "IngredientMap";
	private static String INDEX_NDC_DRUG_LOOKUP = "NDCMap";

	private static Logger logger = null;

	//	Generates the infrastructure for storing drug data
	public static void CreateDrugInfrastructure(GraphDatabase graphDb) {
		logger = LoggerFactory.getLogger(DrugFactory.class);

		logger.info(">>> CreateDrugInfrastructure");
		Transaction tx = graphDb.beginTransaction(AccessMode.READ_WRITE);

		try {
			IndexManager
					.getGenericIndexByName(INDEX_NDC_DRUG_LOOKUP);
		} catch (IndexException e) {
			logger.info("creating drug ndc index");
			try {
				IndexManager.createGenericIndex(
						INDEX_NDC_DRUG_LOOKUP, "com.infinitegraph.samples.ndc.types.NDCVertex", "mProductNDC");
			} catch (IndexException e1) {
				logger.error(e1.getMessage());
				e1.printStackTrace();
				tx.complete();
			}
		}

		try {
			IndexManager
					.getGenericIndexByName(INDEX_PROPRIETARYNAME_DRUG_LOOKUP);
		} catch (IndexException e) {
			logger.info("creating drug name index");
			try {
				IndexManager.createGenericIndex(
						INDEX_PROPRIETARYNAME_DRUG_LOOKUP, "com.infinitegraph.samples.ndc.types.Drug",
						"mProprietaryName");
			} catch (IndexException e1) {
				logger.error(e1.getMessage());
				e1.printStackTrace();
				tx.complete();
			}
		}

		try {
			IndexManager
					.getGenericIndexByName(INDEX_NONPROPRIETARYNAME_GENERIC_LOOKUP);
		} catch (IndexException e) {
			logger.info("creating generic drug name index");
			try {
				IndexManager.createGenericIndex(
						INDEX_NONPROPRIETARYNAME_GENERIC_LOOKUP,
						"com.infinitegraph.samples.ndc.types.GenericDrug", "mNonProprietaryName");
			} catch (IndexException e1) {
				logger.error(e1.getMessage());
				e1.printStackTrace();
				tx.complete();
			}
		}

		try {
			IndexManager
					.getGenericIndexByName(INDEX_SUBSTANCENAME_INGREDIENT_LOOKUP);
		} catch (IndexException e) {
			logger.info("creating ingredient name index");
			try {
				IndexManager.createGenericIndex(
						INDEX_SUBSTANCENAME_INGREDIENT_LOOKUP,
						"com.infinitegraph.samples.ndc.types.Ingredient", "mSubstanceName");
			} catch (IndexException e1) {
				logger.error(e1.getMessage());
				e1.printStackTrace();
				tx.complete();
			}
		}
		tx.commit();
		logger.info("<<< CreateDrugInfrastructure");
	}

	//	Create new drug
	public static Drug CreateNewDrug(GraphDatabase graphDb,
			GenericIndex<Drug> drugIndex, String productNDC,
			String productTypeName, String proprietaryName,
			String proprietaryNameSuffix, String dosageForm,
			String routeNameMV, String startMarketingDate,
			String endMarketingDate, String marketingCategoryName,
			String applicationNumber, String labelerName, String dEASchedule) {

		Drug drug = null;

		drug = new Drug(productTypeName, proprietaryName,
				proprietaryNameSuffix, dosageForm, routeNameMV,
				startMarketingDate, endMarketingDate, marketingCategoryName,
				applicationNumber, labelerName, dEASchedule);

		graphDb.addVertex(drug);
		try {
			drugIndex.put(drug);
		} catch (IndexException e) {
			e.getMessage();
			e.printStackTrace();
		}
		if (ImportProduct.trace)
			logger.info("Drug {} has id {}", proprietaryName, drug.getId());

		return drug;
	}

	public static Drug GetDrugByName(GenericIndex<Drug> drugIndex, String name) {
		Drug drug = null;
		try {
			drug = drugIndex.getSingleResult(name);
		} catch (IndexException e) {
			e.printStackTrace();
		}

		if (drug != null) {
			return drug;
		}
		return null;
	}

	public static IndexIterable<Drug> GetDrugInRange(
			GenericIndex<Drug> drugIndex, Object start, Object end) {
		IndexIterable<Drug> drugIter = null;
		try {
			drugIter = drugIndex.getRange(start, end);
		} catch (IndexException e) {
			e.printStackTrace();
		}

		if (drugIter != null) {
			return drugIter;
		} else {
			return null;
		}
	}

	public static GenericDrug CreateNewGenericDrug(GraphDatabase graphDb,
			GenericIndex<GenericDrug> genericDrugIndex,
			String nonProprietaryNameMV, Drug drug) {

		// Add the generic itself
		GenericDrug genericDrug = null;

		// does the generic drug exist? MV
		genericDrug = null;
		try {
			genericDrug = genericDrugIndex
					.getSingleResult(nonProprietaryNameMV);
		} catch (IndexException e) {
			e.getMessage();
			e.printStackTrace();
		}
		if (genericDrug == null) {
			genericDrug = new GenericDrug(nonProprietaryNameMV);
			graphDb.addVertex(genericDrug);
			if (ImportProduct.trace)
				logger.info("Generic Drug {} has id {}", nonProprietaryNameMV,
						genericDrug.getId());
			try {
				genericDrugIndex.put(genericDrug);
			} catch (IndexException e) {
				e.getMessage();
				e.printStackTrace();
			}
		}
		short weight = 0;
		genericDrug.addEdge(new BrandsEdge(), drug, EdgeKind.OUTGOING, weight);
		drug.addEdge(new GenericEdge(), genericDrug, EdgeKind.OUTGOING, weight);
		return genericDrug;
	}

	public static Ingredient CreateNewIngredient(
			GraphDatabase graphDb,
			GenericDrug genericDrug,
			GenericIndex<Ingredient> ingredientIndex,
			String substanceName,
			String strengthNumber,
			String strengthUnit,
			String pharm_Classes) {
		
		short weight = 0;
		// does the ingredient exist?
		Ingredient ingredient = null;
		try {
			ingredient = ingredientIndex.getSingleResult(substanceName);
		} catch (IndexException e) {
			e.getMessage();
			e.printStackTrace();
		}
		if (ingredient == null)
		{
			ingredient = new Ingredient(substanceName);
			graphDb.addVertex(ingredient);

			if (ImportProduct.trace) logger.info("Ingredient {} has id {}",substanceName,ingredient.getId());
			try {
				ingredientIndex.put(ingredient);
			} catch (IndexException e) {
				e.getMessage();
				e.printStackTrace();
			}
		}
		genericDrug.addEdge(new IngredientsEdge(strengthNumber,strengthUnit,pharm_Classes),ingredient,EdgeKind.OUTGOING,weight);
		ingredient.addEdge(new FoundInEdge(),genericDrug,EdgeKind.OUTGOING,weight);
		
		return ingredient;
	}

	public static Ingredient GetIngredientByName(GraphDatabase graphDb,
			GenericIndex<Ingredient> ingredientIndex, String name) {
		Ingredient ingredient = null;
		try {
			ingredient = ingredientIndex.getSingleResult(name);
		} catch (IndexException e) {
			e.printStackTrace();
		}
		if (ingredient != null) {
			return ingredient;
		} else {
			return null;
		}
	}

	public static ArrayList<String> GetDrugNames(GraphDatabase graphDb,
			GenericIndex<Drug> drugIndex) {
		ArrayList<String> names = new ArrayList<String>();

		// Get the Drug collection and iterate the keys
		IndexIterable<Drug> drugIter;
		try {
			drugIter = drugIndex.getAllElements();

			for (Drug d : drugIter) {
				names.add(d.getProprietaryName());
			}
		} catch (IndexException e) {
			e.printStackTrace();
		}
		return names;
	}

	public static ArrayList<String> GetIngredientNames(GraphDatabase graphDb,
			GenericIndex<Ingredient> ingredientIndex) {
		ArrayList<String> names = new ArrayList<String>();

		// Get the Ingredient collection and iterate the keys
		IndexIterable<Ingredient> ingredientIter;
		try {
			ingredientIter = ingredientIndex.getAllElements();

			for (Ingredient i : ingredientIter) {
				names.add(i.getSubstanceName());
			}
		} catch (IndexException e) {
			e.printStackTrace();
		}
		return names;
	}
}
