package org.openmrs.module.digipath.connector.proforma;

import net.openclinical.beans.Code;
import net.openclinical.beans.DataDefinition;
import net.openclinical.beans.Fhir;
import net.openclinical.proforma.enactment.EnactmentOptions;
import org.openmrs.Concept;
import org.openmrs.Condition;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;

import java.util.*;

public class MedicationEvaluator implements DataDefinitionEvaluator {
	
	OrderService orderService = Context.getService(OrderService.class);
	
	@Override
	public List<EnactmentOptions.TimestampedValue> evaluate(Fhir fhir, Patient patient, String value) {
		
		System.out.println(" MedicationEvaluator " + 111111);
		List<EnactmentOptions.TimestampedValue> timestampedValueList;
		
		switch (fhir.getElement()) {
			case "code":
				timestampedValueList = extractAllDataForCode(fhir.getCode(), patient, value);
				break;
			default:
				throw new IllegalArgumentException();
		}
		
		return timestampedValueList;
	}
	
	private List<EnactmentOptions.TimestampedValue> extractAllDataForCode(Code code, Patient patient, String value) {
		List<EnactmentOptions.TimestampedValue> list = new ArrayList<>();
		Concept concept = DigipathUtils.getConceptByCode(code);
		Optional<Order> optionalOrder = orderService.getOrderHistoryByConcept(patient, concept).stream().findFirst();
		Date date = optionalOrder.map(Order::getDateActivated).orElse(null);
		if(date != null)
			list.add(new EnactmentOptions.TimestampedValue(date.toInstant(), Collections.singletonList(value)));
		if(!list.isEmpty())
			return list;
		return null;
	}
}
