package org.openmrs.module.digipath.connector.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.openclinical.beans.DataDefinition;

import org.openmrs.api.context.Context;
import org.openmrs.module.digipath.connector.api.DigipathRestService;
import org.openmrs.module.digipath.connector.proforma.DpAlerts;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
	
	@RequestMapping(value = "/get-recommendations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<Map<String, Object>> getDigipathRecommendations(@RequestParam String patientUuid) {
		
		String json = fetchDataFromExternalApi();
		ObjectMapper objectMapper = new ObjectMapper();
		
		try {
			DpAlerts dpAlerts = objectMapper.readValue(json, DpAlerts.class);
			JsonNode root = objectMapper.readTree(json);
			JsonNode dataNode = root.get("data");
			String flattenedJson = objectMapper.writeValueAsString(dataNode);
			
			if (dpAlerts == null || dpAlerts.getData() == null)
				throw new IllegalArgumentException();
			
			DigipathRestService digipathRestService = Context.getService(DigipathRestService.class);
			return digipathRestService.performProforma(dpAlerts.getData(), flattenedJson, patientUuid);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	@RequestMapping(value = "/fetch-data", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String fetchDataFromExternalApi() {
		RestTemplate restTemplate = new RestTemplate();
		String url = "https://labs.openclinical.net/api/v1/matt/dp_alerts_next";
		
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		String json = response.getBody();
		
		System.out.println("json fetched " + json);
		
		//		return restTemplate.getForObject(url, DpAlerts.class);
		
		return json;
	}
}
