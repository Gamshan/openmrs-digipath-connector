package org.openmrs.module.digipath.connector.proforma;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Person;
import org.openmrs.User;

import java.io.Serializable;

public class DigipathConnectorUserPreference extends BaseOpenmrsData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer digipathConnectorUserPreferenceId;
	
	private User user;
	
	private DigipathConnector digipathConnector;
	
	private Boolean enabled = Boolean.TRUE;
	
	@Override
	public Integer getId() {
		return digipathConnectorUserPreferenceId;
	}
	
	@Override
	public void setId(Integer id) {
		this.digipathConnectorUserPreferenceId = id;
	}
	
	public Integer getDigipathConnectorUserPreferenceId() {
		return digipathConnectorUserPreferenceId;
	}
	
	public void setDigipathConnectorUserPreferenceId(Integer id) {
		this.digipathConnectorUserPreferenceId = id;
	}
	
	public DigipathConnector getDigipathConnector() {
		return digipathConnector;
	}
	
	public void setDigipathConnector(DigipathConnector digipathConnector) {
		this.digipathConnector = digipathConnector;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public Boolean getEnabled() {
		return enabled;
	}
	
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
}
