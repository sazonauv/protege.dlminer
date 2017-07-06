package org.whatif.tools.axiomgenerator;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredIndividualAxiomGenerator;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.1.0
 */
public class InferredObjectPropertyAssertionGenerator
    extends InferredIndividualAxiomGenerator<OWLPropertyAssertionAxiom<?, ?>> {

	@Override
    protected void addAxioms(OWLNamedIndividual entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
        Set<OWLPropertyAssertionAxiom<?, ?>> result) {
    	
    	for(OWLObjectProperty p:reasoner.getRootOntology().getObjectPropertiesInSignature(true)) {
    		for(OWLNamedIndividual i: reasoner.getObjectPropertyValues(entity, p).getFlattened()) {
    			result.add(dataFactory.getOWLObjectPropertyAssertionAxiom(p, entity, i));
    		}
    	}        
    }
	

    @Override
    public String getLabel() {
        return "Object Property Assertions";
    }
}