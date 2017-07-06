package org.whatif.tools.axiomgenerator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyCharacteristicAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredDataPropertyAxiomGenerator;

/**
 * Generates inferred data property characteristics.
 * 
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.1.0
 */
public class InferredDataPropertyCharacteristicAxiomGenerator
        extends InferredDataPropertyAxiomGenerator<OWLDataPropertyCharacteristicAxiom> {

    @Override
    protected void addAxioms(OWLDataProperty entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
            Set<OWLDataPropertyCharacteristicAxiom> result) {
        OWLFunctionalDataPropertyAxiom axiom = dataFactory.getOWLFunctionalDataPropertyAxiom(entity);
        if (reasoner.isEntailed(axiom)) {
            result.add(axiom);
        }
    }

    @Override
    public String getLabel() {
        return "Data property characteristics";
    }
}
