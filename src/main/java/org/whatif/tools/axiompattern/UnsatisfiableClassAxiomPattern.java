package org.whatif.tools.axiompattern;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class UnsatisfiableClassAxiomPattern implements AxiomPattern {

	UnsatisfiableClassAxiomPattern(){}
	
	@Override
	public boolean matchesPattern(OWLAxiom ax,Object r) {
		if(ax instanceof OWLSubClassOfAxiom) {
			OWLSubClassOfAxiom sax = (OWLSubClassOfAxiom)ax;
			if(sax.getSuperClass().isOWLNothing()) {
				return true;
			}
		} else if(ax instanceof OWLEquivalentClassesAxiom) {
			OWLEquivalentClassesAxiom sax = (OWLEquivalentClassesAxiom)ax;
			if(sax.containsOWLNothing()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Unsatisfiability";
	}

	@Override
	public int getWeight() {
		return 6;
	}

}
