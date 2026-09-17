package org.openmrs.module.digipath.connector.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.ConditionService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.event.Event;
import org.openmrs.event.EventListener;
import org.openmrs.event.SubscribableEventListener;
import org.openmrs.module.DaemonToken;

import javax.jms.MapMessage;
import javax.jms.Message;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EncounterEventListenerImpl implements EventListener {
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	private DaemonToken daemonToken;
	
	public EncounterEventListenerImpl(DaemonToken daemonToken) {
		this.daemonToken = daemonToken;
	}
	
	@Override
	public void onMessage(Message message) {

		List<String> avoidConditionList =  new ArrayList<>();
		avoidConditionList.add("b118c548-0337-5df7-8fd0-5890352da9b0");
		avoidConditionList.add("165206AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		avoidConditionList.add("165203AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		avoidConditionList.add("165204AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		avoidConditionList.add("165205AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		avoidConditionList.add("135576AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		avoidConditionList.add("d3f316c3-7a53-447b-b741-80feeb144630");
		avoidConditionList.add("f785a8d6-92f6-4213-a2bc-3b35f4356265");
		avoidConditionList.add("28e1ddee-fced-48c1-8053-4ee9d5b55966");
		avoidConditionList.add("9ac2cc06-11db-4d0d-88b1-87146f2feecf");
		avoidConditionList.add("b942f3c0-5c08-4cdd-b8c6-9b802cfe2629");

		avoidConditionList.add("cff7d75c-4279-5ca2-aff1-1b9722681a05");
		avoidConditionList.add("158423AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");


		
		if (message instanceof MapMessage) {
			MapMessage mapMessage = (MapMessage) message;
			try {
				
				final String encounterUuid = mapMessage.getString("uuid");
				Daemon.runInDaemonThread(new Runnable() {
					
					@Override
					public void run() {
						try {
							Context.openSession();
							Encounter encounter = Context.getEncounterService().getEncounterByUuid(encounterUuid);
							
							if (encounter != null) {
								java.util.Set<Obs> observations = encounter.getObs();
								for (Obs obs : observations) {

									if(obs.getConcept() != null && obs.getConcept().getAnswers() != null){

//										obs.getConcept().getAnswers().stream().forEach(ans->{
//											System.out.println("FGHhhhhhh Workeddddd" + obs.getConcept());
//										});

									}

									if (isCondition(obs.getValueCoded()) && !avoidConditionList.contains(obs.getValueCoded().getUuid())) {
										System.out.println("SAVING " + obs.getValueCoded().getUuid());
										saveAsActiveCondition(encounter, obs.getValueCoded());
									}
								}
							}
						}
						catch (Exception ex) {
							log.error("Error fetching observations for encounter " + encounterUuid, ex);
						}
						finally {
							Context.closeSession();
						}
					}
				}, daemonToken);
				
			}
			catch (Exception e) {
				log.error("Failed to read JMS message payload", e);
			}
		}
	}
	
	public boolean isCondition(Concept concept) {
		if (concept == null || concept.getConceptClass() == null) {
			return false;
		}
		
		String className = concept.getConceptClass().getName();
		
		return "Diagnosis".equalsIgnoreCase(className); // || "Finding".equalsIgnoreCase(className); //Remove findings
	}
	
	private void saveAsActiveCondition(Encounter encounter, Concept concept) {
		try {
			ConditionService conditionService = Context.getConditionService();
			java.util.List<Condition> activeConditions = conditionService.getActiveConditions(encounter.getPatient());
			
			for (Condition existingCondition : activeConditions) {
				if (existingCondition != null && existingCondition.getCondition().getCoded().equals(concept)) {
					return;
				}
			}
			
			Condition condition = new Condition();
			condition.setPatient(encounter.getPatient());
			CodedOrFreeText coded = new CodedOrFreeText();
			coded.setCoded(concept);
			
			condition.setCondition(coded);
			
			condition.setClinicalStatus(ConditionClinicalStatus.ACTIVE);
			condition.setEndDate(null);
			
			condition.setOnsetDate(new Date());
			condition.setCreator(Context.getAuthenticatedUser());
			condition.setDateCreated(new Date());
			condition.setVoided(false);
			conditionService.saveCondition(condition);
			
		}
		catch (Exception e) {
			log.error("Failed to persist condition to OpenMRS platform database infrastructure", e);
		}
	}
	
}
