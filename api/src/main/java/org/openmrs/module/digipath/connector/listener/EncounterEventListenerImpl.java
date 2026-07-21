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
	
	// 2. Accept the token in the constructor
	public EncounterEventListenerImpl(DaemonToken daemonToken) {
		this.daemonToken = daemonToken;
	}
	
	@Override
	public void onMessage(Message message) {
		log.info("XXXXXXX Encounter event received: " + message.toString());
		System.out.println("XXXXXXX Encounter event received: 222" + message.toString());
		
		if (message instanceof MapMessage) {
			System.out.println("XXXXXXX message 1111");
			MapMessage mapMessage = (MapMessage) message;
			try {
				System.out.println("XXXXXXX message 222222");
				final String encounterUuid = mapMessage.getString("uuid");
				
				// 3. Pass BOTH the Runnable and the daemonToken here
				Daemon.runInDaemonThread(new Runnable() {
					
					@Override
					public void run() {
						try {
							System.out.println("XXXXXXX message 3333333");
							Context.openSession();
							Encounter encounter = Context.getEncounterService().getEncounterByUuid(encounterUuid);
							
							if (encounter != null) {
								System.out.println("XXXXXXX message 444444");
								java.util.Set<Obs> observations = encounter.getObs();
								System.out.println("XXXXXXX message 555555" + observations.toString());
								for (Obs obs : observations) {
									System.out.println("XXXXXXX message 6666666" + obs.getConcept().getUuid());
									String conceptName = obs.getConcept().getName().getName();
									String value = obs.getValueAsString(Context.getLocale());
									System.out.println("XXXXXXX message 7777777" + value + obs.getValueCoded());
									log.info("Found Obs - Concept: " + conceptName + " | Value: " + value);
									
									if (isCondition(obs.getValueCoded())) {
										System.out.println("XXXXXXX isCondition trueee");
										saveAsActiveCondition(encounter, obs.getValueCoded());
									} else
										System.out.println("XXXXXXX isCondition falseee");
								}
							}
						}
						catch (Exception ex) {
							System.out.println("XXXXXXX message EROOR" + ex);
							log.error("Error fetching observations for encounter " + encounterUuid, ex);
						}
						finally {
							Context.closeSession();
						}
					}
				}, daemonToken); // <-- Second argument added here
				
			}
			catch (Exception e) {
				System.out.println("XXXXXXX Failed to read JMS message payload " + e);
				log.error("Failed to read JMS message payload", e);
			}
		}
	}
	
	public boolean isCondition(Concept concept) {
		if (concept == null || concept.getConceptClass() == null) {
			return false;
		}
		
		// Get the developer name of the concept class (e.g., "Diagnosis", "Finding")
		String className = concept.getConceptClass().getName();
		
		return "Diagnosis".equalsIgnoreCase(className) || "Finding".equalsIgnoreCase(className);
	}
	
	private void saveAsActiveCondition(Encounter encounter, Concept concept) {
		try {
			ConditionService conditionService = Context.getConditionService();
			
			System.out.println("XXXXXXX ### " + concept.getUuid());
			
			java.util.List<Condition> activeConditions = conditionService.getActiveConditions(encounter.getPatient());
			System.out.println("XXXXXXX ##333 " + activeConditions.size());
			
			for (Condition existingCondition : activeConditions) {
				System.out.println("XXXXXXX ##555555 " + existingCondition.getCondition().getCoded() + concept);
				System.out.println("XXXXXXX ##666666 " + existingCondition.getCondition().getCoded().getUuid()
				        + concept.getUuid());
				System.out.println("XXXXXXX ##777777 " + existingCondition.getCondition().getCoded().getId()
				        + concept.getId());
				if (existingCondition != null && existingCondition.getCondition().getCoded().equals(concept)) {
					log.info("XXXXXXX Condition already exists on the patient's active condition table. Skipping creation.");
					return;
				}
			}
			
			Condition condition = new Condition();
			condition.setPatient(encounter.getPatient());
			CodedOrFreeText coded = new CodedOrFreeText();
			coded.setCoded(concept);
			System.out.println("XXXXXXX 0000 " + coded.getCoded());
			
			condition.setCondition(coded);
			
			condition.setClinicalStatus(ConditionClinicalStatus.ACTIVE);
			condition.setEndDate(null);
			
			condition.setOnsetDate(new Date());
			condition.setCreator(Context.getAuthenticatedUser());
			condition.setDateCreated(new Date());
			condition.setVoided(false);
			conditionService.saveCondition(condition);
			
			System.out.println("XXXXXXX Successfully added condition " + concept.getName().getName()
			        + " to the OpenMRS Patient Conditions Table.");
			
		}
		catch (Exception e) {
			log.error("XXXXXXX Failed to persist condition to OpenMRS platform database infrastructure", e);
		}
	}
	
}
