package org.whatif.tools.axiomgenerator;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredIndividualAxiomGenerator;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.1.0
 */
public class InferredDataPropertyAssertionGenerator
    extends InferredIndividualAxiomGenerator<OWLPropertyAssertionAxiom<?, ?>> {

	@Override
    protected void addAxioms(OWLNamedIndividual entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
        Set<OWLPropertyAssertionAxiom<?, ?>> result) {
    	
    	for(OWLDataProperty p:reasoner.getRootOntology().getDataPropertiesInSignature(true)) {
    		for(OWLLiteral v: reasoner.getDataPropertyValues(entity, p)) {
    			result.add(dataFactory.getOWLDataPropertyAssertionAxiom(p, entity, v));
    		}
    	}
    }

    @Override
    public String getLabel() {
        return "Data Property Assertions";
    }
}