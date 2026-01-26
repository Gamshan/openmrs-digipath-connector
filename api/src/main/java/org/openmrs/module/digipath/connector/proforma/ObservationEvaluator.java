package org.openmrs.module.digipath.connector.proforma;

import net.openclinical.beans.Code;
import net.openclinical.beans.DataDefinition;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;

import java.util.Optional;

public class ObservationEvaluator implements DataDefinitionEvaluator {
	
	ObsService obsService = Context.getService(ObsService.class);
	
	@Override
	public Object evaluate(DataDefinition dataDefinition, Patient patient) {
		
		System.out.println(" ObservationEvaluator " + 111111);
		Object value;
		switch (dataDefinition.getMeta().getFhir().getElementId()) {
			case "code":
				value = extractAllDataForCode(dataDefinition.getMeta().getFhir().getCode(), patient);
				break;
			default:
				throw new IllegalArgumentException();
		}
		
		return value;
	}
	
	private Float extractAllDataForCode(Code code, Patient patient) {
		System.out.println("Yesss ");
		Concept concept = DigipathUtils.getConceptByCode(code);
		System.out.println("concept " + concept.getUuid());
		Optional<Obs> optionalObs = obsService.getObservationsByPersonAndConcept(patient, concept).stream().findFirst();
        return optionalObs.map(obs -> obs.getValueNumeric().floatValue()).orElseGet(() -> (float) 0);
    }
}
