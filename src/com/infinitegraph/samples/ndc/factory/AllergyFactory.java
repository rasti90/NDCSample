package com.infinitegraph.samples.ndc.factory;

import com.infinitegraph.EdgeKind;
import com.infinitegraph.GraphDatabase;
import com.infinitegraph.VertexHandle;
import com.infinitegraph.indexing.GenericIndex;
import com.infinitegraph.navigation.qualifiers.VertexTypes;
import com.infinitegraph.samples.ndc.types.AllergensEdge;
import com.infinitegraph.samples.ndc.types.Drug;
import com.infinitegraph.samples.ndc.types.DrugAllergy;
import com.infinitegraph.samples.ndc.types.Encounter;
import com.infinitegraph.samples.ndc.types.EncountersEdge;
import com.infinitegraph.samples.ndc.types.GenericDrug;
import com.infinitegraph.samples.ndc.types.Ingredient;
import com.infinitegraph.samples.ndc.types.ObservationsEdge;
import com.infinitegraph.samples.ndc.types.Patient;


public class AllergyFactory 
{
	public static Encounter GenerateDrugAllergy(GraphDatabase graphDb, GenericIndex<Ingredient> ingredientIndex, Patient patient, String allergen)
	{
		// Create an encounter to hold the Allergy observation
		Encounter encounter = new Encounter();
		graphDb.addVertex(encounter);
		encounter.setDate(System.currentTimeMillis());
		encounter.setFacility("Intermountain");
		short weight = 0;
		patient.addEdge(new EncountersEdge(), encounter, EdgeKind.OUTGOING, weight);

		// Create an Allergy Observation
		DrugAllergy drugAllergy = new DrugAllergy();
		graphDb.addVertex(drugAllergy);
		drugAllergy.setDate(System.currentTimeMillis());
		drugAllergy.setDateNoticed(System.currentTimeMillis() - 60000);
		drugAllergy.setDeleted(false);

		// Attach the Drug Allergy to the Encounter (Observation) and the Patient (Allergies)	
		encounter.addEdge(new ObservationsEdge(), drugAllergy,EdgeKind.OUTGOING,weight);

		// Add the Drug Allergy to the Ingredient (Allergens)
		try
		{
			Ingredient ingredient = DrugFactory.GetIngredientByName(graphDb, ingredientIndex, allergen);
			drugAllergy.addEdge(new AllergensEdge(),ingredient,EdgeKind.OUTGOING, weight);
			System.out.println("Ingredient " + ingredient.getSubstanceName()
					+ " is found in:");
			GenericDrug genericDrug = null;
			Drug drug = null;
			long genericDrugTypeId = graphDb.getTypeId(GenericDrug.class.getName());
			VertexTypes genericDrugType = new VertexTypes(genericDrugTypeId);
			long drugTypeId = graphDb.getTypeId(Drug.class.getName());
			VertexTypes drugType = new VertexTypes(drugTypeId);
			//	for each ingredient get the connected generic drugs (foundIn)
			Iterable<VertexHandle> genericDrugIter = ingredient
					.getNeighbors(genericDrugType);
			try{
				for (VertexHandle g : genericDrugIter) {
					genericDrug = (GenericDrug) g.getVertex();
					System.out.println("\tGeneric Drug "
							+ genericDrug.getNonProprietaryName()
							+ " in Branded Drugs");
					try{
						//	for each generic drug get the connected drugs (brands)
						Iterable<VertexHandle> drugIter = genericDrug
								.getNeighbors(drugType);
						for (VertexHandle d : drugIter) {
							drug = (Drug) d.getVertex();
							System.out.println("\t\tbrands "
									+ drug.getProprietaryName());
						}
					}
					catch(OutOfMemoryError ex)
					{
						ex.printStackTrace();
						ex.getMessage();
					}
				}
			}
			catch(OutOfMemoryError ex)
			{
				ex.printStackTrace();
				ex.getMessage();
			}
		}
		catch(OutOfMemoryError ex)
		{
			ex.printStackTrace();
			ex.getMessage();
		}

		return encounter;
	}
}
