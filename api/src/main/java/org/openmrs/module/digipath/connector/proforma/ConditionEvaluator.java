package org.openmrs.module.digipath.connector.proforma;

import net.openclinical.beans.Code;
import net.openclinical.beans.DataDefinition;
import org.openmrs.Concept;
import org.openmrs.Condition;
import org.openmrs.Patient;
import org.openmrs.api.ConditionService;
import org.openmrs.api.context.Context;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ConditionEvaluator implements DataDefinitionEvaluator {
	
	ConditionService conditionService = Context.getService(ConditionService.class);
	
	@Override
	public Object evaluate(DataDefinition dataDefinition, Patient patient) {
		
		System.out.println(" ConditionEvaluator " + 111111);
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
	
	private Instant extractAllDataForCode(Code code, Patient patient) {
		Concept concept = DigipathUtils.getConceptByCode(code);
		List<Condition> conditionList = conditionService.getActiveConditions(patient);

		Stream<Condition> filteredList =  conditionList.stream().filter(condition -> condition.getCondition().getCoded() == concept);
		Optional<Condition> optionalCondition = filteredList.findFirst();
        Date date = optionalCondition.map(Condition::getOnsetDate).orElse(null);
		if(date!= null)
			return date.toInstant();
		return null;
    }
}
