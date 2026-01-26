package org.openmrs.module.digipath.connector.proforma;

import net.openclinical.beans.DataDefinition;
import net.openclinical.beans.Meta;

import java.util.List;

public class DpAlertsData {
	
	private Meta meta;
	
	private String caption;
	
	private List<DataDefinition> dataDefinitions;
	
	public Meta getMeta() {
		return meta;
	}
	
	public void setMeta(Meta meta) {
		this.meta = meta;
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
}
