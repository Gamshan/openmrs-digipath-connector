package org.openmrs.module.digipath.connector.proforma;

import net.openclinical.beans.DataDefinition;
import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;

public class PatientEvaluator implements DataDefinitionEvaluator {
	
	PatientService patientService = Context.getService(PatientService.class);
	
	@Override
	public Object evaluate(DataDefinition dataDefinition, Patient patient) {
		
		System.out.println(" PatientEvaluator " + 111111);
		Object value = extractAllDataForCode(patient, dataDefinition.getMeta().getFhir().getElementId());
		;
		switch (dataDefinition.getMeta().getFhir().getElementId()) {
			case "birthDate":
				value = extractBirthDate(patient);
				break;
			default:
				value = extractAllDataForCode(patient, dataDefinition.getMeta().getFhir().getElementId());
				break;
		}
		
		return value;
	}
	
	private Object extractAllDataForCode(Patient patient, String attribute) {
		return getPropertyValue(patient, attribute);
	}
	
	private Object getPropertyValue(Patient patient, String attribute){
		Object value = null;
		try {
			System.out.println("44444444");
			value = PropertyUtils.getProperty(patient, attribute);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			System.out.println("55555555");
			return null;
		}
		System.out.println("66666666 " + value);
		return value;
	}
	
	public Instant extractBirthDate(Patient patient) {
		return patient.getBirthdate().toInstant();
	}
}
