package org.openmrs.module.digipath.connector.api.dao;

import net.openclinical.beans.DataDefinition;
import net.openclinical.beans.Fhir;
import net.openclinical.beans.Range;
import net.openclinical.proforma.Protocol;
import net.openclinical.proforma.enactment.Enactment;
import net.openclinical.proforma.enactment.EnactmentOptions;
import net.openclinical.proforma.enactment.EnactmentStatus;
import net.openclinical.proforma.tasks.Task;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.digipath.connector.proforma.*;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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
	
	private Session getCurrentSession() {
		try {
			return sessionFactory.getCurrentSession();
		}
		catch (NoSuchMethodError ex) {
			try {
				Method method = sessionFactory.getClass().getMethod("getCurrentSession", null);
				return (Session) method.invoke(sessionFactory, null);
			}
			catch (Exception e) {
				throw new RuntimeException("Failed to get the current hibernate session from HibernateDepartmentDAO", e);
			}
		}
	}
	
	@Override
	public List<Map<String, Object>> performProforma(DpAlertsData dpAlertsData, String json, String patientUuid) {
		
		Map<String, List<EnactmentOptions.TimestampedValue>> dataResults = getDataForDataDefinitions(dpAlertsData,
		    patientUuid);
		
		return executeRuleEngine(json, dpAlertsData, dataResults);
	}
	
	private Map<String, List<EnactmentOptions.TimestampedValue>> getDataForDataDefinitions(DpAlertsData dpAlertsData, String patientUuid){
			List<DataDefinition> dataDefinitionList = dpAlertsData.getDataDefinitions();
			Map<String, List<EnactmentOptions.TimestampedValue>> result = new HashMap<>();
			PatientService patientService = Context.getService(PatientService.class);
			Patient patient = patientService.getPatientByUuid(patientUuid);

			dataDefinitionList.forEach(dataDefinition -> {

				System.out.println("###### " + dataDefinition.hasValueCondition());

				if(dataDefinition.hasValueCondition())
					return;

				if(dataDefinition.getMeta() != null && dataDefinition.getMeta().getFhir() != null) {
					List<EnactmentOptions.TimestampedValue> timestampedValueList = getDataByCodeAndPatient(dataDefinition.getMeta().getFhir(), patient, null, dataDefinition.isMultiValued(), dataDefinition.getRange());
					if(timestampedValueList != null)
						result.put(dataDefinition.getName(), timestampedValueList);
				} else if(dataDefinition.getRange() != null){
					List<EnactmentOptions.TimestampedValue> valueList = new ArrayList<>();
					dataDefinition.getRange().forEach(range -> {
						if(range.getMeta() != null && range.getMeta().getFhir() != null) {
							List<EnactmentOptions.TimestampedValue> timestampedValueList = getDataByCodeAndPatient(range.getMeta().getFhir(), patient, range.getValue(), range.isMultiValue(), null );
							if(timestampedValueList != null)
								valueList.addAll(timestampedValueList);
						}
					});
					result.put(dataDefinition.getName(), valueList);
				}
			});

			return result;
		}
	
	private List<Map<String,Object>> executeRuleEngine(String json, DpAlertsData dpAlertsData, Map<String, List<EnactmentOptions.TimestampedValue>> listMap) {

		try {

			Task protocol = Protocol.inflate(json);

			String protocolName = protocol.getName();
			Map<String, Map<String, List<EnactmentOptions.TimestampedValue>>> enactmentData = new HashMap<>();
			enactmentData.put(protocolName, listMap);

			System.out.println("EXECUTE 11111" + enactmentData);

			listMap.forEach((key,value)->{
				System.out.println("EXECUTE" + key + value.size());
			});


			System.out.println("EXECUTE 22222" + protocol.isValid());
			EnactmentOptions enactmentOptions = new EnactmentOptions();
			enactmentOptions.setData(enactmentData);
			System.out.println("EXECUTE 333333");
			enactmentOptions.setStart(true);
			System.out.println("EXECUTE 4444444");
			Enactment enactment = new Enactment(protocol, enactmentOptions);
			System.out.println("EXECUTE 555555" +  enactment.getStatus().isStarted() +  enactment.getStatus().isFinished() +  enactment.getStatus().getCompleteable() + enactment.getStatus().getCancellable());

			System.out.println("EXECUTE 5555551 1" +  enactment.getData());


			List<Map<String,Object>> recommendations = getRecommendations(enactment);
			System.out.println("<========================================>");
			List<Map<String,Object>> cc = Optional.ofNullable(recommendations).orElse(new ArrayList<>());

			System.out.println( "KKKK" + cc);

			return cc;
		}
		catch (Protocol.ProtocolParseException e) {
			throw new Error("ProtocolParseException " + e);
		}
		
	}
	
	public List<Map<String, Object>>  getRecommendations(Enactment enactment) {
		List<Map<String, Object>> recommendations = new ArrayList<>();
		EnactmentStatus enactmentStatus = enactment.getStatus();
		System.out.println("HEREE 1111 " + enactment.getStatus());
		if (enactmentStatus.isStarted() && !enactmentStatus.isFinished()) {
			if (enactmentStatus.getCompleteable().isEmpty()) {
				System.out.println("HEREE 2 ");
				return null;
			}
			List<String> completable = enactmentStatus.getCompleteable();
			for (String completableTask : completable) {
				System.out.println("Here 3333 " + completable);
				System.out.println("Here 44444 " + completableTask);
				Map<String, Object> component = enactment.getComponent(completableTask);
				System.out.println("Here 555555 " + component);
				recommendations.add(component);
			}
		}
		return recommendations;
	}
	
	private List<EnactmentOptions.TimestampedValue> getDataByCodeAndPatient(Fhir fhir, Patient patient, String value,
	        boolean isMultiValue, List<Range> rangeList) {
		if (fhir.getResourceType() != null) {
			DataDefinitionEvaluator dataDefinitionEvaluator = DataDefinitionFactory.get(fhir.getResourceType());
			List<EnactmentOptions.TimestampedValue> timestampedValueList = dataDefinitionEvaluator.evaluate(fhir, patient,
			    value, isMultiValue, rangeList);
			return timestampedValueList;
		}
		return null;
	}
	
	public Map<String, Object> getFhirFormattedData(DpAlertsData dpAlertsData,String json, String patientUuid){
        return getDataForDataDefinitions(dpAlertsData, patientUuid).entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue // Value is already a List, which is an Object
				));
	}
	
	public DigipathConnector saveDigipathConnectorData(DigipathConnector digipathConnector) {
		getCurrentSession().save(digipathConnector);
		return digipathConnector;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<DigipathConnector> getAllDigipathConnectorData() {
		return (List<DigipathConnector>) getCurrentSession().createQuery("from DigipathConnector where voided = false")
		        .list();
	}
	
	@Override
	public DigipathConnector deleteDigipathConnectorData(Integer id) {
		DigipathConnector digipathConnector = (DigipathConnector) getCurrentSession().get(DigipathConnector.class, id);
		
		digipathConnector.setVoided(true);
		digipathConnector.setVoidedBy(Context.getAuthenticatedUser());
		digipathConnector.setDateVoided(new Date());
		digipathConnector.setVoidReason("User deleted from UI");
		getCurrentSession().saveOrUpdate(digipathConnector);
		
		return digipathConnector;
	}
	
	@Override
	public DigipathConnector getDigipathConnectorDataById(Integer id) {
		return (DigipathConnector) getCurrentSession().get(DigipathConnector.class, id);
	}
	
	@Override
	public DigipathConnector updateDigipathConnector(Integer id, DigipathConnector digipathConnector) {
		getCurrentSession().saveOrUpdate(digipathConnector);
		return digipathConnector;
	}
}
