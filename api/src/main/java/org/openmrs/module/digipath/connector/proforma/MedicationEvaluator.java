package org.openmrs.module.digipath.connector.proforma;

import net.openclinical.beans.Code;
import net.openclinical.beans.DataDefinition;
import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;

import java.util.Date;
import java.util.Optional;

public class MedicationEvaluator implements DataDefinitionEvaluator {
	
	OrderService orderService = Context.getService(OrderService.class);
	
	@Override
	public Object evaluate(DataDefinition dataDefinition, Patient patient) {
		
		System.out.println(" MedicationEvaluator " + 111111);
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
	
	private Date extractAllDataForCode(Code code, Patient patient) {
		Concept concept = DigipathUtils.getConceptByCode(code);
		Optional<Order> optionalOrder = orderService.getOrderHistoryByConcept(patient, concept).stream().findFirst();
		return optionalOrder.map(Order::getDateActivated).orElseGet(() -> null);
	}
}
