package org.whatif.tools.axiompattern;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class NoAxiomPattern implements AxiomPattern {

	NoAxiomPattern() {
	}

	@Override
	public boolean matchesPattern(OWLAxiom ax, Object o) {
		return true;
	}

	@Override
	public String toString() {
		return "Other";
	}

	@Override
	public int getWeight() {
		return -100;
	}

}
