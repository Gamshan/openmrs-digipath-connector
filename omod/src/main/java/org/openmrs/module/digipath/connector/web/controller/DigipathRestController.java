package org.openmrs.module.digipath.connector.web.controller;

import net.openclinical.beans.DataDefinition;
import org.openmrs.api.context.Context;
import org.openmrs.module.digipath.connector.api.DigipathRestService;
import org.openmrs.module.digipath.connector.proforma.DpAlerts;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceController;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/rest/v1/digipath-connector")
public class DigipathRestController extends MainResourceController {
	
	@RequestMapping(value = "/test", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<Map<String, String>> getDigipath() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> hm = new HashMap<String, String>();
		hm.put("date", String.valueOf(new Date()));
		hm.put("action", "Hello World");
		Map<String, String> hm2 = new HashMap<String, String>();
		hm2.put("date", String.valueOf(new Date()));
		hm2.put("action", "Second action");
		list.add(hm);
		list.add(hm2);
		return list;
		//		return "HELLO WORLD";
	}
	
	@RequestMapping(value = "/perfoma", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Map<String, Object> getDigipathPerforma(@RequestParam String patientUuid, @RequestBody DpAlerts dpAlerts) {
		
		//		DpAlerts dpAlerts = fetchDataFromExternalApi();

		
		if (dpAlerts == null || dpAlerts.getData() == null || dpAlerts.getData().getDataDefinitions() == null)
			throw new IllegalArgumentException();
		
		DigipathRestService digipathRestService = Context.getService(DigipathRestService.class);
		return digipathRestService.performProforma(dpAlerts.getData().getDataDefinitions(), patientUuid);
		
	}

	
	public DpAlerts fetchDataFromExternalApi() {
		RestTemplate restTemplate = new RestTemplate();
		String url = "https://labs.openclinical.net/api/v1/matt/dp_alerts_next";
		
		// GET request to fetch data as a String or a custom DTO class
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		
		return restTemplate.getForObject(url, DpAlerts.class);
	}
}
