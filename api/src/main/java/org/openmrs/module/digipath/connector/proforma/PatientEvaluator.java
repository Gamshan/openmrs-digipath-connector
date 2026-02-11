package org.openmrs.module.digipath.connector.proforma;

import net.openclinical.beans.DataDefinition;
import net.openclinical.beans.Fhir;
import net.openclinical.proforma.enactment.EnactmentOptions;
import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PatientEvaluator implements DataDefinitionEvaluator {
	
	@Override
	public List<EnactmentOptions.TimestampedValue> evaluate(Fhir fhir, Patient patient, String value, boolean isMultiValue) {
		
		System.out.println(" PatientEvaluator " + 111111);
		List<EnactmentOptions.TimestampedValue> timestampedValueList = extractAllDataForCode(patient, fhir.getElement());
		switch (fhir.getElement()) {
			case "birthDate":
				timestampedValueList = extractBirthDate(patient);
				break;
			case "gender":
				timestampedValueList = extractGender(patient);
				break;
			default:
				timestampedValueList = extractAllDataForCode(patient, fhir.getElement());
				break;
		}
		
		return timestampedValueList;
	}
	
	private List<EnactmentOptions.TimestampedValue> extractAllDataForCode(Patient patient, String attribute) {
		return getPropertyValue(patient, attribute);
	}
	
	private List<EnactmentOptions.TimestampedValue> getPropertyValue(Patient patient, String attribute){
		List<EnactmentOptions.TimestampedValue> timestampedValueList = new ArrayList<>();
		Object value = null;

		try {
			value = PropertyUtils.getProperty(patient, attribute);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {

		}

		if(value != null)
			timestampedValueList.add(new EnactmentOptions.TimestampedValue(patient.getDateCreated().toInstant(),value));
		if(!timestampedValueList.isEmpty())
			return timestampedValueList;
		return null;
	}
	
	public List<EnactmentOptions.TimestampedValue> extractBirthDate(Patient patient) {
		List<EnactmentOptions.TimestampedValue> timestampedValueList = new ArrayList<>();
		if(patient.getBirthdate() != null)
			timestampedValueList.add(new EnactmentOptions.TimestampedValue(patient.getDateCreated().toInstant(), patient.getBirthdate().toInstant()));
		if(!timestampedValueList.isEmpty())
			return timestampedValueList;
		return null;
	}
	
	public List<EnactmentOptions.TimestampedValue> extractGender(Patient patient) {
		List<EnactmentOptions.TimestampedValue> timestampedValueList = new ArrayList<>();
		if(patient.getGender() != null)
			timestampedValueList.add(new EnactmentOptions.TimestampedValue(patient.getDateCreated().toInstant(), Objects.equals(patient.getGender(), "F") ? "female" : "male"));
		if(!timestampedValueList.isEmpty())
			return timestampedValueList;
		return null;
	}
}
