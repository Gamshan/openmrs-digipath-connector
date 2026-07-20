package org.openmrs.module.digipath.connector.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.event.Event;
import org.openmrs.event.EventListener;
import org.openmrs.event.SubscribableEventListener;
import org.openmrs.module.DaemonToken;

import javax.jms.MapMessage;
import javax.jms.Message;
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
		log.info("Encounter event received: ");
		System.out.println("Encounter event received: 222");
		
		if (message instanceof MapMessage) {
			MapMessage mapMessage = (MapMessage) message;
			try {
				final String encounterUuid = mapMessage.getString("uuid");
				
				// 3. Pass BOTH the Runnable and the daemonToken here
				Daemon.runInDaemonThread(new Runnable() {
					
					@Override
					public void run() {
						try {
							Context.openSession();
							Encounter encounter = Context.getEncounterService().getEncounterByUuid(encounterUuid);
							
							if (encounter != null) {
								java.util.Set<Obs> observations = encounter.getObs();
								for (Obs obs : observations) {
									String conceptName = obs.getConcept().getName().getName();
									String value = obs.getValueAsString(Context.getLocale());
									log.info("Found Obs - Concept: " + conceptName + " | Value: " + value);
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
				}, daemonToken); // <-- Second argument added here
				
			}
			catch (Exception e) {
				log.error("Failed to read JMS message payload", e);
			}
		}
	}
	
}
