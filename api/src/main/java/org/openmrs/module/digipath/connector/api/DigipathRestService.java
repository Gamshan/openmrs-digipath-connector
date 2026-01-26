package org.openmrs.module.digipath.connector.api;

import net.openclinical.beans.DataDefinition;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * The service for managing departments.
 */
@Transactional
public interface DigipathRestService extends OpenmrsService {
	
	Map<String, Object> performProforma(List<DataDefinition> dataDefinitionList, String patientId);
	
}
