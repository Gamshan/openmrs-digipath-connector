package org.openmrs.module.digipath.connector.api.dao;

import net.openclinical.beans.DataDefinition;
import net.openclinical.proforma.enactment.EnactmentOptions;
import org.openmrs.module.digipath.connector.proforma.DigipathConnector;
import org.openmrs.module.digipath.connector.proforma.DpAlertsData;

import java.util.List;
import java.util.Map;

public interface DigipathRestDao {
	
	List<Map<String, Object>> performProforma(DpAlertsData dpAlertsData, String json, String patientUuid);
	
	Map<String, Object> getFhirFormattedData(DpAlertsData dpAlertsData, String json, String patientUuid);
	
	DigipathConnector saveDigipathConnectorData(DigipathConnector digipathConnector);
	
}
