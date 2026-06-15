package org.openmrs.module.digipath.connector.proforma;

import net.openclinical.beans.Code;
import net.openclinical.beans.Coding;
import org.openmrs.Concept;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;

public class DigipathUtils {
	
	public static Concept getConceptByCode(Code code) {
		ConceptService conceptService = Context.getService(ConceptService.class);
		Concept concept = null;
		
		for (Coding coding : code.getCoding()) {
			;
			switch (coding.getSystem()) {
				case "https://cielterminology.org/":
					System.out.println(" CIEL here " + coding.getCode());
					concept = getConcept("CIEL", coding.getCode());
					
					break;
				case "custom":
					concept = conceptService.getConceptByUuid(coding.getCode());
					break;
				default:
					concept = conceptService.getConceptByMapping(coding.getSystem(), coding.getCode());
					break;
			}
			
			if (concept != null)
				return concept;
			
		}
		
		return concept;
		
	}
	
	public static Concept getConcept(String system, String code) {
		
		ConceptService conceptService = Context.getService(ConceptService.class);
		
		ConceptSource source = Context.getConceptService().getConceptSourceByName(system);
		
		ConceptReferenceTerm term = conceptService.getConceptReferenceTermByCode(code, source);
		if (term == null) {
			System.out.println("No ConceptReferenceTerm found" + code);
			return null;
		}
		
		System.out.println("GGGG  3333333" + term + " " + term.getCode() + " " + source.getName());
		
		return conceptService.getConceptByMapping(term.getCode(), source.getName(), false);
		
	}
}
