package org.whatif.tools.axiomgenerator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredIndividualAxiomGenerator;

/**
 * Generates {@code OWLClassAssertionsAxiom}s for inferred individual types.
 * 
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.1.0
 */
public class InferredClassAssertionAxiomGenerator extends InferredIndividualAxiomGenerator<OWLClassAssertionAxiom> {

	final boolean direct;

	public InferredClassAssertionAxiomGenerator(boolean direct) {
		this.direct = direct;
	}
    @Override
    protected void addAxioms(OWLNamedIndividual entity, OWLReasoner reasoner,
        OWLDataFactory dataFactory, Set<OWLClassAssertionAxiom> result) {
    	for(OWLClass c:reasoner.getTypes(entity, direct).getFlattened()) {
    		result.add(dataFactory.getOWLClassAssertionAxiom(c, entity));
    	}
    }

    @Override
    public String getLabel() {
        return "Class assertions (individual types)";
    }
}