package org.whatif.tools.axiompattern;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class SubClassIfNotEquivalentClassAxiomPattern implements AxiomPattern {

	SubClassIfNotEquivalentClassAxiomPattern() {
	}

	@Override
	public boolean matchesPattern(OWLAxiom ax, Object o) {
		if(!(o instanceof OWLReasoner)) {
			return false;
		}
		OWLReasoner r = (OWLReasoner)o;
		if (ax instanceof OWLSubClassOfAxiom) {
			OWLSubClassOfAxiom sax = (OWLSubClassOfAxiom) ax;
			if (sax.getSuperClass().isClassExpressionLiteral()) {
				if (!r.getEquivalentClasses(sax.getSubClass()).contains(sax.getSuperClass().asOWLClass())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "Subclass but not equivalence";
	}

	@Override
	public int getWeight() {
		return 0;
	}

}
