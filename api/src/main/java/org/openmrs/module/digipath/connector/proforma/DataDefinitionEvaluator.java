package org.openmrs.module.digipath.connector.proforma;

import net.openclinical.beans.DataDefinition;
import net.openclinical.beans.Fhir;
import net.openclinical.proforma.enactment.EnactmentOptions;
import org.openmrs.Patient;

import java.util.List;

public interface DataDefinitionEvaluator {
	
	List<EnactmentOptions.TimestampedValue> evaluate(Fhir fhir, Patient patient, String value);
	
}
