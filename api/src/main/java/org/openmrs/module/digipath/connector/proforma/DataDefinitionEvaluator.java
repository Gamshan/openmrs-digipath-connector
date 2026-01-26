package org.openmrs.module.digipath.connector.proforma;

import net.openclinical.beans.DataDefinition;
import org.openmrs.Patient;

public interface DataDefinitionEvaluator {
	
	Object evaluate(DataDefinition dataDefinition, Patient patient);
	
}
