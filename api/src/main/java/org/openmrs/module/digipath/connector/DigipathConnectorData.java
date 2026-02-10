package org.openmrs.module.digipath.connector;

import org.openmrs.BaseOpenmrsData;

import javax.persistence.*;
import java.util.List;

public class DigipathConnectorData extends BaseOpenmrsData {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	private String code;
	
	private String name;
	
	private String description;
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
