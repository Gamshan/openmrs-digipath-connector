package org.openmrs.module.digipath.connector.proforma;

public class DataDefinitionFactory {
	
	public static DataDefinitionEvaluator get(String resourceType) {
		System.out.println("DataDefinitionEvaluator resourceType" + resourceType);
		switch (resourceType) {
			case "Observation":
				return new ObservationEvaluator();
			case "Condition":
				return new ConditionEvaluator();
			case "Medication":
				return new MedicationEvaluator();
			case "Patient":
				return new PatientEvaluator();
			default:
				throw new IllegalArgumentException();
		}
	}
}
