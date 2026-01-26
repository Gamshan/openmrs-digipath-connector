package org.openmrs.module.digipath.connector.api.dao;

import net.openclinical.beans.DataDefinition;
import org.hibernate.SessionFactory;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.digipath.connector.proforma.DataDefinitionEvaluator;
import org.openmrs.module.digipath.connector.proforma.DataDefinitionFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HibernateDigipathRestDao implements DigipathRestDao {
	
	private SessionFactory sessionFactory;
	
	//    /**
	//     * @param sessionFactory the sessionFactory to set
	//     */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	//    /**
	//     * @return the sessionFactory
	//     */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	@Override
		public Map<String,Object> performProforma(List<DataDefinition> dataDefinitionList, String patientUuid){
			Map<String,Object> result = new HashMap<>();
			PatientService patientService = Context.getService(PatientService.class);

			Patient patient = patientService.getPatientByUuid(patientUuid);

			dataDefinitionList.forEach(dataDefinition -> {
				Object value = getDataForDataDefinition(dataDefinition,patient);
				result.put(dataDefinition.getName(), value);
			});

			return result;
		}
	
	private Object getDataForDataDefinition(DataDefinition dataDefinition, Patient patient) {
		
		System.out.println("getValueOfDataDefinition");
		if (dataDefinition.getMeta() != null && dataDefinition.getMeta().getFhir() != null
		        && dataDefinition.getMeta().getFhir().getResourceType() != null) {
			System.out.println("Inside getValueOfDataDefinition");
			DataDefinitionEvaluator dataDefinitionEvaluator = DataDefinitionFactory.get(dataDefinition.getMeta().getFhir()
			        .getResourceType());
			Object object = dataDefinitionEvaluator.evaluate(dataDefinition, patient);
			System.out.println("getValueOfDataDefinition object" + object);
			return object;
		}
		return null;
	}
}
