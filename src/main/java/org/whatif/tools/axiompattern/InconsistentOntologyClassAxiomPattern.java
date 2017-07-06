package org.whatif.tools.axiompattern;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class InconsistentOntologyClassAxiomPattern implements AxiomPattern {

	InconsistentOntologyClassAxiomPattern(){}
	
	@Override
	public boolean matchesPattern(OWLAxiom ax,Object r) {
		if(ax instanceof OWLSubClassOfAxiom) {
			OWLSubClassOfAxiom sax = (OWLSubClassOfAxiom)ax;
			if(sax.getSubClass().isOWLThing()&&sax.getSuperClass().isOWLNothing()) {
				return true;
			}
		} else if(ax instanceof OWLEquivalentClassesAxiom) {
			OWLEquivalentClassesAxiom sax = (OWLEquivalentClassesAxiom)ax;
			if(sax.containsOWLThing()&&sax.containsOWLNothing()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Inconsistent Ontology";
	}

	@Override
	public int getWeight() {
		return 10;
	}

}
