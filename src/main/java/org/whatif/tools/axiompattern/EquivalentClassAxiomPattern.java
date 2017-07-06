package org.whatif.tools.axiompattern;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class EquivalentClassAxiomPattern implements AxiomPattern {

	EquivalentClassAxiomPattern() {}
	
	@Override
	public boolean matchesPattern(OWLAxiom ax,Object r) {
		if(ax instanceof OWLEquivalentClassesAxiom) {
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Equivalence";
	}

	@Override
	public int getWeight() {
		return 5;
	}

}
