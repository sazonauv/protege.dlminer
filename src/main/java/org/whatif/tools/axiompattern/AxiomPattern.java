package org.whatif.tools.axiompattern;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public interface AxiomPattern {
	boolean matchesPattern(OWLAxiom ax,Object r);
	int getWeight();
}
