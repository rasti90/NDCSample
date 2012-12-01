package com.infinitegraph.samples.ndc;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infinitegraph.AccessMode;
import com.infinitegraph.EdgeKind;
import com.infinitegraph.GraphDatabase;
import com.infinitegraph.GraphException;
import com.infinitegraph.GraphFactory;
import com.infinitegraph.GraphRuntimeException;
import com.infinitegraph.StorageException;
import com.infinitegraph.Transaction;
import com.infinitegraph.indexing.GenericIndex;
import com.infinitegraph.indexing.IndexException;
import com.infinitegraph.indexing.IndexManager;
import com.infinitegraph.samples.ndc.factory.DrugFactory;
import com.infinitegraph.samples.ndc.types.Drug;
import com.infinitegraph.samples.ndc.types.GenericDrug;
import com.infinitegraph.samples.ndc.types.Ingredient;
import com.infinitegraph.samples.ndc.types.NDCEdge;
import com.infinitegraph.samples.ndc.types.NDCVertex;

public class ImportProduct {

	/**
	 * @param args
	 */
	public static final boolean trace = false;
	public static Logger logger = null;
	private static String INDEX_PROPRIETARYNAME_DRUG_LOOKUP = "DrugMap";
	private static String INDEX_NONPROPRIETARYNAME_GENERIC_LOOKUP = "GenericMap";
	private static String INDEX_SUBSTANCENAME_INGREDIENT_LOOKUP = "IngredientMap";
	private static String INDEX_NDC_DRUG_LOOKUP = "NDCMap";
	private static String propertiesFileName = "config/NDCSample.properties";
	private static String graphDbName = "NDCSample";

	public static void main(String[] args) {
		logger = LoggerFactory.getLogger(ImportProduct.class);
		logger.info(">>>> ImportProduct");
		Transaction tx = null;
		GraphDatabase graphDb = null;

		try {
			System.setOut(new PrintStream(new FileOutputStream(
					"ImportProductDataQualityIssues.txt")));
			// Open the database, specifying name and the location of a
			// property file which contains the database descriptor
			try {
				graphDb = GraphFactory.open(graphDbName, propertiesFileName);
				logger.info("Opened NDCSample Database");
			} catch (StorageException sE) {
				logger.warn("couldn't open graph database");
				logger.warn(sE.getMessage());
				return;
			}
			// Start a transaction
			tx = graphDb.beginTransaction(AccessMode.READ_WRITE);
			GenericIndex<NDCVertex> ndcIndex = null;
			try {
				ndcIndex = IndexManager
						.getGenericIndexByName(INDEX_NDC_DRUG_LOOKUP);
			} catch (IndexException e) {
				logger.info(
						"drug name index {} does not exist, run StagePatientDatabase first",
						INDEX_NDC_DRUG_LOOKUP);
				logger.error(e.getMessage());
				return;
			}
			GenericIndex<Drug> drugIndex = null;
			try {
				drugIndex = IndexManager
						.getGenericIndexByName(INDEX_PROPRIETARYNAME_DRUG_LOOKUP);
			} catch (IndexException e) {
				logger.info(
						"drug name index {} does not exist, run StagePatientDatabase first",
						INDEX_PROPRIETARYNAME_DRUG_LOOKUP);
				logger.error(e.getMessage());
				return;
			}
			GenericIndex<GenericDrug> genericDrugIndex = null;
			try {
				genericDrugIndex = IndexManager
						.getGenericIndexByName(INDEX_NONPROPRIETARYNAME_GENERIC_LOOKUP);
			} catch (IndexException e) {
				logger.info(
						"generic drug name index {} does not exist, run StagePatientDatabase first",
						INDEX_NONPROPRIETARYNAME_GENERIC_LOOKUP);
				logger.error(e.getMessage());
				return;
			}
			GenericIndex<Ingredient> ingredientIndex = null;
			try {
				ingredientIndex = IndexManager
						.getGenericIndexByName(INDEX_SUBSTANCENAME_INGREDIENT_LOOKUP);
			} catch (IndexException e) {
				logger.info(
						"ingredient name index {} does not exist, run StagePatientDatabase first",
						INDEX_SUBSTANCENAME_INGREDIENT_LOOKUP);
				logger.error(e.getMessage());
				return;
			}

			String fileName = "NDCdata/product.txt";
			BufferedInputStream bis = null;
			try {
				bis = new BufferedInputStream(new FileInputStream(fileName));
			} catch (FileNotFoundException e1) {
				logger.error("Input file does not exist {}", fileName);
				e1.printStackTrace();
				return;
			}

			Scanner fileScanner;
			fileScanner = new Scanner(bis);
			fileScanner.useDelimiter(System.getProperty("line.separator"));

			fileScanner.next(); // skip header line
			int lineCount = 1;
			String noValue = "NOVALUE";
			while (fileScanner.hasNext()) {
				lineCount++;
				Scanner lineScanner = new Scanner(fileScanner.next());
				lineScanner.useDelimiter("\\t");
				int tokenCount = 0;
				String productNDC = lineScanner.next();
				tokenCount++;
				String productTypeName = lineScanner.next();
				tokenCount++;
				String proprietaryName = lineScanner.next();
				tokenCount++;
				String proprietaryNameSuffix = lineScanner.next();
				tokenCount++;
				// String nonProprietaryName = lineScanner.next();
				// Generic Drug MV
				ArrayList<String> nonProprietaryNameMV = new ArrayList<String>();
				nonProprietaryNameMV = ParseToken(lineScanner.next());
				tokenCount++;
				if (ImportProduct.trace)
				for (int i = 0; i < nonProprietaryNameMV.size(); i++) {
					
						logger.info("nonProprietaryName[{}] = {}", i,
								nonProprietaryNameMV.get(i));
				}
				String dosageForm = lineScanner.next();
				String routeNameMV = lineScanner.next(); // multi-value
				String startMarketingDate = lineScanner.next();
				tokenCount++;
				String endMarketingDate = lineScanner.next();
				tokenCount++;
				String marketingCategoryName = lineScanner.next();
				tokenCount++;
				String applicationNumber = lineScanner.next();
				tokenCount++;
				String labelerName = lineScanner.next();
				tokenCount++;
				// String substanceName = lineScanner.next(); // Ingredient MV
				ArrayList<String> substanceNameMV = new ArrayList<String>();
				substanceNameMV = ParseToken(lineScanner.next());
				tokenCount++;
				if (ImportProduct.trace)
				for (int i = 0; i < substanceNameMV.size(); i++) {
					
						logger.info("substanceNameMV[{}] = {}", i,
								substanceNameMV.get(i));
				}
				// String strengthNumber = lineScanner.next(); // Ingredient MV
				ArrayList<String> strengthNumberMV = new ArrayList<String>();
				strengthNumberMV = ParseToken(lineScanner.next());
				tokenCount++;
				if (ImportProduct.trace)
				for (int i = 0; i < strengthNumberMV.size(); i++) {
					
						logger.info("strengthNumberMV[{}] = {}", i,
								strengthNumberMV.get(i));
				}
				// String strengthUnit = lineScanner.next(); // Ingredient MV
				ArrayList<String> strengthUnitMV = new ArrayList<String>();
				strengthUnitMV = ParseToken(lineScanner.next());
				tokenCount++;
				if (ImportProduct.trace)
				for (int i = 0; i < strengthUnitMV.size(); i++) {
					
						logger.info("strengthUnitMV[{}] = {}", i,
								strengthUnitMV.get(i));
				}
				// String pharm_Classes = lineScanner.next(); // Ingredient MV
				ArrayList<String> pharm_ClassesMV = new ArrayList<String>();
				pharm_ClassesMV = ParseToken(lineScanner.next());
				tokenCount++;
				if (ImportProduct.trace)
				for (int i = 0; i < pharm_ClassesMV.size(); i++) {
					
						logger.info("pharm_ClassesMV[{}] = {}", i,
								pharm_ClassesMV.get(i));
				}
				String dEASchedule = null;
				if (lineScanner.hasNext()) {
					dEASchedule = lineScanner.next();
					tokenCount++;
				}
				//	Always create new drug NDC record, report duplicate if already exists
				NDCVertex ndc = null;
				try {
					ndc = ndcIndex.getSingleResult(productNDC);
					if (ndc!= null)
					{
						System.out.println("Data quality issue: duplicate NDC " + productNDC + " at " + lineCount);
					}
					else
					{
						//	create new NDC vertex and add to graph
						ndc = new NDCVertex(productNDC);
						graphDb.addVertex(ndc);
						//	insert in the NDC index with its productNDC
						ndcIndex.put(ndc);
					}
				} catch (IndexException e) {
					e.getMessage();
					e.printStackTrace();
				}

				//	look for the drug in the drug index with its proprietary name
				Drug drug = null;
				try {
					drug = drugIndex.getSingleResult(proprietaryName);
				} catch (IndexException e) {
					e.printStackTrace();
				}
				//	create drug if it does not exist
				if (drug == null) {
					drug = DrugFactory.CreateNewDrug(graphDb, drugIndex,
							productNDC, productTypeName, proprietaryName,
							proprietaryNameSuffix, dosageForm, routeNameMV,
							startMarketingDate, endMarketingDate,
							marketingCategoryName, applicationNumber,
							labelerName, dEASchedule);
				}
				if (ImportProduct.trace)
					logger.info("Drug {} has id {}", proprietaryName,
							drug.getId());
				//	create an edge between the ndc and the drug
				short weight = 0;
				ndc.addEdge(new NDCEdge(),drug,EdgeKind.OUTGOING,weight);
				//	create generic drug for each multi-value
				GenericDrug genericDrug = null;
				for (int i = 0; i < nonProprietaryNameMV.size(); i++) {
					genericDrug = DrugFactory.CreateNewGenericDrug(graphDb,
							genericDrugIndex, nonProprietaryNameMV.get(i), drug);
					if (ImportProduct.trace)
						logger.info("Generic Drug {} has id {}",
								nonProprietaryNameMV.get(i), genericDrug.getId());
				}
				
				//	create ingredient for each multi-value
				//	first pad the arrays if needed
				if (strengthNumberMV.size() < substanceNameMV.size())
				{
					System.out.println("Data quality issue: mismatch strengthNumberMV size " + strengthNumberMV.size() + " < " + substanceNameMV.size() + " at " + lineCount);
					for (int i = strengthNumberMV.size(); i < substanceNameMV.size(); i++)
					{
						strengthNumberMV.add(noValue);
					}
				}
				if (strengthUnitMV.size() < substanceNameMV.size())
				{
					System.out.println("Data quality issue: mismatch strengthUnitMV size " + strengthUnitMV.size() + " < " + substanceNameMV.size() + " at " + lineCount);
					for (int i = strengthUnitMV.size(); i < substanceNameMV.size(); i++)
					{
						strengthUnitMV.add(noValue);
					}
				}
				if (pharm_ClassesMV.size() < substanceNameMV.size())
				{
					if (ImportProduct.trace)
						System.out.println("Data quality issue: mismatch pharm_ClassesMV size " + pharm_ClassesMV.size() + " < " + substanceNameMV.size() + " at " + lineCount);
					for (int i = pharm_ClassesMV.size(); i < substanceNameMV.size(); i++)
					{
						pharm_ClassesMV.add(noValue);
					}
				}
				//	now create the ingredient
				Ingredient ingredient = null;
				for (int i = 0; i < substanceNameMV.size(); i++) {
					ingredient = DrugFactory.CreateNewIngredient(graphDb,
							genericDrug, ingredientIndex, substanceNameMV.get(i),
							strengthNumberMV.get(i), strengthUnitMV.get(i),
							pharm_ClassesMV.get(i));
					if (ingredient == null)
					{
						logger.error("CreateNewIngredient error: token {} : count {}",substanceNameMV.get(i), tokenCount);
					}
				}
				
				lineScanner.close();
				if (ImportProduct.trace)
					logger.info("\tprocessed {} tokens", tokenCount);
				if ((lineCount%5000) == 0)
				{
					tx.checkpoint();
					logger.info("Processed {} lines",lineCount);
				}
			}

			fileScanner.close();
			logger.info("processed {} lines", lineCount);

			tx.commit();
		} catch (GraphRuntimeException grE) {
			logger.warn(" GraphRuntime Exception was thrown .. ");
			logger.error(grE.getMessage());
			grE.printStackTrace();
		} catch (GraphException gE) {
			logger.warn(" Graph Exception was thrown .. ");
			logger.error(gE.getMessage());
			gE.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			// If the transaction was not committed, complete
			// will roll it back
			if (tx != null)
				tx.complete();
			if (graphDb != null)
				graphDb.close();
			logger.info("<<<< ImportProduct");
			logger.info("On Exit: Closed Graph Database");
		}
	}

	public static ArrayList<String> ParseToken(String token) {
		ArrayList<String> names = new ArrayList<String>();
		int count = 0;
		try {
			Scanner mvScanner = new Scanner(token);
			mvScanner.useDelimiter("; ");
			while (mvScanner.hasNext()) {
				names.add(mvScanner.next());
				count++;
			}
			if (ImportProduct.trace)
				logger.info("\t\tprocessed {} MVs", count);
		} catch (ArrayIndexOutOfBoundsException ex) {
			logger.warn("index out of bounds {} {}", count, token);
			ex.getMessage();
		}
		return names;
	}
}
