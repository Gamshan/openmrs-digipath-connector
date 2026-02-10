package org.openmrs.module.digipath.connector.proforma;

import org.openmrs.BaseOpenmrsData;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

public class DigipathConnector extends BaseOpenmrsData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer digipathConnectorId;;
	
	private String url;
	
	private String username;
	
	private String description;
	
	public Integer getDigipathConnectorId() {
		return digipathConnectorId;
	}
	
	public void setDigipathConnectorId(Integer digipathConnectorId) {
		this.digipathConnectorId = digipathConnectorId;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public Integer getId() {
		return getDigipathConnectorId();
	}
	
	@Override
	public void setId(Integer id) {
		setDigipathConnectorId(id);
	}
}
