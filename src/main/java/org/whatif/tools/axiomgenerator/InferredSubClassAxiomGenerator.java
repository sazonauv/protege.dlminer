package org.whatif.tools.axiomgenerator;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredClassAxiomGenerator;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.1.0
 */
public class InferredSubClassAxiomGenerator extends InferredClassAxiomGenerator<OWLSubClassOfAxiom> {

	final boolean direct;

	public InferredSubClassAxiomGenerator(boolean direct) {
		this.direct = direct;
	}
	

	@Override
    protected Set<OWLClass> getEntities(OWLOntology ont) {
        Set<OWLClass> s = ont.getClassesInSignature();
        if (!s.isEmpty()) {
    		
        s.add(ont.getOWLOntologyManager().getOWLDataFactory().getOWLNothing());
        s.add(ont.getOWLOntologyManager().getOWLDataFactory().getOWLThing());
        }
		return s;
    }
	
    @Override
    protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
            Set<OWLSubClassOfAxiom> result) {
        if (reasoner.isSatisfiable(entity)) {
        	
            for(OWLClass sup:reasoner.getSuperClasses(entity, direct).getFlattened()) {
            	result.add(dataFactory.getOWLSubClassOfAxiom(entity, sup));
            }
                    
        } else {
            result.add(dataFactory.getOWLSubClassOfAxiom(entity, dataFactory.getOWLNothing()));
        }
    }

    @Override
    public String getLabel() {
        return "Subclasses";
    }
}
