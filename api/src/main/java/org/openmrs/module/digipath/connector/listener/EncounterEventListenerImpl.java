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
									if (isCondition(obs.getValueCoded())) {
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
		
		return "Diagnosis".equalsIgnoreCase(className) || "Finding".equalsIgnoreCase(className);
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
