package org.openmrs.module.digipath.connector.page.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.digipath.connector.proforma.DigipathConnector;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.module.digipath.connector.api.DigipathRestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

public class ConfigPageController {
	
	public void controller(PageModel model, @RequestParam(value = "editId", required = false) Integer editId) {
		System.out.println("111111 Yes Detete activated" + editId);
		DigipathRestService digipathRestService = Context.getService(DigipathRestService.class);
		model.addAttribute("connectors", digipathRestService.getAllDigipathConnectorData());
		
		DigipathConnector digipathConnector = null;
		if (editId != null) {
			digipathConnector = digipathRestService.getDigipathConnectorDataById(editId);
			System.out.println("CO <<<< " + digipathConnector.getId());
		}
		model.addAttribute("editConnector", digipathConnector);
		
	}
	
	public Object post(HttpServletRequest httpServletRequest,
	        @RequestParam(value = "deleteId", required = false) Integer deleteId,
	        @RequestParam(value = "editId", required = false) Integer editId) {
		System.out.println("Hesssssssssss" + deleteId);
		
		if (deleteId != null) {
			handleDelete(deleteId);
		} else if (editId != null) {
			handleEdit(httpServletRequest, editId);
		} else {
			DigipathRestService digipathRestService = Context.getService(DigipathRestService.class);
			DigipathConnector digipathConnector = new DigipathConnector();
			
			generateDigipathConnector(httpServletRequest, digipathConnector);
			digipathRestService.saveDigipathConnectorData(digipathConnector);
		}
		
		return "redirect:/digipath.connector/config.page";
	}
	
	private DigipathConnector generateDigipathConnector(HttpServletRequest httpServletRequest,
	        DigipathConnector digipathConnector) {
		String username = httpServletRequest.getParameter("username");
		String url = httpServletRequest.getParameter("url");
		String description = httpServletRequest.getParameter("description");
		
		digipathConnector.setUrl(url);
		digipathConnector.setDescription(description);
		digipathConnector.setUsername(username);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		String json = response.getBody();
		digipathConnector.setProtocol(json);
		
		return digipathConnector;
	}
	
	private void handleEdit(HttpServletRequest httpServletRequest, Integer id) {
		DigipathRestService digipathRestService = Context.getService(DigipathRestService.class);
		DigipathConnector digipathConnector = digipathRestService.getDigipathConnectorDataById(id);
		generateDigipathConnector(httpServletRequest, digipathConnector);
		digipathRestService.updateDigipathConnector(id, digipathConnector);
	}
	
	private void handleDelete(Integer id) {
		DigipathRestService digipathRestService = Context.getService(DigipathRestService.class);
		digipathRestService.deleteDigipathConnectorData(id);
	}
	
	//	public void controller(PageModel model, @RequestParam(value = "deleteId", required = false) Integer deleteId,
	//	        @RequestParam(value = "editId", required = false) Integer editId) {
	//
	//		System.out.println("Yes Detete activated" + deleteId);
	//
	//		if (editId != null) {
	//			DigipathRestService digipathRestService = Context.getService(DigipathRestService.class);
	//			DigipathConnector connector = digipathRestService.getDigipathConnectorDataById(editId);
	//			model.addAttribute("editConnector", connector);
	//		}
	//
	//		//		if (deleteId != null) {
	//		//			// Call your service to delete
	//		//			connectorService.deleteConnector(deleteId);
	//		//		}
	//		//
	//		//		model.addAttribute("connectors", connectorService.getAll());
	//	}
	
	//	public String post(@RequestParam(value = "deleteId", required = false) Integer deleteId) {
	//
	//		System.out.println("POST Yes Detete activated" + deleteId);
	//		//		if (deleteId != null) {
	//		//			connectorService.deleteConnector(deleteId);
	//		//		}
	//
	//		// Redirect back to same page after delete
	//		return "redirect:/digipath.connector/config.page";
	//	}
	
}
