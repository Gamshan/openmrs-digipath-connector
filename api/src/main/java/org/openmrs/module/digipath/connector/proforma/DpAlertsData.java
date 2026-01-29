package org.openmrs.module.digipath.connector.proforma;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.openclinical.beans.DataDefinition;
import net.openclinical.beans.DataDefinitionClass;
import net.openclinical.beans.Meta;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DpAlertsData {
	
	private String name;
	
	private String caption;
	
	private String description;
	
	private List<DataDefinition> dataDefinitions;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCaption() {
		return caption;
	}
	
	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	public List<DataDefinition> getDataDefinitions() {
		return dataDefinitions;
	}
	
	public void setDataDefinitions(List<DataDefinition> dataDefinitions) {
		this.dataDefinitions = dataDefinitions;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
