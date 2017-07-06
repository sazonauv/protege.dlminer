package org.whatif.tools.axiomgenerator;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredObjectPropertyAxiomGenerator;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.1.0
 */
public class InferredInverseObjectPropertiesAxiomGenerator
        extends InferredObjectPropertyAxiomGenerator<OWLInverseObjectPropertiesAxiom> {

    @Override
    protected void addAxioms(OWLObjectProperty entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
            Set<OWLInverseObjectPropertiesAxiom> result) {
        
    	for (OWLObjectPropertyExpression prop : reasoner.getInverseObjectProperties(entity)) {
            result.add(dataFactory.getOWLInverseObjectPropertiesAxiom(entity, prop));
        }
    }

    @Override
    public String getLabel() {
        return "Inverse object properties";
    }
}