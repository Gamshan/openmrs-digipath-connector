package org.openmrs.module.digipath.connector.api;

import net.openclinical.beans.DataDefinition;
import net.openclinical.proforma.enactment.EnactmentOptions;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.digipath.connector.proforma.DigipathConnector;
import org.openmrs.module.digipath.connector.proforma.DpAlertsData;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * The service for managing departments.
 */
@Transactional
public interface DigipathRestService extends OpenmrsService {
	
	List<Map<String, Object>> performProforma(DpAlertsData dpAlertsData, String json, String patientId);
	
	Map<String, Object> getFhirFormattedData(DpAlertsData dpAlertsData, String json, String patientUuid);
	
	DigipathConnector saveDigipathConnectorData(DigipathConnector digipathConnector);
	
}
