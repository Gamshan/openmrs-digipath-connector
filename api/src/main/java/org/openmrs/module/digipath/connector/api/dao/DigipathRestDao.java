package org.openmrs.module.digipath.connector.api.dao;

import net.openclinical.beans.DataDefinition;

import java.util.List;
import java.util.Map;

public interface DigipathRestDao {
	
	Map<String, Object> performProforma(List<DataDefinition> dataDefinitionList, String patientUuid);
	
}
