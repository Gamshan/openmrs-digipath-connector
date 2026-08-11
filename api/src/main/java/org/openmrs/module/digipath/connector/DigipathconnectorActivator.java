/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.digipath.connector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.event.EventEngine;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.digipath.connector.listener.EncounterEventListenerImpl;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class DigipathconnectorActivator extends BaseModuleActivator implements DaemonTokenAware {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see #started()
	 */
	
	private EncounterEventListenerImpl encounterEventListener;
	
	private DaemonToken daemonToken;
	
	// OpenMRS will automatically inject the token here before calling started()
	public void setDaemonToken(DaemonToken token) {
		this.daemonToken = token;
	}
	
	public void started() {
		
		if (encounterEventListener == null) {
			System.out.println("XXXXXXX Started");
			encounterEventListener = new EncounterEventListenerImpl(daemonToken);
		}
		System.out.println("XXXXXXX Not Started");
		// Subscribe specifically to CREATED actions on Encounter objects
		Event.subscribe(Encounter.class, String.valueOf(Event.Action.CREATED), encounterEventListener);
	}
	
	/**
	 * @see #shutdown()
	 */
	public void shutdown() {
		log.info("Shutdown Digipath.connector");
		if (encounterEventListener != null) {
			Event.unsubscribe(Encounter.class, Event.Action.CREATED, encounterEventListener);
		}
	}
	
}
