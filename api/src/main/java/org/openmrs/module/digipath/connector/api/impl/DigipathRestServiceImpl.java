package org.openmrs.module.digipath.connector.api.impl;

import net.openclinical.beans.DataDefinition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.digipath.connector.api.DigipathRestService;
import org.openmrs.module.digipath.connector.api.dao.DigipathRestDao;

import java.util.List;
import java.util.Map;

///**
// * It is a default implementation of {@link DepartmentService}.
// */
public class DigipathRestServiceImpl extends BaseOpenmrsService implements DigipathRestService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private DigipathRestDao dao;
	
	/**
	 * @param dao the dao to set
	 */
	public void setDao(DigipathRestDao dao) {
		this.dao = dao;
	}
	
	/**
	 * @return the dao
	 */
	public DigipathRestDao getDao() {
		return dao;
	}
	
	@Override
	public Map<String, Object> performProforma(List<DataDefinition> dataDefinitionList, String patientUuid) {
		return dao.performProforma(dataDefinitionList, patientUuid);
	}
	
}
