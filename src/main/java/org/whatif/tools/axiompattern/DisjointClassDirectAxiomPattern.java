package org.whatif.tools.axiompattern;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.whatif.tools.util.WhatifUtils;

public class DisjointClassDirectAxiomPattern implements AxiomPattern {

	DisjointClassDirectAxiomPattern() {
	}

	@Override
	public boolean matchesPattern(OWLAxiom ax, Object o) {
		if (ax instanceof OWLDisjointClassesAxiom) {
			if (o instanceof OWLReasoner) {
				OWLReasoner r = (OWLReasoner) o;
				OWLDisjointClassesAxiom dax = (OWLDisjointClassesAxiom) ax;
				if (dax.getClassExpressions().size() == 2) {
					OWLClassExpression ce0 = dax.getClassExpressionsAsList().get(0);
					OWLClassExpression ce1 = dax.getClassExpressionsAsList().get(1);
					return WhatifUtils.directlyDisjointWith(r, ce0, ce1);
				}
			}
		}
		return false;
	}

	

	@Override
	public String toString() {
		return "Disjointness (direct)";
	}

	@Override
	public int getWeight() {
		return 0;
	}

}
